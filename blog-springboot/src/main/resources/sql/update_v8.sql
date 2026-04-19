-- =====================================================
-- V8：视觉特效配置项
-- =====================================================

USE blog;

INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('click_effect_enabled', 'true',   '是否开启鼠标点击特效（点击飘出 Emoji 粒子）', NOW(), NOW()),
('particle_enabled',     'true',   '是否开启粒子飘落特效',                         NOW(), NOW()),
('particle_type',        'sakura', '粒子类型：sakura=樱花，snow=雪花，star=星星',   NOW(), NOW()),
('particle_count',       '25',     '粒子数量',                                      NOW(), NOW());
