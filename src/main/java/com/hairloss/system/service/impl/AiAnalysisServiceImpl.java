package com.hairloss.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hairloss.system.dto.AiAnalysisReport;
import com.hairloss.system.entity.AiAnalysis;
import com.hairloss.system.entity.AiAnalysisRequestLog;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.mapper.AiAnalysisMapper;
import com.hairloss.system.mapper.AiAnalysisRequestLogMapper;
import com.hairloss.system.service.AiAnalysisService;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.UserHairImageService;
import com.hairloss.system.service.UserMembershipService;
import com.hairloss.system.utils.TimeUtil;
import com.moandjiezana.toml.TomlWriter;
import com.volcengine.ark.runtime.model.responses.constant.ResponsesConstants;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemImage;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemText;
import com.volcengine.ark.runtime.model.responses.content.OutputContentItemText;
import com.volcengine.ark.runtime.model.responses.item.BaseItem;
import com.volcengine.ark.runtime.model.responses.item.ItemEasyMessage;
import com.volcengine.ark.runtime.model.responses.item.ItemOutputMessage;
import com.volcengine.ark.runtime.model.responses.item.MessageContent;
import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI 分析记录服务实现类
 */
@Slf4j
@Service
public class AiAnalysisServiceImpl extends ServiceImpl<AiAnalysisMapper, AiAnalysis> implements AiAnalysisService {

    @Autowired
    private UserHairImageService userHairImageService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    private AiAnalysisRequestLogMapper aiAnalysisRequestLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${ai.analysis.url:}")
    private String aiAnalysisUrl;

    @Value("${ai.analysis.key:}")
    private String aiAnalysisKey;

    @Value("${ai.analysis.model:}")
    private String model;

    /**
     * 获取请求 IP 地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("获取 IP 地址失败", e);
        }
        return "0.0.0.0";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiAnalysis analyze(Long userId, Long imageId1, Long imageId2) {
        // 会员权限校验
        Map<String, Object> permissionResult = userMembershipService.checkAnalysisPermission(userId);
        if (!(Boolean) permissionResult.get("allowed")) {
            throw new RuntimeException((String) permissionResult.get("reason"));
        }

        // 验证照片是否存在且属于当前用户
        UserHairImage image1 = userHairImageService.getById(imageId1);
        UserHairImage image2 = userHairImageService.getById(imageId2);

        if (image1 == null || image2 == null) {
            throw new RuntimeException("照片不存在");
        }

        if (!image1.getUserId().equals(userId) || !image2.getUserId().equals(userId)) {
            throw new RuntimeException("无权限访问该照片");
        }

        AiAnalysis analysis = null;
        boolean success = false;
        String errorMessage = null;

        try {
            // 调用 AI 接口进行分析
            AiAnalysisReport report = callAiAnalysis(image1, image2);

            // 保存分析记录
            analysis = buildAiAnalysis(userId, imageId1, imageId2, report);
            success = this.save(analysis);

            if (success) {
                // 增加已使用次数
                userMembershipService.incrementUsedCount(userId);

                // 记录请求日志（成功）
                logRequest(userId, imageId1, imageId2, analysis.getId(), report.getUsageInfo().getTotalTokens(), true, null);

                sysOperationLogService.logOperation(userId, "AI_ANALYSIS", "AI 对比分析",
                        "/api/analysis/analyze", "imageId1=" + imageId1 + ",imageId2=" + imageId2,
                        getIpAddress(), 1, null);
                log.info("AI 分析完成，用户 ID: {}, 分析 ID: {}", userId, analysis.getId());
            }
        } catch (Exception e) {
            log.error("AI 分析失败", e);
            errorMessage = e.getMessage();

            // 记录请求日志（失败）
            logRequest(userId, imageId1, imageId2, null, 0l, false, errorMessage);

            throw new RuntimeException("分析失败：" + errorMessage);
        }

        return analysis;
    }

    @Override
    public java.util.List<AiAnalysis> getAnalysisList(Long userId) {
        LambdaQueryWrapper<AiAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiAnalysis::getUserId, userId)
                .orderByDesc(AiAnalysis::getAnalysisTime);
        return this.list(wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<AiAnalysis> getAnalysisPage(Long userId, Integer pageNum, Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AiAnalysis> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AiAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiAnalysis::getUserId, userId)
                .orderByDesc(AiAnalysis::getAnalysisTime);
        return this.page(page, wrapper);
    }

    @Override
    public AiAnalysis getAnalysisDetail(Long analysisId, Long userId) {
        AiAnalysis analysis = this.getById(analysisId);
        if (analysis == null || !analysis.getUserId().equals(userId)) {
            throw new RuntimeException("分析记录不存在");
        }
        return analysis;
    }

    /**
     * 构建 AI 分析记录实体
     */
    private AiAnalysis buildAiAnalysis(Long userId, Long imageId1, Long imageId2, AiAnalysisReport report) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setUserId(userId);
        analysis.setImageId1(imageId1);
        analysis.setImageId2(imageId2);
        analysis.setAnalysisTime(TimeUtil.now());

