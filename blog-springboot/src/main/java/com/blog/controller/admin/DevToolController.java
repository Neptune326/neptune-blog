package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 开发工具接口（仅 dev 环境生效）
 * 用于调试和初始化数据，生产环境自动禁用
 */
@Slf4j
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Profile("dev")
public class DevToolController {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 重置 admin 密码为 admin123
     * GET /api/dev/reset-password
     */
    @GetMapping("/reset-password")
    public Result<String> resetPassword() {
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
            log.info("创建 admin 账号，密码已设置为 admin123，哈希: {}", newHash);
        } else {
            // 存在则更新密码
            adminMapper.update(null,
                    new LambdaUpdateWrapper<Admin>()
                            .eq(Admin::getUsername, "admin")
                            .set(Admin::getPassword, newHash)
                            .set(Admin::getRole, "super")
            );
            log.info("admin 密码已重置为 admin123，新哈希: {}", newHash);
        }

        return Result.success("密码已重置为 admin123，哈希值: " + newHash);
    }

    /**
     * 验证密码是否匹配
     * GET /api/dev/check-password?password=admin123
     */
    @GetMapping("/check-password")
    public Result<String> checkPassword(@RequestParam String password) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin")
        );
        if (admin == null) {
            return Result.success("admin 账号不存在");
        }
        boolean match = passwordEncoder.matches(password, admin.getPassword());
        return Result.success("密码验证结果: " + match + "，数据库哈希: " + admin.getPassword());
    }
}
