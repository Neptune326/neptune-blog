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
      // 深色主题
      googleDark: {
        dark: true,
        colors: {
          primary: '#8ab4f8',
          secondary: '#81c995',
          error: '#f28b82',
          warning: '#fdd663',
          info: '#8ab4f8',
          success: '#81c995',
          background: '#202124',
          surface: '#292a2d',
          'on-surface-variant': '#9aa0a6'
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
      color: 'white',
      style: 'border-bottom: 1px solid #e8eaed;'
    },
    VNavigationDrawer: {
      color: 'white'
    }
  }
})

export default vuetify
