package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 前台公开站点配置接口（无需鉴权）
 * 只暴露前台需要的配置项，不暴露安全相关配置
 */
@RestController
@RequestMapping("/api/site-config")
@Validated
@RequiredArgsConstructor
public class SiteConfigFrontController {

    private final SysConfigService sysConfigService;

    /** 允许前台读取的配置键白名单 */
    private static final Set<String> PUBLIC_KEYS = new HashSet<>(Arrays.asList(
            "blog_name",
            "blog_description",
            "blog_author",
            "anime_theme_enabled",
            "gallery_images",
            "click_effect_enabled",
            "particle_enabled",
            "particle_type",
            "particle_count",
            "live2d_enabled",
            "frontend_theme_enabled",
            "frontend_theme_switcher_enabled",
            "frontend_theme_default",
            "frontend_ambient_enabled",
            "about_enabled",
            "message_enabled",
            "like_enabled"
    ));

    /**
     * 获取前台公开配置
     * GET /api/site-config
     */
    @GetMapping
    public Result<Map<String, String>> getPublicConfig() {
        Map<String, String> all = sysConfigService.getAll();
        Map<String, String> publicConfig = all.entrySet().stream()
                .filter(e -> PUBLIC_KEYS.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Result.success(publicConfig);
    }
}
