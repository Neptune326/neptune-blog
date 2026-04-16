<template>
  <div>
    <!-- 欢迎语 -->
    <div class="mb-6 d-flex align-center justify-space-between flex-wrap" style="gap: 12px;">
      <div>
        <h1 style="font-size: 22px; font-weight: 700; color: #202124; margin: 0 0 4px;">
          👋 欢迎回来，{{ adminName }}
        </h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">{{ currentDate }} · 博客运营数据一览</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/admin/articles/edit" style="font-weight: 500;">
        写新文章
      </v-btn>
    </div>

    <!-- 统计卡片 -->
    <v-row class="mb-2">
      <v-col
        v-for="stat in stats"
        :key="stat.label"
        cols="6" sm="4" md="2"
      >
        <v-card
          elevation="0"
          rounded="xl"
          style="border: 1px solid #e8eaed; overflow: hidden; cursor: default;"
          :to="stat.link || undefined"
        >
          <div class="pa-4">
            <div
              :style="{
                width: '40px', height: '40px',
                background: stat.bgColor,
                borderRadius: '10px',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                marginBottom: '12px'
              }"
            >
              <v-icon :color="stat.color" size="20">{{ stat.icon }}</v-icon>
            </div>
            <div style="font-size: 28px; font-weight: 800; color: #202124; line-height: 1; margin-bottom: 4px;">
              {{ stat.displayValue }}
            </div>
            <div style="font-size: 12px; color: #80868b; font-weight: 500;">{{ stat.label }}</div>
          </div>
          <div :style="{ height: '3px', background: stat.color, opacity: 0.5 }"></div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 待处理提醒 -->
    <v-alert
      v-if="pendingCount > 0"
      type="warning"
      variant="tonal"
      rounded="xl"
      class="mb-6"
      density="compact"
      :text="'有 ' + pendingCount + ' 条评论待审核'"
      prepend-icon="mdi-bell-outline"
    >
      <template #append>
        <v-btn size="small" variant="text" color="warning" to="/admin/comments">去处理</v-btn>
      </template>
    </v-alert>

    <v-row>
      <!-- 近 7 天访问趋势 -->
      <v-col cols="12" md="8">
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
          <div class="pa-5">
            <div class="d-flex align-center justify-space-between mb-4">
              <div>
                <div style="font-size: 15px; font-weight: 600; color: #202124;">访问趋势</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">近 7 天页面浏览量</div>
              </div>
              <div style="font-size: 13px; color: #80868b;">
                总计 <strong style="color: #1a73e8;">{{ totalPv }}</strong> PV
              </div>
            </div>

            <div v-if="visitStats.length === 0" style="text-align: center; padding: 32px; color: #9aa0a6; font-size: 13px;">
              暂无访问数据
            </div>

            <div v-else style="display: flex; align-items: flex-end; gap: 6px; height: 100px; padding: 0 4px;">
              <div
                v-for="stat in visitStats"
                :key="stat.visitDate"
                style="flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px;"
              >
                <div style="font-size: 10px; color: #80868b; font-weight: 500;">{{ stat.pv || 0 }}</div>
                <div
                  :style="{
                    width: '100%',
                    background: 'linear-gradient(180deg, #1a73e8, #4285f4)',
                    borderRadius: '4px 4px 0 0',
                    minHeight: '4px',
                    height: Math.max(4, ((stat.pv || 0) / maxPv) * 72) + 'px',
                    transition: 'height 0.5s ease'
                  }"
                ></div>
                <div style="font-size: 10px; color: #9aa0a6; white-space: nowrap;">
                  {{ stat.visitDate ? String(stat.visitDate).substring(5) : '' }}
                </div>
              </div>
            </div>
          </div>
        </v-card>
      </v-col>

      <!-- 快捷操作 -->
      <v-col cols="12" md="4">
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; height: 100%;">
          <div class="pa-5">
            <div style="font-size: 15px; font-weight: 600; color: #202124; margin-bottom: 16px;">快捷操作</div>
            <div style="display: flex; flex-direction: column; gap: 8px;">
              <v-btn
                v-for="action in quickActions"
                :key="action.label"
                :to="action.to"
                :color="action.color || 'default'"
                :variant="action.variant || 'tonal'"
                :prepend-icon="action.icon"
                block
                style="justify-content: flex-start; font-weight: 500; font-size: 13px;"
              >
                {{ action.label }}
                <v-badge
                  v-if="action.badge"
                  :content="action.badge"
                  color="error"
                  inline
                  class="ml-auto"
                />
              </v-btn>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import request from '../../api/request.js'
