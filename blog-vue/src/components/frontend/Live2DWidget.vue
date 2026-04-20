<template>
  <!-- live2d-widget 会自动挂载到 body，此组件只负责加载脚本 -->
  <div></div>
</template>

<script>
export default {
  name: 'Live2DWidget',
  mounted: function() {
    this.loadWidget()
  },
  beforeUnmount: function() {
    // 移除挂载的 live2d 元素
    var el = document.getElementById('waifu')
    if (el) el.remove()
    var toggle = document.getElementById('waifu-toggle')
    if (toggle) toggle.remove()
  },
  methods: {
    loadWidget: function() {
      // 避免重复加载
      if (document.getElementById('live2d-widget-script')) return

      // 配置项（在加载脚本前设置）
      window.live2d_settings = {
        // 模型目录（使用 stevenjoezhang 的 CDN）
        modelAPI: 'https://live2d.fghrsh.net/api/',
        // 随机切换模型
        modelRandMode: true,
        // 显示工具栏
        showToolMenu: true,
        // 工具栏按钮
        tools: ['hitokoto', 'asteroids', 'switch-model', 'switch-texture', 'photo', 'info', 'quit'],
        // 一言 API
        hitokotoAPI: 'https://v1.hitokoto.cn/?encode=text',
        // 位置：左下角
        waifuPath: 'https://fastly.jsdelivr.net/gh/stevenjoezhang/live2d-widget@latest/waifu-tips.json',
        // 看板娘尺寸
        modelWidth: 150,
        modelHeight: 200
      }

      var script = document.createElement('script')
      script.id = 'live2d-widget-script'
      script.src = 'https://fastly.jsdelivr.net/gh/stevenjoezhang/live2d-widget@latest/autoload.js'
      script.async = true
      script.onerror = function() {
        // jsdelivr 失败时尝试备用 CDN
        var fallback = document.createElement('script')
        fallback.src = 'https://cdn.jsdelivr.net/gh/stevenjoezhang/live2d-widget@latest/autoload.js'
        fallback.async = true
        document.head.appendChild(fallback)
      }
      document.head.appendChild(script)

      // 注入样式（覆盖默认位置，确保不遮挡内容）
      var style = document.createElement('style')
      style.id = 'live2d-widget-style'
      style.textContent = [
        '#waifu { bottom: 0; left: 0; }',
        '#waifu-toggle { bottom: 150px; left: 0; }',
        '#waifu canvas { max-width: 150px; max-height: 200px; }'
      ].join('\n')
      document.head.appendChild(style)
    }
  }
}
</script>
