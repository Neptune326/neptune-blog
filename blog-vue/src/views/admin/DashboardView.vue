<template>
  <div>
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">数据概览</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">博客运营数据一览</p>
    </div>

    <!-- 统计卡片 -->
    <v-row>
      <v-col
        v-for="stat in stats"
        :key="stat.label"
        cols="12" sm="6" md="4"
      >
        <v-card
          elevation="0"
          rounded="xl"
          style="border: 1px solid #e8eaed; overflow: hidden;"
        >
          <div class="pa-5">
            <div class="d-flex align-center justify-space-between mb-4">
              <div
                :style="{
                  width: '44px',
                  height: '44px',
                  background: stat.bgColor,
                  borderRadius: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }"
              >
                <v-icon :color="stat.color" size="22">{{ stat.icon }}</v-icon>
              </div>
              <v-chip
                v-if="stat.badge"
                :color="stat.badgeColor"
                size="x-small"
                variant="tonal"
              >
                {{ stat.badge }}
              </v-chip>
            </div>

            <div style="font-size: 32px; font-weight: 700; color: #202124; line-height: 1; margin-bottom: 6px;">
              {{ stat.value }}
            </div>
            <div style="font-size: 13px; color: #80868b; font-weight: 500;">
              {{ stat.label }}
            </div>
          </div>

          <!-- 底部色条 -->
          <div :style="{ height: '3px', background: stat.color, opacity: 0.6 }"></div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 快捷操作 -->
    <div class="mt-8 mb-4">
      <h2 style="font-size: 16px; font-weight: 600; color: #202124; margin: 0 0 16px;">快捷操作</h2>
      <div class="d-flex flex-wrap" style="gap: 12px;">
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          to="/admin/articles/edit"
          style="font-weight: 500;"
        >
          写新文章
        </v-btn>
        <v-btn
          variant="outlined"
          color="primary"
          prepend-icon="mdi-comment-check-outline"
          to="/admin/comments"
          style="font-weight: 500;"
        >
          审核评论
          <v-badge
            v-if="pendingCount > 0"
            :content="pendingCount"
            color="error"
            inline
            class="ml-1"
          />
        </v-btn>
        <v-btn
          variant="outlined"
          prepend-icon="mdi-folder-plus-outline"
          to="/admin/categories"
          style="font-weight: 500; color: #5f6368;"
        >
          管理分类
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script>
import request from '../../api/request.js'

export default {
  name: 'DashboardView',
  data: function() {
    return {
      pendingCount: 0,
      stats: [
        {
          label: '文章总数',
          icon: 'mdi-file-document-outline',
          color: '#1a73e8',
          bgColor: '#e8f0fe',
          value: 0
        },
        {
          label: '分类总数',
          icon: 'mdi-folder-outline',
          color: '#34a853',
          bgColor: '#e6f4ea',
          value: 0
        },
        {
          label: '标签总数',
          icon: 'mdi-tag-outline',
          color: '#f29900',
          bgColor: '#fef7e0',
          value: 0
        },
        {
          label: '评论总数',
          icon: 'mdi-comment-outline',
          color: '#9334e6',
          bgColor: '#f3e8fd',
          value: 0
        },
        {
          label: '待审核评论',
          icon: 'mdi-comment-alert-outline',
          color: '#ea4335',
          bgColor: '#fce8e6',
          value: 0,
          badge: '待处理',
          badgeColor: 'error'
        }
      ]
    }
  },
  mounted: function() {
    this.loadDashboard()
  },
  methods: {
    loadDashboard: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/dashboard' })
        .then(function(data) {
          self.stats[0].value = data.articleCount || 0
          self.stats[1].value = data.categoryCount || 0
          self.stats[2].value = data.tagCount || 0
          self.stats[3].value = data.commentCount || 0
          self.stats[4].value = data.pendingCommentCount || 0
          self.pendingCount = data.pendingCommentCount || 0
        })
        .catch(function(err) {
          console.error('加载仪表盘失败:', err)
        })
    }
  }
}
</script>
