import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// Vite 配置文件
export default defineConfig({
  plugins: [
    vue()
  ],
  resolve: {
    alias: {
      // @ 指向 src 目录，避免相对路径层级问题
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    chunkSizeWarningLimit: 900,
    // 避免首屏预取后台 Markdown 编辑器和图表库（仅进对应路由时再下）
    modulePreload: {
      resolveDependencies: function(filename, deps) {
        return deps.filter(function(dep) {
          var s = String(dep)
          if (s.indexOf('md-editor') >= 0) return false
          if (s.indexOf('echarts') >= 0) return false
          return true
        })
      }
    },
    rollupOptions: {
      output: {
        // 不对 md-editor 单独拆包名，避免入口 chunk 为衔接依赖而静态引用该大包
        manualChunks: function(id) {
          if (!id.includes('node_modules')) return
          if (id.includes('vuetify')) return 'vuetify'
          if (id.includes('@mdi')) return 'mdi'
          if (id.includes('echarts')) return 'echarts'
          if (id.includes('marked')) return 'marked'
          if (id.includes('axios')) return 'axios'
        }
      }
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
