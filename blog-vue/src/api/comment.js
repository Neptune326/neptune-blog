// 评论相关 API
import request from './request.js'

/**
 * 前台：提交评论
 * @param {Object} data - 评论数据
 */
export function submitComment(data) {
  return request({
    method: 'post',
    url: '/api/comments',
    data: data
  })
}

/**
 * 前台：获取指定文章的评论列表
 * @param {number|string} articleId - 文章 ID
 * @param {Object} params - 查询参数（分页等）
 */
export function getArticleComments(articleId, params) {
  return request({
    method: 'get',
    url: '/api/articles/' + articleId + '/comments',
    params: params
  })
}

/**
 * 后台：获取评论列表
 * @param {Object} params - 查询参数（分页、状态等）
 */
export function adminGetComments(params) {
  return request({
    method: 'get',
    url: '/api/admin/comments',
    params: params
  })
}

/**
 * 后台：审核通过评论
 * @param {number|string} id - 评论 ID
 */
export function approveComment(id) {
  return request({
    method: 'put',
    url: '/api/admin/comments/' + id + '/approve'
  })
}

/**
 * 后台：拒绝评论
 * @param {number|string} id - 评论 ID
 */
export function rejectComment(id) {
  return request({
    method: 'put',
    url: '/api/admin/comments/' + id + '/reject'
  })
}

/**
 * 后台：删除评论
 * @param {number|string} id - 评论 ID
 */
export function deleteComment(id) {
  return request({
    method: 'delete',
    url: '/api/admin/comments/' + id
  })
}
