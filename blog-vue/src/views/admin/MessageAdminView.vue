<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">留言管理</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">审核和管理访客留言</p>
      </div>
    </div>

    <!-- 筛选 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
      <div class="pa-4">
        <v-row dense align="center">
          <v-col cols="12" sm="4">
            <v-select
              v-model="filters.status"
              :items="statusOptions"
              item-title="label"
              item-value="value"
              label="状态"
              variant="outlined"
              density="compact"
              clearable
              hide-details
            />
          </v-col>
          <v-col cols="12" sm="4">
            <v-btn color="primary" @click="loadMessages">筛选</v-btn>
            <v-btn variant="text" @click="resetFilters" class="ml-2">重置</v-btn>
          </v-col>
        </v-row>
      </div>
    </v-card>

    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
          <tr style="background: #f8f9fa;">
            <th style="font-size: 12px; color: #80868b;">昵称</th>
            <th style="font-size: 12px; color: #80868b;">内容</th>
            <th style="font-size: 12px; color: #80868b;">状态</th>
            <th style="font-size: 12px; color: #80868b;">时间</th>
            <th style="font-size: 12px; color: #80868b;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="5" class="text-center pa-6"><v-progress-circular indeterminate color="primary" size="28" /></td></tr>
          <tr v-else-if="messages.length === 0"><td colspan="5" class="text-center pa-6" style="color: #80868b;">暂无留言</td></tr>
          <tr v-for="msg in messages" :key="msg.id" style="border-bottom: 1px solid #f1f3f4;">
            <td style="font-size: 13px; font-weight: 500;">{{ msg.nickname }}</td>
            <td style="font-size: 13px; max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ msg.content }}</td>
            <td>
              <v-chip size="x-small" :color="msg.status === 1 ? 'success' : msg.status === 0 ? 'warning' : 'error'" variant="tonal">
                {{ msg.status === 1 ? '已通过' : msg.status === 0 ? '待审核' : '已拒绝' }}
              </v-chip>
            </td>
            <td style="font-size: 12px; color: #80868b;">{{ formatDate(msg.createTime) }}</td>
            <td>
              <v-btn v-if="msg.status === 0" size="small" variant="text" color="success" @click="approve(msg.id)">通过</v-btn>
              <v-btn v-if="msg.status === 0" size="small" variant="text" color="warning" @click="reject(msg.id)">拒绝</v-btn>
              <v-btn size="small" variant="text" color="error" @click="deleteMsg(msg.id)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
      <div class="d-flex justify-center pa-4">
        <v-pagination v-model="pagination.page" :length="pagination.totalPages" :total-visible="7" @update:model-value="loadMessages" />
      </div>
    </v-card>
  </div>
</template>

<script>
import request from '../../api/request.js'

export default {
  name: 'MessageAdminView',
  data: function() {
    return {
      loading: false,
      messages: [],
      filters: { status: null },
      statusOptions: [
        { label: '全部', value: null },
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 }
      ],
      pagination: { page: 1, pageSize: 20, totalPages: 1 }
    }
  },
  mounted: function() { this.loadMessages() },
  methods: {
    loadMessages: function() {
      var self = this
      self.loading = true
      var params = { pageNum: self.pagination.page, pageSize: self.pagination.pageSize }
      if (self.filters.status !== null) params.status = self.filters.status
      request({ method: 'get', url: '/api/admin/messages', params: params })
        .then(function(data) {
          self.messages = data.list || []
          self.pagination.totalPages = data.pages || 1
        })
        .catch(function() {})
        .finally(function() { self.loading = false })
    },
    resetFilters: function() { this.filters.status = null; this.pagination.page = 1; this.loadMessages() },
    approve: function(id) {
      var self = this
      request({ method: 'put', url: '/api/admin/messages/' + id + '/approve' }).then(function() { self.loadMessages() })
    },
    reject: function(id) {
      var self = this
      request({ method: 'put', url: '/api/admin/messages/' + id + '/reject' }).then(function() { self.loadMessages() })
    },
    deleteMsg: function(id) {
      var self = this
      if (!confirm('确认删除？')) return
      request({ method: 'delete', url: '/api/admin/messages/' + id }).then(function() { self.loadMessages() })
    },
    formatDate: function(t) { return t ? String(t).substring(0, 10) : '-' }
  }
}
</script>
