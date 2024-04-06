package com.neptune.aspect;

import com.alibaba.fastjson2.JSON;
import com.neptune.annotation.BackLog;
import com.neptune.entity.OperationLog;
import com.neptune.service.OperationLogService;
import com.neptune.utils.AopUtils;
import com.neptune.utils.CommonUtils;
import com.neptune.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class BackLogAspect {

    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private IpUtils ipUtils;

    @Pointcut("@annotation(backLog)")
    public void pointCut(BackLog backLog) {
    }

    @Around(value = "pointCut(log)")
    public Object handle(ProceedingJoinPoint point, BackLog log) throws Throwable {
        String requestParam = null;
        if (log.param()) {
            requestParam = AopUtils.getRequestParamJson(point);
        }

        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;

        HttpServletRequest request = CommonUtils.getRequest();

        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();
        String ip = ipUtils.getRequestIp(request);

        OperationLog t = OperationLog.builder()
                .optModule(log.module())
                .optType(log.type())
                .optMethod(methodName)
                .optUri(request.getRequestURI())
                .requestMethod(request.getMethod())
                .ipAddress(ip)
                .ipSource(ipUtils.getLocation(ip))
                .handleTime(handleTime)
                .requestParam(requestParam)
                .build();
        if (log.result()) {
            t.setResponseData(JSON.toJSONString(result));
        }
        operationLogService.save(t);

        return result;
    }

}
