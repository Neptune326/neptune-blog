package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 开发工具接口（仅 dev 环境生效）
 * 用于调试和初始化数据，生产环境自动禁用
 */
@Slf4j
@RestController
@RequestMapping("/api/dev")
@Validated
@RequiredArgsConstructor
@Profile("dev")
public class DevToolController {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Value("${blog.dev-tool.enabled:false}")
    private boolean devToolEnabled;
    
    @Value("${blog.dev-tool.token:}")
    private String devToolToken;

    /**
     * 重置 admin 密码为 admin123
     * GET /api/dev/reset-password
     */
    @GetMapping("/reset-password")
    public Result<String> resetPassword(@RequestHeader(value = "X-Dev-Token", required = false) String token) {
        if (!validateDevToolAccess(token)) {
            return Result.error(com.blog.common.result.ResultCode.FORBIDDEN, "无权限访问开发工具接口");
        }
        String newHash = passwordEncoder.encode("admin123");

        // 查询是否存在 admin 账号
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin")
        );

        if (admin == null) {
            // 不存在则创建
            Admin newAdmin = new Admin();
            newAdmin.setUsername("admin");
            newAdmin.setPassword(newHash);
            newAdmin.setRole("super");
            adminMapper.insert(newAdmin);
            log.info("[开发工具] 创建 admin 账号，密码已重置");
        } else {
            // 存在则更新密码
            adminMapper.update(null,
                    new LambdaUpdateWrapper<Admin>()
                            .eq(Admin::getUsername, "admin")
                            .set(Admin::getPassword, newHash)
                            .set(Admin::getRole, "super")
            );
            log.info("[开发工具] admin 密码已重置");
        }

        return Result.success("密码已重置为 admin123");
    }

    /**
     * 验证密码是否匹配
     * GET /api/dev/check-password?password=admin123
     */
    @GetMapping("/check-password")
    public Result<String> checkPassword(@RequestHeader(value = "X-Dev-Token", required = false) String token,
                                        @RequestParam String password) {
        if (!validateDevToolAccess(token)) {
            return Result.error(com.blog.common.result.ResultCode.FORBIDDEN, "无权限访问开发工具接口");
        }
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin")
        );
        if (admin == null) {
            return Result.success("admin 账号不存在");
        }
        boolean match = passwordEncoder.matches(password, admin.getPassword());
        return Result.success("密码验证结果: " + match);
    }
    
    private boolean validateDevToolAccess(String token) {
        if (!devToolEnabled) {
            return false;
        }
        return StringUtils.hasText(devToolToken) && devToolToken.equals(token);
    }
}
