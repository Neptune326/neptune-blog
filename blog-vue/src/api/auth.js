// 认证相关 API
import request from './request.js'

/**
 * 管理员登录
 * @param {Object} data - 登录数据 { username, password }
 */
export function login(data) {
  return request({
    method: 'post',
    url: '/api/admin/auth/login',
    data: data
  })
}

/**
 * 获取登录验证码
 */
export function getLoginCaptcha() {
  return request({
    method: 'get',
    url: '/api/admin/auth/captcha'
  })
}

/**
 * 管理员登出
 */
export function logout() {
  return request({
    method: 'post',
    url: '/api/admin/auth/logout'
  })
}
