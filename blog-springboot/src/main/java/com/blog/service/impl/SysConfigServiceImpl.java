package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.SysConfig;
import com.blog.mapper.SysConfigMapper;
import com.blog.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, String> getAll() {
        List<SysConfig> list = sysConfigMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (SysConfig config : list) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    @Override
    public String getValue(String key) {
        SysConfig config = sysConfigMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key)
        );
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("系统配置 {} 的值 {} 不是有效整数，使用默认值 {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolValue(String key) {
        String value = getValue(key);
        return "true".equalsIgnoreCase(value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            SysConfig config = sysConfigMapper.selectOne(
                    new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, entry.getKey())
            );
            if (config != null) {
                // 已存在：更新
                config.setConfigValue(entry.getValue());
                config.setUpdateTime(LocalDateTime.now());
                sysConfigMapper.updateById(config);
            } else {
                // 不存在：插入（前端新增的配置项）
                SysConfig newConfig = new SysConfig();
                newConfig.setConfigKey(entry.getKey());
                newConfig.setConfigValue(entry.getValue());
                newConfig.setCreateTime(LocalDateTime.now());
                newConfig.setUpdateTime(LocalDateTime.now());
                sysConfigMapper.insert(newConfig);
            }
            log.info("系统配置更新：{} = {}", entry.getKey(),
                    entry.getValue() != null && entry.getValue().length() > 100
                            ? entry.getValue().substring(0, 100) + "..." : entry.getValue());
        }
    }
}
