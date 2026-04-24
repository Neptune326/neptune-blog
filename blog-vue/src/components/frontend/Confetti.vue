<template>
  <!-- 彩带礼花特效 canvas -->
  <canvas
    v-if="active"
    ref="canvas"
    style="position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; pointer-events: none; z-index: 99995;"
  />
</template>

<script>
export default {
  name: 'Confetti',
  data: function() {
    return {
      active: false,
      particles: [],
      animId: null,
      manualStopTimer: null
    }
  },
  mounted: function() {
    this.checkSpecialDate()
  },
  beforeUnmount: function() {
    this.stopAll()
  },
  beforeDestroy: function() {
    this.stopAll()
  },
  methods: {
    /**
     * 停止动画与定时器（供 Konami 等手动触发前清理）
     */
    stopAll: function() {
      if (this.manualStopTimer) {
        clearTimeout(this.manualStopTimer)
        this.manualStopTimer = null
      }
      if (this.animId) {
        cancelAnimationFrame(this.animId)
        this.animId = null
      }
    },
    /**
     * 短时有礼花（如 Konami 彩蛋），ms 后收起 canvas
     * @param {number} [durationMs] 默认 4000
     */
    playFor: function(durationMs) {
      var self = this
      var ms = durationMs > 0 ? durationMs : 4000
      self.stopAll()
      self.particles = []
      self.active = true
      self.$nextTick(function() {
        self.launch()
        self.manualStopTimer = setTimeout(function() {
          self.active = false
          self.particles = []
          if (self.animId) {
            cancelAnimationFrame(self.animId)
            self.animId = null
          }
          self.manualStopTimer = null
        }, ms)
      })
    },
    checkSpecialDate: function() {
      var now = new Date()
      var month = now.getMonth() + 1
      var day = now.getDate()
      // 特殊日期：元旦(1/1)、春节(2/1前后)、情人节(2/14)、劳动节(5/1)、国庆(10/1)、圣诞(12/25)
      var specialDays = [
        [1, 1], [2, 14], [5, 1], [10, 1], [12, 25]
      ]
      var isSpecial = specialDays.some(function(d) { return d[0] === month && d[1] === day })
      if (isSpecial) {
        this.active = true
        var self = this
        this.$nextTick(function() {
          self.launch()
          // 5秒后停止
          setTimeout(function() { self.active = false }, 5000)
        })
      }
    },
    launch: function() {
      var self = this
      self.particles = []
      if (self.animId) {
        cancelAnimationFrame(self.animId)
        self.animId = null
      }
      var canvas = this.$refs.canvas
      if (!canvas) return
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      var ctx = canvas.getContext('2d')
      var colors = ['#f43f5e', '#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#6366f1']

      // 生成粒子
      for (var i = 0; i < 150; i++) {
        this.particles.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height - canvas.height,
          w: 8 + Math.random() * 8,
          h: 4 + Math.random() * 4,
          color: colors[Math.floor(Math.random() * colors.length)],
          speedY: 2 + Math.random() * 4,
          speedX: (Math.random() - 0.5) * 2,
          rotation: Math.random() * Math.PI * 2,
          rotSpeed: (Math.random() - 0.5) * 0.1,
          opacity: 1
        })
      }
      this.animate(ctx, canvas)
    },
    animate: function(ctx, canvas) {
      var self = this
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      var alive = false
      self.particles.forEach(function(p) {
        p.y += p.speedY
        p.x += p.speedX
        p.rotation += p.rotSpeed
        if (p.y < canvas.height + 20) alive = true
        ctx.save()
        ctx.translate(p.x, p.y)
        ctx.rotate(p.rotation)
        ctx.globalAlpha = Math.max(0, 1 - p.y / canvas.height)
        ctx.fillStyle = p.color
        ctx.fillRect(-p.w / 2, -p.h / 2, p.w, p.h)
        ctx.restore()
      })
      if (alive) {
        self.animId = requestAnimationFrame(function() { self.animate(ctx, canvas) })
      }
    }
  }
}
</script>
