package com.androgenic.common.exception;

import com.androgenic.common.result.Result;
import com.androgenic.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ResultCode.BAD_REQUEST.getMessage());
        log.warn("参数校验异常：{}", message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理参数绑定异常（@RequestParam / @ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ResultCode.BAD_REQUEST.getMessage());
        log.warn("参数绑定异常：{}", message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("资源未找到：{}", e.getRequestURL());
        return Result.error(ResultCode.NOT_FOUND.getCode(), "请求的资源不存在");
    }

    /**
     * 处理 Sa-Token 相关异常（可根据需要扩展）
     */
    @ExceptionHandler(cn.dev33.satoken.exception.SaTokenException.class)
    public Result<Void> handleSaTokenException(cn.dev33.satoken.exception.SaTokenException e) {
        log.warn("Sa-Token 异常：{}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), e.getMessage());
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(ResultCode.ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}
