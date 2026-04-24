-- =====================================================
-- 个人博客系统初始化脚本
-- 合并原 init.sql 与 update_v2.sql ~ update_v12.sql 的最终结构和初始数据
--
-- 维护约定：
-- 1. 新增表字段、修改表字段时，直接合并到对应 CREATE TABLE 中。
-- 2. 新增表时，按业务模块放到建表区域中。
-- 3. 后续如果需要追加建表、字段变更、数据更新等 SQL，请追加到文件末尾的“后续变更追加区”，并备注说明和日期。
-- =====================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS article_tag;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS article_like;
DROP TABLE IF EXISTS article_history;
DROP TABLE IF EXISTS visit_log;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS friend_link;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS article_series;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS admin;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 基础业务表
-- =====================================================

CREATE TABLE admin (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    role        VARCHAR(20)  NOT NULL DEFAULT 'admin' COMMENT '角色：super=超级管理员，admin=普通管理员',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

CREATE TABLE category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(50)  NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

CREATE TABLE tag (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME COMMENT '创建时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

CREATE TABLE article_series (
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

CREATE TABLE article (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    title           VARCHAR(200) NOT NULL COMMENT '文章标题',
    content         LONGTEXT NOT NULL COMMENT '文章正文（Markdown）',
    summary         VARCHAR(500) COMMENT '文章摘要',
    cover_url       VARCHAR(500) COMMENT '封面图片 URL',
    category_id     BIGINT COMMENT '所属分类 ID',
    series_id       BIGINT DEFAULT NULL COMMENT '所属系列 ID',
    series_sort     INT NOT NULL DEFAULT 0 COMMENT '在系列中的顺序',
    status          TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态：0=草稿，1=已发布',
    publish_time    DATETIME DEFAULT NULL COMMENT '定时发布时间（NULL 表示立即发布）',
    is_top          TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否，1=是',
    view_count      INT NOT NULL DEFAULT 0 COMMENT '阅读次数',
    like_count      INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    comment_count   INT NOT NULL DEFAULT 0 COMMENT '评论数（冗余字段）',
    view_count_real INT NOT NULL DEFAULT 0 COMMENT '真实阅读次数（防刷）',
    deleted         TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标志：0=正常，1=已删除',
    create_time     DATETIME COMMENT '创建时间',
    create_by       VARCHAR(50) COMMENT '创建人',
    update_time     DATETIME COMMENT '更新时间',
    update_by       VARCHAR(50) COMMENT '更新人',
    CONSTRAINT fk_article_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

CREATE TABLE article_tag (
    article_id BIGINT NOT NULL COMMENT '文章 ID',
    tag_id     BIGINT NOT NULL COMMENT '标签 ID',
    PRIMARY KEY (article_id, tag_id),
    CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    CONSTRAINT fk_article_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

CREATE TABLE comment (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT NOT NULL COMMENT '所属文章 ID',
    parent_id   BIGINT DEFAULT NULL COMMENT '父评论 ID（NULL 表示顶级评论）',
    reply_to    VARCHAR(50) DEFAULT NULL COMMENT '回复的用户昵称',
    nickname    VARCHAR(50) NOT NULL COMMENT '评论者昵称',
    email       VARCHAR(100) COMMENT '评论者邮箱（可选）',
    content     TEXT NOT NULL COMMENT '评论内容',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0=待审核，1=已通过，2=已拒绝',
    create_time DATETIME COMMENT '评论时间',
    create_by   VARCHAR(50) COMMENT '创建人（访客填 visitor）',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人',
    CONSTRAINT fk_comment_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =====================================================
-- 系统、日志与扩展功能表
-- =====================================================

CREATE TABLE sys_config (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    config_key   VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT NOT NULL COMMENT '配置值',
    config_desc  VARCHAR(200) COMMENT '配置说明',
    create_time  DATETIME COMMENT '创建时间',
    update_time  DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

CREATE TABLE operation_log (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    operator       VARCHAR(50) COMMENT '操作人',
    module         VARCHAR(50) COMMENT '操作模块（如：文章、分类、标签）',
    action         VARCHAR(100) COMMENT '操作描述',
    method         VARCHAR(10) COMMENT 'HTTP 方法',
    request_url    VARCHAR(500) COMMENT '请求路径',
    request_ip     VARCHAR(50) COMMENT '请求 IP',
    request_param  TEXT COMMENT '请求参数（JSON）',
    response_code  INT COMMENT '响应业务码',
    cost_time      INT COMMENT '耗时（ms）',
    status         TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态：1=成功，0=失败',
    create_time    DATETIME COMMENT '操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

CREATE TABLE login_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    username    VARCHAR(50) COMMENT '登录用户名',
    login_ip    VARCHAR(50) COMMENT '登录 IP',
    login_time  DATETIME COMMENT '登录时间',
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态：1=成功，0=失败',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    user_agent  VARCHAR(500) COMMENT '浏览器 UA'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

CREATE TABLE visit_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    visit_date  DATE NOT NULL COMMENT '访问日期',
    pv          INT NOT NULL DEFAULT 0 COMMENT '页面浏览量',
    uv          INT NOT NULL DEFAULT 0 COMMENT '独立访客数',
    article_id  BIGINT DEFAULT NULL COMMENT '文章 ID（NULL 表示首页）',
    create_time DATETIME COMMENT '记录时间',
    UNIQUE KEY uk_date_article (visit_date, article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问统计表';

CREATE TABLE article_history (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT NOT NULL COMMENT '文章 ID',
    title       VARCHAR(200) NOT NULL COMMENT '版本标题',
    content     LONGTEXT NOT NULL COMMENT '版本内容',
    version     INT NOT NULL DEFAULT 1 COMMENT '版本号',
    remark      VARCHAR(200) COMMENT '版本备注',
    create_time DATETIME COMMENT '保存时间',
    create_by   VARCHAR(50) COMMENT '操作人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章版本历史表';

CREATE TABLE message (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    nickname    VARCHAR(50) NOT NULL COMMENT '留言者昵称',
    email       VARCHAR(100) COMMENT '邮箱（可选）',
    content     TEXT NOT NULL COMMENT '留言内容',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核，1=已通过，2=已拒绝',
    create_time DATETIME COMMENT '留言时间',
    create_by   VARCHAR(50) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by   VARCHAR(50) COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言板表';

CREATE TABLE article_like (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    article_id  BIGINT NOT NULL COMMENT '文章 ID',
    like_ip     VARCHAR(50) NOT NULL COMMENT '点赞 IP',
    create_time DATETIME COMMENT '点赞时间',
    UNIQUE KEY uk_article_ip (article_id, like_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

CREATE TABLE friend_link (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    name        VARCHAR(100) NOT NULL COMMENT '链接名称',
    url         VARCHAR(500) NOT NULL COMMENT '链接地址',
    description VARCHAR(200) COMMENT '链接描述',
    icon_url    VARCHAR(500) COMMENT '图标 URL',
    sort        INT NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- =====================================================
-- 初始数据
-- =====================================================

-- 初始管理员账号：admin
-- 初始管理员密码：admin123
-- BCrypt 哈希：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK
INSERT INTO admin (username, password, role, create_time, create_by, update_time, update_by)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK',
    'super',
    NOW(),
    'system',
    NOW(),
    'system'
)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    role = VALUES(role),
    update_time = VALUES(update_time),
    update_by = VALUES(update_by);

INSERT INTO category (id, name, description, create_time, create_by, update_time, update_by) VALUES
(1, '前端开发', 'Vue、React、CSS 等前端技术', NOW(), 'system', NOW(), 'system'),
(2, '后端开发', 'Java、Spring Boot、数据库', NOW(), 'system', NOW(), 'system'),
(3, '工具效率', '开发工具、效率提升', NOW(), 'system', NOW(), 'system');

INSERT INTO tag (id, name, create_time, create_by, update_time, update_by) VALUES
(1, 'Vue3', NOW(), 'system', NOW(), 'system'),
(2, 'Spring Boot', NOW(), 'system', NOW(), 'system'),
(3, 'MySQL', NOW(), 'system', NOW(), 'system'),
(4, 'TypeScript', NOW(), 'system', NOW(), 'system'),
(5, 'Docker', NOW(), 'system', NOW(), 'system'),
(6, 'Git', NOW(), 'system', NOW(), 'system'),
(7, 'Tailwind CSS', NOW(), 'system', NOW(), 'system'),
(8, 'MyBatis', NOW(), 'system', NOW(), 'system');

INSERT INTO article (id, title, content, summary, cover_url, category_id, status, is_top, view_count, view_count_real, like_count, deleted, create_time, create_by, update_time, update_by) VALUES
(1, '使用 Vue 3 + Vuetify 构建现代化博客系统',
 '# 使用 Vue 3 + Vuetify 构建现代化博客系统\n\n本文介绍如何使用 Vue 3 的 Composition API 结合 Vuetify 3 组件库，打造一个具有 Material Design 风格的个人博客系统。\n\n## 技术选型\n\n- **Vue 3**：使用 Composition API 编写更清晰的逻辑\n- **Vuetify 3**：Material Design 组件库\n- **Pinia**：状态管理\n- **Vue Router 4**：路由管理\n\n## 项目结构\n\n```\nsrc/\n├── api/        # 接口封装\n├── components/ # 公共组件\n├── router/     # 路由配置\n├── store/      # 状态管理\n└── views/      # 页面视图\n```\n\n## 路由守卫\n\n后台路由通过 `meta.requiresAuth` 标记，在 `beforeEach` 中统一拦截未登录访问。',
 '本文介绍如何使用 Vue 3 的 Composition API 结合 Vuetify 3 组件库，打造一个具有 Material Design 风格的个人博客系统，包括前台展示和后台管理两套界面的完整实现。',
 'https://picsum.photos/seed/vue3blog/800/400',
 1, 1, 0, 1280, 1280, 42, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 2 DAY), 'admin'),

(2, 'Spring Boot 3.x + MyBatis-Plus 企业级最佳实践',
 '# Spring Boot 3.x + MyBatis-Plus 企业级最佳实践\n\n深入探讨 Spring Boot 3.x 与 MyBatis-Plus 的整合方案。\n\n## 多环境配置\n\n```yaml\nspring:\n  profiles:\n    active: dev\n```\n\n## 统一响应封装\n\n```java\n@Data\npublic class Result<T> {\n    private Integer code;\n    private String message;\n    private T data;\n}\n```\n\n## Sa-Token 鉴权\n\nSa-Token 是一个轻量级 Java 权限认证框架，相比 Spring Security 配置更简单。',
 '深入探讨 Spring Boot 3.x 与 MyBatis-Plus 的整合方案，包括多环境配置、统一响应封装、全局异常处理、Sa-Token 鉴权等企业级开发规范。',
 'https://picsum.photos/seed/springboot/800/400',
 2, 1, 0, 2340, 2340, 88, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 5 DAY), 'admin'),

(3, 'MySQL 索引优化与查询性能调优实战',
 '# MySQL 索引优化与查询性能调优实战\n\n从 B+ 树索引原理出发，讲解覆盖索引、联合索引、索引下推等优化技巧。\n\n## EXPLAIN 分析\n\n```sql\nEXPLAIN SELECT * FROM article WHERE category_id = 1;\n```\n\n关注 `type`、`key`、`rows` 三个关键字段。\n\n## 联合索引最左前缀原则\n\n联合索引 `(a, b, c)` 可以命中 `a`、`a,b`、`a,b,c` 的查询，但不能命中 `b` 或 `c` 单独的查询。',
 '从 B+ 树索引原理出发，讲解覆盖索引、联合索引、索引下推等优化技巧，结合 EXPLAIN 分析工具，系统性地提升 MySQL 查询性能。',
 'https://picsum.photos/seed/mysqlopt/800/400',
 2, 1, 0, 1560, 1560, 35, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY), 'admin'),

(4, 'TypeScript 高级类型体操实战',
 '# TypeScript 高级类型体操实战\n\n深入 TypeScript 的类型系统，掌握条件类型、映射类型、模板字面量类型等高级特性。\n\n## 条件类型\n\n```typescript\ntype IsString<T> = T extends string ? true : false\n```\n\n## infer 关键字\n\n```typescript\ntype ReturnType<T> = T extends (...args: any[]) => infer R ? R : never\n```\n\n## 映射类型\n\n```typescript\ntype Readonly<T> = { readonly [K in keyof T]: T[K] }\n```',
 '深入 TypeScript 的类型系统，掌握条件类型、映射类型、模板字面量类型等高级特性，通过实际案例理解类型编程的精髓。',
 'https://picsum.photos/seed/typescript/800/400',
 1, 1, 0, 943, 943, 21, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 15 DAY), 'admin'),

(5, 'Docker 容器化部署完整实践指南',
 '# Docker 容器化部署完整实践指南\n\n从 Docker 基础概念到 Dockerfile 编写，再到 docker-compose 多服务编排。\n\n## Dockerfile 最佳实践\n\n```dockerfile\n# 多阶段构建\nFROM node:18-alpine AS builder\nWORKDIR /app\nCOPY package*.json ./\nRUN npm ci\nCOPY . .\nRUN npm run build\n\nFROM nginx:alpine\nCOPY --from=builder /app/dist /usr/share/nginx/html\n```\n\n## docker-compose 编排\n\n```yaml\nservices:\n  app:\n    build: .\n    ports:\n      - "8080:8080"\n  db:\n    image: mysql:8.0\n    environment:\n      MYSQL_ROOT_PASSWORD: root\n```',
 '从 Docker 基础概念到 Dockerfile 编写，再到 docker-compose 多服务编排，手把手带你完成一个完整的前后端分离项目容器化部署。',
 'https://picsum.photos/seed/dockerguide/800/400',
 3, 1, 0, 1120, 1120, 56, 0, DATE_SUB(NOW(), INTERVAL 20 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 20 DAY), 'admin'),

(6, 'Git 工作流与团队协作规范',
 '# Git 工作流与团队协作规范\n\n介绍 Git Flow、GitHub Flow 等常见工作流模型，以及 Conventional Commits 提交规范。\n\n## Git Flow\n\n- `main`：生产分支\n- `develop`：开发分支\n- `feature/*`：功能分支\n- `hotfix/*`：紧急修复分支\n\n## Conventional Commits\n\n```\nfeat: 新增用户登录功能\nfix: 修复评论提交失败的问题\ndocs: 更新 README\nrefactor: 重构文章列表查询逻辑\n```\n\n## commitlint 配置\n\n```js\nmodule.exports = { extends: ["@commitlint/config-conventional"] }\n```',
 '介绍 Git Flow、GitHub Flow 等常见工作流模型，以及 Conventional Commits 提交规范、代码审查流程，帮助团队建立高效的协作机制。',
 'https://picsum.photos/seed/gitflow/800/400',
 3, 1, 0, 876, 876, 18, 0, DATE_SUB(NOW(), INTERVAL 25 DAY), 'admin', DATE_SUB(NOW(), INTERVAL 25 DAY), 'admin');

INSERT INTO article_tag (article_id, tag_id) VALUES
(1, 1), (1, 7),
(2, 2), (2, 8),
(3, 3),
(4, 4),
(5, 5),
(6, 6);

INSERT INTO comment (id, article_id, nickname, email, content, status, create_time, create_by, update_time, update_by) VALUES
(1, 1, '张三', NULL, '写得很好，学到了很多！特别是路由守卫那部分，之前一直没搞清楚。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 1 DAY), 'visitor'),
(2, 1, '前端小白', NULL, '请问 Vuetify 3 和 Tailwind CSS 一起用会不会有样式冲突？', 1, NOW(), 'visitor', NOW(), 'visitor'),
(3, 2, 'Java开发者', NULL, 'Sa-Token 那部分讲得很清楚，之前用 Spring Security 配置太复杂了。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 3 DAY), 'visitor'),
(4, 2, '后端新手', NULL, '多环境配置那块很实用，之前一直把数据库密码写死在配置文件里。', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 2 DAY), 'visitor'),
(5, 3, 'DBA小李', NULL, 'EXPLAIN 分析那部分非常实用，帮我优化了一个慢查询，从 3s 降到 50ms！', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 8 DAY), 'visitor'),
(6, 4, 'TS爱好者', NULL, '条件类型和 infer 那部分讲得很透彻，之前一直云里雾里的。', 1, DATE_SUB(NOW(), INTERVAL 12 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 12 DAY), 'visitor'),
(7, 5, '运维工程师', NULL, '多阶段构建那个技巧很好，我们的镜像从 1.2G 压缩到了 180M。', 1, DATE_SUB(NOW(), INTERVAL 18 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 18 DAY), 'visitor'),
(8, 6, '团队Leader', NULL, 'Conventional Commits 规范我们团队已经在用了，配合 commitlint 效果很好。', 1, DATE_SUB(NOW(), INTERVAL 22 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 22 DAY), 'visitor'),
(100, 2, '李四', NULL, '这篇文章对我帮助很大！', 0, NOW(), 'visitor', NOW(), 'visitor'),
(101, 3, '王五', NULL, '期待更多 Git 相关内容', 0, NOW(), 'visitor', NOW(), 'visitor');

INSERT INTO message (id, nickname, email, content, status, create_time, create_by) VALUES
(1, '路人甲', NULL, '博客写得很好，继续加油！', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'visitor'),
(2, '技术爱好者', NULL, '请问有没有关于 Kubernetes 的文章？', 0, DATE_SUB(NOW(), INTERVAL 2 DAY), 'visitor'),
(3, '前端开发者', NULL, 'Vue 3 那篇文章帮了我大忙，感谢！', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 'visitor');

INSERT INTO friend_link (id, name, url, description, icon_url, sort, create_time, update_time) VALUES
(1, 'Vue.js 官网', 'https://vuejs.org', 'Vue 3 官方文档', '', 1, NOW(), NOW()),
(2, 'Spring Boot', 'https://spring.io/projects/spring-boot', 'Spring Boot 官方', '', 2, NOW(), NOW()),
(3, 'MDN Web Docs', 'https://developer.mozilla.org', 'Web 技术权威文档', '', 3, NOW(), NOW());

INSERT INTO sys_config (config_key, config_value, config_desc, create_time, update_time) VALUES
('live2d_enabled', 'true', '是否开启 Live2D 看板娘', NOW(), NOW()),
('login_max_fail_count', '5', '登录最大失败次数（超出后锁定）', NOW(), NOW()),
('login_lock_duration', '10', '登录锁定时长（分钟）', NOW(), NOW()),
('comment_audit_enabled', 'true', '评论是否需要审核', NOW(), NOW()),
('blog_name', '我的博客', '博客名称', NOW(), NOW()),
('blog_description', '记录技术成长，分享开发心得', '博客描述', NOW(), NOW()),
('blog_author', 'Admin', '博主名称', NOW(), NOW()),
('about_content', '# 关于我\n\n你好，我是博客的作者。\n\n这里是我的个人博客，记录技术成长，分享开发心得。\n\n## 技术栈\n\n- 后端：Java、Spring Boot、MySQL\n- 前端：Vue 3、Vuetify\n\n## 联系方式\n\n- GitHub：https://github.com/your-username\n- Email：your-email@example.com', '关于我页面内容（Markdown）', NOW(), NOW()),
('about_enabled', 'true', '是否显示关于我页面', NOW(), NOW()),
('sitemap_enabled', 'true', '是否开启 Sitemap', NOW(), NOW()),
('message_enabled', 'true', '是否开启留言板', NOW(), NOW()),
('like_enabled', 'true', '是否开启文章点赞', NOW(), NOW()),
('visit_stats_enabled', 'true', '是否开启访问统计', NOW(), NOW()),
('anime_theme_enabled', 'false', '是否开启动漫主题首页（开启后首页切换为暗色动漫风格，背景图从画廊轮播）', NOW(), NOW()),
('gallery_images', '[]', '首页背景画廊图片列表（JSON 数组，存储图片 URL）', NOW(), NOW()),
('click_effect_enabled', 'true', '是否开启鼠标点击特效（点击飘出 Emoji 粒子）', NOW(), NOW()),
('particle_enabled', 'true', '是否开启粒子飘落特效', NOW(), NOW()),
('particle_type', 'sakura', '粒子类型：sakura=樱花，snow=雪花，star=星星', NOW(), NOW()),
('particle_count', '25', '粒子数量', NOW(), NOW()),
('mouse_trail_enabled', 'false', '是否开启鼠标轨迹特效', NOW(), NOW()),
('music_playlist', '[]', '音乐播放列表（JSON数组；直链用 url，平台曲用 server+id）', NOW(), NOW()),
('comment_blacklist', '[]', '评论黑名单（JSON数组，含IP和关键词）', NOW(), NOW()),
('ai_enabled', 'false', 'Enable AI writing assistant', NOW(), NOW()),
('ai_base_url', 'https://api.openai.com', 'OpenAI compatible base URL', NOW(), NOW()),
('ai_model', '', 'AI model name', NOW(), NOW()),
('ai_api_key', '', 'AI API key', NOW(), NOW()),
('ai_temperature', '0.7', 'AI generation temperature', NOW(), NOW()),
('ai_max_tokens', '1200', 'AI max output tokens', NOW(), NOW()),
('frontend_theme_enabled', 'true', 'Enable frontend immersive themes', NOW(), NOW()),
('frontend_theme_switcher_enabled', 'true', 'Show frontend theme switcher', NOW(), NOW()),
('frontend_theme_default', 'sakura', 'Default frontend theme', NOW(), NOW()),
('frontend_ambient_enabled', 'true', 'Enable frontend ambient layer', NOW(), NOW()),
-- 2026-04-24：音乐播放与 Meting（直链 / 各平台 server+id 解析，对应前台音乐播放器与系统设置 meting_api_base）
('meting_api_base', 'https://api.injahow.cn/meting/', 'Meting 兼容 API 根地址（解析网易云/QQ音乐等，可自建）', NOW(), NOW()),
-- 2026-04-24：可配置趣味功能（Konami 彩蛋、侧栏开发者今日小签，对应 easter_konami_enabled、dev_fortune_enabled）
('easter_konami_enabled', 'true', '是否开启 Konami 键盘彩蛋（上上下下左右左右BA）', NOW(), NOW()),
('dev_fortune_enabled', 'true', '是否显示侧栏开发者今日小签', NOW(), NOW());

-- =====================================================
-- 后续变更追加区
-- =====================================================
-- 2026-04-22：合并原 init.sql 与 update_v2.sql ~ update_v12.sql。
-- 已将新增字段、字段修改、建表和初始数据统一折叠到上方最终初始化结构中。
-- 2026-04-22：合并 reset_admin_password.sql。
-- 已将 admin 账号初始密码 admin123 的 BCrypt 哈希和重置语义合并到 admin 初始化数据中。
-- 2026-04-24：sys_config 音乐与 Meting 相关行（见上方「INSERT INTO sys_config」中 2026-04-24 行注释）。
-- 2026-04-24：easter_konami_enabled、dev_fortune_enabled（见本文件 INSERT 中 2026-04-24 趣味功能行注释）。
-- 后续新增 SQL 请按以下格式追加：
--
-- yyyy-MM-dd：变更说明
-- SQL 语句...
