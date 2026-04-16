// Mock 请求处理器
// 根据请求 URL 和参数返回对应的 mock 数据
// 数据结构与真实后端接口保持一致：{ code: 200, message: 'success', data: ... }

import {
  categories,
  tags,
  articleList,
  articleDetails,
  comments,
  dashboard,
  archive
} from './data.js'

// 构造分页响应
function pageResult(list, pageNum, pageSize) {
  pageNum = pageNum || 1
  pageSize = pageSize || 10
  var start = (pageNum - 1) * pageSize
  var end = start + pageSize
  var sliced = list.slice(start, end)
  return {
    total: list.length,
    pages: Math.ceil(list.length / pageSize),
    list: sliced
  }
}

// 构造成功响应
function ok(data) {
  return { code: 200, message: 'success', data: data }
}

/**
 * 根据 URL 和参数匹配 mock 数据
 * @param {string} method - HTTP 方法（小写）
 * @param {string} url - 请求路径
 * @param {object} params - 查询参数
 * @param {object} data - 请求体
 * @returns {object|null} - 返回 mock 响应，null 表示不匹配
 */
export function mockHandler(method, url, params, data) {
  params = params || {}
  data = data || {}

  var pageNum = parseInt(params.pageNum) || 1
  var pageSize = parseInt(params.pageSize) || 10

  // ===== 前台 API =====

  // GET /api/articles - 文章列表
  if (method === 'get' && url === '/api/articles') {
    var list = articleList.slice()
    // 关键词筛选
    if (params.keyword) {
      var kw = params.keyword.toLowerCase()
      list = list.filter(function(a) {
        return a.title.toLowerCase().indexOf(kw) >= 0 ||
               (a.summary && a.summary.toLowerCase().indexOf(kw) >= 0)
      })
    }
    // 分类筛选
    if (params.categoryId) {
      list = list.filter(function(a) { return a.categoryId === parseInt(params.categoryId) })
    }
    // 标签筛选
    if (params.tagId) {
      var tid = parseInt(params.tagId)
      list = list.filter(function(a) {
        return a.tags && a.tags.some(function(t) { return t.id === tid })
      })
    }
    return ok(pageResult(list, pageNum, pageSize))
  }

  // GET /api/articles/archive - 归档
  if (method === 'get' && url === '/api/articles/archive') {
    return ok(archive)
  }

  // GET /api/articles/:id - 文章详情
  var articleDetailMatch = url.match(/^\/api\/articles\/(\d+)$/)
  if (method === 'get' && articleDetailMatch) {
    var articleId = parseInt(articleDetailMatch[1])
    var detail = articleDetails[articleId]
    if (!detail) {
      // 没有详情数据时，从列表中找基础信息并补充正文
      var base = articleList.find(function(a) { return a.id === articleId })
      if (base) {
        detail = Object.assign({}, base, {
          content: '# ' + base.title + '\n\n> 这是 Mock 数据，后端启动后将显示真实内容。\n\n' + (base.summary || '')
        })
      }
    }
    if (detail) {
      var d = Object.assign({}, detail)
      d.viewCount = (d.viewCount || 0) + 1
      return ok(d)
    }
    return { code: 1005, message: '文章不存在', data: null }
  }

  // GET /api/articles/:id/comments - 文章评论
  var commentsMatch = url.match(/^\/api\/articles\/(\d+)\/comments$/)
  if (method === 'get' && commentsMatch) {
    var cid = parseInt(commentsMatch[1])
    var cList = comments[cid] || []
    return ok(pageResult(cList, pageNum, pageSize))
  }

  // POST /api/comments - 提交评论
  if (method === 'post' && url === '/api/comments') {
    return ok(null)
  }

  // GET /api/categories - 分类列表
  if (method === 'get' && url === '/api/categories') {
    return ok(categories)
  }

  // GET /api/categories/:id/articles - 分类文章
  var catArticlesMatch = url.match(/^\/api\/categories\/(\d+)\/articles$/)
  if (method === 'get' && catArticlesMatch) {
    var catId = parseInt(catArticlesMatch[1])
    var catList = articleList.filter(function(a) { return a.categoryId === catId })
    return ok(pageResult(catList, pageNum, pageSize))
  }

  // GET /api/tags - 标签列表
  if (method === 'get' && url === '/api/tags') {
    return ok(tags)
  }

  // GET /api/tags/:id/articles - 标签文章
  var tagArticlesMatch = url.match(/^\/api\/tags\/(\d+)\/articles$/)
  if (method === 'get' && tagArticlesMatch) {
    var tagId = parseInt(tagArticlesMatch[1])
    var tagList = articleList.filter(function(a) {
      return a.tags && a.tags.some(function(t) { return t.id === tagId })
    })
    return ok(pageResult(tagList, pageNum, pageSize))
  }

  // GET /api/about - 关于我
  if (method === 'get' && url === '/api/about') {
    return ok({
      content: '# 关于我\n\n你好，我是博客的作者。\n\n这里是我的个人博客，记录技术成长，分享开发心得。\n\n## 技术栈\n\n- 后端：Java、Spring Boot、MySQL\n- 前端：Vue 3、Vuetify\n\n## 联系方式\n\n- GitHub：https://github.com/Neptune326\n- Email：your-email@example.com',
      authorName: 'Admin',
      description: '记录技术成长，分享开发心得'
    })
  }

  // ===== 后台 API =====

  // POST /api/admin/auth/login - 登录
  if (method === 'post' && url === '/api/admin/auth/login') {
    if (data.username === 'admin' && data.password === 'admin123') {
      return ok('mock-token-' + Date.now())
    }
    return { code: 1001, message: '用户名或密码错误', data: null }
  }

  // POST /api/admin/auth/logout - 登出
  if (method === 'post' && url === '/api/admin/auth/logout') {
    return ok(null)
  }

  // GET /api/admin/dashboard - 仪表盘
  if (method === 'get' && url === '/api/admin/dashboard') {
    return ok(dashboard)
  }

  // GET /api/admin/articles - 后台文章列表
  if (method === 'get' && url === '/api/admin/articles') {
    var adminList = articleList.slice()
    if (params.keyword) {
      var akw = params.keyword.toLowerCase()
      adminList = adminList.filter(function(a) {
        return a.title.toLowerCase().indexOf(akw) >= 0
      })
    }
    if (params.categoryId) {
      adminList = adminList.filter(function(a) { return a.categoryId === parseInt(params.categoryId) })
    }
    if (params.status !== undefined && params.status !== null && params.status !== '') {
      adminList = adminList.filter(function(a) { return a.status === parseInt(params.status) })
    }
    return ok(pageResult(adminList, pageNum, pageSize))
  }

  // GET /api/admin/articles/:id - 后台文章详情
  var adminArticleMatch = url.match(/^\/api\/admin\/articles\/(\d+)$/)
  if (method === 'get' && adminArticleMatch) {
    var aid = parseInt(adminArticleMatch[1])
    var aDetail = articleDetails[aid]
    if (!aDetail) {
      var aBase = articleList.find(function(a) { return a.id === aid })
      if (aBase) {
        aDetail = Object.assign({}, aBase, { content: '# ' + aBase.title + '\n\nMock 内容' })
      }
    }
    return aDetail ? ok(aDetail) : { code: 1005, message: '文章不存在', data: null }
  }

  // POST /api/admin/articles - 创建文章
  if (method === 'post' && url === '/api/admin/articles') {
    return ok(null)
  }

  // PUT /api/admin/articles/:id - 更新文章
  var updateArticleMatch = url.match(/^\/api\/admin\/articles\/(\d+)$/)
  if (method === 'put' && updateArticleMatch) {
    return ok(null)
  }

  // DELETE /api/admin/articles/:id - 删除文章
  var deleteArticleMatch = url.match(/^\/api\/admin\/articles\/(\d+)$/)
  if (method === 'delete' && deleteArticleMatch) {
    return ok(null)
  }

  // GET /api/admin/categories - 后台分类列表
  if (method === 'get' && url === '/api/admin/categories') {
    return ok(categories)
  }

  // POST /api/admin/categories - 创建分类
  if (method === 'post' && url === '/api/admin/categories') {
    return ok(null)
  }

  // PUT /api/admin/categories/:id - 更新分类
  var updateCatMatch = url.match(/^\/api\/admin\/categories\/(\d+)$/)
  if (method === 'put' && updateCatMatch) {
    return ok(null)
  }

  // DELETE /api/admin/categories/:id - 删除分类
  var deleteCatMatch = url.match(/^\/api\/admin\/categories\/(\d+)$/)
  if (method === 'delete' && deleteCatMatch) {
    return ok(null)
  }

  // GET /api/admin/tags - 后台标签列表
  if (method === 'get' && url === '/api/admin/tags') {
    return ok(tags)
  }

  // POST /api/admin/tags - 创建标签
  if (method === 'post' && url === '/api/admin/tags') {
    return ok(null)
  }

  // PUT /api/admin/tags/:id - 更新标签
  var updateTagMatch = url.match(/^\/api\/admin\/tags\/(\d+)$/)
  if (method === 'put' && updateTagMatch) {
    return ok(null)
  }

  // DELETE /api/admin/tags/:id - 删除标签
  var deleteTagMatch = url.match(/^\/api\/admin\/tags\/(\d+)$/)
  if (method === 'delete' && deleteTagMatch) {
    return ok(null)
  }

  // GET /api/admin/comments - 后台评论列表
  if (method === 'get' && url === '/api/admin/comments') {
    var allComments = []
    Object.values(comments).forEach(function(list) {
      allComments = allComments.concat(list)
    })
    // 补充 mock 待审核评论
    allComments = allComments.concat([
      { id: 100, articleId: 2, nickname: '李四', content: '这篇文章对我帮助很大！', status: 0, createTime: new Date().toISOString().substring(0, 19) },
      { id: 101, articleId: 3, nickname: '王五', content: '期待更多 Git 相关内容', status: 0, createTime: new Date().toISOString().substring(0, 19) },
      { id: 102, articleId: 1, nickname: '赵六', content: '代码示例很清晰', status: 2, createTime: new Date().toISOString().substring(0, 19) }
    ])
    if (params.status !== undefined && params.status !== null && params.status !== '') {
      allComments = allComments.filter(function(c) { return c.status === parseInt(params.status) })
    }
    return ok(pageResult(allComments, pageNum, pageSize))
  }

  // PUT /api/admin/comments/:id/approve - 审核通过
  var approveMatch = url.match(/^\/api\/admin\/comments\/(\d+)\/approve$/)
  if (method === 'put' && approveMatch) {
    return ok(null)
  }

  // PUT /api/admin/comments/:id/reject - 拒绝
  var rejectMatch = url.match(/^\/api\/admin\/comments\/(\d+)\/reject$/)
  if (method === 'put' && rejectMatch) {
    return ok(null)
  }

  // DELETE /api/admin/comments/:id - 删除评论
  var deleteCommentMatch = url.match(/^\/api\/admin\/comments\/(\d+)$/)
  if (method === 'delete' && deleteCommentMatch) {
    return ok(null)
  }

  // ===== 系统配置 =====

  // GET /api/admin/sys-config
  if (method === 'get' && url === '/api/admin/sys-config') {
    return ok({
      live2d_enabled: 'true',
      login_max_fail_count: '5',
      login_lock_duration: '10',
      comment_audit_enabled: 'true',
      blog_name: '我的博客',
      blog_description: '记录技术成长，分享开发心得',
      blog_author: 'Admin'
    })
  }

  // PUT /api/admin/sys-config
  if (method === 'put' && url === '/api/admin/sys-config') {
    return ok(null)
  }

  // ===== 操作日志 =====

  // GET /api/admin/logs/operation
  if (method === 'get' && url === '/api/admin/logs/operation') {
    var mockOpLogs = [
      { id: 1, operator: 'admin', module: '文章管理', action: '创建文章', method: 'POST', requestUrl: '/api/admin/articles', requestIp: '127.0.0.1', costTime: 45, status: 1, createTime: new Date(Date.now() - 3600000).toISOString() },
      { id: 2, operator: 'admin', module: '分类管理', action: '创建分类', method: 'POST', requestUrl: '/api/admin/categories', requestIp: '127.0.0.1', costTime: 23, status: 1, createTime: new Date(Date.now() - 7200000).toISOString() },
      { id: 3, operator: 'admin', module: '文章管理', action: '删除文章', method: 'DELETE', requestUrl: '/api/admin/articles/3', requestIp: '127.0.0.1', costTime: 18, status: 1, createTime: new Date(Date.now() - 86400000).toISOString() },
      { id: 4, operator: 'admin', module: '标签管理', action: '更新标签', method: 'PUT', requestUrl: '/api/admin/tags/1', requestIp: '127.0.0.1', costTime: 31, status: 1, createTime: new Date(Date.now() - 172800000).toISOString() }
    ]
    return ok(pageResult(mockOpLogs, pageNum, pageSize))
  }

  // DELETE /api/admin/logs/operation
  if (method === 'delete' && url === '/api/admin/logs/operation') {
    return ok(null)
  }

  // ===== 登录日志 =====

  // GET /api/admin/logs/login
  if (method === 'get' && url === '/api/admin/logs/login') {
    var mockLoginLogs = [
      { id: 1, username: 'admin', loginIp: '127.0.0.1', loginTime: new Date(Date.now() - 1800000).toISOString(), status: 1, failReason: null, userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0' },
      { id: 2, username: 'admin', loginIp: '192.168.1.100', loginTime: new Date(Date.now() - 3600000).toISOString(), status: 0, failReason: '用户名或密码错误', userAgent: 'Mozilla/5.0 (Macintosh) Safari/537.36' },
      { id: 3, username: 'hacker', loginIp: '10.0.0.1', loginTime: new Date(Date.now() - 7200000).toISOString(), status: 0, failReason: '用户名或密码错误', userAgent: 'curl/7.68.0' },
      { id: 4, username: 'admin', loginIp: '127.0.0.1', loginTime: new Date(Date.now() - 86400000).toISOString(), status: 1, failReason: null, userAgent: 'Mozilla/5.0 (Windows NT 10.0) Firefox/121.0' }
    ]
    var filteredLogs = mockLoginLogs
    if (params.status !== undefined && params.status !== null && params.status !== '') {
      filteredLogs = filteredLogs.filter(function(l) { return l.status === parseInt(params.status) })
    }
    return ok(pageResult(filteredLogs, pageNum, pageSize))
  }

  // DELETE /api/admin/logs/login
  if (method === 'delete' && url === '/api/admin/logs/login') {
    return ok(null)
  }

  // 未匹配到任何路由
  return null
}
