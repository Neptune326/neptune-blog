<template>
  <!-- 深色/浅色模式切换按钮 -->
  <button
    @click="toggle"
    :title="isDark ? '切换到浅色模式' : '切换到深色模式'"
    style="
      background: none;
      border: 1px solid #e8eaed;
      border-radius: 20px;
      padding: 5px 10px;
      cursor: pointer;
      display: flex; align-items: center; gap: 5px;
      font-size: 13px;
      color: #5f6368;
      transition: all 0.2s;
    "
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
      this.theme.global.name.value = this.isDark ? 'googleLight' : 'googleDark'
      // 持久化到 localStorage
      localStorage.setItem('blog-theme', this.theme.global.name.value)
    }
  },
  mounted: function() {
    // 恢复上次的主题设置
    var saved = localStorage.getItem('blog-theme')
    if (saved) {
      this.theme.global.name.value = saved
    }
  }
}
</script>

<style scoped>
.dark-toggle-btn:hover {
  background: #f1f3f4;
  border-color: #dadce0;
}
</style>
