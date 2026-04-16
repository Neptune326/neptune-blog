-- V4：文章系列、访问统计、文章版本历史、定时发布、留言板、点赞收藏
USE blog;

-- 文章系列表
CREATE TABLE IF NOT EXISTS article_series (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(100) NOT NULL COMMENT '系列名称',
    description VARCHAR(500) COMMENT '系列描述',
    cover_url   VARCHAR(500) COMMENT '封面图',
    sort        INT NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章系列表';

-- 文章与系列关联（一篇文章可属于一个系列，有顺序）
ALTER TABLE article ADD COLUMN IF NOT EXISTS series_id BIGINT DEFAULT NULL COMMENT '所属系列 ID' AFTER category_id;
ALTER TABLE article ADD COLUMN IF NOT EXISTS series_sort INT NOT NULL DEFAULT 0 COMMENT '在系列中的顺序' AFTER series_id;

-- 访问统计表（按天记录 PV/UV）
CREATE TABLE IF NOT EXISTS visit_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    visit_date  DATE NOT NULL COMMENT '访问日期',
    pv          INT NOT NULL DEFAULT 0 COMMENT '页面浏览量',
    uv          INT NOT NULL DEFAULT 0 COMMENT '独立访客数',
    article_id  BIGINT DEFAULT NULL COMMENT '文章 ID（NULL 表示首页）',
    create_time DATETIME COMMENT '记录时间',
    UNIQUE KEY uk_date_article (visit_date, article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问统计表';

-- 文章版本历史表
CREATE TABLE IF NOT EXISTS article_history (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT NOT NULL COMMENT '文章 ID',
    title       VARCHAR(200) NOT NULL COMMENT '版本标题',
    content     LONGTEXT NOT NULL COMMENT '版本内容',
    version     INT NOT NULL DEFAULT 1 COMMENT '版本号',
    remark      VARCHAR(200) COMMENT '版本备注',
    create_time DATETIME COMMENT '保存时间',
    create_by   VARCHAR(50) COMMENT '操作人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章版本历史表';

-- 文章定时发布（在 article 表加字段）
ALTER TABLE article ADD COLUMN IF NOT EXISTS publish_time DATETIME DEFAULT NULL COMMENT '定时发布时间（NULL 表示立即发布）' AFTER status;

-- 留言板表
CREATE TABLE IF NOT EXISTS message (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    nickname    VARCHAR(50) NOT NULL COMMENT '留言者昵称',
    email       VARCHAR(100) COMMENT '邮箱（可选）',
    content     TEXT NOT NULL COMMENT '留言内容',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核，1=已通过，2=已拒绝',
    create_time DATETIME COMMENT '留言时间',
    create_by   VARCHAR(50) COMMENT '创建人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言板表';

-- 文章点赞表（防重复点赞，用 IP + 文章 ID 唯一）
CREATE TABLE IF NOT EXISTS article_like (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT NOT NULL COMMENT '文章 ID',
    like_ip     VARCHAR(50) NOT NULL COMMENT '点赞 IP',
    create_time DATETIME COMMENT '点赞时间',
    UNIQUE KEY uk_article_ip (article_id, like_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- article 表加点赞数字段
ALTER TABLE article ADD COLUMN IF NOT EXISTS like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数' AFTER view_count;

-- 系统配置补充
INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('message_enabled', 'true', '是否开启留言板', NOW(), NOW()),
('like_enabled', 'true', '是否开启文章点赞', NOW(), NOW()),
('visit_stats_enabled', 'true', '是否开启访问统计', NOW(), NOW());
