package com.blog.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.annotation.OperationLog;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员账号管理接口（仅超级管理员可操作）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    /** 获取管理员列表（隐藏密码） */
    @GetMapping
    public Result<List<Admin>> list() {
        checkSuperAdmin();
        List<Admin> admins = adminMapper.selectList(null);
        // 隐藏密码字段
        admins.forEach(a -> a.setPassword(null));
        return Result.success(admins);
    }

    /** 创建管理员 */
    @OperationLog(module = "管理员管理", action = "创建管理员")
    @PostMapping
    public Result<Void> create(@RequestBody @Valid CreateAdminRequest req) {
        checkSuperAdmin();
        // 检查用户名是否已存在
        long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, req.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        Admin admin = new Admin();
        admin.setUsername(req.getUsername());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setRole("admin");
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insert(admin);
        log.info("创建管理员：{}", req.getUsername());
        return Result.success();
    }

    /** 重置管理员密码 */
    @OperationLog(module = "管理员管理", action = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id,
                                       @RequestBody @Valid ResetPasswordRequest req) {
        checkSuperAdmin();
        Admin admin = adminMapper.selectById(id);
        if (admin == null) throw new BusinessException(ResultCode.NOT_FOUND);
        admin.setPassword(passwordEncoder.encode(req.getNewPassword()));
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.updateById(admin);
        log.info("重置管理员 {} 的密码", admin.getUsername());
        return Result.success();
    }

    /** 删除管理员（不能删除自己和超级管理员） */
    @OperationLog(module = "管理员管理", action = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkSuperAdmin();
        Admin admin = adminMapper.selectById(id);
        if (admin == null) throw new BusinessException(ResultCode.NOT_FOUND);
        if ("super".equals(admin.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        String currentUser = (String) StpUtil.getLoginId();
        if (admin.getUsername().equals(currentUser)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能删除自己");
        }
        adminMapper.deleteById(id);
        log.info("删除管理员：{}", admin.getUsername());
        return Result.success();
    }

    /** 检查当前用户是否为超级管理员 */
    private void checkSuperAdmin() {
        String username = (String) StpUtil.getLoginId();
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username)
        );
        if (admin == null || !"super".equals(admin.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    @Data
    public static class CreateAdminRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码至少 6 位")
        private String password;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, message = "密码至少 6 位")
        private String newPassword;
    }
}
