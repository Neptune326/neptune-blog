// 应用入口文件
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'
import vuetify from './plugins/vuetify.js'
import { setupMock } from './api/request.js'

function bootstrap(mockPlugin) {
  // 注册 mock 插件（生产环境传 null，不注册）
  if (mockPlugin) {
    setupMock(mockPlugin)
  }

  var app = createApp(App)
  app.use(createPinia())
  app.use(router)
  app.use(vuetify)
  app.mount('#app')
}

// 开发环境：先加载 mock 插件再启动
// 生产环境：直接启动，import.meta.env.DEV 为 false，整个分支被 tree-shaking 移除
if (import.meta.env.DEV) {
  import('./mock/plugin.js').then(function(module) {
    bootstrap(module.default)
  })
} else {
  bootstrap(null)
}
