package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 通过配置文件控制允许来源，避免默认全开放
 */
@Slf4j
@Configuration
public class CorsConfig {
    
    @Value("${blog.security.cors.allowed-origin-patterns:*}")
    private String allowedOriginPatterns;

    /**
     * 注册 CorsFilter
     * 允许来源按白名单匹配，支持携带凭证（Cookie/Token）
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        String[] patterns = allowedOriginPatterns == null ? new String[0] : allowedOriginPatterns.split(",");
        boolean hasValidPattern = false;
        if (patterns.length == 0) {
            config.addAllowedOriginPattern("*");
            log.warn("[跨域配置] 未配置允许来源，已回退为 *，建议在生产环境配置白名单");
        } else {
            for (String pattern : patterns) {
                String trimmed = pattern.trim();
                if (!trimmed.isEmpty()) {
                    config.addAllowedOriginPattern(trimmed);
                    hasValidPattern = true;
                }
            }
            if (!hasValidPattern) {
                config.addAllowedOriginPattern("*");
                log.warn("[跨域配置] 允许来源配置为空白，已回退为 *，建议在生产环境配置白名单");
            }
        }
        // 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带凭证（如 Cookie、Authorization 头）
        config.setAllowCredentials(true);
        // 允许浏览器缓存预检结果，减少 OPTIONS 请求
        config.setMaxAge(3600L);
        // 暴露认证头，便于前端读取
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
