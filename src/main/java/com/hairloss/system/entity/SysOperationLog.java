package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志表
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作人 ID（0：系统）
     */
    private Long userId;

    /**
     * 操作类型（登录/上传/分析/配置等）
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求接口地址
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 操作 IP 地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 操作状态（0：失败，1：成功）
     */
    private Integer status;

    /**
     * 错误信息（失败时记录）
     */
    private String errorMsg;
}
