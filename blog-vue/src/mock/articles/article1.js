// 文章 1：使用 Vue 3 + Vuetify 构建现代化博客系统
export var content1 = `# 使用 Vue 3 + Vuetify 构建现代化博客系统

## 前言

随着前端技术的快速发展，Vue 3 凭借其出色的性能和优雅的 Composition API 成为了众多开发者的首选框架。本文将带你从零开始，使用 **Vue 3 + Vuetify 3 + Tailwind CSS** 构建一个功能完整、界面美观的个人博客系统。

> 本项目采用前后端分离架构，前端使用 Vue 3，后端使用 Spring Boot 3.x，数据库使用 MySQL 8。

## 技术栈选型

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.4+ | 核心框架 |
| Vuetify | 3.5+ | UI 组件库 |
| Tailwind CSS | 3.4+ | 原子化样式 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.3+ | 路由管理 |
| Axios | 1.6+ | HTTP 请求 |
| Marked | 12.0+ | Markdown 渲染 |

## 项目结构设计

一个清晰的项目结构是可维护性的基础：

\`\`\`
blog-vue/
├── src/
│   ├── api/                 # API 请求封装
│   │   ├── request.js       # axios 实例 + 拦截器
│   │   ├── article.js       # 文章相关接口
│   │   ├── category.js      # 分类接口
│   │   └── comment.js       # 评论接口
│   ├── components/
│   │   ├── frontend/        # 前台公共组件
│   │   └── admin/           # 后台公共组件
│   ├── views/
│   │   ├── frontend/        # 前台页面
│   │   └── admin/           # 后台页面
│   ├── store/
│   │   └── auth.js          # 登录态管理
│   ├── router/
│   │   └── index.js         # 路由配置
│   ├── mock/                # Mock 数据
│   └── plugins/
│       └── vuetify.js       # Vuetify 主题配置
\`\`\`

## Vuetify 主题配置

Vuetify 3 支持完整的主题定制，我们配置 Google Material Design 3 风格的主题：

\`\`\`javascript
var vuetify = createVuetify({
  theme: {
    defaultTheme: 'googleLight',
    themes: {
      googleLight: {
        dark: false,
        colors: {
          primary: '#1a73e8',    // Google Blue
          secondary: '#34a853',  // Google Green
          error: '#ea4335',      // Google Red
          warning: '#fbbc04',    // Google Yellow
          background: '#f8f9fa'
        }
      }
    }
  },
  defaults: {
    VCard: { rounded: 'lg', elevation: 1 },
    VBtn: {
      rounded: 'lg',
      style: 'text-transform: none; font-weight: 500;'
    }
  }
})
\`\`\`

## axios 请求封装

统一的请求封装是前端工程化的重要一环。我们需要处理以下场景：

1. **Token 自动注入**：后台接口（\`/api/admin\`）自动携带 Authorization 头
2. **统一错误处理**：业务错误码统一提示，401 自动跳转登录
3. **Mock 降级**：后端未启动时自动使用本地 Mock 数据

\`\`\`javascript
// 请求拦截器：后台接口自动注入 Token
request.interceptors.request.use(function(config) {
  if (config.url && config.url.indexOf('/api/admin') === 0) {
    var authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = authStore.token
    }
  }
  return config
})

// 响应拦截器：统一处理业务错误
request.interceptors.response.use(
  function(response) {
    var data = response.data
    if (data.code !== 200) {
      if (data.code === 401) {
        // Token 过期，清除登录态并跳转
        useAuthStore().clearAuth()
        window.location.href = '/admin/login'
      }
      return Promise.reject(new Error(data.message))
    }
    return data.data  // 直接返回业务数据
  },
  function(error) {
    // 网络错误时降级到 Mock 数据
    var mockResult = mockHandler(
      error.config.method,
      error.config.url,
      error.config.params
    )
    if (mockResult) return Promise.resolve(mockResult.data)
    return Promise.reject(error)
  }
)
\`\`\`

## 路由设计

博客系统分为前台和后台两套路由，通过路径前缀区分：

\`\`\`javascript
var routes = [
  // 前台路由（无需登录）
  { path: '/', component: HomeView },
  { path: '/article/:id', component: ArticleDetailView },
  { path: '/category/:id', component: CategoryView },
  { path: '/archive', component: ArchiveView },
  { path: '/search', component: SearchView },

  // 后台路由（需要登录，嵌套在 AdminLayout 下）
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', component: DashboardView },
      { path: 'articles', component: ArticleListView },
      { path: 'articles/edit/:id?', component: ArticleEditView }
    ]
  }
]

// 路由守卫
router.beforeEach(function(to, from, next) {
  if (to.meta.requiresAuth && !useAuthStore().isLoggedIn) {
    next('/admin/login')
  } else {
    next()
  }
})
\`\`\`

## Pinia 状态管理

使用 Pinia 管理登录态，支持 localStorage 持久化：

\`\`\`javascript
export var useAuthStore = defineStore('auth', {
  state: function() {
    return {
      token: localStorage.getItem('blog_token') || ''
    }
  },
  getters: {
    isLoggedIn: function(state) {
      return !!state.token
    }
  },
  actions: {
    setAuth: function(token) {
      this.token = token
      localStorage.setItem('blog_token', token)
    },
    clearAuth: function() {
      this.token = ''
      localStorage.removeItem('blog_token')
    }
  }
})
\`\`\`

## Markdown 渲染

文章内容使用 Markdown 格式存储，前端使用 \`marked\` 库渲染为 HTML：

\`\`\`javascript
import { marked } from 'marked'

// 自定义渲染器，为标题添加锚点 id（用于目录跳转）
var renderer = new marked.Renderer()
renderer.heading = function(text, level) {
  var id = text.toLowerCase().replace(/[^\\w\\u4e00-\\u9fa5]+/g, '-')
  return '<h' + level + ' id="' + id + '">' + text + '</h' + level + '>'
}

var html = marked(markdownContent, { renderer: renderer })
\`\`\`

## 文章目录（TOC）

使用 \`IntersectionObserver\` 实现滚动高亮当前章节：

\`\`\`javascript
// 监听标题元素的可见性
var observer = new IntersectionObserver(
  function(entries) {
    entries.forEach(function(entry) {
      if (entry.isIntersecting) {
        activeId = entry.target.id
      }
    })
  },
  { rootMargin: '-64px 0px -70% 0px' }
)

// 观察所有标题元素
document.querySelectorAll('h1, h2, h3').forEach(function(el) {
  observer.observe(el)
})
\`\`\`

## 代码块复制功能

为每个代码块动态添加复制按钮：

\`\`\`javascript
function addCopyButtons() {
  document.querySelectorAll('pre').forEach(function(pre) {
    var btn = document.createElement('button')
    btn.textContent = '复制'
    btn.addEventListener('click', function() {
      var code = pre.querySelector('code')
      navigator.clipboard.writeText(code.textContent).then(function() {
        btn.textContent = '✓ 已复制'
        setTimeout(function() { btn.textContent = '复制' }, 2000)
      })
    })
    pre.style.position = 'relative'
    pre.appendChild(btn)
  })
}
\`\`\`

## 阅读进度条

监听 \`scroll\` 事件，计算阅读进度：

\`\`\`javascript
window.addEventListener('scroll', function() {
  var scrollTop = window.scrollY
  var docHeight = document.documentElement.scrollHeight
                - document.documentElement.clientHeight
  progress = (scrollTop / docHeight) * 100
}, { passive: true })
\`\`\`

## 总结

通过本文，我们完成了一个功能完整的博客前端系统，涵盖了：

- ✅ Material Design 3 主题配置
- ✅ 统一 axios 请求封装（含 Mock 降级）
- ✅ 前后台路由分离 + 守卫
- ✅ Pinia 登录态持久化
- ✅ Markdown 渲染 + 目录导航
- ✅ 代码块复制、阅读进度条等细节功能

希望本文对你有所帮助！如有问题欢迎在评论区留言。
`
