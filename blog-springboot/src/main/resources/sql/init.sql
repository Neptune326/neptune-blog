-- =====================================================
-- 个人博客系统数据库初始化脚本
-- 创建数据库及所有业务表，并插入初始管理员数据
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- =====================================================
-- 删除已存在的表（注意顺序：先删有外键依赖的子表）
-- =====================================================
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS article_tag;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS admin;

-- =====================================================
-- 创建管理员表
-- =====================================================
CREATE TABLE admin (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50)  COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50)  COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- =====================================================
-- 创建分类表
-- =====================================================
CREATE TABLE category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(50)  NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50)  COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50)  COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =====================================================
-- 创建标签表
-- =====================================================
CREATE TABLE tag (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =====================================================
-- 创建文章表
-- =====================================================
CREATE TABLE article (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    title       VARCHAR(200) NOT NULL COMMENT '文章标题',
    content     LONGTEXT     NOT NULL COMMENT '文章正文（Markdown）',
    summary     VARCHAR(500) COMMENT '文章摘要',
    cover_url   VARCHAR(500) COMMENT '封面图片 URL',
    category_id BIGINT       COMMENT '所属分类 ID',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '发布状态：0=草稿，1=已发布',
    view_count  INT          NOT NULL DEFAULT 0 COMMENT '阅读次数',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '软删除标志：0=正常，1=已删除',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50)  COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- =====================================================
-- 创建文章标签关联表
-- =====================================================
CREATE TABLE article_tag (
    article_id  BIGINT NOT NULL COMMENT '文章 ID',
    tag_id      BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)     REFERENCES tag(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- =====================================================
-- 创建评论表
-- =====================================================
CREATE TABLE comment (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT       NOT NULL COMMENT '所属文章 ID',
    nickname    VARCHAR(50)  NOT NULL COMMENT '评论者昵称',
    email       VARCHAR(100) COMMENT '评论者邮箱（可选）',
    content     TEXT         NOT NULL COMMENT '评论内容',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态：0=待审核，1=已通过，2=已拒绝',
    create_time DATETIME COMMENT '评论时间',
    create_by   VARCHAR(50)  COMMENT '创建人（访客填 visitor）',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50)  COMMENT '更新人',
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 插入初始管理员数据（任务 2.2）
-- 用户名：admin
-- 密码明文：admin123（已使用 BCrypt 加密）
-- =====================================================
INSERT INTO admin (username, password, create_time, create_by, update_time, update_by)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK',
    NOW(),
    'system',
    NOW(),
    'system'
);
