package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.AiAnalysis;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.mapper.AiAnalysisMapper;
import com.hairloss.system.service.AiAnalysisService;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.UserHairImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * AI 分析记录服务实现类
 */
@Service
public class AiAnalysisServiceImpl extends ServiceImpl<AiAnalysisMapper, AiAnalysis> implements AiAnalysisService {

    @Autowired
    private UserHairImageService userHairImageService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Value("${ai.analysis.url:}")
    private String aiAnalysisUrl;

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
            // 忽略异常
        }
        return "0.0.0.0";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiAnalysis analyze(Long userId, Long imageId1, Long imageId2) {
        // 验证照片是否存在且属于当前用户
        UserHairImage image1 = userHairImageService.getById(imageId1);
        UserHairImage image2 = userHairImageService.getById(imageId2);

        if (image1 == null || image2 == null) {
            throw new RuntimeException("照片不存在");
        }

        if (!image1.getUserId().equals(userId) || !image2.getUserId().equals(userId)) {
            throw new RuntimeException("无权限访问该照片");
        }

        // 调用 AI 接口进行分析（模拟）
        AiAnalysisResult result = callAiAnalysis(image1, image2);

        // 保存分析记录
        AiAnalysis analysis = new AiAnalysis();
        analysis.setUserId(userId);
        analysis.setImageId1(imageId1);
        analysis.setImageId2(imageId2);
        analysis.setScore(result.getScore());
        analysis.setTrend(result.getTrend());
        analysis.setReportContent(result.getReportContent());
        analysis.setAnalysisTime(LocalDateTime.now());

        boolean success = this.save(analysis);

        if (success) {
            sysOperationLogService.logOperation(userId, "AI_ANALYSIS", "AI 对比分析",
                    "/api/analysis/analyze", "imageId1=" + imageId1 + ",imageId2=" + imageId2,
                    getIpAddress(), 1, null);
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
    public AiAnalysis getAnalysisDetail(Long analysisId, Long userId) {
        AiAnalysis analysis = this.getById(analysisId);
        if (analysis == null || !analysis.getUserId().equals(userId)) {
            throw new RuntimeException("分析记录不存在");
        }
        return analysis;
    }

    /**
     * 调用 AI 接口进行分析（模拟实现）
     */
    private AiAnalysisResult callAiAnalysis(UserHairImage image1, UserHairImage image2) {
        // TODO: 实际项目中需要调用真实的 AI 接口
        // 这里使用模拟数据

        Random random = new Random();
        BigDecimal score = new BigDecimal(random.nextInt(41) + 60); // 60-100 分

        String[] trends = {"改善", "稳定", "恶化"};
        String trend = trends[random.nextInt(3)];

        StringBuilder report = new StringBuilder();
        report.append("AI 毛发分析报告\n\n");
        report.append("对比照片 1：").append(image1.getPart()).append(" - ").append(image1.getUploadDate()).append("\n");
        report.append("对比照片 2：").append(image2.getPart()).append(" - ").append(image2.getUploadDate()).append("\n\n");
        report.append("分析结果：\n");
        report.append("1. 毛发密度评分：").append(score).append("分\n");
        report.append("2. 变化趋势：").append(trend).append("\n");
        report.append("3. 专业建议：\n");

        if ("改善".equals(trend)) {
            report.append("   您的毛发状况有所改善，请继续保持当前的治疗方案。\n");
            report.append("   建议定期记录毛发变化，持续观察效果。");
        } else if ("稳定".equals(trend)) {
            report.append("   您的毛发状况保持稳定，当前治疗方案有效。\n");
            report.append("   建议继续按医嘱用药，定期复查。");
        } else {
            report.append("   您的毛发状况有所恶化，建议及时调整治疗方案。\n");
            report.append("   请尽快咨询专业医生，获取更详细的治疗建议。");
        }

        return new AiAnalysisResult(score, trend, report.toString());
    }

    /**
     * AI 分析结果内部类
     */
    private static class AiAnalysisResult {
        private final BigDecimal score;
        private final String trend;
        private final String reportContent;

        public AiAnalysisResult(BigDecimal score, String trend, String reportContent) {
            this.score = score;
            this.trend = trend;
            this.reportContent = reportContent;
        }

        public BigDecimal getScore() {
            return score;
        }

        public String getTrend() {
            return trend;
        }

        public String getReportContent() {
            return reportContent;
        }
    }
}
