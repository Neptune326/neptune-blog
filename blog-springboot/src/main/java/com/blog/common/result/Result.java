package com.blog.common.result;

import lombok.Data;

/**
 * 统一响应体
 * 所有接口返回值均包装为此格式，保证前端处理一致性
 *
 * @param <T> 业务数据类型
 */
@Data
public class Result<T> {

    /** 业务状态码 */
    private Integer code;

    /** 描述信息 */
    private String message;

    /** 业务数据 */
    private T data;

    /**
     * 成功响应（携带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    /**
     * 错误响应（使用错误码枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    /**
     * 错误响应（使用错误码枚举，并自定义描述信息）
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        return result;
    }
}
