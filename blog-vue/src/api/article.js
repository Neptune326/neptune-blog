// 文章相关 API
import request from './request.js'

/**
 * 前台：获取文章列表
 * @param {Object} params - 查询参数（分页、关键词等）
 */
export function getArticles(params) {
  return request({
    method: 'get',
    url: '/api/articles',
    params: params
  })
}

/**
 * 前台：根据 ID 获取文章详情
 * @param {number|string} id - 文章 ID
 */
export function getArticleById(id) {
  return request({
    method: 'get',
    url: '/api/articles/' + id
  })
}

/**
 * 前台：获取文章归档
 */
export function getArchive() {
  return request({
    method: 'get',
    url: '/api/articles/archive'
  })
}

/**
 * 后台：获取文章列表
 * @param {Object} params - 查询参数（分页、状态等）
 */
export function adminGetArticles(params) {
  return request({
    method: 'get',
    url: '/api/admin/articles',
    params: params
  })
}

/**
 * 后台：根据 ID 获取文章详情
 * @param {number|string} id - 文章 ID
 */
export function adminGetArticleById(id) {
  return request({
    method: 'get',
    url: '/api/admin/articles/' + id
  })
}

/**
 * 后台：创建文章
 * @param {Object} data - 文章数据
 */
export function createArticle(data) {
  return request({
    method: 'post',
    url: '/api/admin/articles',
    data: data
  })
}

/**
 * 后台：更新文章
 * @param {number|string} id - 文章 ID
 * @param {Object} data - 更新数据
 */
export function updateArticle(id, data) {
  return request({
    method: 'put',
    url: '/api/admin/articles/' + id,
    data: data
  })
}

/**
 * 后台：删除文章
 * @param {number|string} id - 文章 ID
 */
export function deleteArticle(id) {
  return request({
    method: 'delete',
    url: '/api/admin/articles/' + id
  })
}

export function getArticleHistory(id) {
  return request({
    method: 'get',
    url: '/api/admin/articles/' + id + '/history'
  })
}
