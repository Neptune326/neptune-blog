package com.blog.config;

import com.blog.common.interceptor.RequestLogInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置：注册拦截器 + 静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;

    @Value("${blog.upload.path:./uploads/}")
    private String uploadPath;

    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor) {
        this.requestLogInterceptor = requestLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**");
    }

    /**
     * 将本地 uploads 目录映射为 /uploads/** 静态资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
