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
     * AI 分析评分（0-100）
     */
    private BigDecimal score;

    /**
     * 变化趋势（改善/稳定/恶化）
     */
    private String trend;

    /**
     * AI 分析报告详情
     */
    private String reportContent;

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
