<template>
  <!-- 樱花/雪花/星星飘落 canvas，全屏覆盖，pointer-events: none -->
  <canvas
    ref="canvas"
    style="
      position: fixed;
      top: 0; left: 0;
      width: 100vw; height: 100vh;
      pointer-events: none;
      z-index: 1;
    "
  />
</template>

<script>
export default {
  name: 'ParticleCanvas',
  props: {
    // 粒子类型：sakura（樱花）| snow（雪花）| star（星星）
    type: { type: String, default: 'sakura' },
    count: { type: Number, default: 30 }
  },
  data: function() {
    return {
      particles: [],
      animId: null,
      ctx: null,
      W: 0,
      H: 0
    }
  },
  mounted: function() {
    this.init()
    window.addEventListener('resize', this.onResize)
  },
  beforeUnmount: function() {
    if (this.animId) cancelAnimationFrame(this.animId)
    window.removeEventListener('resize', this.onResize)
  },
  methods: {
    init: function() {
      var canvas = this.$refs.canvas
      this.ctx = canvas.getContext('2d')
      this.onResize()
      this.createParticles()
      this.animate()
    },
    onResize: function() {
      var canvas = this.$refs.canvas
      this.W = canvas.width = window.innerWidth
      this.H = canvas.height = window.innerHeight
    },
    createParticles: function() {
      this.particles = []
      for (var i = 0; i < this.count; i++) {
        this.particles.push(this.newParticle(true))
      }
    },
    newParticle: function(randomY) {
      var type = this.type
      var p = {
        x: Math.random() * this.W,
        y: randomY ? Math.random() * this.H : -20,
        size: type === 'snow' ? 3 + Math.random() * 5 : 6 + Math.random() * 10,
        speedY: 0.5 + Math.random() * 1.5,
        speedX: (Math.random() - 0.5) * 0.8,
        rotation: Math.random() * Math.PI * 2,
        rotSpeed: (Math.random() - 0.5) * 0.04,
        opacity: 0.5 + Math.random() * 0.5,
        swing: Math.random() * Math.PI * 2,
        swingSpeed: 0.01 + Math.random() * 0.02,
        swingAmp: 1 + Math.random() * 2
      }
      return p
    },
    animate: function() {
      var self = this
      var ctx = self.ctx
      ctx.clearRect(0, 0, self.W, self.H)

      self.particles.forEach(function(p) {
        p.swing += p.swingSpeed
        p.x += p.speedX + Math.sin(p.swing) * p.swingAmp
        p.y += p.speedY
        p.rotation += p.rotSpeed

        if (p.y > self.H + 20) {
          // 重置到顶部
          var np = self.newParticle(false)
          p.x = np.x; p.y = np.y; p.size = np.size
          p.speedY = np.speedY; p.speedX = np.speedX
          p.rotation = np.rotation; p.opacity = np.opacity
          p.swing = np.swing
        }

        ctx.save()
        ctx.globalAlpha = p.opacity
        ctx.translate(p.x, p.y)
        ctx.rotate(p.rotation)

        if (self.type === 'sakura') {
          self.drawSakura(ctx, p.size)
        } else if (self.type === 'snow') {
          self.drawSnow(ctx, p.size)
        } else {
          self.drawStar(ctx, p.size)
        }

        ctx.restore()
      })

      self.animId = requestAnimationFrame(self.animate.bind(self))
    },
    drawSakura: function(ctx, size) {
      // 五瓣樱花
      ctx.fillStyle = '#ffb7c5'
      for (var i = 0; i < 5; i++) {
        ctx.save()
        ctx.rotate((i * Math.PI * 2) / 5)
        ctx.beginPath()
        ctx.ellipse(0, -size * 0.5, size * 0.3, size * 0.5, 0, 0, Math.PI * 2)
        ctx.fill()
        ctx.restore()
      }
      // 花心
      ctx.fillStyle = '#ff8fab'
      ctx.beginPath()
      ctx.arc(0, 0, size * 0.2, 0, Math.PI * 2)
      ctx.fill()
    },
    drawSnow: function(ctx, size) {
      ctx.strokeStyle = 'rgba(200, 230, 255, 0.9)'
      ctx.lineWidth = 1.5
      for (var i = 0; i < 6; i++) {
        ctx.save()
        ctx.rotate((i * Math.PI) / 3)
        ctx.beginPath()
        ctx.moveTo(0, 0)
        ctx.lineTo(0, -size)
        // 小分支
        ctx.moveTo(0, -size * 0.4)
        ctx.lineTo(size * 0.25, -size * 0.65)
        ctx.moveTo(0, -size * 0.4)
        ctx.lineTo(-size * 0.25, -size * 0.65)
        ctx.stroke()
        ctx.restore()
      }
    },
    drawStar: function(ctx, size) {
      ctx.fillStyle = 'rgba(255, 220, 80, 0.85)'
      ctx.beginPath()
      for (var i = 0; i < 5; i++) {
        var angle = (i * Math.PI * 2) / 5 - Math.PI / 2
        var innerAngle = angle + Math.PI / 5
        if (i === 0) ctx.moveTo(Math.cos(angle) * size, Math.sin(angle) * size)
        else ctx.lineTo(Math.cos(angle) * size, Math.sin(angle) * size)
        ctx.lineTo(Math.cos(innerAngle) * size * 0.4, Math.sin(innerAngle) * size * 0.4)
      }
      ctx.closePath()
      ctx.fill()
    }
  }
}
</script>
