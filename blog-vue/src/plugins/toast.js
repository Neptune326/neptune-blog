/**
 * 全局 Toast 提示插件
 * 用法：this.$toast.success('操作成功')
 *       this.$toast.error('操作失败')
 *       this.$toast.warning('请注意')
 *       this.$toast.info('提示信息')
 */
import { createApp, h } from 'vue'
import Toast from '../components/common/Toast.vue'

var toastInstance = null

function createToast() {
  var mountNode = document.createElement('div')
  document.body.appendChild(mountNode)
  var app = createApp({ render: function() { return h(Toast) } })
  var vm = app.mount(mountNode)
  return vm.$el ? vm : vm
}

export var toast = {
  _instance: null,
  _getInstance: function() {
    if (!this._instance) {
      var mountNode = document.createElement('div')
      document.body.appendChild(mountNode)
      var vueApp = createApp(Toast)
      this._instance = vueApp.mount(mountNode)
    }
    return this._instance
  },
  success: function(msg, duration) { this._getInstance().success(msg, duration) },
  error:   function(msg, duration) { this._getInstance().error(msg, duration) },
  warning: function(msg, duration) { this._getInstance().warning(msg, duration) },
  info:    function(msg, duration) { this._getInstance().info(msg, duration) }
}

export default {
  install: function(app) {
    app.config.globalProperties.$toast = toast
  }
}
