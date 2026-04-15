package com.blog.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求日志拦截器
 * 记录每个请求的路径、方法、参数及响应耗时，便于排查问题
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    /** 存储请求开始时间的 attribute key */
    private static final String START_TIME_ATTR = "startTime";

    /**
     * 请求处理前：记录请求信息，并将开始时间存入 request attribute
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("请求开始 {} {}，参数：{}", method, uri, queryString);
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    /**
     * 请求处理完成后：计算耗时并记录日志
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long cost = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            log.info("请求结束 {} {}，耗时：{}ms", method, uri, cost);
        }
    }
}
