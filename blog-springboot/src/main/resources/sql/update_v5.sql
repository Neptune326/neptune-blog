-- =====================================================
-- V5：修复实体类与数据库字段不一致问题
-- =====================================================

USE blog;

-- 1. admin 表补充 role 字段（实体类有，数据库表缺失）
ALTER TABLE admin ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色：super=超级管理员，admin=普通管理员' AFTER password;

-- 更新已有管理员为超级管理员
UPDATE admin SET role = 'super' WHERE username = 'admin';-- 2. 补充初始 mock 数据（文章、分类、标签）
-- 插入分类数据
INSERT IGNORE INTO category (id, name, description, create_time, create_by, update_time, update_by) VALUES
(1, '前端开发', 'Vue、React、CSS 等前端技术', NOW(), 'system', NOW(), 'system'),
(2, '后端开发', 'Java、Spring Boot、数据库', NOW(), 'system', NOW(), 'system'),
(3, '工具效率', '开发工具、效率提升', NOW(), 'system', NOW(), 'system');

-- 插入标签数据
INSERT IGNORE INTO tag (id, name, create_time, create_by, update_time, update_by) VALUES
(1, 'Vue3',        NOW(), 'system', NOW(), 'system'),
(2, 'Spring Boot', NOW(), 'system', NOW(), 'system'),
(3, 'MySQL',       NOW(), 'system', NOW(), 'system'),
(4, 'TypeScript',  NOW(), 'system', NOW(), 'system'),
(5, 'Docker',      NOW(), 'system', NOW(), 'system'),
(6, 'Git',         NOW(), 'system', NOW(), 'system'),
(7, 'Tailwind CSS',NOW(), 'system', NOW(), 'system'),
(8, 'MyBatis',     NOW(), 'system', NOW(), 'system');

-- 插入文章数据
INSERT IGNORE INTO article (id, title, content, summary, cover_url, category_id, status, is_top, view_count, view_count_real, like_count, deleted, create_time, create_by, update_time, update_by) VALUES
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

-- 插入文章标签关联
INSERT IGNORE INTO article_tag (article_id, tag_id) VALUES
(1, 1), (1, 7),
(2, 2), (2, 8),
(3, 3),
(4, 4),
(5, 5),
(6, 6);

-- 插入评论数据
INSERT IGNORE INTO comment (id, article_id, nickname, email, content, status, create_time, create_by, update_time, update_by) VALUES
(1, 1, '张三',     NULL, '写得很好，学到了很多！特别是路由守卫那部分，之前一直没搞清楚。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY),  'visitor', DATE_SUB(NOW(), INTERVAL 1 DAY),  'visitor'),
(2, 1, '前端小白', NULL, '请问 Vuetify 3 和 Tailwind CSS 一起用会不会有样式冲突？',       1, NOW(),                             'visitor', NOW(),                             'visitor'),
(3, 2, 'Java开发者',NULL,'Sa-Token 那部分讲得很清楚，之前用 Spring Security 配置太复杂了。',1, DATE_SUB(NOW(), INTERVAL 3 DAY),  'visitor', DATE_SUB(NOW(), INTERVAL 3 DAY),  'visitor'),
(4, 2, '后端新手', NULL, '多环境配置那块很实用，之前一直把数据库密码写死在配置文件里。',   1, DATE_SUB(NOW(), INTERVAL 2 DAY),  'visitor', DATE_SUB(NOW(), INTERVAL 2 DAY),  'visitor'),
(5, 3, 'DBA小李',  NULL, 'EXPLAIN 分析那部分非常实用，帮我优化了一个慢查询，从 3s 降到 50ms！', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 8 DAY), 'visitor'),
(6, 4, 'TS爱好者', NULL, '条件类型和 infer 那部分讲得很透彻，之前一直云里雾里的。',       1, DATE_SUB(NOW(), INTERVAL 12 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 12 DAY), 'visitor'),
(7, 5, '运维工程师',NULL, '多阶段构建那个技巧很好，我们的镜像从 1.2G 压缩到了 180M。',    1, DATE_SUB(NOW(), INTERVAL 18 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 18 DAY), 'visitor'),
(8, 6, '团队Leader',NULL, 'Conventional Commits 规范我们团队已经在用了，配合 commitlint 效果很好。', 1, DATE_SUB(NOW(), INTERVAL 22 DAY), 'visitor', DATE_SUB(NOW(), INTERVAL 22 DAY), 'visitor'),
-- 待审核评论
(100, 2, '李四', NULL, '这篇文章对我帮助很大！', 0, NOW(), 'visitor', NOW(), 'visitor'),
(101, 3, '王五', NULL, '期待更多 Git 相关内容', 0, NOW(), 'visitor', NOW(), 'visitor');

-- 插入留言数据
INSERT IGNORE INTO message (id, nickname, email, content, status, create_time, create_by) VALUES
(1, '路人甲',    NULL, '博客写得很好，继续加油！',              1, DATE_SUB(NOW(), INTERVAL 1 DAY),  'visitor'),
(2, '技术爱好者',NULL, '请问有没有关于 Kubernetes 的文章？',    0, DATE_SUB(NOW(), INTERVAL 2 DAY),  'visitor'),
(3, '前端开发者',NULL, 'Vue 3 那篇文章帮了我大忙，感谢！',      1, DATE_SUB(NOW(), INTERVAL 3 DAY),  'visitor');
