package com.neptune.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonUtils {

    public static final long KILOBYTE = 1024;
    public static final long MEGABYTE = 1024 * 1024;

    public static Map<String, String> createSearch(HttpServletRequest request, String... params) {
        Map<String, String> map = new HashMap<>();
        if (null == request || null == params) {
            return map;
        }
        for (String param : params) {
            String parameter = request.getParameter(param);
            if (StrUtil.isNotEmpty(parameter)) {
                map.put(param, parameter);
            }
        }
        return map;
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.notNull(requestAttributes);
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        Assert.notNull(request);
        return request;
    }

    public static String convertFileSize(Long fileSize) {
        if (fileSize == null) {
            return StrUtil.EMPTY;
        }
        if (fileSize < KILOBYTE) {
            return fileSize + "B";
        } else if (fileSize < MEGABYTE) {
            return String.format("%.2fKB", fileSize / (double) KILOBYTE);
        } else {
            return String.format("%.2fMB", fileSize / (double) MEGABYTE);
        }
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String snowflakeId() {
        return new Snowflake(1, 1).nextIdStr();
    }


}
