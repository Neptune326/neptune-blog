<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">管理员账号</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">仅超级管理员可管理账号</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-account-plus" @click="openCreateDialog">新建管理员</v-btn>
    </div>

    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
          <tr style="background: #f8f9fa;">
            <th style="font-size: 12px; color: #80868b;">用户名</th>
            <th style="font-size: 12px; color: #80868b;">角色</th>
            <th style="font-size: 12px; color: #80868b;">创建时间</th>
            <th style="font-size: 12px; color: #80868b;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="4" class="text-center pa-6"><v-progress-circular indeterminate color="primary" size="28" /></td></tr>
          <tr v-else-if="admins.length === 0"><td colspan="4" class="text-center pa-6" style="color: #80868b;">暂无数据</td></tr>
          <tr v-for="admin in admins" :key="admin.id" style="border-bottom: 1px solid #f1f3f4;">
            <td style="font-size: 14px; font-weight: 500;">
              <v-icon size="16" color="primary" class="mr-1">mdi-account-circle-outline</v-icon>
              {{ admin.username }}
            </td>
            <td>
              <v-chip size="x-small" :color="admin.role === 'super' ? 'error' : 'primary'" variant="tonal">
                {{ admin.role === 'super' ? '超级管理员' : '普通管理员' }}
              </v-chip>
            </td>
            <td style="font-size: 12px; color: #80868b;">{{ formatDate(admin.createTime) }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" @click="openResetPwd(admin)">重置密码</v-btn>
              <v-btn
                v-if="admin.role !== 'super'"
                size="small"
                variant="text"
                color="error"
                @click="deleteAdmin(admin)"
              >删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
      <TablePagination :total="allAdmins.length" :page="page" :page-size="pageSize" @change="page = $event" />
    </v-card>

    <!-- 新建管理员弹窗 -->
    <v-dialog v-model="createDialog" max-width="420">
      <v-card rounded="xl">
        <div class="pa-6">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 20px;">新建管理员</div>
          <v-form ref="createFormRef" @submit.prevent="doCreate">
            <v-text-field v-model="createForm.username" label="用户名" variant="outlined" density="comfortable" class="mb-3"
              :rules="[function(v) { return !!v || '请输入用户名' }]" />
            <v-text-field v-model="createForm.password" label="密码" type="password" variant="outlined" density="comfortable"
              :rules="[function(v) { return !!v || '请输入密码' }, function(v) { return v.length >= 6 || '至少 6 位' }]" />
            <v-alert v-if="createError" type="error" variant="tonal" density="compact" rounded="lg" class="mt-2 mb-2">{{ createError }}</v-alert>
            <div class="d-flex justify-end mt-4" style="gap: 8px;">
              <v-btn variant="text" @click="createDialog = false">取消</v-btn>
              <v-btn color="primary" type="submit" :loading="createLoading">创建</v-btn>
            </div>
          </v-form>
        </div>
      </v-card>
    </v-dialog>

    <!-- 重置密码弹窗 -->
    <v-dialog v-model="resetPwdDialog" max-width="380">
      <v-card rounded="xl">
        <div class="pa-6">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 8px;">重置密码</div>
          <div style="font-size: 13px; color: #5f6368; margin-bottom: 16px;">为 <strong>{{ resetTarget && resetTarget.username }}</strong> 设置新密码</div>
          <v-text-field v-model="newPassword" label="新密码" type="password" variant="outlined" density="comfortable"
            :rules="[function(v) { return v.length >= 6 || '至少 6 位' }]" />
          <div class="d-flex justify-end mt-3" style="gap: 8px;">
            <v-btn variant="text" @click="resetPwdDialog = false">取消</v-btn>
            <v-btn color="primary" :loading="resetLoading" @click="doResetPwd">确认</v-btn>
          </div>
        </div>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="380">
      <v-card rounded="xl">
        <v-card-title class="pa-5 pb-2">确认删除</v-card-title>
        <v-card-text class="pa-5 pt-0">确定删除管理员「{{ deleteTarget && deleteTarget.username }}」？</v-card-text>
        <v-card-actions class="pa-5 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">取消</v-btn>
          <v-btn color="error" :loading="deleteLoading" @click="doDeleteAdmin">确认删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import request from '../../api/request.js'
import TablePagination from '../../components/admin/TablePagination.vue'

export default {
  name: 'AdminUserView',
  components: { TablePagination },
  data: function() {
    return {
      loading: false,
      allAdmins: [],
      page: 1,
      pageSize: 10,
      createDialog: false,
      createLoading: false,
      createError: '',
      createForm: { username: '', password: '' },
      resetPwdDialog: false,
      resetLoading: false,
      resetTarget: null,
      newPassword: '',
      deleteDialog: false,
      deleteLoading: false,
      deleteTarget: null
    }
  },
  mounted: function() { this.loadAdmins() },
  computed: {
    admins: function() {
      var start = (this.page - 1) * this.pageSize
      return this.allAdmins.slice(start, start + this.pageSize)
    }
  },
  methods: {
    loadAdmins: function() {
      var self = this
      self.loading = true
      request({ method: 'get', url: '/api/admin/users' })
        .then(function(data) { self.allAdmins = data || [] })
        .catch(function(err) {
          if (err.message && err.message.includes('403')) {
            self.$toast.warning('仅超级管理员可访问此页面')
          }
        })
        .finally(function() { self.loading = false })
    },
    openCreateDialog: function() {
      this.createForm = { username: '', password: '' }
      this.createError = ''
      this.createDialog = true
    },
    doCreate: function() {
      var self = this
      if (!self.createForm.username || !self.createForm.password) return
      self.createLoading = true
      self.createError = ''
      request({ method: 'post', url: '/api/admin/users', data: self.createForm })
        .then(function() {
          self.createDialog = false
          self.$toast.success('管理员创建成功')
          self.loadAdmins()
        })
        .catch(function(err) { self.createError = err.message || '创建失败' })
        .finally(function() { self.createLoading = false })
    },
    openResetPwd: function(admin) {
      this.resetTarget = admin
      this.newPassword = ''
      this.resetPwdDialog = true
    },
    doResetPwd: function() {
      var self = this
      if (!self.newPassword || self.newPassword.length < 6) return
      self.resetLoading = true
      request({ method: 'put', url: '/api/admin/users/' + self.resetTarget.id + '/reset-password', data: { newPassword: self.newPassword } })
        .then(function() {
          self.resetPwdDialog = false
          self.$toast.success('密码重置成功')
        })
        .catch(function(err) { self.$toast.error(err.message || '重置失败') })
        .finally(function() { self.resetLoading = false })
    },
    deleteAdmin: function(admin) {
      this.deleteTarget = admin
      this.deleteDialog = true
    },
    doDeleteAdmin: function() {
      var self = this
      self.deleteLoading = true
      request({ method: 'delete', url: '/api/admin/users/' + self.deleteTarget.id })
        .then(function() {
          self.deleteDialog = false
          self.deleteTarget = null
          self.$toast.success('删除成功')
          self.loadAdmins()
        })
        .catch(function(err) { self.$toast.error(err.message || '删除失败') })
        .finally(function() { self.deleteLoading = false })
    },
    formatDate: function(t) { return t ? String(t).substring(0, 10) : '-' }
  }
}
</script>


