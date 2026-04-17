-- V3：关于我、文章置顶、评论回复、Sitemap 相关
USE blog;

-- 关于我内容存入系统配置
INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('about_content', '# 关于我\n\n你好，我是博客的作者。\n\n这里是我的个人博客，记录技术成长，分享开发心得。\n\n## 技术栈\n\n- 后端：Java、Spring Boot、MySQL\n- 前端：Vue 3、Vuetify\n\n## 联系方式\n\n- GitHub：https://github.com/your-username\n- Email：your-email@example.com', '关于我页面内容（Markdown）', NOW(), NOW()),
('about_enabled', 'true', '是否显示关于我页面', NOW(), NOW()),
('sitemap_enabled', 'true', '是否开启 Sitemap', NOW(), NOW());

-- 文章表增加置顶字段
ALTER TABLE article ADD COLUMN is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是' AFTER status;

-- 评论表增加父评论 ID（支持回复）
ALTER TABLE article ADD COLUMN view_count_real INT NOT NULL DEFAULT 0 COMMENT '真实阅读次数（防刷）' AFTER view_count;
ALTER TABLE comment ADD COLUMN parent_id BIGINT DEFAULT NULL COMMENT '父评论 ID（NULL 表示顶级评论）' AFTER article_id;
ALTER TABLE comment ADD COLUMN reply_to VARCHAR(50) DEFAULT NULL COMMENT '回复的用户昵称' AFTER parent_id;
