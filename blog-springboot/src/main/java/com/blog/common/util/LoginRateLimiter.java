package com.blog.common.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录频率限制器
 * 同一 IP 在 10 分钟内登录失败超过 5 次则锁定
 */
@Component
public class LoginRateLimiter {

    /** 失败次数记录：key=IP，value=失败次数 */
    private final ConcurrentHashMap<String, AtomicInteger> failCountMap = new ConcurrentHashMap<>();

    /** 锁定时间记录：key=IP，value=锁定到期时间戳 */
    private final ConcurrentHashMap<String, Long> lockMap = new ConcurrentHashMap<>();

    /** 最大失败次数 */
    private static final int MAX_FAIL = 5;

    /** 锁定时长：10 分钟（毫秒） */
    private static final long LOCK_DURATION = 10 * 60 * 1000L;

    /**
     * 检查 IP 是否被锁定
     */
    public boolean isLocked(String ip) {
        Long lockUntil = lockMap.get(ip);
        if (lockUntil == null) return false;
        if (System.currentTimeMillis() < lockUntil) return true;
        // 锁定已过期，清除记录
        lockMap.remove(ip);
        failCountMap.remove(ip);
        return false;
    }

    /**
     * 记录登录失败，超过阈值则锁定
     */
    public void recordFail(String ip) {
        AtomicInteger count = failCountMap.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();
        if (current >= MAX_FAIL) {
            lockMap.put(ip, System.currentTimeMillis() + LOCK_DURATION);
        }
    }

    /**
     * 登录成功，清除失败记录
     */
    public void recordSuccess(String ip) {
        failCountMap.remove(ip);
        lockMap.remove(ip);
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public long getLockRemainingSeconds(String ip) {
        Long lockUntil = lockMap.get(ip);
        if (lockUntil == null) return 0;
        long remaining = (lockUntil - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
}
