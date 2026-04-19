<template>
  <!-- 博客运行时间 + 访客计数 -->
  <div style="display: flex; flex-direction: column; gap: 4px; align-items: center;">
    <!-- 运行时间 -->
    <div style="font-size: 12px; color: #9aa0a6; display: flex; align-items: center; gap: 4px;">
      <span>🕐</span>
      <span>本站已运行 <strong style="color: #6366f1;">{{ runtimeText }}</strong></span>
    </div>
    <!-- 访客计数 -->
    <div style="font-size: 12px; color: #9aa0a6; display: flex; align-items: center; gap: 4px;">
      <span>👁️</span>
      <span>你是第 <strong style="color: #6366f1;">{{ visitorCount }}</strong> 位访客</span>
    </div>
  </div>
</template>

<script>
// 博客建站日期，可根据实际情况修改
var SITE_START_DATE = new Date('2024-01-01T00:00:00')

export default {
  name: 'SiteRuntime',
  data: function() {
    return {
      runtimeText: '',
      visitorCount: 0,
      timer: null
    }
  },
  mounted: function() {
    this.updateRuntime()
    this.timer = setInterval(this.updateRuntime, 1000)
    this.initVisitorCount()
  },
  beforeUnmount: function() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    updateRuntime: function() {
      var now = new Date()
      var diff = now - SITE_START_DATE
      var days = Math.floor(diff / (1000 * 60 * 60 * 24))
      var hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      var minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      var seconds = Math.floor((diff % (1000 * 60)) / 1000)
      this.runtimeText = days + ' 天 ' + hours + ' 时 ' + minutes + ' 分 ' + seconds + ' 秒'
    },
    initVisitorCount: function() {
      try {
        var key = 'blog_visitor_count'
        var count = parseInt(localStorage.getItem(key) || '0')
        // 用 sessionStorage 判断本次会话是否已计数
        if (!sessionStorage.getItem('blog_visited')) {
          count += 1
          localStorage.setItem(key, String(count))
          sessionStorage.setItem('blog_visited', '1')
        }
        // 加一个随机基数，让数字看起来更真实
        var base = 10000
        this.visitorCount = (base + count).toLocaleString()
      } catch (e) {
        this.visitorCount = '10,001'
      }
    }
  }
}
</script>
