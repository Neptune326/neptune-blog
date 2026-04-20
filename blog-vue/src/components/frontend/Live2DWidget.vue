<template>
  <div></div>
</template>

<script>
export default {
  name: 'Live2DWidget',
  mounted: function() {
    this.loadWidget()
  },
  beforeUnmount: function() {
    var el = document.getElementById('waifu')
    if (el) el.remove()
    var toggle = document.getElementById('waifu-toggle')
    if (toggle) toggle.remove()
    var script = document.getElementById('live2d-widgets-script')
    if (script) script.remove()
  },
  methods: {
    loadWidget: function() {
      if (document.getElementById('live2d-widgets-script')) return

      // live2d-widgets 配置，在脚本加载前设置
      // cdnPath 指向模型仓库，使用 GitHub CDN 镜像
      window.live2d_settings = {
        // 模型 CDN 路径（使用 fghrsh 的模型仓库，通过 jsdelivr 加速）
        cdnPath: 'https://fastly.jsdelivr.net/gh/fghrsh/live2d_api/',
        // 默认模型 id（0=随机）
        modelId: 0,
        // 工具栏按钮
        tools: ['hitokoto', 'switch-model', 'switch-texture', 'photo', 'info', 'quit'],
        // 支持拖动
        drag: false,
        // 日志等级
        logLevel: 'error'
      }

      var script = document.createElement('script')
      script.id = 'live2d-widgets-script'
      script.src = 'https://fastly.jsdelivr.net/npm/live2d-widgets@1.0.0/dist/autoload.js'
      script.async = true
      script.onerror = function() {
        // fastly 失败时用 cdn.jsdelivr.net
        var fallback = document.createElement('script')
        fallback.id = 'live2d-widgets-script-fallback'
        fallback.src = 'https://cdn.jsdelivr.net/npm/live2d-widgets@1.0.0/dist/autoload.js'
        fallback.async = true
        document.head.appendChild(fallback)
      }
      document.head.appendChild(script)
    }
  }
}
</script>
