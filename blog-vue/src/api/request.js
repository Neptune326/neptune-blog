// axios 请求封装 —— 纯净版，不含任何 mock 引用
// mock 功能通过 setupMock() 插件接口注入，完全解耦
import axios from 'axios'
import { useAuthStore } from '../store/auth.js'
import { toast } from '../plugins/toast.js'

// ===== 插件接口 =====
// mock 插件需实现的接口：
// {
//   handle(method, url, params, data): object|null  // 返回 mock 响应或 null
//   detect(): Promise<boolean>                       // 检测后端是否可用
// }
var _mockPlugin = null
var _mockMode = false

/**
 * 注册 mock 插件（仅在开发环境调用）
 * @param {object} plugin - 实现 handle / detect 接口的插件对象
 */
export function setupMock(plugin) {
  _mockPlugin = plugin
}

// ===== axios 实例 =====
var request = axios.create({
  baseURL: '',
  timeout: 4000
})

// ===== 请求拦截器 =====
request.interceptors.request.use(function(config) {
  // 注入 Token
  if (config.url && config.url.indexOf('/api/admin') === 0) {
    var authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = authStore.token
    }
  }

  // mock 模式：通过插件拦截请求
  if (_mockMode && _mockPlugin) {
    var method = (config.method || 'get').toLowerCase()
    var url = config.url || ''
    var params = config.params || {}
    var reqData = {}
    try { reqData = config.data ? JSON.parse(config.data) : {} } catch (e) {}

    var mockResult = _mockPlugin.handle(method, url, params, reqData)
    if (mockResult !== null) {
      config._mockResult = mockResult
      var controller = new AbortController()
      config.signal = controller.signal
      controller.abort('__mock__')
    }
  }

  return config
}, function(error) {
  return Promise.reject(error)
})

// ===== 响应拦截器 =====
request.interceptors.response.use(
  function(response) {
    var data = response.data
    if (data.code !== 200) {
      if (data.code === 401) {
        if (window.location.pathname.startsWith('/admin') &&
            !window.location.pathname.startsWith('/admin/login')) {
          useAuthStore().clearAuth()
          window.location.href = '/admin/login'
        }
      } else {
        toast.error(data.message || '请求失败')
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data
  },
  function(error) {
    // mock 拦截的请求，直接返回 mock 数据
    if (error.config && error.config._mockResult) {
      var mockResult = error.config._mockResult
      if (mockResult.code !== 200) {
        return Promise.reject(new Error(mockResult.message || '请求失败'))
      }
      return Promise.resolve(mockResult.data)
    }

    // 后端不可用时，通过插件降级
    if (_mockPlugin) {
      var isNetworkError = !error.response
      var isTimeout = error.code === 'ECONNABORTED' || error.code === 'ERR_NETWORK'
      var isProxyError = error.response && (
        error.response.status === 502 ||
        error.response.status === 503 ||
        error.response.status === 504
      )

      if (isNetworkError || isTimeout || isProxyError) {
        var config = error.config || {}
        var method = (config.method || 'get').toLowerCase()
        var url = config.url || ''
        var params = config.params || {}
        var reqData = {}
        try { reqData = config.data ? JSON.parse(config.data) : {} } catch (e) {}

        var fallbackResult = _mockPlugin.handle(method, url, params, reqData)
        if (fallbackResult !== null) {
          if (!_mockMode) {
            _mockMode = true
            console.warn('[Mock] 后端未启动，已切换到 Mock 数据模式。')
          }
          if (fallbackResult.code !== 200) {
            return Promise.reject(new Error(fallbackResult.message || '请求失败'))
          }
          return Promise.resolve(fallbackResult.data)
        }
      }
    }

    console.error('请求失败:', error.message || error)
    // HTTP 401：只在后台页面跳转登录
    if (error.response && error.response.status === 401) {
      if (window.location.pathname.startsWith('/admin') &&
          !window.location.pathname.startsWith('/admin/login')) {
        useAuthStore().clearAuth()
        window.location.href = '/admin/login'
      }
      return Promise.reject(error)
    }
    return Promise.reject(error)
  }
)

/**
 * 检测后端是否可用
 * 有 mock 插件时委托给插件的 detect()，否则直接返回 true
 */
export function detectBackend() {
  if (!_mockPlugin) return Promise.resolve(true)
  return _mockPlugin.detect().then(function(backendOk) {
    _mockMode = !backendOk
    return backendOk
  })
}

export function isMockMode() {
  return _mockMode
}

export default request
