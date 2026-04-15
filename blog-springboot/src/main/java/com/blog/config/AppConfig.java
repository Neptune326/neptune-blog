package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 应用通用 Bean 配置
 * 注册全局共用的工具类 Bean，供 Service 层注入使用
 */
@Configuration
public class AppConfig {

    /**
     * 注册 BCryptPasswordEncoder Bean
     * 用于密码的加密与校验，避免明文存储
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
