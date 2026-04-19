-- =====================================================
-- V10：sys_config.config_value 扩容为 TEXT，支持存储长内容（如画廊图片 JSON）
-- =====================================================

USE blog;

ALTER TABLE sys_config MODIFY COLUMN config_value TEXT NOT NULL COMMENT '配置值';
