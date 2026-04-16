package com.blog.common.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录频率限制器（支持动态配置）
 */
@Component
public class LoginRateLimiter {

    private final ConcurrentHashMap<String, AtomicInteger> failCountMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockMap = new ConcurrentHashMap<>();

    public boolean isLocked(String ip) {
        Long lockUntil = lockMap.get(ip);
        if (lockUntil == null) return false;
        if (System.currentTimeMillis() < lockUntil) return true;
        lockMap.remove(ip);
        failCountMap.remove(ip);
        return false;
    }

    /**
     * 记录失败（支持动态 maxFail 和 lockMinutes）
     */
    public void recordFail(String ip, int maxFail, int lockMinutes) {
        AtomicInteger count = failCountMap.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();
        if (current >= maxFail) {
            lockMap.put(ip, System.currentTimeMillis() + (long) lockMinutes * 60 * 1000);
        }
    }

    /** 兼容旧调用（使用默认值） */
    public void recordFail(String ip) {
        recordFail(ip, 5, 10);
    }

    public void recordSuccess(String ip) {
        failCountMap.remove(ip);
        lockMap.remove(ip);
    }

    public long getLockRemainingSeconds(String ip) {
        Long lockUntil = lockMap.get(ip);
        if (lockUntil == null) return 0;
        return Math.max(0, (lockUntil - System.currentTimeMillis()) / 1000);
    }

    /** 获取当前失败次数 */
    public int getFailCount(String ip) {
        AtomicInteger count = failCountMap.get(ip);
        return count != null ? count.get() : 0;
    }
}