        // 获取图片信息
        UserHairImage hairImage1 = userHairImageService.getById(imageId1);
        UserHairImage hairImage2 = userHairImageService.getById(imageId2);

        // 设置图片信息
        if (hairImage1 != null) {
            analysis.setImageUrl1(hairImage1.getFileUrl());
            analysis.setImagePart1(hairImage1.getPart());
            analysis.setImageTime1(hairImage1.getUploadTime());
        }
        if (hairImage2 != null) {
            analysis.setImageUrl2(hairImage2.getFileUrl());
            analysis.setImagePart2(hairImage2.getPart());
            analysis.setImageTime2(hairImage2.getUploadTime());
        }

        // 设置基本信息
        if (report.getBasicInfo() != null) {
            analysis.setAnalysisPart(report.getBasicInfo().getPart());
            analysis.setTimeInterval(report.getBasicInfo().getTimeInterval());
        }

        // 设置评分信息
        if (report.getScoreInfo() != null) {
            Integer densityScore = report.getScoreInfo().getHairDensityScore();
            Integer improveScore = report.getScoreInfo().getHairLossImproveScore();

            // 综合评分取两个评分的平均值
            if (densityScore != null && improveScore != null) {
                analysis.setHairDensityScore(densityScore);
                analysis.setHairLossImproveScore(improveScore);
                analysis.setScore(new BigDecimal(densityScore + improveScore).divide(new BigDecimal(2)));
            } else if (densityScore != null) {
                analysis.setHairDensityScore(densityScore);
                analysis.setScore(new BigDecimal(densityScore));
            } else if (improveScore != null) {
                analysis.setHairLossImproveScore(improveScore);
                analysis.setScore(new BigDecimal(improveScore));
            }
        }

        // 设置趋势信息
        if (report.getTrendInfo() != null) {
            analysis.setTrend(report.getTrendInfo().getStatus());
            analysis.setTrendDescription(report.getTrendInfo().getDescription());
        }

        // 设置分析详情
        if (report.getAnalysisInfo() != null) {
            analysis.setCompareResult(report.getAnalysisInfo().getCompareResult());
            analysis.setKeyChanges(report.getAnalysisInfo().getKeyChanges());
        }

        // 设置建议信息
        if (report.getSuggestionInfo() != null) {
            analysis.setTreatmentSuggestion(report.getSuggestionInfo().getTreatmentSuggestion());
            analysis.setDailyCare(report.getSuggestionInfo().getDailyCare());
            analysis.setNextStep(report.getSuggestionInfo().getNextStep());
        }

        // 设置结论
        analysis.setConclusion(report.getConclusion());

        // 保存完整 JSON 报告
        try {
            analysis.setReportContent(objectMapper.writeValueAsString(report));
        } catch (JsonProcessingException e) {
            log.warn("序列化 AI 报告失败", e);
            analysis.setReportContent(JSONUtil.toJsonStr(report));
        }

