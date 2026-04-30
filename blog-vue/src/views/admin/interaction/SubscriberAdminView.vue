<template>
  <div>
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">订阅管理</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">管理访客邮箱订阅</p>
    </div>
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
        <tr><th>邮箱</th><th>状态</th><th>时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <tr v-if="loading"><td colspan="4" class="text-center pa-4"><v-progress-circular indeterminate color="primary" /></td></tr>
        <tr v-for="item in list" :key="item.id" v-else>
          <td>{{ item.email }}</td>
          <td>
            <v-chip size="small" :color="item.status === 1 ? 'success' : 'default'" variant="tonal">
              {{ item.status === 1 ? '启用' : '停用' }}
            </v-chip>
          </td>
          <td>{{ item.createTime ? String(item.createTime).substring(0, 10) : '-' }}</td>
          <td>
            <v-btn size="small" variant="text" color="primary" @click="toggle(item)">
              {{ item.status === 1 ? '停用' : '启用' }}
            </v-btn>
            <v-btn size="small" variant="text" color="error" @click="remove(item.id)">删除</v-btn>
          </td>
        </tr>
        </tbody>
      </v-table>
    </v-card>
  </div>
</template>

<script>
import request from '@/api/request.js'

export default {
  name: 'SubscriberAdminView',
  data: function() {
    return { loading: false, list: [] }
  },
  mounted: function() { this.loadData() },
  methods: {
    loadData: function() {
      var self = this
      self.loading = true
      request({ method: 'get', url: '/api/admin/subscribers', params: { pageNum: 1, pageSize: 200 } })
        .then(function(data) { self.list = data.list || [] })
        .finally(function() { self.loading = false })
    },
    toggle: function(item) {
      var self = this
      request({ method: 'put', url: '/api/admin/subscribers/' + item.id + '/status', data: { status: item.status === 1 ? 0 : 1 } })
        .then(function() { self.$toast.success('状态已更新'); self.loadData() })
    },
    remove: function(id) {
      var self = this
      request({ method: 'delete', url: '/api/admin/subscribers/' + id })
        .then(function() { self.$toast.success('订阅已删除'); self.loadData() })
    }
  }
}
</script>