import { useAuthStore } from '../../store/auth.js'

export default {
  name: 'DashboardView',
  setup: function() {
    var authStore = useAuthStore()
    return { authStore }
  },
  data: function() {
    return {
      pendingCount: 0,
      visitStats: [],
      stats: [
        { label: '文章', icon: 'mdi-file-document-outline', color: '#1a73e8', bgColor: '#e8f0fe', value: 0, displayValue: 0 },
        { label: '分类', icon: 'mdi-folder-outline', color: '#34a853', bgColor: '#e6f4ea', value: 0, displayValue: 0 },
        { label: '标签', icon: 'mdi-tag-outline', color: '#f29900', bgColor: '#fef7e0', value: 0, displayValue: 0 },
        { label: '评论', icon: 'mdi-comment-outline', color: '#9334e6', bgColor: '#f3e8fd', value: 0, displayValue: 0, link: '/admin/comments' },
        { label: '待审核', icon: 'mdi-comment-alert-outline', color: '#ea4335', bgColor: '#fce8e6', value: 0, displayValue: 0, link: '/admin/comments' },
        { label: '留言', icon: 'mdi-message-text-outline', color: '#00bcd4', bgColor: '#e0f7fa', value: 0, displayValue: 0, link: '/admin/messages' }
      ]
    }
  },
  computed: {
    adminName: function() {
      return this.authStore.token ? 'Admin' : '管理员'
    },
    currentDate: function() {
      var d = new Date()
      var days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return d.getFullYear() + ' 年 ' + (d.getMonth() + 1) + ' 月 ' + d.getDate() + ' 日 ' + days[d.getDay()]
    },
    maxPv: function() {
      if (!this.visitStats.length) return 1
      return Math.max.apply(null, this.visitStats.map(function(s) { return s.pv || 1 }))
    },
    totalPv: function() {
      return this.visitStats.reduce(function(sum, s) { return sum + (s.pv || 0) }, 0)
    },
    quickActions: function() {
      var self = this
      return [
        { label: '写新文章', icon: 'mdi-pencil-plus-outline', to: '/admin/articles/edit', color: 'primary', variant: 'flat' },
        { label: '审核评论', icon: 'mdi-comment-check-outline', to: '/admin/comments', color: 'default', variant: 'tonal', badge: self.pendingCount > 0 ? self.pendingCount : null },
        { label: '管理文章', icon: 'mdi-file-document-multiple-outline', to: '/admin/articles', color: 'default', variant: 'tonal' },
        { label: '留言管理', icon: 'mdi-message-text-outline', to: '/admin/messages', color: 'default', variant: 'tonal' },
        { label: '系统设置', icon: 'mdi-cog-outline', to: '/admin/sys-config', color: 'default', variant: 'tonal' }
      ]
    }
  },
  mounted: function() {
    this.loadDashboard()
    this.loadVisitStats()
  },
  methods: {
    loadDashboard: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/dashboard' })
        .then(function(data) {
          var values = [
            data.articleCount || 0,
            data.categoryCount || 0,
            data.tagCount || 0,
            data.commentCount || 0,
            data.pendingCommentCount || 0,
            0
          ]
          self.pendingCount = data.pendingCommentCount || 0
          // 数字滚动动画
          values.forEach(function(val, i) {
            self.stats[i].value = val
            self.animateNumber(i, val)
          })
        })
        .catch(function(err) {
          console.error('加载仪表盘失败:', err)
        })
    },
    loadVisitStats: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/stats/visit', params: { days: 7 } })
        .then(function(data) {
          self.visitStats = data || []
        })
        .catch(function() {})
    },
    // 数字滚动动画
    animateNumber: function(index, target) {
      var self = this
      var start = 0
      var duration = 800
      var startTime = null

      function step(currentTime) {
        if (!startTime) startTime = currentTime
        var elapsed = currentTime - startTime
        var progress = Math.min(elapsed / duration, 1)
        // easeOutCubic
        var ease = 1 - Math.pow(1 - progress, 3)
        self.stats[index].displayValue = Math.round(start + (target - start) * ease)
        if (progress < 1) {
          requestAnimationFrame(step)
        }
      }
      requestAnimationFrame(step)
    }
  }
}
</script>
