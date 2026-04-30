package com.blog.service.impl;

import com.blog.entity.OperationLog;
import com.blog.mapper.OperationLogMapper;
import com.blog.service.AsyncLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncLogServiceImpl implements AsyncLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    @Async("blogTaskExecutor")
    public void saveOperationLog(OperationLog logEntity) {
        try {
            operationLogMapper.insert(logEntity);
            log.debug("操作日志记录：{} - {} - {}ms",
                    logEntity.getModule(), logEntity.getAction(), logEntity.getCostTime());
        } catch (Exception e) {
            log.warn("操作日志保存失败", e);
        }
    }
}
