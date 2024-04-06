package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionLogVo {

    private String id;
    private String createUser;
    private String createTime;
    private String optUri;
    private String optMethod;
    private String requestMethod;
    private String requestParam;
    private String exceptionInfo;
    private String ipAddress;
    private String ipSource;

}
