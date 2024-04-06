package com.neptune.exception;

public class RedisLimitException extends RuntimeException {
    public RedisLimitException(String msg) {
        super(msg);
    }
}
