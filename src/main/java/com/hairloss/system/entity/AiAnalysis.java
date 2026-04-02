package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 分析记录表
 */
@Data
@TableName("ai_analysis")
public class AiAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分析记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 对比照片 1 ID
     */
    private Long imageId1;

    /**
     * 对比照片 2 ID
     */
    private Long imageId2;

    /**
     * AI 分析综合评分（0-100）
     */
    private BigDecimal score;

    /**
     * 毛发密度评分（0-100）
     */
    private Integer hairDensityScore;

    /**
     * 脱发改善评分（0-100）
     */
    private Integer hairLossImproveScore;

    /**
     * 变化趋势（改善/稳定/恶化）
     */
    private String trend;

    /**
     * 趋势详细描述
     */
    private String trendDescription;

    /**
     * 对比分析结果
     */
    private String compareResult;

    /**
     * 关键变化点
     */
    private String keyChanges;

    /**
     * 治疗建议
     */
    private String treatmentSuggestion;

    /**
     * 日常护理建议
     */
    private String dailyCare;

    /**
     * 下一步建议
     */
    private String nextStep;

    /**
     * 总体结论
     */
    private String conclusion;

    /**
     * AI 分析报告完整 JSON（原始数据）
     */
    private String reportContent;

    /**
     * 分析部位
     */
    private String analysisPart;

    /**
     * 时间间隔描述
     */
    private String timeInterval;

    /**
     * 图片 1 URL
     */
    private String imageUrl1;

    /**
     * 图片 2 URL
     */
    private String imageUrl2;

    /**
     * 图片 1 部位
     */
    private String imagePart1;

    /**
     * 图片 2 部位
     */
    private String imagePart2;

    /**
     * 图片 1 时间
     */
    private LocalDateTime imageTime1;

    /**
     * 图片 2 时间
     */
    private LocalDateTime imageTime2;

    /**
     * 分析完成时间
     */
    private LocalDateTime analysisTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
