// 用户认证状态管理
import { defineStore } from 'pinia'

export var useAuthStore = defineStore('auth', {
  state: function() {
    return {
      // 从 localStorage 读取初始 token
      token: localStorage.getItem('blog_token') || '',
      userInfo: null
    }
  },
  getters: {
    // 是否已登录
    isLoggedIn: function(state) {
      return !!state.token
    }
  },
  actions: {
    // 保存登录态
    setAuth: function(token) {
      this.token = token
      localStorage.setItem('blog_token', token)
    },
    // 清除登录态
    clearAuth: function() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('blog_token')
    }
  }
})
