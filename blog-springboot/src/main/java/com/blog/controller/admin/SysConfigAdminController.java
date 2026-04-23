package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置后台接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/sys-config")
@Validated
@RequiredArgsConstructor
public class SysConfigAdminController {

    private final SysConfigService sysConfigService;

    /**
     * 获取所有系统配置
     * GET /api/admin/sys-config
     */
    @GetMapping
    public Result<Map<String, String>> getAll() {
        log.debug("查询系统配置");
        return Result.success(sysConfigService.getAll());
    }

    /**
     * 批量更新系统配置
     * PUT /api/admin/sys-config
     */
    @PutMapping
    public Result<Void> updateBatch(@RequestBody Map<String, String> configs) {
        log.info("更新系统配置，共 {} 项", configs.size());
        sysConfigService.updateBatch(configs);
        return Result.success();
    }
}
