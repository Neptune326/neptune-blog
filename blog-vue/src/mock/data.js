// Mock 数据 —— 后端未启动时使用
import { content1 } from './articles/article1.js'
import { content2 } from './articles/article2.js'
import { content3 } from './articles/article3.js'
import { content4 } from './articles/article4.js'
import { content5 } from './articles/article5.js'
import { content6 } from './articles/article6.js'

var now = new Date()

function dateStr(daysAgo) {
  var d = new Date(now.getTime() - daysAgo * 24 * 3600 * 1000)
  return d.toISOString().replace('T', ' ').substring(0, 19)
}

// 分类数据
export var categories = [
  { id: 1, name: '前端开发', description: 'Vue、React、CSS 等前端技术', articleCount: 3 },
  { id: 2, name: '后端开发', description: 'Java、Spring Boot、数据库', articleCount: 2 },
  { id: 3, name: '工具效率', description: '开发工具、效率提升', articleCount: 1 }
]

// 标签数据
export var tags = [
  { id: 1, name: 'Vue3', articleCount: 2 },
  { id: 2, name: 'Spring Boot', articleCount: 2 },
  { id: 3, name: 'MySQL', articleCount: 1 },
  { id: 4, name: 'TypeScript', articleCount: 1 },
  { id: 5, name: 'Docker', articleCount: 1 },
  { id: 6, name: 'Git', articleCount: 1 },
  { id: 7, name: 'Tailwind CSS', articleCount: 1 },
  { id: 8, name: 'MyBatis', articleCount: 1 }
]

// 文章列表（摘要，不含正文）
export var articleList = [
  {
    id: 1,
    title: '使用 Vue 3 + Vuetify 构建现代化博客系统',
    summary: '本文介绍如何使用 Vue 3 的 Composition API 结合 Vuetify 3 组件库，打造一个具有 Material Design 风格的个人博客系统，包括前台展示和后台管理两套界面的完整实现。',
    coverUrl: 'https://picsum.photos/seed/vue3blog/800/400',
    categoryId: 1, categoryName: '前端开发', status: 1, viewCount: 1280, commentCount: 2, isTop: 0,
    createTime: dateStr(2),
    tags: [{ id: 1, name: 'Vue3' }, { id: 7, name: 'Tailwind CSS' }]
  },
  {
    id: 2,
    title: 'Spring Boot 3.x + MyBatis-Plus 企业级最佳实践',
    summary: '深入探讨 Spring Boot 3.x 与 MyBatis-Plus 的整合方案，包括多环境配置、统一响应封装、全局异常处理、Sa-Token 鉴权等企业级开发规范，助你快速搭建生产级后端服务。',
    coverUrl: 'https://picsum.photos/seed/springboot/800/400',
    categoryId: 2, categoryName: '后端开发', status: 1, viewCount: 2340, commentCount: 2, isTop: 1,
    createTime: dateStr(5),
    tags: [{ id: 2, name: 'Spring Boot' }, { id: 8, name: 'MyBatis' }]
  },
  {
    id: 3,
    title: 'MySQL 索引优化与查询性能调优实战',
    summary: '从 B+ 树索引原理出发，讲解覆盖索引、联合索引、索引下推等优化技巧，结合 EXPLAIN 分析工具，系统性地提升 MySQL 查询性能，附真实慢查询优化案例。',
    coverUrl: 'https://picsum.photos/seed/mysqlopt/800/400',
    categoryId: 2, categoryName: '后端开发', status: 1, viewCount: 1560, commentCount: 1, isTop: 0,
    createTime: dateStr(10),
    tags: [{ id: 3, name: 'MySQL' }]
  },
  {
    id: 4,
    title: 'TypeScript 高级类型体操实战',
    summary: '深入 TypeScript 的类型系统，掌握条件类型、映射类型、模板字面量类型等高级特性，通过实际案例理解类型编程的精髓，让你的代码更加健壮。',
    coverUrl: 'https://picsum.photos/seed/typescript/800/400',
    categoryId: 1, categoryName: '前端开发', status: 1, viewCount: 943, commentCount: 1, isTop: 0,
    createTime: dateStr(15),
    tags: [{ id: 4, name: 'TypeScript' }]
  },
  {
    id: 5,
    title: 'Docker 容器化部署完整实践指南',
    summary: '从 Docker 基础概念到 Dockerfile 编写，再到 docker-compose 多服务编排，手把手带你完成一个完整的前后端分离项目容器化部署，包含 Nginx 反向代理配置。',
    coverUrl: 'https://picsum.photos/seed/dockerguide/800/400',
    categoryId: 3, categoryName: '工具效率', status: 1, viewCount: 1120, commentCount: 1, isTop: 0,
    createTime: dateStr(20),
    tags: [{ id: 5, name: 'Docker' }]
  },
  {
    id: 6,
    title: 'Git 工作流与团队协作规范',
    summary: '介绍 Git Flow、GitHub Flow 等常见工作流模型，以及 Conventional Commits 提交规范、代码审查流程，帮助团队建立高效的协作机制，减少合并冲突。',
    coverUrl: 'https://picsum.photos/seed/gitflow/800/400',
    categoryId: 3, categoryName: '工具效率', status: 1, viewCount: 876, commentCount: 1, isTop: 0,
    createTime: dateStr(25),
    tags: [{ id: 6, name: 'Git' }]
  }
]

