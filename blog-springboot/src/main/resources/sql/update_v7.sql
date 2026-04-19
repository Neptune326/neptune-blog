-- =====================================================
-- V7：友情链接、文章批量操作支持
-- =====================================================

USE blog;

-- 友情链接表
CREATE TABLE IF NOT EXISTS friend_link (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(100) NOT NULL COMMENT '链接名称',
    url         VARCHAR(500) NOT NULL COMMENT '链接地址',
    description VARCHAR(200) COMMENT '链接描述',
    icon_url    VARCHAR(500) COMMENT '图标 URL',
    sort        INT NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- 插入示例友情链接
INSERT IGNORE INTO friend_link (id, name, url, description, icon_url, sort, create_time, update_time) VALUES
(1, 'Vue.js 官网',   'https://vuejs.org',                              'Vue 3 官方文档',    '', 1, NOW(), NOW()),
(2, 'Spring Boot',   'https://spring.io/projects/spring-boot',         'Spring Boot 官方',  '', 2, NOW(), NOW()),
(3, 'MDN Web Docs',  'https://developer.mozilla.org',                  'Web 技术权威文档',  '', 3, NOW(), NOW());

-- article 表补充 comment_count 字段（文章评论数冗余字段，避免每次 JOIN 统计）
ALTER TABLE article ADD COLUMN comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数（冗余字段）' AFTER like_count;
