package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 开发阶段允许所有来源访问，生产环境应限制为具体域名
 */
@Configuration
public class CorsConfig {

    /**
     * 注册 CorsFilter
     * 允许所有来源、所有方法、所有请求头，并允许携带凭证（Cookie/Token）
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源（开发阶段）
        config.addAllowedOriginPattern("*");
        // 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带凭证（如 Cookie、Authorization 头）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
