<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">文章系列</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">将相关文章组织成系列，方便读者连续阅读</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">新建系列</v-btn>
    </div>

    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed;">
      <v-table>
        <thead>
          <tr style="background: #f8f9fa;">
            <th style="font-size: 12px; color: #80868b;">系列名称</th>
            <th style="font-size: 12px; color: #80868b;">描述</th>
            <th style="font-size: 12px; color: #80868b;">排序</th>
            <th style="font-size: 12px; color: #80868b;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading"><td colspan="4" class="text-center pa-6"><v-progress-circular indeterminate color="primary" size="28" /></td></tr>
          <tr v-else-if="series.length === 0"><td colspan="4" class="text-center pa-6" style="color: #80868b;">暂无系列，点击右上角新建</td></tr>
          <tr v-for="item in series" :key="item.id" style="border-bottom: 1px solid #f1f3f4;">
            <td style="font-size: 14px; font-weight: 500; color: #202124;">
              <v-icon size="16" color="primary" class="mr-1">mdi-book-open-variant</v-icon>
              {{ item.name }}
            </td>
            <td style="font-size: 13px; color: #5f6368; max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {{ item.description || '-' }}
            </td>
            <td style="font-size: 13px; color: #80868b;">{{ item.sort || 0 }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" @click="openEdit(item)">编辑</v-btn>
              <v-btn size="small" variant="text" color="error" @click="deleteSeries(item)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- 新建/编辑弹窗 -->
    <v-dialog v-model="formDialog" max-width="480">
      <v-card rounded="xl">
        <div class="pa-6">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 20px;">
            {{ editTarget ? '编辑系列' : '新建系列' }}
          </div>
          <v-form @submit.prevent="submitForm">
            <v-text-field
              v-model="form.name"
              label="系列名称"
              variant="outlined"
              density="comfortable"
              class="mb-3"
              :rules="[function(v) { return !!v || '请输入系列名称' }]"
            />
            <v-textarea
              v-model="form.description"
              label="系列描述（可选）"
              variant="outlined"
              density="comfortable"
              rows="3"
              class="mb-3"
            />
            <v-text-field
              v-model="form.sort"
              label="排序（数字越小越靠前）"
              variant="outlined"
              density="comfortable"
              type="number"
            />
            <div class="d-flex justify-end mt-4" style="gap: 8px;">
              <v-btn variant="text" @click="formDialog = false">取消</v-btn>
              <v-btn color="primary" type="submit" :loading="formLoading">保存</v-btn>
            </div>
          </v-form>
        </div>
      </v-card>
    </v-dialog>

    
  </div>
</template>

<script>
import request from '../../api/request.js'

export default {
  name: 'SeriesView',
  data: function() {
    return {
      loading: false,
      series: [],
      formDialog: false,
      formLoading: false,
      editTarget: null,
      form: { name: '', description: '', sort: 0 },
      
    }
  },
  mounted: function() { this.loadSeries() },
  methods: {
    loadSeries: function() {
      var self = this
      self.loading = true
      request({ method: 'get', url: '/api/admin/series' })
        .then(function(data) { self.series = data || [] })
        .catch(function() {})
        .finally(function() { self.loading = false })
    },
    openCreate: function() {
      this.editTarget = null
      this.form = { name: '', description: '', sort: 0 }
      this.formDialog = true
    },
    openEdit: function(item) {
      this.editTarget = item
      this.form = { name: item.name, description: item.description || '', sort: item.sort || 0 }
      this.formDialog = true
    },
    submitForm: function() {
      var self = this
      if (!self.form.name) return
      self.formLoading = true
      var promise = self.editTarget
        ? request({ method: 'put', url: '/api/admin/series/' + self.editTarget.id, data: self.form })
        : request({ method: 'post', url: '/api/admin/series', data: self.form })
      promise
        .then(function() {
          self.formDialog = false
          // handled by interceptor
          self.loadSeries()
        })
        .catch(function(err) {
          self.$toast.error(err.message || '保存失败')
        })
        .finally(function() { self.formLoading = false })
    },
    deleteSeries: function(item) {
      var self = this
      if (!confirm('确认删除系列「' + item.name + '」？关联文章不会被删除。')) return
      request({ method: 'delete', url: '/api/admin/series/' + item.id })
        .then(function() {
          // handled by interceptor
          self.loadSeries()
        })
        .catch(function(err) {
          self.$toast.error(err.message || '删除失败')
        })
    }
  }
}
</script>


