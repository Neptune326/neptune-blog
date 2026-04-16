// 系统配置 API
import request from './request.js'

/** 获取所有系统配置 */
export function getSysConfig() {
  return request({ method: 'get', url: '/api/admin/sys-config' })
}

/** 批量更新系统配置 */
export function updateSysConfig(data) {
  return request({ method: 'put', url: '/api/admin/sys-config', data: data })
}

/** 获取操作日志列表 */
export function getOperationLogs(params) {
  return request({ method: 'get', url: '/api/admin/logs/operation', params: params })
}

/** 获取登录日志列表 */
export function getLoginLogs(params) {
  return request({ method: 'get', url: '/api/admin/logs/login', params: params })
}

/** 清空操作日志 */
export function clearOperationLogs() {
  return request({ method: 'delete', url: '/api/admin/logs/operation' })
}

/** 清空登录日志 */
export function clearLoginLogs() {
  return request({ method: 'delete', url: '/api/admin/logs/login' })
}
