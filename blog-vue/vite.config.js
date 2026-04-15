import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置文件
export default defineConfig({
  plugins: [
    // 启用 Vue 3 单文件组件支持
    vue()
  ],
  server: {
    proxy: {
      // 将 /api 开头的请求代理到后端服务
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
