-- =====================================================
-- V6：动漫主题首页 + 画廊配置
-- =====================================================

USE blog;

-- 新增动漫主题和画廊相关系统配置
INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('anime_theme_enabled', 'false', '是否开启动漫主题首页（开启后首页切换为暗色动漫风格，背景图从画廊轮播）', NOW(), NOW()),
('gallery_images',      '[]',    '首页背景画廊图片列表（JSON 数组，存储图片 URL）', NOW(), NOW());
