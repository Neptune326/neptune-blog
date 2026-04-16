package com.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.common.util.LoginRateLimiter;
import com.blog.dto.LoginDTO;
import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import com.blog.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginRateLimiter rateLimiter;

    public AuthServiceImpl(AdminMapper adminMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginRateLimiter rateLimiter) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        String ip = getClientIp();

        // 检查 IP 是否被锁定
        if (rateLimiter.isLocked(ip)) {
            long remaining = rateLimiter.getLockRemainingSeconds(ip);
            log.warn("IP {} 登录频率超限，剩余锁定时间 {}s", ip, remaining);
            throw new BusinessException(ResultCode.LOGIN_TOO_MANY_ATTEMPTS);
        }

        // 查询管理员
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, loginDTO.getUsername())
        );

        // 用户不存在或密码错误（统一错误信息，防止用户名枚举）
        if (admin == null || !passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            rateLimiter.recordFail(ip);
            log.warn("IP {} 登录失败，用户名: {}", ip, loginDTO.getUsername());
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 登录成功
        rateLimiter.recordSuccess(ip);
        StpUtil.login(admin.getUsername());
        log.info("管理员 {} 登录成功，IP: {}", admin.getUsername(), ip);
        return StpUtil.getTokenValue();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 获取客户端真实 IP（兼容反向代理）
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();
            // 依次检查代理头
            String[] headers = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_CLIENT_IP"
            };
            for (String header : headers) {
                String ip = request.getHeader(header);
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // X-Forwarded-For 可能包含多个 IP，取第一个
                    return ip.split(",")[0].trim();
                }
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
