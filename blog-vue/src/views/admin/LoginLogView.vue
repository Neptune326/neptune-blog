<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">登录日志</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">记录所有登录尝试（成功与失败）</p>
      </div>
      <v-btn
        variant="outlined"
        color="error"
        size="small"
        prepend-icon="mdi-delete-sweep-outline"
        @click="confirmClear"
      >清空日志</v-btn>
    </div>

    <!-- 筛选 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
      <div class="pa-4">
        <v-row dense align="center">
          <v-col cols="12" sm="4">
            <v-text-field
              v-model="filters.username"
              label="用户名"
              variant="outlined"
              density="compact"
              clearable
              hide-details
              prepend-inner-icon="mdi-account-outline"
              @keyup.enter="loadLogs"
            />
          </v-col>
          <v-col cols="12" sm="3">
            <v-select
              v-model="filters.status"
              :items="statusOptions"
              item-title="label"
              item-value="value"
              label="登录状态"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
          <v-col cols="12" sm="5">
            <v-btn color="primary" @click="loadLogs" prepend-icon="mdi-magnify">查询</v-btn>
            <v-btn variant="text" @click="resetFilters" class="ml-2">重置</v-btn>
          </v-col>
        </v-row>
      </div>
    </v-card>

    <!-- 日志列表 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
          <tr style="background: #f8f9fa;">
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">用户名</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">登录 IP</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">状态</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">失败原因</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">浏览器</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">登录时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center pa-6">
              <v-progress-circular indeterminate color="primary" size="28" />
            </td>
          </tr>
          <tr v-else-if="logs.length === 0">
            <td colspan="6" class="text-center pa-6" style="color: #80868b;">暂无登录日志</td>
          </tr>
          <tr v-for="log in logs" :key="log.id" style="border-bottom: 1px solid #f1f3f4;">
            <td style="font-size: 13px; font-weight: 500;">{{ log.username }}</td>
            <td style="font-size: 12px; color: #80868b; font-family: monospace;">{{ log.loginIp }}</td>
            <td>
              <v-chip
                size="x-small"
                :color="log.status === 1 ? 'success' : 'error'"
                variant="tonal"
                :prepend-icon="log.status === 1 ? 'mdi-check-circle-outline' : 'mdi-close-circle-outline'"
              >{{ log.status === 1 ? '成功' : '失败' }}</v-chip>
            </td>
            <td style="font-size: 12px; color: #ea4335;">{{ log.failReason || '-' }}</td>
            <td style="font-size: 12px; color: #80868b; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {{ parseUA(log.userAgent) }}
            </td>
            <td style="font-size: 12px; color: #80868b;">{{ formatTime(log.loginTime) }}</td>
          </tr>
        </tbody>
      </v-table>

      <div class="d-flex justify-center pa-4">
        <v-pagination
          v-model="pagination.page"
          :length="pagination.totalPages"
          :total-visible="7"
          @update:model-value="loadLogs"
        />
      </div>
    </v-card>

    <!-- 清空确认 -->
    <v-dialog v-model="clearDialog" max-width="380">
      <v-card rounded="xl">
        <div class="pa-5">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 8px;">确认清空</div>
          <div style="font-size: 14px; color: #5f6368;">确定要清空所有登录日志吗？此操作不可撤销。</div>
          <div class="d-flex justify-end mt-4" style="gap: 8px;">
            <v-btn variant="text" @click="clearDialog = false">取消</v-btn>
            <v-btn color="error" :loading="clearing" @click="doClear">确认清空</v-btn>
          </div>
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { getLoginLogs, clearLoginLogs } from '../../api/sysConfig.js'

export default {
  name: 'LoginLogView',
  data: function() {
    return {
      loading: false,
      clearing: false,
      clearDialog: false,
      logs: [],
      filters: { username: '', status: null },
      statusOptions: [
        { label: '全部', value: null },
        { label: '成功', value: 1 },
        { label: '失败', value: 0 }
      ],
      pagination: { page: 1, pageSize: 20, totalPages: 1 }
    }
  },
  mounted: function() {
    this.loadLogs()
  },
  methods: {
    loadLogs: function() {
      var self = this
      self.loading = true
      var params = { pageNum: self.pagination.page, pageSize: self.pagination.pageSize }
      if (self.filters.username) params.username = self.filters.username
      if (self.filters.status !== null && self.filters.status !== undefined) {
        params.status = self.filters.status
      }
      getLoginLogs(params)
        .then(function(data) {
          self.logs = data.list || []
          self.pagination.totalPages = data.pages || 1
        })
        .catch(function(err) { console.error('加载登录日志失败:', err) })
        .finally(function() { self.loading = false })
    },
    resetFilters: function() {
      this.filters = { username: '', status: null }
      this.pagination.page = 1
      this.loadLogs()
    },
    confirmClear: function() { this.clearDialog = true },
    doClear: function() {
      var self = this
      self.clearing = true
      clearLoginLogs()
        .then(function() {
          self.clearDialog = false
          self.loadLogs()
        })
        .catch(function(err) { console.error('清空失败:', err) })
        .finally(function() { self.clearing = false })
    },
    // 简单解析 UA，提取浏览器名称
    parseUA: function(ua) {
      if (!ua) return '-'
      if (ua.indexOf('Chrome') > -1) return 'Chrome'
      if (ua.indexOf('Firefox') > -1) return 'Firefox'
      if (ua.indexOf('Safari') > -1) return 'Safari'
      if (ua.indexOf('Edge') > -1) return 'Edge'
      return ua.substring(0, 30)
    },
    formatTime: function(t) {
      if (!t) return '-'
      return String(t).substring(0, 19).replace('T', ' ')
    }
  }
}
</script>
