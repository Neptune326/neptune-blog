<template>
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
        width: p.size + 'px',
        height: p.size + 'px',
        borderRadius: '50%',
        background: p.color,
        transform: 'translate(-50%, -50%)',
        animation: 'color-click-float ' + p.duration + 'ms ease-out forwards'
      }"
    />
  </teleport>
</template>

<script>
var COLORS = ['#f43f5e', '#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#6366f1', '#ef4444', '#22c55e']
var idCounter = 0

export default {
  name: 'ColorClickEffect',
  data: function() {
    return { particles: [] }
  },
  mounted: function() {
    document.addEventListener('click', this.onDocClick)
  },
  beforeUnmount: function() {
    document.removeEventListener('click', this.onDocClick)
  },
  methods: {
    onDocClick: function(e) {
      if (window.location.pathname.startsWith('/admin')) return
      var self = this
      var count = 6 + Math.floor(Math.random() * 5)
      for (var i = 0; i < count; i++) {
        var id = ++idCounter
        var angle = (Math.PI * 2 / count) * i + Math.random() * 0.5
        var dist = 20 + Math.random() * 40
        var p = {
          id: id,
          x: e.clientX + Math.cos(angle) * dist * 0.3,
          y: e.clientY + Math.sin(angle) * dist * 0.3,
          size: 6 + Math.random() * 8,
          color: COLORS[Math.floor(Math.random() * COLORS.length)],
          duration: 600 + Math.floor(Math.random() * 400)
        }
        self.particles.push(p)
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
@keyframes color-click-float {
  0%   { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0; transform: translate(-50%, calc(-50% - 60px)) scale(0.3); }
}
</style>
