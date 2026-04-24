<template>
  <!-- 深色/浅色模式切换按钮 -->
  <button
    type="button"
    @click="toggle"
    :title="isDark ? '切换到浅色模式' : '切换到深色模式'"
    class="dark-toggle-btn"
  >
    <span style="font-size: 15px;">{{ isDark ? '☀️' : '🌙' }}</span>
    <span class="d-none d-md-inline">{{ isDark ? '浅色' : '深色' }}</span>
  </button>
</template>

<script>
import { useTheme } from 'vuetify'

export default {
  name: 'DarkModeToggle',
  setup: function() {
    var theme = useTheme()
    return { theme }
  },
  computed: {
    isDark: function() {
      return this.theme.global.current.value.dark
    }
  },
  methods: {
    toggle: function() {
      // 必须在改 theme 之前算好目标态，否则 isDark 会立即变化导致 body 类错位
      var toDark = !this.isDark
      this.theme.global.name.value = toDark ? 'googleDark' : 'googleLight'
      localStorage.setItem('blog-theme', this.theme.global.name.value)
      document.body.classList.toggle('dark-mode', toDark)
    }
  },
  mounted: function() {
    // 与 main.js 启动时一致；此处再同步一次，防止热更新或仅挂载子树时失步
    var saved = localStorage.getItem('blog-theme')
    if (saved === 'googleDark' || saved === 'googleLight') {
      this.theme.global.name.value = saved
    }
    document.body.classList.toggle('dark-mode', this.theme.global.name.value === 'googleDark')
  }
}
</script>

<style scoped>
.dark-toggle-btn {
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-color, #e8eaed);
  border-radius: 20px;
  padding: 5px 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--text-secondary, #5f6368);
  transition: background 0.2s, border-color 0.2s, color 0.2s;
}
.dark-toggle-btn:hover {
  background: var(--bg-hover, #f1f3f4);
  border-color: var(--border-color);
  color: var(--text-primary);
}
</style>
<style>
/* 深色系下开关按钮不刺眼 */
body.dark-mode .dark-toggle-btn {
  background: var(--bg-hover) !important;
  border-color: var(--border-color) !important;
  color: var(--text-secondary) !important;
}
body.dark-mode .dark-toggle-btn:hover {
  background: #3c3f45 !important;
  color: var(--text-primary) !important;
}
</style>
