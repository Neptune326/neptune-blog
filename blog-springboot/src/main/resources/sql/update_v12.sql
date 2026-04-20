-- =====================================================
-- V12：新增功能配置项
-- =====================================================

USE blog;

-- 新增系统配置项
INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('mouse_trail_enabled', 'false', '是否开启鼠标轨迹特效', NOW(), NOW()),
('music_playlist',      '[]',    '音乐播放列表（JSON数组，每项含name/artist/url）', NOW(), NOW()),
('comment_blacklist',   '[]',    '评论黑名单（JSON数组，含IP和关键词）', NOW(), NOW());
