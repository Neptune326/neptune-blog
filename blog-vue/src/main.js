// 应用入口文件
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'
import vuetify from './plugins/vuetify.js'
import toastPlugin from './plugins/toast.js'

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

var app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(vuetify)
app.use(toastPlugin)
app.mount('#app')
// 与 vuetify 插件内恢复的 theme 一致，保证无 DarkModeToggle 的页面（如落地页）也有正确 body 类
try {
  var savedTheme = localStorage.getItem('blog-theme')
  if (document.body) {
    document.body.classList.toggle('dark-mode', savedTheme === 'googleDark')
  }
} catch (e) {}
