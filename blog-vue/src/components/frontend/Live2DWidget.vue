<template>
  <!-- Live2D 看板娘 —— 对话气泡（模型由 oh-my-live2d 自动挂载到 body） -->
  <div
    v-if="bubbleVisible && message"
    style="
      position: fixed;
      bottom: 230px;
      left: 30px;
      background: white;
      border: 1px solid #e8eaed;
      border-radius: 12px 12px 12px 0;
      padding: 10px 14px;
      font-size: 13px;
      color: #3c4043;
      max-width: 200px;
      line-height: 1.6;
      box-shadow: 0 2px 8px rgba(0,0,0,0.12);
      z-index: 9100;
      pointer-events: none;
      animation: bubbleFadeIn 0.3s ease;
    "
    class="d-none d-lg-block"
  >
    {{ message }}
    <!-- 气泡小尾巴 -->
    <div style="
      position: absolute;
      bottom: -8px; left: 16px;
      width: 0; height: 0;
      border-left: 8px solid transparent;
      border-right: 8px solid transparent;
      border-top: 8px solid white;
      filter: drop-shadow(0 2px 1px rgba(0,0,0,0.05));
    "></div>
  </div>
</template>

<script>
export default {
  name: 'Live2DWidget',
  data: function() {
    return {
      message: '',
      bubbleVisible: false,
      messageTimer: null,
      greetingTimer: null,
      greetings: [
        '你好呀，欢迎来到我的博客！✨',
        '今天也要好好学习哦～ 📚',
        '有什么想看的文章吗？',
        '记得收藏喜欢的文章哦！',
        '代码写累了？休息一下吧～ ☕',
        '发现 Bug 了？别慌，慢慢调试！🐛',
        '坚持每天进步一点点！💪'
      ],
      greetingIndex: 0
    }
  },
  mounted: function() {
    this.initLive2D()
  },
  beforeUnmount: function() {
    if (this.messageTimer) clearTimeout(this.messageTimer)
    if (this.greetingTimer) clearInterval(this.greetingTimer)
  },
  methods: {
    initLive2D: function() {
      var self = this

      // 动态 import oh-my-live2d（避免 SSR 问题）
      import('oh-my-live2d').then(function(module) {
        var loadOml2d = module.loadOml2d || module.default
        if (typeof loadOml2d !== 'function') {
          console.warn('[Live2D] loadOml2d 不是函数，module:', module)
          return
        }

        loadOml2d({
          // 模型配置 —— 使用 jsdelivr CDN（国内相对稳定）
          models: [
            {
              path: 'https://cdn.jsdelivr.net/gh/eikanya/Live2d-model@master/Live2D/Senko_Normals/senko.model3.json',
              scale: 0.12,
              position: [0, 50],
              stageStyle: { width: 200, height: 250 }
            },
            {
              path: 'https://cdn.jsdelivr.net/gh/guansss/pixi-live2d-display@master/test/assets/haru/haru_greeter_t03.model3.json',
              scale: 0.1,
              position: [0, 30],
              stageStyle: { width: 200, height: 250 }
            }
          ],
          stageStyle: { width: 200, height: 250 },
          tips: {
            style: { width: 180, height: 80, offsetX: 20, offsetY: 80 },
            idleTips: {
              wordTheDay: function(wordTheDayData) {
                return wordTheDayData && wordTheDayData.hitokoto
              }
            }
          },
          menus: {
            items: function(defaultItems) { return defaultItems }
          },
          dockedPosition: 'left',
          onLoad: function() {
            self.showGreeting()
            self.greetingTimer = setInterval(self.showGreeting.bind(self), 30000)
          }
        })
      }).catch(function(err) {
        console.warn('[Live2D] oh-my-live2d 加载失败:', err)
        self.loadFallback()
      })
    },

    // 降级方案：使用 CDN 加载
    loadFallback: function() {
      var self = this
      var script = document.createElement('script')
      // 使用国内可访问的 CDN
      script.src = 'https://cdn.jsdelivr.net/npm/oh-my-live2d/dist/index.min.js'
      script.onload = function() {
        if (window.OML2D) {
          window.OML2D.loadOml2d({
            models: [{
              path: 'https://cdn.jsdelivr.net/gh/eikanya/Live2d-model/Live2D/Senko_Normals/senko.model3.json',
              scale: 0.12
            }],
            dockedPosition: 'left'
          })
          self.showGreeting()
          self.greetingTimer = setInterval(self.showGreeting.bind(self), 30000)
        }
      }
      document.head.appendChild(script)
    },

    showGreeting: function() {
      var self = this
      self.message = self.greetings[self.greetingIndex]
      self.greetingIndex = (self.greetingIndex + 1) % self.greetings.length
      self.bubbleVisible = true
      if (self.messageTimer) clearTimeout(self.messageTimer)
      self.messageTimer = setTimeout(function() {
        self.bubbleVisible = false
      }, 5000)
    }
  }
}
</script>

<style scoped>
@keyframes bubbleFadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
