package com.androgenic.common.result;

import lombok.Getter;

/**
 * 统一返回状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 参数校验失败
     */
    BAD_REQUEST(400, "参数校验失败"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(422, "业务异常"),

    /**
     * Token 无效或过期
     */
    TOKEN_INVALID(401, "Token 无效或过期"),

    /**
     * Token 已过期
     */
    TOKEN_TIMEOUT(401, "Token 已过期"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(403, "账号已被禁用"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(500, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(400, "不支持的文件类型"),

    /**
     * 文件大小超出限制
     */
    FILE_SIZE_EXCEEDED(400, "文件大小超出限制");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
