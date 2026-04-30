package com.blog.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.annotation.OperationLog;
import com.blog.service.AsyncLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 * 自动拦截带有 @OperationLog 注解的方法，记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final AsyncLogService asyncLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.blog.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        com.blog.entity.OperationLog logEntity = new com.blog.entity.OperationLog();
        logEntity.setCreateTime(LocalDateTime.now());

        // 获取请求信息
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                logEntity.setMethod(request.getMethod());
                logEntity.setRequestUrl(request.getRequestURI());
                logEntity.setRequestIp(getClientIp(request));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败", e);
        }

        // 获取注解信息
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog annotation = method.getAnnotation(OperationLog.class);
            logEntity.setModule(annotation.module());
            logEntity.setAction(annotation.action());
        } catch (Exception e) {
            log.warn("获取注解信息失败", e);
        }

        // 获取操作人
        try {
            if (StpUtil.isLogin()) {
                logEntity.setOperator((String) StpUtil.getLoginId());
            }
        } catch (Exception e) {
            logEntity.setOperator("unknown");
        }

        // 记录请求参数（截断过长内容）
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String paramJson = objectMapper.writeValueAsString(args[0]);
                if (paramJson.length() > 1000) {
                    paramJson = paramJson.substring(0, 1000) + "...(截断)";
                }
                logEntity.setRequestParam(paramJson);
            }
        } catch (Exception e) {
            logEntity.setRequestParam("序列化失败");
        }

        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            logEntity.setStatus(1);
            logEntity.setResponseCode(200);
        } catch (Throwable e) {
            logEntity.setStatus(0);
            logEntity.setResponseCode(500);
            log.error("操作执行异常：{}", e.getMessage());
            throw e;
        } finally {
            // 记录耗时并异步保存日志
            logEntity.setCostTime((int) (System.currentTimeMillis() - startTime));
            asyncLogService.saveOperationLog(logEntity);
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP"};
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
