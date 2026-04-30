package com.blog.service.impl;

import com.blog.service.CaptchaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 简单算术验证码（内存存储）
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    private static final int EXPIRE_MINUTES = 5;
    private static final Map<String, CaptchaSession> CACHE = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> generate() {
        clearExpired();
        int left = ThreadLocalRandom.current().nextInt(1, 10);
        int right = ThreadLocalRandom.current().nextInt(1, 10);
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        CACHE.put(captchaId, new CaptchaSession(String.valueOf(left + right), LocalDateTime.now().plusMinutes(EXPIRE_MINUTES)));
        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("captchaQuestion", left + " + " + right + " = ?");
        return result;
    }

    @Override
    public boolean verify(String captchaId, String captchaCode) {
        clearExpired();
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        CaptchaSession session = CACHE.remove(captchaId);
        if (session == null || session.expireAt.isBefore(LocalDateTime.now())) {
            return false;
        }
        return session.answer.equals(captchaCode.trim());
    }

    private void clearExpired() {
        LocalDateTime now = LocalDateTime.now();
        CACHE.entrySet().removeIf(e -> e.getValue().expireAt.isBefore(now));
    }

    private static class CaptchaSession {
        private final String answer;
        private final LocalDateTime expireAt;

        private CaptchaSession(String answer, LocalDateTime expireAt) {
            this.answer = answer;
            this.expireAt = expireAt;
        }
    }
}
