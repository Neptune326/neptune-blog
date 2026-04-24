<template>
  <span style="display: none;" aria-hidden="true"></span>
</template>

<script>
import { loadOml2d } from 'oh-my-live2d'

var SDK_URL = 'https://cdn.jsdelivr.net/npm/oh-my-live2d@0.18.0/lib/complete.js'
var OML2D_NODE_IDS = ['oml2d-stage', 'oml2d-statusBar', 'oml2d-tips', 'oml2d-menus']
var LIVE2D_PREFETCH_DELAY = 1200
var LIVE2D_PREFETCH_INTERVAL = 900
function hasMultipleOutfits(model) {
  return !!(model && Array.isArray(model.path) && model.path.length > 1)
}
function flattenModelPaths(model) {
  if (!model) return []
  return Array.isArray(model.path) ? model.path : [model.path]
}
function toAbsoluteUrl(path, baseUrl) {
  try {
    return new URL(path, baseUrl).toString()
  } catch (e) {
    return ''
  }
}
function collectModelAssetUrls(modelJson, modelUrl) {
  var urls = []
  var references = modelJson && modelJson.FileReferences ? modelJson.FileReferences : {}
  ;[
    references.Moc,
    references.Physics,
    references.Pose,
    modelJson && modelJson.model,
    modelJson && modelJson.physics,
    modelJson && modelJson.pose
  ].forEach(function(path) {
    if (path) urls.push(toAbsoluteUrl(path, modelUrl))
  })

  ;(references.Textures || modelJson.textures || []).forEach(function(path) {
    if (path) urls.push(toAbsoluteUrl(path, modelUrl))
  })

  ;(references.Expressions || modelJson.expressions || []).forEach(function(expression) {
    var path = expression && (expression.File || expression.file)
    if (path) urls.push(toAbsoluteUrl(path, modelUrl))
  })

  var motions = references.Motions || modelJson.motions || {}
  Object.keys(motions).forEach(function(key) {
    ;(motions[key] || []).forEach(function(motion) {
      var motionPath = motion && (motion.File || motion.file)
      var soundPath = motion && (motion.Sound || motion.sound)
      if (motionPath) urls.push(toAbsoluteUrl(motionPath, modelUrl))
      if (soundPath) urls.push(toAbsoluteUrl(soundPath, modelUrl))
    })
  })

  return urls.filter(function(url, index) {
    return url && urls.indexOf(url) === index
  })
}
function scheduleIdleTask(callback, delay) {
  var run = function() {
    if (window.requestIdleCallback) {
      window.requestIdleCallback(callback, { timeout: 3000 })
      return
    }
    callback()
  }
  window.setTimeout(run, delay)
}
var LIVE2D_MODELS = [
  {
    name: 'shizuku',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-shizuku@1.0.5/assets/shizuku.model.json',
    scale: 0.12,
    position: [0, 20]
  },
  {
    name: 'chitose',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-chitose@1.0.5/assets/chitose.model.json',
    scale: 0.12,
    position: [0, 16]
  },
  {
    name: 'haru',
    path: [
      'https://cdn.jsdelivr.net/npm/live2d-widget-model-haru@1.0.5/assets/haru/01.model.json',
      'https://cdn.jsdelivr.net/npm/live2d-widget-model-haru@1.0.5/assets/haru/02.model.json'
    ],
    scale: 0.12,
    position: [0, 16]
  },
  {
    name: 'hibiki',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-hibiki@1.0.5/assets/hibiki.model.json',
    scale: 0.14,
    position: [0, 12]
  },
  {
    name: 'haruto',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-haruto@1.0.5/assets/haruto.model.json',
    scale: 0.12,
    position: [0, 16]
  },
  {
    name: 'izumi',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-izumi@1.0.5/assets/izumi.model.json',
    scale: 0.13,
    position: [0, 14]
  },
  {
    name: 'koharu',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-koharu@1.0.5/assets/koharu.model.json',
    scale: 0.13,
    position: [0, 16]
  },
  {
    name: 'miku',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-miku@1.0.5/assets/miku.model.json',
    scale: 0.12,
    position: [0, 14]
  },
  {
    name: 'nico',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-nico@1.0.5/assets/nico.model.json',
    scale: 0.12,
    position: [0, 14]
  },
  {
    name: 'nipsilon',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-nipsilon@1.0.5/assets/nipsilon.model.json',
    scale: 0.12,
    position: [0, 16]
  },
  {
    name: 'nito',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-nito@1.0.5/assets/nito.model.json',
    scale: 0.12,
    position: [0, 14]
  },
  {
    name: 'tsumiki',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-tsumiki@1.0.5/assets/tsumiki.model.json',
    scale: 0.11,
    position: [0, 18]
  },
  {
    name: 'unitychan',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-unitychan@1.0.5/assets/unitychan.model.json',
    scale: 0.12,
    position: [0, 14]
  },
  {
    name: 'z16',
    path: 'https://cdn.jsdelivr.net/npm/live2d-widget-model-z16@1.0.5/assets/z16.model.json',
    scale: 0.11,
    position: [0, 18]
  }
]
var DESKTOP_STAGE_STYLE = {
  left: '16px',
  bottom: '12px',
  zIndex: 9100
}
var MOBILE_STAGE_STYLE = {
  left: '8px',
  bottom: '8px',
  zIndex: 9100
}

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
      visibilityTimers: [],
      prefetchTimers: [],
      prefetchedUrls: {}
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
    this.clearPrefetchTimers()
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
          models: LIVE2D_MODELS.map(function(model) {
            return {
              name: model.name,
              path: model.path,
              scale: model.scale,
              position: model.position,
              stageStyle: DESKTOP_STAGE_STYLE,
              mobileScale: Math.max(Number((model.scale * 0.7).toFixed(3)), 0.08),
              mobileStageStyle: MOBILE_STAGE_STYLE,
              motionPreloadStrategy: 'NONE',
              volume: 0
            }
          }),
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
          menus: function(currentModel) {
            return {
              items: function(defaultItems) {
                return defaultItems.filter(function(item) {
                  if (!item || item.id === 'About') {
                    return false
                  }
                  if (item.id === 'SwitchModelClothes' && !hasMultipleOutfits(currentModel)) {
                    return false
                  }
                  return true
                })
              },
              style: {
                right: '-28px',
                zIndex: 9102
              }
            }
          }
        })
        if (this.widget && this.widget.onLoad) {
          this.widget.onLoad(function() {
            self.setWidgetVisible(self.enabled)
            if (!self.enabled) self.hideWidget()
            self.scheduleModelPrefetch()
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
    scheduleModelPrefetch: function() {
      if (typeof window === 'undefined' || !window.fetch) return

      var self = this
      var currentIndex = this.widget && typeof this.widget.modelIndex === 'number' ? this.widget.modelIndex : 0
      var orderedModels = LIVE2D_MODELS.slice(currentIndex + 1).concat(LIVE2D_MODELS.slice(0, currentIndex))
      this.clearPrefetchTimers()
      orderedModels.forEach(function(model, index) {
        var delay = LIVE2D_PREFETCH_DELAY + index * LIVE2D_PREFETCH_INTERVAL
        var timer = window.setTimeout(function() {
          scheduleIdleTask(function() {
            self.prefetchModel(model)
          }, 0)
        }, delay)
        self.prefetchTimers.push(timer)
      })
    },
    clearPrefetchTimers: function() {
      this.prefetchTimers.forEach(function(timer) {
        window.clearTimeout(timer)
      })
      this.prefetchTimers = []
    },
    prefetchModel: function(model) {
      var self = this
      flattenModelPaths(model).forEach(function(modelUrl) {
        if (!modelUrl || self.prefetchedUrls[modelUrl]) return

        self.prefetchJson(modelUrl).then(function(modelJson) {
          collectModelAssetUrls(modelJson, modelUrl).forEach(function(assetUrl) {
            self.prefetchUrl(assetUrl)
          })
        }).catch(function(e) {
          console.warn('[Live2D] 模型预加载失败', e)
        })
      })
    },
    prefetchJson: function(url) {
      return this.prefetchUrl(url).then(function(response) {
        if (!response || !response.ok) return null
        return response.clone().json()
      })
    },
    prefetchUrl: function(url) {
      if (!url || this.prefetchedUrls[url]) {
        return Promise.resolve(null)
      }
      this.prefetchedUrls[url] = true
      return window.fetch(url, {
        mode: 'cors',
        cache: 'force-cache',
        credentials: 'omit'
      }).catch(function() {
        return null
      })
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
