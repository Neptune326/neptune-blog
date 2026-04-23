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
import java.util.Set;

/**
 * System config service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private static final Set<String> SENSITIVE_KEYS = Set.of("ai_api_key");
    private static final String MASK_TEXT = "******";

    private final SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, String> getAll() {
        List<SysConfig> list = sysConfigMapper.selectList(null);
        Map<String, String> result = new HashMap<>();
        for (SysConfig config : list) {
            result.put(config.getConfigKey(), maskValue(config.getConfigKey(), config.getConfigValue()));
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
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("[System config] invalid integer config, key: {}, value: {}, default: {}",
                    key, logValue(key, value), defaultValue);
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
            if (isSensitiveMaskedValue(entry.getKey(), entry.getValue())) {
                log.info("[System config] keep existing sensitive config: {}", entry.getKey());
                continue;
            }

            SysConfig config = sysConfigMapper.selectOne(
                    new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, entry.getKey())
            );
            if (config != null) {
                config.setConfigValue(entry.getValue());
                config.setUpdateTime(LocalDateTime.now());
                sysConfigMapper.updateById(config);
            } else {
                SysConfig newConfig = new SysConfig();
                newConfig.setConfigKey(entry.getKey());
                newConfig.setConfigValue(entry.getValue());
                newConfig.setCreateTime(LocalDateTime.now());
                newConfig.setUpdateTime(LocalDateTime.now());
                sysConfigMapper.insert(newConfig);
            }
            log.info("[System config] updated: {} = {}", entry.getKey(), logValue(entry.getKey(), entry.getValue()));
        }
    }

    private String maskValue(String key, String value) {
        if (!SENSITIVE_KEYS.contains(key) || value == null || value.isBlank()) {
            return value;
        }
        if (value.length() <= 8) {
            return MASK_TEXT;
        }
        return value.substring(0, Math.min(3, value.length())) + MASK_TEXT + value.substring(value.length() - 4);
    }

    private boolean isSensitiveMaskedValue(String key, String value) {
        return SENSITIVE_KEYS.contains(key) && value != null && value.contains(MASK_TEXT);
    }

    private String logValue(String key, String value) {
        if (SENSITIVE_KEYS.contains(key)) {
            return MASK_TEXT;
        }
        if (value != null && value.length() > 100) {
            return value.substring(0, 100) + "...";
        }
        return value;
    }
}
