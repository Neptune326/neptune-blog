package com.blog.common.exception;

import com.blog.common.result.ResultCode;

/**
 * 业务异常
 * 用于在 Service 层抛出可预期的业务错误，由全局异常处理器统一捕获并返回对应错误码
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
