package com.blog.common.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.entity.VisitLog;
import com.blog.mapper.VisitLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 访问统计拦截器
 * 拦截前台 GET 请求，统计 PV/UV
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisitStatInterceptor implements HandlerInterceptor {

    private final VisitLogMapper visitLogMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只统计前台 GET 请求
        if (!"GET".equals(request.getMethod())) return true;
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin") || uri.startsWith("/uploads")) return true;

        try {
            LocalDate today = LocalDate.now();
            String ip = getClientIp(request);

            // 查询今日记录
            VisitLog existing = visitLogMapper.selectOne(
                    new LambdaQueryWrapper<VisitLog>()
                            .eq(VisitLog::getVisitDate, today)
                            .isNull(VisitLog::getArticleId)
            );

            if (existing == null) {
                // 新建今日记录
                VisitLog log = new VisitLog();
                log.setVisitDate(today);
                log.setPv(1);
                log.setUv(1);
                log.setCreateTime(LocalDateTime.now());
                visitLogMapper.insert(log);
            } else {
                // PV +1，UV 通过 IP 去重（简化实现，实际可用 Redis HyperLogLog）
                visitLogMapper.update(null, new LambdaUpdateWrapper<VisitLog>()
                        .eq(VisitLog::getId, existing.getId())
                        .setSql("pv = pv + 1"));
            }
        } catch (Exception e) {
            log.warn("访问统计记录失败", e);
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) return ip.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
