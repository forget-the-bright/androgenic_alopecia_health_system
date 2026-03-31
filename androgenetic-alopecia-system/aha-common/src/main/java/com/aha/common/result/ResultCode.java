package com.aha.common.result;

import lombok.Getter;

/**
 * 统一状态码枚举
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
    UNAUTHORIZED(401, "未登录或 token 已过期"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 参数校验失败
     */
    BAD_REQUEST(400, "参数错误"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(4000, "业务异常"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(4001, "用户不存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(4002, "用户名或密码错误"),

    /**
     * 账号已被禁用
     */
    USER_DISABLED(4003, "账号已被禁用"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(5001, "文件上传失败"),

    /**
     * 文件格式不支持
     */
    FILE_TYPE_NOT_SUPPORTED(5002, "文件格式不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(5003, "文件大小超限");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
