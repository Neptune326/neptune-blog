package com.neptune.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private Integer code;
    private String message;

    public ApiException(ApiException e) {
        super(e);
        this.code = e.getCode();
    }

    public ApiException(String message) {
        super();
        this.message = message;
    }

}
