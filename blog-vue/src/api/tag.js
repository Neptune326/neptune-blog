// 标签相关 API
import request from './request.js'

/**
 * 前台：获取所有标签
 */
export function getTags() {
  return request({
    method: 'get',
    url: '/api/tags'
  })
}

/**
 * 前台：获取指定标签下的文章列表
 * @param {number|string} id - 标签 ID
 * @param {Object} params - 查询参数（分页等）
 */
export function getTagArticles(id, params) {
  return request({
    method: 'get',
    url: '/api/tags/' + id + '/articles',
    params: params
  })
}

/**
 * 后台：获取所有标签
 */
export function adminGetTags() {
  return request({
    method: 'get',
    url: '/api/admin/tags'
  })
}

/**
 * 后台：创建标签
 * @param {Object} data - 标签数据
 */
export function adminCreateTag(data) {
  return request({
    method: 'post',
    url: '/api/admin/tags',
    data: data
  })
}

/**
 * 后台：更新标签
 * @param {number|string} id - 标签 ID
 * @param {Object} data - 更新数据
 */
export function adminUpdateTag(id, data) {
  return request({
    method: 'put',
    url: '/api/admin/tags/' + id,
    data: data
  })
}

/**
 * 后台：删除标签
 * @param {number|string} id - 标签 ID
 */
export function adminDeleteTag(id) {
  return request({
    method: 'delete',
    url: '/api/admin/tags/' + id
  })
}
