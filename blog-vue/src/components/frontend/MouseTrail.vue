<template>
  <!-- 鼠标轨迹特效：全局 canvas，pointer-events: none -->
  <canvas
    ref="canvas"
    style="position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; pointer-events: none; z-index: 99990;"
  />
</template>

<script>
export default {
  name: 'MouseTrail',
  data: function() {
    return {
      ctx: null,
      points: [],
      animId: null,
      mouse: { x: 0, y: 0 }
    }
  },
  mounted: function() {
    var canvas = this.$refs.canvas
    this.ctx = canvas.getContext('2d')
    this.resize()
    window.addEventListener('resize', this.resize)
    window.addEventListener('mousemove', this.onMove)
    this.animate()
  },
  beforeUnmount: function() {
    window.removeEventListener('resize', this.resize)
    window.removeEventListener('mousemove', this.onMove)
    if (this.animId) cancelAnimationFrame(this.animId)
  },
  methods: {
    resize: function() {
      var canvas = this.$refs.canvas
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
    },
    onMove: function(e) {
      // 后台页面不显示
      if (window.location.pathname.startsWith('/admin')) return
      this.points.push({
        x: e.clientX,
        y: e.clientY,
        life: 1.0,
        hue: Math.floor(Math.random() * 360)
      })
      if (this.points.length > 40) this.points.shift()
    },
    animate: function() {
      var self = this
      var ctx = self.ctx
      var canvas = self.$refs.canvas
      if (!canvas) return
      ctx.clearRect(0, 0, canvas.width, canvas.height)

      for (var i = self.points.length - 1; i >= 0; i--) {
        var p = self.points[i]
        p.life -= 0.04
        if (p.life <= 0) {
          self.points.splice(i, 1)
          continue
        }
        var radius = p.life * 6
        ctx.beginPath()
        ctx.arc(p.x, p.y, radius, 0, Math.PI * 2)
        ctx.fillStyle = 'hsla(' + p.hue + ', 80%, 60%, ' + p.life * 0.6 + ')'
        ctx.fill()
      }

      self.animId = requestAnimationFrame(self.animate.bind(self))
    }
  }
}
</script>
