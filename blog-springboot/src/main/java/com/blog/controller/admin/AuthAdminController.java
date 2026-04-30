package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.dto.ChangePasswordDTO;
import com.blog.dto.LoginDTO;
import com.blog.service.AuthService;
import com.blog.service.CaptchaService;
import com.blog.service.SysConfigService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员认证控制器
 * 提供登录、登出接口，登录接口已在 Sa-Token 拦截器中排除鉴权
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/auth")
@Validated
public class AuthAdminController {

    /** 认证服务 */
    private final AuthService authService;
    private final CaptchaService captchaService;
    private final SysConfigService sysConfigService;

    public AuthAdminController(AuthService authService, CaptchaService captchaService, SysConfigService sysConfigService) {
        this.authService = authService;
        this.captchaService = captchaService;
        this.sysConfigService = sysConfigService;
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        return Result.success(captchaService.generate());
    }

    /**
     * 管理员登录
     * POST /api/admin/auth/login
     *
     * @param loginDTO 登录请求体（用户名、密码），参数校验由 @Valid 触发
     * @return 包含 token 字符串的统一响应
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        if (sysConfigService.getBoolValue("login_captcha_enabled")
                && !captchaService.verify(loginDTO.getCaptchaId(), loginDTO.getCaptchaCode())) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "验证码错误或已过期");
        }
        String token = authService.login(loginDTO);
        return Result.success(token);
    }

    /**
     * 管理员登出
     * POST /api/admin/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 修改密码
     * POST /api/admin/auth/change-password
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return Result.success();
    }
}
