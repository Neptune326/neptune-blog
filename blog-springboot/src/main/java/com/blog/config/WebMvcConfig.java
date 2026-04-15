package com.blog.config;

import com.blog.common.interceptor.RequestLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置
 * 注册请求日志拦截器，拦截所有 API 请求
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 请求日志拦截器 */
    private final RequestLogInterceptor requestLogInterceptor;

    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor) {
        this.requestLogInterceptor = requestLogInterceptor;
    }

    /**
     * 注册拦截器
     * RequestLogInterceptor 拦截所有 /api/** 路径，记录请求日志和耗时
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**");
    }
}
