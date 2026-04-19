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
        // 规范化为绝对路径，兼容 Windows 和 Linux
        java.io.File uploadDir = new java.io.File(uploadPath).getAbsoluteFile();
        String absolutePath = uploadDir.getAbsolutePath();
        if (!absolutePath.endsWith(java.io.File.separator)) {
            absolutePath = absolutePath + java.io.File.separator;
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
