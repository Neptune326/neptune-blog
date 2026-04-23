<template>
  <span style="display: none;" aria-hidden="true"></span>
</template>

<script>
import { loadOml2d } from 'oh-my-live2d'

var MODEL_URL = 'https://cdn.jsdelivr.net/npm/live2d-widget-model-shizuku@1.0.5/assets/shizuku.model.json'
var SDK_URL = 'https://cdn.jsdelivr.net/npm/oh-my-live2d@0.18.0/lib/complete.js'
var OML2D_NODE_IDS = ['oml2d-stage', 'oml2d-statusBar', 'oml2d-tips', 'oml2d-menus']

export default {
  name: 'Live2DWidget',
  props: {
    enabled: { type: Boolean, default: false }
  },
  data: function() {
    return {
      widget: null,
      initializing: false,
      initialized: false,
      visibilityTimers: []
    }
  },
  watch: {
    enabled: function(value) {
      if (value) {
        this.ensureWidget()
      } else {
        this.hideWidget()
      }
    }
  },
  mounted: function() {
    if (this.enabled) {
      this.ensureWidget()
    }
  },
  beforeUnmount: function() {
    this.clearVisibilityTimers()
    this.hideWidget()
  },
  methods: {
    ensureWidget: function() {
      if (this.initialized || this.initializing || typeof window === 'undefined') {
        if (this.enabled) this.showWidget()
        return
      }

      this.initializing = true
      try {
        var self = this
        this.widget = loadOml2d({
          dockedPosition: 'left',
          mobileDisplay: false,
          importType: 'complete',
          libraryUrls: {
            complete: SDK_URL
          },
          primaryColor: '#1a73e8',
          sayHello: false,
          transitionTime: 700,
          models: [
            {
              name: 'shizuku',
              path: MODEL_URL,
              scale: 0.12,
              position: [0, 20],
              stageStyle: {
                left: '16px',
                bottom: '12px',
                zIndex: 9100
              },
              mobileScale: 0.08,
              mobileStageStyle: {
                left: '8px',
                bottom: '8px',
                zIndex: 9100
              },
              motionPreloadStrategy: 'IDLE',
              volume: 0
            }
          ],
          tips: {
            style: {
              width: '220px',
              minHeight: '64px',
              fontSize: '13px',
              borderRadius: '10px',
              boxShadow: '0 4px 18px rgba(26, 115, 232, 0.14)'
            },
            idleTips: {
              wordTheDay: false,
              interval: 12000,
              message: [
                '欢迎回来，今天也要保持好奇心。',
                '文章慢慢读，我会在这里陪你。',
                '遇到喜欢的内容可以点个赞。'
              ]
            },
            welcomeTips: {
              duration: 4500,
              message: {
                daybreak: '早上好，新的灵感开始了。',
                morning: '上午好，适合读点技术文章。',
                noon: '中午好，休息一下再继续。',
                afternoon: '下午好，注意放松眼睛。',
                dusk: '傍晚好，今天辛苦了。',
                night: '晚上好，慢慢整理今天的收获。',
                lateNight: '夜深了，早点休息更重要。',
                weeHours: '已经很晚了，别熬太久。'
              }
            }
          },
          statusBar: {
            loadingMessage: '看板娘加载中',
            loadSuccessMessage: '看板娘已就位',
            loadFailMessage: '看板娘加载失败',
            reloadMessage: '重新加载',
            restMessage: '看板娘休息中',
            switchingMessage: '正在切换',
            style: {
              left: '18px',
              bottom: '18px',
              zIndex: 9101
            }
          },
          menus: {
            items: function(defaultItems) {
              return defaultItems
            },
            style: {
              zIndex: 9102
            }
          }
        })
        if (this.widget && this.widget.onLoad) {
          this.widget.onLoad(function() {
            self.setWidgetVisible(self.enabled)
            if (!self.enabled) self.hideWidget()
          })
        }
        this.initialized = true
        this.$nextTick(function() {
          if (self.enabled) {
            self.showWidget()
          } else {
            self.hideWidget()
          }
          self.scheduleVisibilitySync()
        })
      } catch (e) {
        this.widget = null
        this.initialized = false
        this.removeWidgetNodes()
        console.warn('[Live2D] 初始化失败', e)
      } finally {
        this.initializing = false
      }
    },
    showWidget: function() {
      this.setWidgetVisible(true)
      if (this.widget && this.widget.stageSlideIn) {
        this.widget.stageSlideIn().catch(function() {})
      }
    },
    hideWidget: function() {
      if (this.widget && this.widget.stageSlideOut) {
        this.widget.stageSlideOut().catch(function() {})
      }
      this.setWidgetVisible(false)
    },
    scheduleVisibilitySync: function() {
      var self = this
      this.clearVisibilityTimers()
      ;[0, 500, 1500, 3000].forEach(function(delay) {
        var timer = window.setTimeout(function() {
          self.setWidgetVisible(self.enabled)
        }, delay)
        self.visibilityTimers.push(timer)
      })
    },
    clearVisibilityTimers: function() {
      this.visibilityTimers.forEach(function(timer) {
        window.clearTimeout(timer)
      })
      this.visibilityTimers = []
    },
    setWidgetVisible: function(visible) {
      OML2D_NODE_IDS.forEach(function(id) {
        var node = document.getElementById(id)
        if (node) {
          node.style.display = visible ? '' : 'none'
          node.setAttribute('aria-hidden', visible ? 'false' : 'true')
        }
      })
    },
    removeWidgetNodes: function() {
      this.clearVisibilityTimers()
      OML2D_NODE_IDS.forEach(function(id) {
        var node = document.getElementById(id)
        if (node && node.parentNode) {
          node.parentNode.removeChild(node)
        }
      })
    }
  }
}
</script>
