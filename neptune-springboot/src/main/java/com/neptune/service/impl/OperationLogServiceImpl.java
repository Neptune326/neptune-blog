package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.OperationLog;
import com.neptune.mapper.OperationLogMapper;
import com.neptune.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

}
