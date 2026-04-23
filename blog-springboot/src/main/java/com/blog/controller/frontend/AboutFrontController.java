package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 关于我前台接口
 */
@RestController
@RequestMapping("/api/about")
@Validated
@RequiredArgsConstructor
public class AboutFrontController {

    private final SysConfigService sysConfigService;

    @GetMapping
    public Result<Map<String, String>> getAbout() {
        Map<String, String> result = new HashMap<>();
        result.put("content", sysConfigService.getValue("about_content"));
        result.put("authorName", sysConfigService.getValue("blog_author"));
        result.put("description", sysConfigService.getValue("blog_description"));
        return Result.success(result);
    }
}
