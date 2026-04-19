// axios 请求封装
import axios from 'axios'
import { useAuthStore } from '../store/auth.js'
import { toast } from '../plugins/toast.js'

// ===== axios 实例 =====
var request = axios.create({
  baseURL: '',
  timeout: 15000
})

// ===== 请求拦截器 =====
request.interceptors.request.use(function(config) {
  // 后台接口注入 Token
  if (config.url && config.url.indexOf('/api/admin') === 0) {
    var authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = authStore.token
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
        // 只有在后台页面才跳转登录，前台页面的 401 静默忽略
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
    // HTTP 401：只在后台页面跳转登录
    if (error.response && error.response.status === 401) {
      if (window.location.pathname.startsWith('/admin') &&
          !window.location.pathname.startsWith('/admin/login')) {
        useAuthStore().clearAuth()
        window.location.href = '/admin/login'
      }
      return Promise.reject(error)
    }
    console.error('请求失败:', error.message || error)
    return Promise.reject(error)
  }
)

export default request
