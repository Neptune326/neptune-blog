package com.blog.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 安全响应头过滤器
 * 添加常见的 HTTP 安全响应头，防御 XSS、点击劫持等攻击
 */
@Component
public class SecurityHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 防止点击劫持（禁止在 iframe 中嵌入）
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // 防止 MIME 类型嗅探
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // XSS 保护（现代浏览器已内置，旧浏览器兼容）
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // 引用策略（不泄露 Referer 信息）
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // 权限策略（禁用不需要的浏览器特性）
        httpResponse.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

        chain.doFilter(request, response);
    }
}
