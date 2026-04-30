package com.blog.service;

import java.util.Map;

/**
 * 登录验证码服务
 */
public interface CaptchaService {

    /**
     * 生成验证码会话
     * @return 包含 captchaId 和 captchaQuestion
     */
    Map<String, String> generate();

    /**
     * 校验验证码
     */
    boolean verify(String captchaId, String captchaCode);
}
