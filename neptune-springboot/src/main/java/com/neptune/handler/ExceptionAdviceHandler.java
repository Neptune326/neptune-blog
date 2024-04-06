package com.neptune.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.neptune.entity.ResponseResult;
import com.neptune.enums.HttpStatusEnum;
import com.neptune.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ExceptionAdviceHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseResult<?> errorHandler(ApiException e) {
        log.error("ApiException=>{}", e.getMessage());
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler(value = NotPermissionException.class)
    public ResponseResult<?> errorHandler(NotPermissionException e) {
        log.error("NotPermissionException=>{}", e.getMessage());
        return ResponseResult.fail(HttpStatusEnum.FORBIDDEN);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseResult<?> errorHandler(IllegalArgumentException e) {
        log.error("IllegalArgumentException=>{}", e.getMessage());
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler(value = NotLoginException.class)
    public ResponseResult<?> errorHandler(NotLoginException e) {
        log.error("NotLoginException=>{}", e.getMessage());
        return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseResult<?> errorHandler(BindException e) {
        log.error("BindException=>{}", e.getMessage());
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST);
        }
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<?> errorHandler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException=>{}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder("校验失败:{");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(", ");
        }
        String msg = sb.append("}").toString();
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseResult<?> errorHandler(ConstraintViolationException e) {
        log.error("ConstraintViolationException=>{}", e.getMessage());
        return ResponseResult.fail(HttpStatusEnum.BAD_REQUEST.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseResult<?> errorHandler(Exception e) {
        log.error("Exception=>{}", e.getMessage());
        return ResponseResult.fail(HttpStatusEnum.ERROR.getCode(), e.getMessage());
    }

}
