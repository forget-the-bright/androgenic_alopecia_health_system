/*
package com.hairloss.system.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.hairloss.system.common.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

*/
/**
 * Sa-Token 配置类
 * 处理 AJAX 请求的 401 响应（不重定向）
 *//*

@Configuration
public class SaTokenConfig {

    */
/**
     * 注册 Sa-Token 全局过滤器
     * 处理 AJAX 请求的未登录情况，返回 401 而不是 302 重定向
     *//*

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定拦截的路由
                .addInclude("/**")
                // 排除不需要拦截的路由
                .addExclude("/login.html", "/register.html", "/static/**", "/doc.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**", "/favicon.ico", "/api/not-login", "/api/no-auth")
                // 认证函数：每次请求执行
                .setAuth(obj -> {
                    // 如果是 AJAX 请求，检查登录状态
                    if (isAjaxRequest()) {
                        // 未登录则抛出异常，由异常处理器统一处理
                        StpUtil.checkLogin();
                    }
                })
                // 异常处理函数：每次认证函数发生异常时执行
                .setError(e -> {
                    // 返回 JSON 格式的 401 响应
                    return Result.error(401, "请先登录");
                })
                // 前置函数：每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                            // 允许的 header 参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600");

                    // 如果是预检请求，直接返回
                    if (SaHolder.getRequest().getMethod().equals("OPTIONS")) {
                        SaRouter.back();
                    }
                });
    }

    */
/**
     * 判断是否为 AJAX 请求
     *//*

    private boolean isAjaxRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String accept = request.getHeader("Accept");
                String xRequestedWith = request.getHeader("X-Requested-With");
                // 如果 Accept 包含 application/json 或者 X-Requested-With 为 XMLHttpRequest，则为 AJAX 请求
                return (accept != null && accept.contains("application/json"))
                        || "XMLHttpRequest".equals(xRequestedWith);
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }
}
*/
