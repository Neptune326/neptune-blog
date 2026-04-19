<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">友情链接</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">管理前台侧边栏显示的友情链接</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openDialog()">添加链接</v-btn>
    </div>

    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
          <tr>
            <th>名称</th>
            <th>链接地址</th>
            <th>描述</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="text-center pa-6">
              <v-progress-circular indeterminate color="primary" size="32" />
            </td>
          </tr>
          <tr v-else-if="links.length === 0">
            <td colspan="5" class="text-center pa-6" style="color: #9aa0a6;">暂无友情链接</td>
          </tr>
          <tr v-for="link in links" :key="link.id" v-else>
            <td>
              <div class="d-flex align-center" style="gap: 8px;">
                <img v-if="link.iconUrl" :src="link.iconUrl" style="width: 16px; height: 16px; border-radius: 3px;" />
                <v-icon v-else size="16" color="primary">mdi-link-variant</v-icon>
                <span style="font-weight: 500;">{{ link.name }}</span>
              </div>
            </td>
            <td>
              <a :href="link.url" target="_blank" style="color: #1a73e8; font-size: 13px; text-decoration: none;">
                {{ link.url }}
              </a>
            </td>
            <td style="color: #80868b; font-size: 13px;">{{ link.description || '-' }}</td>
            <td>{{ link.sort }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" @click="openDialog(link)">编辑</v-btn>
              <v-btn size="small" variant="text" color="error" @click="confirmDelete(link)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- 新增/编辑对话框 -->
    <v-dialog v-model="dialog" max-width="480">
      <v-card rounded="xl">
        <v-card-title class="pa-5 pb-2" style="font-size: 16px; font-weight: 600;">
          {{ editTarget ? '编辑链接' : '添加链接' }}
        </v-card-title>
        <v-card-text class="pa-5 pt-3">
          <v-text-field v-model="form.name" label="名称 *" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="form.url" label="链接地址 *" variant="outlined" density="comfortable" class="mb-3" placeholder="https://" />
          <v-text-field v-model="form.description" label="描述（可选）" variant="outlined" density="comfortable" class="mb-3" />
          <v-text-field v-model="form.iconUrl" label="图标 URL（可选）" variant="outlined" density="comfortable" class="mb-3" placeholder="https://example.com/favicon.ico" />
          <v-text-field v-model.number="form.sort" label="排序（数字越小越靠前）" variant="outlined" density="comfortable" type="number" />
        </v-card-text>
        <v-card-actions class="pa-5 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="dialog = false">取消</v-btn>
          <v-btn color="primary" :loading="saving" @click="save">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认 -->
    <v-dialog v-model="deleteDialog" max-width="360">
      <v-card rounded="xl">
        <v-card-title class="pa-5 pb-2">确认删除</v-card-title>
        <v-card-text class="pa-5 pt-0">确定删除友情链接「{{ deleteTarget && deleteTarget.name }}」？</v-card-text>
        <v-card-actions class="pa-5 pt-0">
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">取消</v-btn>
          <v-btn color="error" :loading="deleteLoading" @click="doDelete">删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    
  </div>
</template>

<script>
import request from '../../api/request.js'

export default {
  name: 'FriendLinkView',
  data: function() {
    return {
      loading: false,
      saving: false,
      links: [],
      dialog: false,
      editTarget: null,
      form: { name: '', url: '', description: '', iconUrl: '', sort: 0 },
      deleteDialog: false,
      deleteTarget: null,
      deleteLoading: false,
      
    }
  },
  mounted: function() {
    this.loadLinks()
  },
  methods: {
    loadLinks: function() {
      var self = this
      self.loading = true
      request({ method: 'get', url: '/api/admin/friend-links' })
        .then(function(data) { self.links = data || [] })
        .catch(function() {})
        .finally(function() { self.loading = false })
    },
    openDialog: function(link) {
      this.editTarget = link || null
      if (link) {
        this.form = { name: link.name, url: link.url, description: link.description || '', iconUrl: link.iconUrl || '', sort: link.sort || 0 }
      } else {
        this.form = { name: '', url: '', description: '', iconUrl: '', sort: 0 }
      }
      this.dialog = true
    },
    save: function() {
      var self = this
      if (!self.form.name || !self.form.url) {
        self.$toast.error('名称和链接地址不能为空')
        return
      }
      self.saving = true
      var req = self.editTarget
        ? request({ method: 'put', url: '/api/admin/friend-links/' + self.editTarget.id, data: self.form })
        : request({ method: 'post', url: '/api/admin/friend-links', data: self.form })
      req.then(function() {
          self.dialog = false
          self.$toast.success('保存成功')
          self.loadLinks()
        })
        .catch(function() { self.$toast.error('保存失败') })
        .finally(function() { self.saving = false })
    },
    confirmDelete: function(link) {
      this.deleteTarget = link
      this.deleteDialog = true
    },
    doDelete: function() {
      var self = this
      self.deleteLoading = true
      request({ method: 'delete', url: '/api/admin/friend-links/' + self.deleteTarget.id })
        .then(function() {
          self.deleteDialog = false
          self.$toast.success('删除成功')
          self.loadLinks()
        })
        .catch(function() { self.$toast.error('删除失败') })
        .finally(function() { self.deleteLoading = false })
    }
  }
}
</script>


