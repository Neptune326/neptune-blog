package com.neptune.aspect;

import com.neptune.entity.ExceptionLog;
import com.neptune.service.ExceptionLogService;
import com.neptune.utils.AopUtils;
import com.neptune.utils.CommonUtils;
import com.neptune.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Aspect
@Component
public class ExceptionLogAspect {

    @Autowired
    private ExceptionLogService exceptionLogService;
    @Autowired
    private IpUtils ipUtils;

    @Pointcut("execution(* com.neptune.controller..*.*(..))")
    public void exceptionLogPointcut() {
    }

    @AfterThrowing(pointcut = "exceptionLogPointcut()", throwing = "e")
    public void handlerException(JoinPoint point, Throwable e) throws Throwable {
        HttpServletRequest request = CommonUtils.getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();

        String ip = ipUtils.getRequestIp(request);
        ExceptionLog log = ExceptionLog.builder()
                .optMethod(methodName)
                .optUri(request.getRequestURI())
                .requestMethod(request.getMethod())
                .ipAddress(ip)
                .ipSource(ipUtils.getLocation(ip))
                .exceptionInfo(getTrace(e))
                .build();

        log.setRequestParam(AopUtils.getRequestParamJson(point));

        exceptionLogService.save(log);

    }

    public String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }

}
