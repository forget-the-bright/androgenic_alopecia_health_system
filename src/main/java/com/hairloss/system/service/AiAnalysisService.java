package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.AiAnalysis;

import java.util.List;

/**
 * AI 分析记录服务接口
 */
public interface AiAnalysisService extends IService<AiAnalysis> {

    /**
     * 发起 AI 对比分析
     * @param userId 用户 ID
     * @param imageId1 照片 1 ID
     * @param imageId2 照片 2 ID
     * @return 分析结果
     */
    AiAnalysis analyze(Long userId, Long imageId1, Long imageId2);

    Object getAnalyzePrompt(Long userId, Long imageId1, Long imageId2);
    /**
     * 获取用户分析记录列表
     * @param userId 用户 ID
     * @return 分析记录列表
     */
    List<AiAnalysis> getAnalysisList(Long userId);

    /**
     * 分页获取用户分析记录列表
     * @param userId 用户 ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<AiAnalysis> getAnalysisPage(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取分析详情
     * @param analysisId 分析 ID
     * @param userId 用户 ID
     * @return 分析详情
     */
    AiAnalysis getAnalysisDetail(Long analysisId, Long userId);

}
