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
      <!-- 近 7 天访问趋势（ECharts 折线图） -->
      <v-col cols="12" md="8">
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
          <div class="pa-5">
            <div class="d-flex align-center justify-space-between mb-2">
              <div>
                <div style="font-size: 15px; font-weight: 600; color: #202124;">访问趋势</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">近 7 天 PV / UV</div>
              </div>
              <div style="font-size: 13px; color: #80868b;">
                总计 <strong style="color: #1a73e8;">{{ totalPv }}</strong> PV
              </div>
            </div>
            <div ref="visitChart" style="height: 200px; width: 100%;"></div>
          </div>
        </v-card>
      </v-col>

      <!-- 分类文章占比（ECharts 饼图） + 快捷操作 -->
      <v-col cols="12" md="4">
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
          <div class="pa-4">
            <div style="font-size: 14px; font-weight: 600; color: #202124; margin-bottom: 4px;">分类占比</div>
            <div ref="categoryChart" style="height: 160px; width: 100%;"></div>
          </div>
        </v-card>
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
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
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import request from '@/api/request.js'
import { useAuthStore } from '@/store/auth.js'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

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
      categoryStats: [],
      visitChartInstance: null,
      categoryChartInstance: null,
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
    this.loadCategoryStats()
  },
  beforeUnmount: function() {
    if (this.visitChartInstance) this.visitChartInstance.dispose()
    if (this.categoryChartInstance) this.categoryChartInstance.dispose()
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
          values.forEach(function(val, i) {
            self.stats[i].value = val
            self.animateNumber(i, val)
          })
        })
        .catch(function(err) { console.error('加载仪表盘失败:', err) })
    },
    loadVisitStats: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/stats/visit', params: { days: 7 } })
        .then(function(data) {
          self.visitStats = data || []
          self.$nextTick(function() { self.renderVisitChart() })
        })
        .catch(function() {})
    },
    loadCategoryStats: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/categories' })
        .then(function(data) {
          self.categoryStats = (data || []).map(function(c) {
            return { name: c.name, value: c.articleCount || 0 }
          }).filter(function(c) { return c.value > 0 })
          self.$nextTick(function() { self.renderCategoryChart() })
        })
        .catch(function() {})
    },
    renderVisitChart: function() {
      var el = this.$refs.visitChart
      if (!el || !this.visitStats.length) return
      if (this.visitChartInstance) this.visitChartInstance.dispose()
      var chart = echarts.init(el)
      this.visitChartInstance = chart
      var dates = this.visitStats.map(function(s) { return String(s.visitDate || '').substring(5) })
      var pvData = this.visitStats.map(function(s) { return s.pv || 0 })
      var uvData = this.visitStats.map(function(s) { return s.uv || 0 })
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
        legend: { data: ['PV', 'UV'], top: 0, right: 0, textStyle: { fontSize: 12 } },
        grid: { left: 40, right: 20, top: 30, bottom: 30 },
        xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 11 } },
        yAxis: { type: 'value', axisLabel: { fontSize: 11 }, minInterval: 1 },
        series: [
          {
            name: 'PV', type: 'line', data: pvData, smooth: true,
            lineStyle: { color: '#1a73e8', width: 2 },
            areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(26,115,232,0.2)' }, { offset: 1, color: 'rgba(26,115,232,0)' }] } },
            itemStyle: { color: '#1a73e8' }, symbol: 'circle', symbolSize: 6
          },
          {
            name: 'UV', type: 'line', data: uvData, smooth: true,
            lineStyle: { color: '#34a853', width: 2 },
            areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(52,168,83,0.15)' }, { offset: 1, color: 'rgba(52,168,83,0)' }] } },
            itemStyle: { color: '#34a853' }, symbol: 'circle', symbolSize: 6
          }
        ]
      })
    },
    renderCategoryChart: function() {
      var el = this.$refs.categoryChart
      if (!el || !this.categoryStats.length) return
      if (this.categoryChartInstance) this.categoryChartInstance.dispose()
      var chart = echarts.init(el)
      this.categoryChartInstance = chart
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 篇 ({d}%)' },
        legend: { orient: 'vertical', right: 0, top: 'center', textStyle: { fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
        series: [{
          type: 'pie', radius: ['40%', '70%'], center: ['35%', '50%'],
          data: this.categoryStats,
          label: { show: false },
          emphasis: { itemStyle: { shadowBlur: 8, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } }
        }]
      })
    },
    animateNumber: function(index, target) {
      var self = this
      var start = 0
      var duration = 800
      var startTime = null
      function step(currentTime) {
        if (!startTime) startTime = currentTime
        var elapsed = currentTime - startTime
        var progress = Math.min(elapsed / duration, 1)
        var ease = 1 - Math.pow(1 - progress, 3)
        self.stats[index].displayValue = Math.round(start + (target - start) * ease)
        if (progress < 1) requestAnimationFrame(step)
      }
      requestAnimationFrame(step)
    }
  }
}
</script>


