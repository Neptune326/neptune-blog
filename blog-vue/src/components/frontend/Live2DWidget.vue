<template>
  <div></div>
</template>

<script>
// 可用模型列表（来自 npm CDN，国内访问稳定）
// 每个模型包含 path（model.json路径）和 textures（可选换装数量）
var MODELS = [
  {
    name: 'shizuku',
    path: 'https://npm.elemecdn.com/live2d-widget-model-shizuku@1.0.5/assets/shizuku.model.json',
    textures: 1
  },
  {
    name: 'miku',
    path: 'https://npm.elemecdn.com/live2d-widget-model-miku@1.0.5/assets/miku.model.json',
    textures: 1
  },
  {
    name: 'hijiki',
    path: 'https://npm.elemecdn.com/live2d-widget-model-hijiki@1.0.5/assets/hijiki.model.json',
    textures: 1
  },
  {
    name: 'tororo',
    path: 'https://npm.elemecdn.com/live2d-widget-model-tororo@1.0.5/assets/tororo.model.json',
    textures: 1
  },
  {
    name: 'koharu',
    path: 'https://npm.elemecdn.com/live2d-widget-model-koharu@1.0.5/assets/koharu.model.json',
    textures: 1
  },
  {
    name: 'haruto',
    path: 'https://npm.elemecdn.com/live2d-widget-model-haruto@1.0.5/assets/haruto.model.json',
    textures: 1
  },
  {
    name: 'wanko',
    path: 'https://npm.elemecdn.com/live2d-widget-model-wanko@1.0.5/assets/wanko.model.json',
    textures: 1
  },
  {
    name: 'z16',
    path: 'https://npm.elemecdn.com/live2d-widget-model-z16@1.0.5/assets/z16.model.json',
    textures: 1
  }
]

var currentModelIndex = Math.floor(Math.random() * MODELS.length)

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
    var script = document.getElementById('live2d-widget-script')
    if (script) script.remove()
    var style = document.getElementById('live2d-widget-style')
    if (style) style.remove()
  },
  methods: {
    loadWidget: function() {
      if (document.getElementById('live2d-widget-script')) return

      var self = this
      var model = MODELS[currentModelIndex]

      // 自定义 modelAPI：返回当前模型信息
      // live2d-widget 会调用 modelAPI?id=1&textures=1 这样的接口
      // 我们用一个假的 API 函数替代
      window.live2d_settings = {
        // 不使用远程 API，改用本地模型切换函数
        modelAPI: '',
        modelRandMode: false,
        showToolMenu: true,
        tools: ['hitokoto', 'switch-model', 'switch-texture', 'photo', 'info', 'quit'],
        hitokotoAPI: 'https://v1.hitokoto.cn/?encode=text',
        waifuPath: 'https://cdn.jsdelivr.net/gh/stevenjoezhang/live2d-widget@latest/waifu-tips.json',
        modelWidth: 150,
        modelHeight: 200
      }

      // 注入样式
      var style = document.createElement('style')
      style.id = 'live2d-widget-style'
      style.textContent = [
        '#waifu { bottom: 0 !important; left: 0 !important; }',
        '#waifu-toggle { bottom: 150px !important; left: 0 !important; }',
        '#waifu canvas { max-width: 150px !important; max-height: 200px !important; }'
      ].join('\n')
      document.head.appendChild(style)

      // 加载 live2d-widget 核心脚本
      var script = document.createElement('script')
      script.id = 'live2d-widget-script'
      script.src = 'https://cdn.jsdelivr.net/gh/stevenjoezhang/live2d-widget@latest/autoload.js'
      script.async = true
      script.onload = function() {
        // 脚本加载完成后，等待 waifu 元素出现，然后加载模型
        self.waitAndLoadModel(model.path)
      }
      script.onerror = function() {
        // 备用：直接用 oh-my-live2d 加载
        self.loadFallback(model.path)
      }
      document.head.appendChild(script)
    },

    waitAndLoadModel: function(modelPath) {
      var self = this
      var attempts = 0
      var timer = setInterval(function() {
        attempts++
        // 等待 loadlive2d 函数可用
        if (typeof window.loadlive2d === 'function') {
          clearInterval(timer)
          window.loadlive2d('live2d', modelPath)
          self.setupModelSwitch()
        } else if (attempts > 50) {
          clearInterval(timer)
          // 超时则用备用方案
          self.loadFallback(modelPath)
        }
      }, 200)
    },

    setupModelSwitch: function() {
      // 监听换模型按钮点击
      var self = this
      document.addEventListener('click', function(e) {
        var btn = e.target.closest('[data-action="switch-model"]') ||
                  (e.target.title && e.target.title.includes('换装') ? e.target : null)
        if (btn) {
          currentModelIndex = (currentModelIndex + 1) % MODELS.length
          var model = MODELS[currentModelIndex]
          if (typeof window.loadlive2d === 'function') {
            window.loadlive2d('live2d', model.path)
          }
        }
      })
    },

    loadFallback: function(modelPath) {
      // 备用：使用 oh-my-live2d
      import('oh-my-live2d').then(function(module) {
        var loadOml2d = module.loadOml2d || module.default
        if (typeof loadOml2d !== 'function') return
        loadOml2d({
          models: MODELS.map(function(m) {
            return { path: m.path, scale: 0.15, position: [0, 50], stageStyle: { width: 180, height: 220 } }
          }),
          stageStyle: { width: 180, height: 220 },
          dockedPosition: 'left'
        })
      }).catch(function() {})
    }
  }
}
</script>
