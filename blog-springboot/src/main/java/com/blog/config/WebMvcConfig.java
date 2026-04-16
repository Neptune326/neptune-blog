package com.blog.config;

import com.blog.common.interceptor.RequestLogInterceptor;
import com.blog.common.interceptor.VisitStatInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;
    private final VisitStatInterceptor visitStatInterceptor;

    @Value("${blog.upload.path:./uploads/}")
    private String uploadPath;

    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor,
                        VisitStatInterceptor visitStatInterceptor) {
        this.requestLogInterceptor = requestLogInterceptor;
        this.visitStatInterceptor = visitStatInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(visitStatInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
