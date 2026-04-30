<template>
  <div>
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">数据备份</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">导出全站数据为 JSON 文件，用于备份或迁移</p>
    </div>

    <v-row>
      <v-col v-for="item in backupItems" :key="item.key" cols="12" sm="6" md="4">
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
          <div class="pa-5">
            <div class="d-flex align-center mb-3" style="gap: 10px;">
              <div :style="{ width: '40px', height: '40px', background: item.bgColor, borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center' }">
                <v-icon :color="item.color" size="20">{{ item.icon }}</v-icon>
              </div>
              <div>
                <div style="font-size: 14px; font-weight: 600; color: #202124;">{{ item.label }}</div>
                <div style="font-size: 12px; color: #80868b;">{{ item.desc }}</div>
              </div>
            </div>
            <v-btn
              block
              variant="outlined"
              :color="item.color"
              :loading="loading[item.key]"
              prepend-icon="mdi-download"
              @click="exportData(item)"
              style="font-size: 13px;"
            >导出</v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 全量导出 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-top: 16px;">
      <div class="pa-5 d-flex align-center justify-space-between">
        <div>
          <div style="font-size: 15px; font-weight: 600; color: #202124;">全量备份</div>
          <div style="font-size: 13px; color: #80868b; margin-top: 2px;">导出所有数据为单个 JSON 文件</div>
        </div>
        <v-btn color="primary" variant="tonal" :loading="loading.all" prepend-icon="mdi-database-export-outline" @click="exportAll">
          导出全部
        </v-btn>
      </div>
    </v-card>

    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-top: 16px;">
      <div class="pa-5">
        <div style="font-size: 15px; font-weight: 600; color: #202124; margin-bottom: 10px;">数据恢复</div>
        <div class="d-flex align-center" style="gap: 10px;">
          <input ref="restoreInput" type="file" accept=".json,application/json" @change="onRestoreFileChange" />
          <v-select
            v-model="restoreMode"
            :items="[{ title: '覆盖更新', value: 'upsert' }, { title: '仅新增', value: 'insert' }]"
            item-title="title"
            item-value="value"
            variant="outlined"
            density="compact"
            style="max-width: 160px;"
            hide-details
          />
          <v-btn color="error" variant="tonal" :loading="loading.restore" prepend-icon="mdi-database-import-outline" @click="restoreData">导入恢复</v-btn>
        </div>
      </div>
    </v-card>
  </div>
</template>

<script>
import request from '@/api/request.js'

export default {
  name: 'DataBackupView',
  data: function() {
    return {
      loading: {},
      restoreMode: 'upsert',
      restoreFile: null,
      backupItems: [
        { key: 'articles', label: '文章', desc: '含正文、标签、分类', icon: 'mdi-file-document-outline', color: '#1a73e8', bgColor: '#e8f0fe', url: '/api/admin/articles', params: { pageNum: 1, pageSize: 9999 } },
        { key: 'categories', label: '分类', desc: '所有分类数据', icon: 'mdi-folder-outline', color: '#34a853', bgColor: '#e6f4ea', url: '/api/admin/categories', params: {} },
        { key: 'tags', label: '标签', desc: '所有标签数据', icon: 'mdi-tag-outline', color: '#f29900', bgColor: '#fef7e0', url: '/api/admin/tags', params: {} },
        { key: 'comments', label: '评论', desc: '所有评论数据', icon: 'mdi-comment-outline', color: '#9334e6', bgColor: '#f3e8fd', url: '/api/admin/comments', params: { pageNum: 1, pageSize: 9999 } },
        { key: 'messages', label: '留言', desc: '所有留言数据', icon: 'mdi-message-text-outline', color: '#00bcd4', bgColor: '#e0f7fa', url: '/api/admin/messages', params: { pageNum: 1, pageSize: 9999 } },
        { key: 'friendLinks', label: '友情链接', desc: '所有友情链接', icon: 'mdi-link-variant', color: '#ea4335', bgColor: '#fce8e6', url: '/api/admin/friend-links', params: {} },
        { key: 'sysConfig', label: '系统配置', desc: '全局配置项', icon: 'mdi-cog-outline', color: '#546e7a', bgColor: '#eceff1', url: '/api/admin/sys-config', params: {} }
      ]
    }
  },
  methods: {
    exportData: function(item) {
      var self = this
      self.$set ? self.$set(self.loading, item.key, true) : (self.loading[item.key] = true)
      request({ method: 'get', url: item.url, params: item.params })
        .then(function(data) {
          var content = JSON.stringify(data, null, 2)
          self.download(item.key + '_backup_' + self.dateStr() + '.json', content)
        })
        .catch(function() { self.$toast.error('导出失败') })
        .finally(function() { self.loading[item.key] = false })
    },
    onRestoreFileChange: function(event) {
      var files = event.target.files || []
      this.restoreFile = files.length ? files[0] : null
    },
    restoreData: function() {
      var self = this
      if (!self.restoreFile) {
        self.$toast.error('请先选择备份文件')
        return
      }
      var form = new FormData()
      form.append('file', self.restoreFile)
      form.append('mode', self.restoreMode || 'upsert')
      self.loading.restore = true
      request({
        method: 'post',
        url: '/api/admin/data/restore',
        data: form,
        headers: { 'Content-Type': 'multipart/form-data' }
      }).then(function(data) {
        self.$toast.success('恢复完成：' + JSON.stringify(data))
      }).catch(function() {
        self.$toast.error('恢复失败')
      }).finally(function() {
        self.loading.restore = false
      })
    },
    exportAll: function() {
      var self = this
      self.loading.all = true
      var urls = self.backupItems.map(function(item) {
        return request({ method: 'get', url: item.url, params: item.params })
          .then(function(data) { return { key: item.key, data: data } })
          .catch(function() { return { key: item.key, data: null } })
      })
      Promise.all(urls).then(function(results) {
        var all = {}
        results.forEach(function(r) { all[r.key] = r.data })
        all._exportTime = new Date().toISOString()
        self.download('blog_full_backup_' + self.dateStr() + '.json', JSON.stringify(all, null, 2))
      }).finally(function() { self.loading.all = false })
    },
    download: function(filename, content) {
      var blob = new Blob([content], { type: 'application/json' })
      var url = URL.createObjectURL(blob)
      var a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      this.$toast.success('导出成功：' + filename)
    },
    dateStr: function() {
      var d = new Date()
      return d.getFullYear() + String(d.getMonth() + 1).padStart(2, '0') + String(d.getDate()).padStart(2, '0')
    }
  }
}
</script>


