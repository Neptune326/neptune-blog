package com.blog.service;

import com.blog.dto.ChangePasswordDTO;
import com.blog.dto.LoginDTO;

/**
 * 认证服务接口
 * 定义管理员登录、登出的核心操作
 */
public interface AuthService {

    /**
     * 管理员登录
     *
     * @param loginDTO 登录请求参数（用户名、密码）
     * @return Sa-Token 生成的 token 字符串
     */
    String login(LoginDTO loginDTO);

    /** 管理员登出 */
    void logout();

    /** 修改密码 */
    void changePassword(ChangePasswordDTO dto);
}
