// Vuetify 插件配置 —— Material Design 3 Google 风格主题
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

var vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi'
  },
  theme: {
    defaultTheme: 'googleLight',
    themes: {
      googleLight: {
        dark: false,
        colors: {
          // Google Blue 主色
          primary: '#1a73e8',
          // Google Green 辅色
          secondary: '#34a853',
          // Google Red 错误色
          error: '#ea4335',
          // Google Yellow 警告色
          warning: '#fbbc04',
          // 信息色
          info: '#4285f4',
          // 成功色
          success: '#34a853',
          // 背景色
          background: '#f8f9fa',
          // 卡片/表面色
          surface: '#ffffff',
          // 次要文字色
          'on-surface-variant': '#5f6368'
        }
      },
      // 深色主题（与 App.vue CSS 变量对齐，略分层避免一片灰）
      googleDark: {
        dark: true,
        colors: {
          primary: '#a8c7fa',
          secondary: '#7fc89c',
          error: '#f2b8b5',
          warning: '#f9d975',
          info: '#a8c7fa',
          success: '#7fc89c',
          background: '#1c1b1f',
          surface: '#2d2f35',
          'on-background': '#e3e3e3',
          'on-surface': '#e3e3e3',
          'on-surface-variant': '#b0b3b8',
          'surface-variant': '#444746',
          outline: '#8e918f',
          'outline-variant': '#444746'
        }
      }
    }
  },
  defaults: {
    // 全局组件默认配置
    VCard: {
      rounded: 'lg',
      elevation: 1
    },
    VBtn: {
      rounded: 'lg',
      style: 'text-transform: none; letter-spacing: 0.01em; font-weight: 500;'
    },
    VTextField: {
      rounded: 'lg',
      color: 'primary'
    },
    VTextarea: {
      rounded: 'lg',
      color: 'primary'
    },
    VSelect: {
      rounded: 'lg',
      color: 'primary'
    },
    VChip: {
      rounded: 'xl'
    },
    VAppBar: {
      elevation: 0,
      color: 'surface'
    },
    VNavigationDrawer: {
      color: 'surface'
    }
  }
})

// 首屏前恢复用户主题，避免从落地页进站点时与 localStorage 不一致
; (function() {
  try {
    if (typeof localStorage === 'undefined' || !vuetify || !vuetify.theme) return
    var saved = localStorage.getItem('blog-theme')
    if (saved === 'googleDark' || saved === 'googleLight') {
      vuetify.theme.global.name.value = saved
    }
  } catch (e) {}
})()

export default vuetify
