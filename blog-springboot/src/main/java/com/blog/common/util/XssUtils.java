package com.blog.common.util;

/**
 * XSS 防护工具类
 * 对用户输入的内容进行 HTML 转义，防止 XSS 攻击
 */
public class XssUtils {

    /**
     * 转义 HTML 特殊字符
     * 适用于评论昵称、评论内容等用户输入字段
     */
    public static String escape(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * 移除潜在危险字符（用于昵称等纯文本字段）
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        // 移除 HTML 标签
        String result = input.replaceAll("<[^>]*>", "");
        // 移除脚本相关关键词
        result = result.replaceAll("(?i)(javascript|vbscript|onload|onerror|onclick):", "");
        return result.trim();
    }
}
