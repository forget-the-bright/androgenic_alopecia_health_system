package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 分析请求日志表
 */
@Data
@TableName("ai_analysis_request_log")
public class AiAnalysisRequestLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求日志唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 对比照片 1 ID
     */
    private Long imageId1;

    /**
     * 对比照片 2 ID
     */
    private Long imageId2;

    /**
     * 关联分析记录 ID
     */
    private Long analysisId;

    /**
     * 消耗 token 数量
     */
    private Long tokenUsed;

    /**
     * 请求状态（0：失败，1：成功）
     */
    private Integer requestStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
