// 分类相关 API
import request from './request.js'

/**
 * 前台：获取所有分类
 */
export function getCategories() {
  return request({
    method: 'get',
    url: '/api/categories'
  })
}

/**
 * 前台：获取指定分类下的文章列表
 * @param {number|string} id - 分类 ID
 * @param {Object} params - 查询参数（分页等）
 */
export function getCategoryArticles(id, params) {
  return request({
    method: 'get',
    url: '/api/categories/' + id + '/articles',
    params: params
  })
}

/**
 * 后台：获取所有分类
 */
export function adminGetCategories() {
  return request({
    method: 'get',
    url: '/api/admin/categories'
  })
}

/**
 * 后台：创建分类
 * @param {Object} data - 分类数据
 */
export function adminCreateCategory(data) {
  return request({
    method: 'post',
    url: '/api/admin/categories',
    data: data
  })
}

/**
 * 后台：更新分类
 * @param {number|string} id - 分类 ID
 * @param {Object} data - 更新数据
 */
export function adminUpdateCategory(id, data) {
  return request({
    method: 'put',
    url: '/api/admin/categories/' + id,
    data: data
  })
}

/**
 * 后台：删除分类
 * @param {number|string} id - 分类 ID
 */
export function adminDeleteCategory(id) {
  return request({
    method: 'delete',
    url: '/api/admin/categories/' + id
  })
}
