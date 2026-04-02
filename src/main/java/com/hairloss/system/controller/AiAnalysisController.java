package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.AiAnalysis;
import com.hairloss.system.service.AiAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 分析控制器
 */
@RestController
@RequestMapping("/api/analysis")
@Api(tags = "AI 对比分析")
public class AiAnalysisController {

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @PostMapping("/analyze")
    @ApiOperation("发起 AI 对比分析")
    public Result<AiAnalysis> analyze(@RequestBody Map<String, Long> params) {
        Long imageId1 = params.get("imageId1");
        Long imageId2 = params.get("imageId2");

        if (imageId1 == null || imageId2 == null) {
            return Result.error("请选择两张照片进行对比");
        }

        if (imageId1.equals(imageId2)) {
            return Result.error("请选择两张不同的照片");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        AiAnalysis analysis = aiAnalysisService.analyze(userId, imageId1, imageId2);
        return Result.success(analysis);
    }

    @GetMapping("/page")
    @ApiOperation("分页获取分析记录列表")
    public Result<Page<AiAnalysis>> getAnalysisPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<AiAnalysis> page = aiAnalysisService.getAnalysisPage(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/list")
    @ApiOperation("获取分析记录列表（全部）")
    public Result<List<AiAnalysis>> getAnalysisList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<AiAnalysis> list = aiAnalysisService.getAnalysisList(userId);
        return Result.success(list);
    }

    @GetMapping("/{analysisId}")
    @ApiOperation("获取分析详情")
    public Result<AiAnalysis> getAnalysisDetail(@PathVariable Long analysisId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(aiAnalysisService.getAnalysisDetail(analysisId, userId));
    }
}
