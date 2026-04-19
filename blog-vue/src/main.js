// 应用入口文件
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'
import vuetify from './plugins/vuetify.js'
import toastPlugin from './plugins/toast.js'
import { setupMock, detectBackend } from './api/request.js'

// 拦截 oml2d.com 的统计请求（SSL 证书有问题），静默丢弃
;(function() {
  var _origFetch = window.fetch
  window.fetch = function(input, init) {
    var url = typeof input === 'string' ? input : (input && input.url) || ''
    if (url.indexOf('oml2d.com') >= 0) {
      return Promise.resolve(new Response('', { status: 200 }))
    }
    return _origFetch.apply(this, arguments)
  }
})()

function bootstrap(mockPlugin) {
  if (mockPlugin) {
    setupMock(mockPlugin)
  }

  var app = createApp(App)
  app.use(createPinia())
  app.use(router)
  app.use(vuetify)
  app.use(toastPlugin)
  app.mount('#app')
}

// 开发环境：先加载 mock 插件，检测后端是否可用，再启动应用
// 生产环境：直接启动，import.meta.env.DEV 为 false，整个分支被 tree-shaking 移除
if (import.meta.env.DEV) {
  import('./mock/plugin.js').then(function(module) {
    var plugin = module.default
    setupMock(plugin)
    // 先检测后端，确定是否进入 mock 模式，再挂载应用
    // 这样路由守卫执行时 isMockMode() 已有正确值
    return detectBackend().then(function() {
      bootstrap(null) // plugin 已通过 setupMock 注册，无需再传
    })
  })
} else {
  bootstrap(null)
}
