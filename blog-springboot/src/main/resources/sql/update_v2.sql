-- =====================================================
-- V2 版本增量 SQL：系统配置、操作日志、登录日志
-- =====================================================

USE blog;

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    config_key  VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    config_desc VARCHAR(200) COMMENT '配置说明',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    operator     VARCHAR(50)  COMMENT '操作人',
    module       VARCHAR(50)  COMMENT '操作模块（如：文章、分类、标签）',
    action       VARCHAR(100) COMMENT '操作描述',
    method       VARCHAR(10)  COMMENT 'HTTP 方法',
    request_url  VARCHAR(500) COMMENT '请求路径',
    request_ip   VARCHAR(50)  COMMENT '请求 IP',
    request_param TEXT        COMMENT '请求参数（JSON）',
    response_code INT         COMMENT '响应业务码',
    cost_time    INT          COMMENT '耗时（ms）',
    status       TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态：1=成功，0=失败',
    create_time  DATETIME COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS login_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username    VARCHAR(50)  COMMENT '登录用户名',
    login_ip    VARCHAR(50)  COMMENT '登录 IP',
    login_time  DATETIME     COMMENT '登录时间',
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态：1=成功，0=失败',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    user_agent  VARCHAR(500) COMMENT '浏览器 UA'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- 初始化系统配置默认值
INSERT IGNORE INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('live2d_enabled',        'true',  '是否开启 Live2D 看板娘',          NOW(), NOW()),
('login_max_fail_count',  '5',     '登录最大失败次数（超出后锁定）',    NOW(), NOW()),
('login_lock_duration',   '10',    '登录锁定时长（分钟）',              NOW(), NOW()),
('comment_audit_enabled', 'true',  '评论是否需要审核',                  NOW(), NOW()),
('blog_name',             '我的博客', '博客名称',                       NOW(), NOW()),
('blog_description',      '记录技术成长，分享开发心得', '博客描述',     NOW(), NOW()),
('blog_author',           'Admin', '博主名称',                          NOW(), NOW());
