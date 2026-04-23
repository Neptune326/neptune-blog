package com.blog.property;

import com.blog.entity.SysConfig;
import com.blog.mapper.SysConfigMapper;
import com.blog.service.impl.SysConfigServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

class SysConfigServiceImplTest {

    @Test
    void getAll_shouldMaskAiApiKey() {
        SysConfigMapper sysConfigMapper = Mockito.mock(SysConfigMapper.class);
        SysConfigServiceImpl service = new SysConfigServiceImpl(sysConfigMapper);

        Mockito.when(sysConfigMapper.selectList(null)).thenReturn(List.of(config("ai_api_key", "sk-test123456")));

        Map<String, String> configs = service.getAll();

        Assertions.assertEquals("sk-******3456", configs.get("ai_api_key"));
    }

    @Test
    void updateBatch_maskedAiApiKey_shouldKeepExistingValue() {
        SysConfigMapper sysConfigMapper = Mockito.mock(SysConfigMapper.class);
        SysConfigServiceImpl service = new SysConfigServiceImpl(sysConfigMapper);

        service.updateBatch(Map.of("ai_api_key", "sk-******3456"));

        Mockito.verify(sysConfigMapper, Mockito.never()).updateById(Mockito.any(SysConfig.class));
        Mockito.verify(sysConfigMapper, Mockito.never()).insert(Mockito.any(SysConfig.class));
    }

    private SysConfig config(String key, String value) {
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        return config;
    }
}
