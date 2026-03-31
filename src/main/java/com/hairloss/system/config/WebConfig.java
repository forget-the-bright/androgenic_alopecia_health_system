package com.hairloss.system.config;

import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.hairloss.system.common.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web 配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器 - 对所有路径生效
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 登录验证
                    if (!StpUtil.isLogin()) {
                        // 获取 response
                        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                        HttpServletResponse response = attributes.getResponse();

                        // 重定向到登录页（低版本通用写法）
                        try {
                            response.sendRedirect("/login.html");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 放行的页面
                        "/login.html",
                        "/register.html",
                        // 放行的 API
                        "/api/login",
                        "/api/register",
                        "/api/logout",
                        // 静态资源
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        // 接口文档
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**"
                );

        // 注册管理员权限拦截器 - 返回 JSON
        registry.addInterceptor(new SaInterceptor(handle -> {
                    StpUtil.checkRole("admin");
                }))
                .addPathPatterns("/api/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login.html");
        registry.addViewController("/register.html").setViewName("register.html");
        registry.addViewController("/index.html").setViewName("index.html");
        registry.addViewController("/hair-data.html").setViewName("hair-data.html");
        registry.addViewController("/ai-analysis.html").setViewName("ai-analysis.html");
        registry.addViewController("/medicine.html").setViewName("medicine.html");
        registry.addViewController("/profile.html").setViewName("profile.html");
        registry.addViewController("/admin.html").setViewName("admin.html");
    }
}
