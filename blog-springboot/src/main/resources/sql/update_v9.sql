-- =====================================================
-- V9：修复 message 表缺少 update_time / update_by 字段
-- =====================================================

USE blog;

ALTER TABLE message ADD COLUMN update_time DATETIME COMMENT '更新时间' AFTER create_by;
ALTER TABLE message ADD COLUMN update_by   VARCHAR(50) COMMENT '更新人' AFTER update_time;
