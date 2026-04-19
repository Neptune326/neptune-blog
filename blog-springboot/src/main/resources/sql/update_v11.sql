-- =====================================================
-- V11：补充 live2d_enabled 配置项（如果不存在）
-- =====================================================

USE blog;

INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time)
VALUES ('live2d_enabled', 'true', '是否开启 Live2D 看板娘', NOW(), NOW());
