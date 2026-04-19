<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">操作日志</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">记录管理员的所有写操作</p>
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
              v-model="filters.operator"
              label="操作人"
              variant="outlined"
              density="compact"
              clearable
              hide-details
              prepend-inner-icon="mdi-account-search-outline"
              @keyup.enter="loadLogs"
            />
          </v-col>
          <v-col cols="12" sm="4">
            <v-text-field
              v-model="filters.module"
              label="操作模块"
              variant="outlined"
              density="compact"
              clearable
              hide-details
              prepend-inner-icon="mdi-view-module-outline"
              @keyup.enter="loadLogs"
            />
          </v-col>
          <v-col cols="12" sm="4">
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
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">操作人</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">模块</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">操作</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">请求方法</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">IP</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">耗时</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">状态</th>
            <th style="font-size: 12px; color: #80868b; font-weight: 600;">时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="8" class="text-center pa-6">
              <v-progress-circular indeterminate color="primary" size="28" />
            </td>
          </tr>
          <tr v-else-if="logs.length === 0">
            <td colspan="8" class="text-center pa-6" style="color: #80868b;">暂无操作日志</td>
          </tr>
          <tr v-for="log in logs" :key="log.id" style="border-bottom: 1px solid #f1f3f4;">
            <td style="font-size: 13px;">{{ log.operator || '-' }}</td>
            <td>
              <v-chip size="x-small" color="primary" variant="tonal">{{ log.module || '-' }}</v-chip>
            </td>
            <td style="font-size: 13px; color: #3c4043;">{{ log.action || '-' }}</td>
            <td>
              <v-chip
                size="x-small"
                :color="methodColor(log.method)"
                variant="tonal"
              >{{ log.method }}</v-chip>
            </td>
            <td style="font-size: 12px; color: #80868b; font-family: monospace;">{{ log.requestIp }}</td>
            <td style="font-size: 12px; color: #80868b;">{{ log.costTime }}ms</td>
            <td>
              <v-chip
                size="x-small"
                :color="log.status === 1 ? 'success' : 'error'"
                variant="tonal"
              >{{ log.status === 1 ? '成功' : '失败' }}</v-chip>
            </td>
            <td style="font-size: 12px; color: #80868b;">{{ formatTime(log.createTime) }}</td>
          </tr>
        </tbody>
      </v-table>

      <!-- 分页 -->
      <TablePagination
        :total="pagination.total"
        :page="pagination.page"
        :page-size="pagination.pageSize"
        @change="pagination.page = $event; loadLogs()"
      />
    </v-card>

    <!-- 清空确认 -->
    <v-dialog v-model="clearDialog" max-width="380">
      <v-card rounded="xl">
        <div class="pa-5">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 8px;">确认清空</div>
          <div style="font-size: 14px; color: #5f6368;">确定要清空所有操作日志吗？此操作不可撤销。</div>
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
import { getOperationLogs, clearOperationLogs } from '../../api/sysConfig.js'
import TablePagination from '../../components/admin/TablePagination.vue'

export default {
  name: 'OperationLogView',
  components: { TablePagination },
  data: function() {
    return {
      loading: false,
      clearing: false,
      clearDialog: false,
      logs: [],
      filters: { operator: '', module: '' },
      pagination: { page: 1, pageSize: 10, totalPages: 1, total: 0 }
    }
  },
  mounted: function() {
    this.loadLogs()
  },
  methods: {
    loadLogs: function() {
      var self = this
      self.loading = true
      var params = {
        pageNum: self.pagination.page,
        pageSize: self.pagination.pageSize
      }
      if (self.filters.operator) params.operator = self.filters.operator
      if (self.filters.module) params.module = self.filters.module

      getOperationLogs(params)
        .then(function(data) {
          self.logs = data.list || []
          self.pagination.total = data.total || 0
          self.pagination.totalPages = data.pages || 1
        })
        .catch(function(err) { console.error('加载操作日志失败:', err) })
        .finally(function() { self.loading = false })
    },
    resetFilters: function() {
      this.filters = { operator: '', module: '' }
      this.pagination.page = 1
      this.loadLogs()
    },
    confirmClear: function() { this.clearDialog = true },
    doClear: function() {
      var self = this
      self.clearing = true
      clearOperationLogs()
        .then(function() {
          self.clearDialog = false
          self.loadLogs()
        })
        .catch(function(err) { console.error('清空失败:', err) })
        .finally(function() { self.clearing = false })
    },
    methodColor: function(method) {
      var map = { GET: 'info', POST: 'success', PUT: 'warning', DELETE: 'error' }
      return map[method] || 'default'
    },
    formatTime: function(t) {
      if (!t) return '-'
      return String(t).substring(0, 19).replace('T', ' ')
    }
  }
}
</script>
