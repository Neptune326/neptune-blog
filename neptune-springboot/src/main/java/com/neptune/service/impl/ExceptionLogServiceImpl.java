package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.ExceptionLog;
import com.neptune.mapper.ExceptionLogMapper;
import com.neptune.service.ExceptionLogService;
import org.springframework.stereotype.Service;

/**
 * 异常日志表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog> implements ExceptionLogService {

}
