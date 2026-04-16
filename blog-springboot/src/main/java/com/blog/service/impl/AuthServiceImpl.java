package com.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.common.util.LoginRateLimiter;
import com.blog.dto.LoginDTO;
import com.blog.entity.Admin;
import com.blog.entity.LoginLog;
import com.blog.mapper.AdminMapper;
import com.blog.mapper.LoginLogMapper;
import com.blog.service.AuthService;
import com.blog.service.SysConfigService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginRateLimiter rateLimiter;
    private final LoginLogMapper loginLogMapper;
    private final SysConfigService sysConfigService;

    public AuthServiceImpl(AdminMapper adminMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginRateLimiter rateLimiter,
                           LoginLogMapper loginLogMapper,
                           SysConfigService sysConfigService) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.rateLimiter = rateLimiter;
        this.loginLogMapper = loginLogMapper;
        this.sysConfigService = sysConfigService;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        String ip = getClientIp();
        String ua = getUserAgent();

        // 从系统配置读取最大失败次数（动态配置）
        int maxFail = sysConfigService.getIntValue("login_max_fail_count", 5);
        int lockMinutes = sysConfigService.getIntValue("login_lock_duration", 10);

        // 检查 IP 是否被锁定
        if (rateLimiter.isLocked(ip)) {
            long remaining = rateLimiter.getLockRemainingSeconds(ip);
            log.warn("IP {} 登录频率超限，剩余锁定时间 {}s", ip, remaining);
            saveLoginLog(loginDTO.getUsername(), ip, ua, false,
                    "登录失败次数过多，请 " + lockMinutes + " 分钟后再试");
            throw new BusinessException(ResultCode.LOGIN_TOO_MANY_ATTEMPTS);
        }

        // 查询管理员
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, loginDTO.getUsername())
        );

        // 用户不存在或密码错误
        if (admin == null || !passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            rateLimiter.recordFail(ip, maxFail, lockMinutes);
            log.warn("IP {} 登录失败，用户名: {}", ip, loginDTO.getUsername());
            saveLoginLog(loginDTO.getUsername(), ip, ua, false, "用户名或密码错误");
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 登录成功
        rateLimiter.recordSuccess(ip);
        StpUtil.login(admin.getUsername());
        log.info("管理员 {} 登录成功，IP: {}", admin.getUsername(), ip);
        saveLoginLog(admin.getUsername(), ip, ua, true, null);
        return StpUtil.getTokenValue();
    }

    @Override
    public void logout() {
        try {
            String username = (String) StpUtil.getLoginId();
            log.info("管理员 {} 退出登录", username);
        } catch (Exception ignored) {}
        StpUtil.logout();
    }

    /** 保存登录日志 */
    private void saveLoginLog(String username, String ip, String ua,
                               boolean success, String failReason) {
        try {
            LoginLog loginLog = new LoginLog();
            loginLog.setUsername(username);
            loginLog.setLoginIp(ip);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setStatus(success ? 1 : 0);
            loginLog.setFailReason(failReason);
            loginLog.setUserAgent(ua != null && ua.length() > 500 ? ua.substring(0, 500) : ua);
            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.warn("登录日志保存失败", e);
        }
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();
            String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP"};
            for (String header : headers) {
                String ip = request.getHeader(header);
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.split(",")[0].trim();
                }
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            return attrs.getRequest().getHeader("User-Agent");
        } catch (Exception e) {
            return null;
        }
    }
}