// 文章详情（含完整正文）
export var articleDetails = {
  1: Object.assign({}, articleList[0], { content: content1, viewCount: 1281 }),
  2: Object.assign({}, articleList[1], { content: content2, viewCount: 2341 }),
  3: Object.assign({}, articleList[2], { content: content3, viewCount: 1561 }),
  4: Object.assign({}, articleList[3], { content: content4, viewCount: 944 }),
  5: Object.assign({}, articleList[4], { content: content5, viewCount: 1121 }),
  6: Object.assign({}, articleList[5], { content: content6, viewCount: 877 })
}

// 评论数据
export var comments = {
  1: [
    { id: 1, articleId: 1, nickname: '张三', content: '写得很好，学到了很多！特别是路由守卫那部分，之前一直没搞清楚。', status: 1, createTime: dateStr(1) },
    { id: 2, articleId: 1, nickname: '前端小白', content: '请问 Vuetify 3 和 Tailwind CSS 一起用会不会有样式冲突？', status: 1, createTime: dateStr(0) }
  ],
  2: [
    { id: 3, articleId: 2, nickname: 'Java开发者', content: 'Sa-Token 那部分讲得很清楚，之前用 Spring Security 配置太复杂了。', status: 1, createTime: dateStr(3) },
    { id: 4, articleId: 2, nickname: '后端新手', content: '多环境配置那块很实用，之前一直把数据库密码写死在配置文件里。', status: 1, createTime: dateStr(2) }
  ],
  3: [
    { id: 5, articleId: 3, nickname: 'DBA小李', content: 'EXPLAIN 分析那部分非常实用，帮我优化了一个慢查询，从 3s 降到 50ms！', status: 1, createTime: dateStr(8) }
  ],
  4: [
    { id: 6, articleId: 4, nickname: 'TS爱好者', content: '条件类型和 infer 那部分讲得很透彻，之前一直云里雾里的。', status: 1, createTime: dateStr(12) }
  ],
  5: [
    { id: 7, articleId: 5, nickname: '运维工程师', content: '多阶段构建那个技巧很好，我们的镜像从 1.2G 压缩到了 180M。', status: 1, createTime: dateStr(18) }
  ],
  6: [
    { id: 8, articleId: 6, nickname: '团队Leader', content: 'Conventional Commits 规范我们团队已经在用了，配合 commitlint 效果很好。', status: 1, createTime: dateStr(22) }
  ]
}

// 仪表盘统计
export var dashboard = {
  articleCount: 6, categoryCount: 3, tagCount: 8, commentCount: 8, pendingCommentCount: 2, messageCount: 3
}

// 归档数据
export var archive = articleList.map(function(a) {
  return { id: a.id, title: a.title, createTime: a.createTime }
})
