package com.blog.common.exception;

import com.blog.common.result.ResultCode;

/**
 * 业务异常
 * 用于在 Service 层抛出可预期的业务错误，由全局异常处理器统一捕获并返回对应错误码
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final ResultCode resultCode;
    private final String customMessage;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.customMessage = null;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.customMessage = message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public String getCustomMessage() {
        return customMessage != null ? customMessage : resultCode.getMessage();
    }
}
