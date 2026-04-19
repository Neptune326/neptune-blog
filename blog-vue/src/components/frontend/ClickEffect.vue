<template>
  <!-- 鼠标点击特效：全局挂载，点击时在点击位置飘出粒子 -->
  <teleport to="body">
    <div
      v-for="p in particles"
      :key="p.id"
      :style="{
        position: 'fixed',
        left: p.x + 'px',
        top: p.y + 'px',
        pointerEvents: 'none',
        zIndex: 99999,
        fontSize: p.fontSize + 'px',
        color: p.color,
        fontWeight: '700',
        userSelect: 'none',
        transform: 'translate(-50%, -50%)',
        animation: 'click-float ' + p.duration + 'ms ease-out forwards',
        whiteSpace: 'nowrap'
      }"
    >{{ p.text }}</div>
  </teleport>
</template>

<script>
// 点击特效粒子池
var TEXTS = ['❤️', '✨', '⭐', '🌟', '💫', '🎉', '👍', '🔥', '💖', '🌸']
var COLORS = ['#f43f5e', '#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#6366f1']
var idCounter = 0

export default {
  name: 'ClickEffect',
  props: {
    enabled: { type: Boolean, default: true }
  },
  data: function() {
    return {
      particles: []
    }
  },
  mounted: function() {
    if (this.enabled) {
      document.addEventListener('click', this.onDocClick)
    }
  },
  beforeUnmount: function() {
    document.removeEventListener('click', this.onDocClick)
  },
  watch: {
    enabled: function(val) {
      if (val) {
        document.addEventListener('click', this.onDocClick)
      } else {
        document.removeEventListener('click', this.onDocClick)
      }
    }
  },
  methods: {
    onDocClick: function(e) {
      // 忽略后台页面
      if (window.location.pathname.startsWith('/admin')) return
      var self = this
      var count = 3 + Math.floor(Math.random() * 3) // 每次飘出 3~5 个
      for (var i = 0; i < count; i++) {
        var id = ++idCounter
        var offsetX = (Math.random() - 0.5) * 60
        var offsetY = (Math.random() - 0.5) * 40
        var p = {
          id: id,
          x: e.clientX + offsetX,
          y: e.clientY + offsetY,
          text: TEXTS[Math.floor(Math.random() * TEXTS.length)],
          color: COLORS[Math.floor(Math.random() * COLORS.length)],
          fontSize: 14 + Math.floor(Math.random() * 10),
          duration: 800 + Math.floor(Math.random() * 400)
        }
        self.particles.push(p)
        // 动画结束后移除
        ;(function(pid, dur) {
          setTimeout(function() {
            var idx = self.particles.findIndex(function(x) { return x.id === pid })
            if (idx >= 0) self.particles.splice(idx, 1)
          }, dur + 50)
        })(id, p.duration)
      }
    }
  }
}
</script>

<style>
@keyframes click-float {
  0%   { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  60%  { opacity: 0.8; transform: translate(-50%, -120%) scale(1.2); }
  100% { opacity: 0; transform: translate(-50%, -200%) scale(0.8); }
}
</style>
