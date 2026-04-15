package com.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.LoginDTO;
import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import com.blog.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * 处理管理员登录校验与登出逻辑
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** 管理员数据访问层 */
    private final AdminMapper adminMapper;

    /** BCrypt 密码编码器，用于密码比对 */
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(AdminMapper adminMapper, BCryptPasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 管理员登录
     * 1. 根据用户名查询管理员记录
     * 2. 用户不存在则抛出业务异常
     * 3. BCrypt 比对密码，不匹配则抛出业务异常
     * 4. 通过 Sa-Token 创建登录会话
     * 5. 返回生成的 token
     */
    @Override
    public String login(LoginDTO loginDTO) {
        // 根据用户名查询管理员
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, loginDTO.getUsername())
        );

        // 用户不存在，抛出统一错误（不区分用户名/密码，防止枚举攻击）
        if (admin == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 密码比对失败，抛出统一错误
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 登录成功，以用户名为 loginId 创建 Sa-Token 会话
        StpUtil.login(admin.getUsername());

        // 返回当前会话的 token 值
        return StpUtil.getTokenValue();
    }

    /**
     * 管理员登出
     * 清除当前请求对应的 Sa-Token 会话
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }
}