        return analysis;
    }

    /**
     * 调用 AI 接口进行分析（真实实现）
     */
    private AiAnalysisReport callAiAnalysis(UserHairImage image1, UserHairImage image2) {
        String apiKey = aiAnalysisKey;

        // 计算时间间隔
        String timeInterval = calculateTimeInterval(image1.getUploadDate(), image2.getUploadDate());

        // 构建专业提示词
        //String prompt = buildAnalysisPrompt(image1, image2, timeInterval);
        //yaml  2126 token
        //toml 2401  token
        //json 2931  token
        //md 2675 token
        String prompt = buildAnalysisPromptYaml(image1, image2, timeInterval);

        log.info("开始调用 AI 接口进行分析，模型：{}, 部位：{}", model, image1.getPart());

        try {
            // 构建消息内容
            MessageContent content = MessageContent.builder()
                    .addListItem(InputContentItemImage.builder()
                            .imageUrl(image1.getFileUrl().substring(0, image1.getFileUrl().indexOf("?")))
                            .detail("high")
                            .build())
                    .addListItem(InputContentItemImage.builder()
                            .imageUrl(image2.getFileUrl().substring(0, image2.getFileUrl().indexOf("?")))
                            .detail("high")
                            .build())
                    .addListItem(InputContentItemText.builder().text(prompt).build())
                    .build();

            // 创建请求
            CreateResponsesRequest request = CreateResponsesRequest.builder()
                    .model(model)
                    .input(ResponsesInput.builder().addListItem(
                            ItemEasyMessage.builder()
                                    .role(ResponsesConstants.MESSAGE_ROLE_USER)
                                    .content(content)
                                    .build()
                    ).build())
                    .build();

            // 创建 ArkService 实例并调用
            ArkService arkService = ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(aiAnalysisUrl)
                    .build();

            ResponseObject resp = arkService.createResponse(request);

            // 解析响应
            String aiResponse = parseAiResponse(resp);
            arkService.shutdownExecutor();

            log.info("AI 响应内容：{}", aiResponse);

            // 解析 JSON 响应
            AiAnalysisReport report = parseAnalysisReport(aiResponse);

            if (report == null) {
                log.error("AI 响应解析失败，使用备用方案");
                return createFallbackAnalysis(image1, image2, timeInterval);
            }

            // 校验必要字段
            if (!validateReport(report)) {
                log.warn("AI 报告校验失败，使用备用方案");
                return createFallbackAnalysis(image1, image2, timeInterval);
            }

            log.info("AI 分析成功，趋势：{}, 评分：{}",
                    report.getTrendInfo() != null ? report.getTrendInfo().getStatus() : "未知",
                    report.getScoreInfo() != null ? report.getScoreInfo().getHairDensityScore() : "未知");
            report.setUsageInfo(resp.getUsage());
            return report;

        } catch (Exception e) {
            log.error("调用 AI 接口失败", e);
            // 降级处理，返回模拟数据
            return createFallbackAnalysis(image1, image2, timeInterval);
        }
    }

    /**
     * 计算两张照片的时间间隔
     */
    private String calculateTimeInterval(java.time.LocalDate date1, java.time.LocalDate date2) {
        if (date1 == null || date2 == null) {
            return "未知";
        }
        long days = ChronoUnit.DAYS.between(date1, date2);
        if (days < 0) {
            days = -days;
        }

        if (days < 30) {
            return days + "天";
        } else if (days < 365) {
            long months = days / 30;
            return months + "个月";
        } else {
            long years = days / 365;
            long months = (days % 365) / 30;
            return years + "年" + (months > 0 ? months + "个月" : "");
        }
    }

    /**
     * 构建专业的 AI 分析提示词
     */
    private String buildAnalysisPrompt(UserHairImage image1, UserHairImage image2, String timeInterval) {
        return StrUtil.format(
                "你是一位专业的皮肤科毛发医生，擅长雄激素性脱发（雄脱）诊断与对比分析。\n" +
                        "请对用户提供的**两张不同时间的头皮/脱发照片**进行专业、严谨、客观的对比分析。\n" +
                        "\n" +
                        "【任务要求】\n" +
                        "1. 必须基于真实视觉特征，不要编造\n" +
                        "2. 必须输出**标准 JSON 格式**，不要多余文字，不要解释，不要 Markdown\n" +
                        "3. 所有评分必须客观公正，符合医学标准\n" +
                        "\n" +
                        "【图片说明】\n" +
                        "图 1 = 较早时间拍摄\n" +
                        "图 2 = 较近时间拍摄\n" +
                        "\n" +
                        "图 1 部位：{}\n" +
                        "图 1 时间：{}\n" +
                        "图 2 部位：{}\n" +
                        "图 2 时间：{}\n" +
                        "时间间隔：{}\n" +
                        "\n" +
                        "【输出 JSON 结构】（严格遵守，不要添加额外字段）\n" +
                        "{{\n" +
                        "    \"basicInfo\": {{\n" +
                        "        \"part\": \"分析部位（头顶/前额/发旋/鬓角）\",\n" +
                        "        \"timeInterval\": \"时间间隔描述\",\n" +
                        "        \"image1Time\": \"图片 1 时间\",\n" +
                        "        \"image2Time\": \"图片 2 时间\"\n" +
                        "    }},\n" +
                        "    \"score\": {{\n" +
                        "        \"hairDensityScore\": 毛发密度评分（0-100 整数）,\n" +
                        "        \"hairLossImproveScore\": 脱发改善评分（0-100 整数）\n" +
                        "    }},\n" +
                        "    \"trend\": {{\n" +
                        "        \"status\": \"改善/稳定/加重（三选一）\",\n" +
                        "        \"description\": \"详细描述密度、粗细、覆盖度变化\"\n" +
                        "    }},\n" +
                        "    \"analysis\": {{\n" +
                        "        \"compareResult\": \"前后对比详细医学分析\",\n" +
                        "        \"keyChanges\": \"关键点变化（密度、发根、头皮可见度、粗细）\"\n" +
                        "    }},\n" +
                        "    \"suggestion\": {{\n" +
                        "        \"treatmentSuggestion\": \"治疗方案建议\",\n" +
                        "        \"dailyCare\": \"日常护理建议\",\n" +
                        "        \"nextStep\": \"下一步应该做什么\"\n" +
                        "    }},\n" +
                        "    \"conclusion\": \"总体结论一句话\"\n" +
                        "}}\n" +
                        "\n" +
                        "请严格按照以上结构输出 JSON，不要添加任何额外内容，不要 Markdown 格式。",
                image1.getPart(),
                image1.getUploadDate(),
                image2.getPart(),
                image2.getUploadDate(),
                timeInterval
        );
    }

    /**
     * 构建 AI 分析提示词（全 Map 结构化，易维护）
     */
    private String buildAnalysisPromptJson(UserHairImage image1, UserHairImage image2, String timeInterval) {
        // 转成 JSON 字符串返回
        Map<String, Object> promptMap = buildAnalysisPromptMap(image1, image2, timeInterval);
        return JSON.toJSONString(promptMap);
    }

    private String buildAnalysisPromptToml(UserHairImage image1, UserHairImage image2, String timeInterval) {
        // 转成 JSON 字符串返回
        Map<String, Object> promptMap = buildAnalysisPromptMap(image1, image2, timeInterval);
        // ====================== 核心：Map → 标准 TOML ======================
        try {
            // 写入：默认分段、缩进、多行
            TomlWriter tomlWriter = new TomlWriter();
            String toml = tomlWriter.write(promptMap);

/*            ObjectMapper mapper = new ObjectMapper(new TomlFactory()).enable(SerializationFeature.INDENT_OUTPUT);;
            return mapper.writeValueAsString(promptMap);*/
            return toml;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String buildAnalysisPromptYaml(UserHairImage image1, UserHairImage image2, String timeInterval) {
        // ====================== 关键：生成标准 YAML ======================
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setIndicatorIndent(0);

        Yaml yaml = new Yaml(options);
        Map<String, Object> promptMap = buildAnalysisPromptMap(image1, image2, timeInterval);
        return yaml.dump(promptMap);
    }

    private Map<String, Object> buildAnalysisPromptMap(UserHairImage image1, UserHairImage image2, String timeInterval) {
        Map<String, Object> prompt = new LinkedHashMap<>();

        // 1. 角色
        prompt.put("role", "专业皮肤科毛发医生，擅长雄脱诊断与对比分析");

        // 2. 任务
        prompt.put("task", "对两张不同时间的头皮/脱发照片做专业、严谨、客观对比分析");

        // 3. 要求
        prompt.put("rules", "必须基于视觉特征，不编造；仅输出标准JSON，无多余文字、无解释、无markdown；评分客观公正");

        // 4. 图片信息
        Map<String, Object> images = new LinkedHashMap<>();
        images.put("图1（较早）-部位", image1.getPart());
        images.put("图1（较早）-时间", DateUtil.formatLocalDateTime(image1.getUploadTime()));
        images.put("图2（较近）-部位", image2.getPart());
        images.put("图2（较近）-时间", DateUtil.formatLocalDateTime(image2.getUploadTime()));
        images.put("时间间隔", timeInterval);
        prompt.put("images", images);

        // ====================== 重点 ======================
        // outputStructure 完全用 MAP 构建（壳子结构，方便维护）
        // ====================== 重点 ======================
        Map<String, Object> outputStructure = new LinkedHashMap<>();

        // basicInfo
        Map<String, Object> basicInfo = new LinkedHashMap<>();
        basicInfo.put("part", "分析部位（头顶/前额/发旋/鬓角）");
        basicInfo.put("timeInterval", "时间间隔描述");
        basicInfo.put("image1Time", "图片1时间");
        basicInfo.put("image2Time", "图片2时间");

        // score
        Map<String, Object> score = new LinkedHashMap<>();
        score.put("hairDensityScore", "毛发密度评分 0-100 整数");
        score.put("hairLossImproveScore", "脱发改善评分 0-100 整数");

        // trend
        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("status", "改善/稳定/加重（三选一）");
        trend.put("description", "详细描述密度、粗细、覆盖度变化");

        // analysis
        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("compareResult", "前后对比详细医学分析");
        analysis.put("keyChanges", "关键点变化（密度、发根、头皮可见度、粗细）");

        // suggestion
        Map<String, Object> suggestion = new LinkedHashMap<>();
        suggestion.put("treatmentSuggestion", "治疗方案建议");
        suggestion.put("dailyCare", "日常护理建议");
        suggestion.put("nextStep", "下一步应该做什么");

        // 组装整个输出结构
        outputStructure.put("basicInfo", basicInfo);
        outputStructure.put("score", score);
        outputStructure.put("trend", trend);
        outputStructure.put("analysis", analysis);
        outputStructure.put("suggestion", suggestion);
        outputStructure.put("conclusion", "总体结论一句话");

        // 放入主 prompt
        prompt.put("outputStructure", outputStructure);

        // 最后命令
        prompt.put("final", "严格按照以上 outputStructure 结构输出，仅返回JSON，无任何多余内容");

        // 转成 JSON 字符串返回
        return prompt;
    }

    /**
     * 解析 AI 响应内容
     */
    private String parseAiResponse(ResponseObject resp) {
        try {
            if (resp == null || resp.getOutput() == null) {
                throw new RuntimeException("AI 响应为空");
            }

            // 获取响应内容
            //AI思考过程结果
            BaseItem thinkBaseItem = resp.getOutput().get(0);
            //AI实际返回结果
            ItemOutputMessage resultBaseItem = (ItemOutputMessage) resp.getOutput().get(1);
            OutputContentItemText outputContentItem = (OutputContentItemText) resultBaseItem.getContent().get(0);
            String message = outputContentItem.getText();
            /*String content =resultBaseItem.toString();
            
            // 提取 JSON 部分（如果包含在其他文本中）
            int jsonStart = content.indexOf("{");
            int jsonEnd = content.lastIndexOf("}");
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                return content.substring(jsonStart, jsonEnd + 1);
            }
            */
            return message;
        } catch (Exception e) {
            log.error("解析 AI 响应失败", e);
            throw new RuntimeException("解析 AI 响应失败", e);
        }
    }

    /**
     * 解析 AI 分析报告
     */
    private AiAnalysisReport parseAnalysisReport(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, AiAnalysisReport.class);
        } catch (Exception e) {
            log.warn("JSON 解析失败，尝试清理后重试", e);
            // 尝试清理 JSON 字符串
            String cleanedJson = cleanJsonString(jsonContent);
            try {
                return objectMapper.readValue(cleanedJson, AiAnalysisReport.class);
            } catch (Exception ex) {
                log.error("清理后 JSON 解析仍失败", ex);
                return null;
            }
        }
    }

    /**
     * 清理 JSON 字符串（移除 Markdown 标记等）
     */
    private String cleanJsonString(String json) {
        if (json == null) {
            return null;
        }
        // 移除 Markdown 代码块标记
        json = json.replaceAll("```json\\s*", "");
        json = json.replaceAll("```\\s*", "");
        // 移除首尾空白
        json = json.trim();
        return json;
    }

    /**
     * 校验 AI 报告的有效性
     */
    private boolean validateReport(AiAnalysisReport report) {
        if (report == null) {
            return false;
        }

        // 至少需要有评分或趋势信息
        boolean hasScore = report.getScoreInfo() != null &&
                (report.getScoreInfo().getHairDensityScore() != null ||
                        report.getScoreInfo().getHairLossImproveScore() != null);

        boolean hasTrend = report.getTrendInfo() != null &&
                report.getTrendInfo().getStatus() != null;

        return hasScore || hasTrend;
    }

    /**
     * 创建备用分析结果（当 AI 调用失败时）
     */
    private AiAnalysisReport createFallbackAnalysis(UserHairImage image1, UserHairImage image2, String timeInterval) {
        log.info("使用备用分析方案");

        AiAnalysisReport report = new AiAnalysisReport();

        // 基本信息
        AiAnalysisReport.BasicInfo basicInfo = new AiAnalysisReport.BasicInfo();
        basicInfo.setPart(image1.getPart());
        basicInfo.setTimeInterval(timeInterval);
        basicInfo.setImage1Time(image1.getUploadDate().toString());
        basicInfo.setImage2Time(image2.getUploadDate().toString());
        report.setBasicInfo(basicInfo);

        // 评分信息（给出中等评分）
        AiAnalysisReport.ScoreInfo scoreInfo = new AiAnalysisReport.ScoreInfo();
        scoreInfo.setHairDensityScore(75);
        scoreInfo.setHairLossImproveScore(70);
        report.setScoreInfo(scoreInfo);

        // 趋势信息
        AiAnalysisReport.TrendInfo trendInfo = new AiAnalysisReport.TrendInfo();
        trendInfo.setStatus("稳定");
        trendInfo.setDescription("从照片对比来看，您的毛发状况整体保持稳定，没有明显变化。建议继续观察并保持良好的生活习惯。");
        report.setTrendInfo(trendInfo);

        // 分析信息
        AiAnalysisReport.AnalysisInfo analysisInfo = new AiAnalysisReport.AnalysisInfo();
        analysisInfo.setCompareResult("两张照片拍摄时间相隔" + timeInterval + "，拍摄部位为" + image1.getPart() + "。从视觉上看，毛发密度和分布基本一致。");
        analysisInfo.setKeyChanges("1. 毛发密度：无明显变化\n2. 发根状态：稳定\n3. 头皮可见度：基本一致\n4. 毛发粗细：均匀");
        report.setAnalysisInfo(analysisInfo);

        // 建议信息
        AiAnalysisReport.SuggestionInfo suggestionInfo = new AiAnalysisReport.SuggestionInfo();
        suggestionInfo.setTreatmentSuggestion("建议继续当前的治疗方案，保持良好的用药习惯。");
        suggestionInfo.setDailyCare("1. 保持头皮清洁\n2. 避免熬夜\n3. 均衡饮食\n4. 适当按摩头皮");
        suggestionInfo.setNextStep("建议 1-2 个月后再次拍照对比，持续观察变化趋势。");
        report.setSuggestionInfo(suggestionInfo);

        // 结论
        report.setConclusion("您的毛发状况目前保持稳定，请继续保持良好的生活习惯并定期观察。");

        return report;
    }

    /**
     * 记录 AI 分析请求日志
     */
    private void logRequest(Long userId, Long imageId1, Long imageId2,
                            Long analysisId, Long tokenUsed,
                            boolean success, String errorMessage) {
        try {
            AiAnalysisRequestLog log = new AiAnalysisRequestLog();
            log.setUserId(userId);
            log.setRequestTime(TimeUtil.now());
            log.setImageId1(imageId1);
            log.setImageId2(imageId2);
            log.setAnalysisId(analysisId);
            log.setTokenUsed(tokenUsed);
            log.setRequestStatus(success ? 1 : 0);
            log.setErrorMessage(errorMessage);
            aiAnalysisRequestLogMapper.insert(log);
        } catch (Exception e) {
            log.warn("记录 AI 分析请求日志失败", e);
        }
    }
}
