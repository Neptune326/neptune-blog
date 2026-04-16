package com.blog.service;

import com.blog.entity.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SysConfigService {

    /** 获取所有配置（返回 key-value Map，方便前端使用） */
    Map<String, String> getAll();

    /** 获取单个配置值 */
    String getValue(String key);

    /** 获取整数配置值（带默认值） */
    int getIntValue(String key, int defaultValue);

    /** 获取布尔配置值 */
    boolean getBoolValue(String key);

    /** 批量更新配置 */
    void updateBatch(Map<String, String> configs);
}
