package com.blog.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获并处理各类异常，保证所有接口返回格式一致
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 由 Service 层主动抛出，返回对应业务错误码
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getResultCode());
    }

    /**
     * 处理请求体参数校验异常（@Valid 触发）
     * 拼接所有字段错误信息，返回 code=400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理路径参数/请求参数校验异常（@Validated + @NotNull 等触发）
     * 拼接所有约束违反信息，返回 code=400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理 Sa-Token 未登录异常
     * Token 不存在、已过期或无效时触发，返回 code=401
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.warn("未登录访问: {}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED);
    }

    /**
     * 处理未捕获的系统异常
     * 兜底处理，返回 code=500 并记录完整堆栈
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
