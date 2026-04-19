<template>
  <teleport to="body">
    <transition-group name="toast-slide" tag="div" style="position: fixed; top: 20px; left: 50%; transform: translateX(-50%); z-index: 99999; display: flex; flex-direction: column; align-items: center; gap: 8px; pointer-events: none;">
      <div
        v-for="item in toasts"
        :key="item.id"
        :style="{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '10px 20px',
          borderRadius: '8px',
          fontSize: '14px',
          fontWeight: '500',
          boxShadow: '0 4px 16px rgba(0,0,0,0.12)',
          pointerEvents: 'auto',
          minWidth: '200px',
          maxWidth: '420px',
          background: item.type === 'success' ? '#f0fdf4' :
                      item.type === 'error'   ? '#fef2f2' :
                      item.type === 'warning' ? '#fffbeb' : '#eff6ff',
          border: '1px solid ' + (
                      item.type === 'success' ? '#bbf7d0' :
                      item.type === 'error'   ? '#fecaca' :
                      item.type === 'warning' ? '#fde68a' : '#bfdbfe'),
          color: item.type === 'success' ? '#15803d' :
                 item.type === 'error'   ? '#dc2626' :
                 item.type === 'warning' ? '#d97706' : '#1d4ed8'
        }"
      >
        <!-- 图标 -->
        <svg v-if="item.type === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" style="flex-shrink:0"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>
        <svg v-else-if="item.type === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" style="flex-shrink:0"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
        <svg v-else-if="item.type === 'warning'" width="16" height="16" viewBox="0 0 24 24" fill="currentColor" style="flex-shrink:0"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
        <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="currentColor" style="flex-shrink:0"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg>
        <span style="flex:1; line-height:1.5;">{{ item.message }}</span>
      </div>
    </transition-group>
  </teleport>
</template>

<script>
export default {
  name: 'Toast',
  data: function() {
    return { toasts: [] }
  },
  methods: {
    show: function(message, type, duration) {
      var self = this
      type = type || 'info'
      duration = duration !== undefined ? duration : 3000
      var id = Date.now() + Math.random()
      self.toasts.push({ id: id, message: message, type: type })
      if (duration > 0) {
        setTimeout(function() {
          var idx = self.toasts.findIndex(function(t) { return t.id === id })
          if (idx >= 0) self.toasts.splice(idx, 1)
        }, duration)
      }
    },
    success: function(msg, duration) { this.show(msg, 'success', duration) },
    error:   function(msg, duration) { this.show(msg, 'error',   duration) },
    warning: function(msg, duration) { this.show(msg, 'warning', duration) },
    info:    function(msg, duration) { this.show(msg, 'info',    duration) }
  }
}
</script>

<style>
.toast-slide-enter-active { transition: all 0.25s ease; }
.toast-slide-leave-active { transition: all 0.2s ease; }
.toast-slide-enter-from   { opacity: 0; transform: translateY(-12px); }
.toast-slide-leave-to     { opacity: 0; transform: translateY(-8px); }
</style>
