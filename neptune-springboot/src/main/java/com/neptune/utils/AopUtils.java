package com.neptune.utils;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AopUtils {

    public static String getRequestParamJson(JoinPoint point) {
        List<Object> paramList = new ArrayList<>();
        Object[] args = point.getArgs();
        if (args.length > 0) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            String methodName = signature.getMethod().getName();
            paramList = Arrays.stream(point.getArgs())
                    .filter(arg -> Objects.nonNull(arg) && !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse))
                    .map(arg -> {
                        if (arg instanceof MultipartFile) {
                            return MapUtil.builder().put("file", "File").build();
                        }
                        return arg;
                    })
                    .collect(Collectors.toList());
        }
        return JSON.toJSONString(paramList);
    }
}
