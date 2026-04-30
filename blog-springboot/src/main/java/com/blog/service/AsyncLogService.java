package com.blog.service;

import com.blog.entity.OperationLog;

public interface AsyncLogService {
    void saveOperationLog(OperationLog logEntity);
}
