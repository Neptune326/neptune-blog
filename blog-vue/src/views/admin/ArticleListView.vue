<template>
  <div>
    <!-- 筛选工具栏 -->
    <v-card class="mb-4 pa-4">
      <v-row align="center">
        <v-col cols="12" sm="4">
          <v-text-field
            v-model="filters.keyword"
            label="搜索关键词"
            variant="outlined"
            density="compact"
            prepend-inner-icon="mdi-magnify"
            clearable
            hide-details
            @keyup.enter="loadArticles"
          />
        </v-col>
        <v-col cols="12" sm="3">
          <v-select
            v-model="filters.categoryId"
            :items="categories"
            item-title="name"
            item-value="id"
            label="分类"
            variant="outlined"
            density="compact"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="3">
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
        <v-col cols="12" sm="2" class="d-flex gap-2">
          <v-btn color="primary" @click="loadArticles">搜索</v-btn>
          <v-btn variant="outlined" @click="resetFilters">重置</v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- 操作按钮区 -->
    <div class="d-flex justify-end mb-3" style="gap: 8px;">
      <!-- 导入 -->
      <v-btn
        variant="outlined"
        prepend-icon="mdi-upload"
        :loading="importing"
        @click="triggerImport"
        style="font-size: 13px;"
      >
        导入 Markdown
      </v-btn>
      <input ref="importInput" type="file" accept=".zip" style="display:none;" @change="handleImport" />

      <!-- 导出 -->
      <v-btn
        variant="outlined"
        prepend-icon="mdi-download"
        @click="exportArticles"
        style="font-size: 13px;"
      >
        导出全部
      </v-btn>

      <!-- 新建 -->
      <v-btn color="primary" prepend-icon="mdi-plus" :to="'/admin/articles/edit'">新建文章</v-btn>
    </div>

    <!-- 导入结果提示 -->
    <v-snackbar v-model="importSnackbar.show" :color="importSnackbar.color" timeout="4000" location="top" rounded="lg">
      {{ importSnackbar.text }}
    </v-snackbar>

    <!-- 文章列表表格 -->
    <v-card>
      <!-- 批量操作栏 -->
      <div v-if="selectedIds.length > 0" class="d-flex align-center pa-3" style="background: #e8f0fe; gap: 12px; border-bottom: 1px solid #c5d8f8;">
        <span style="font-size: 13px; color: #1a73e8; font-weight: 500;">已选 {{ selectedIds.length }} 篇</span>
        <v-btn size="small" color="success" variant="tonal" prepend-icon="mdi-check-circle-outline" @click="batchPublish" :loading="batchLoading">批量发布</v-btn>
        <v-btn size="small" color="warning" variant="tonal" prepend-icon="mdi-file-outline" @click="batchDraft" :loading="batchLoading">批量转草稿</v-btn>
        <v-btn size="small" color="error" variant="tonal" prepend-icon="mdi-delete-outline" @click="batchDeleteConfirm" :loading="batchLoading">批量删除</v-btn>
        <v-btn size="small" variant="text" @click="selectedIds = []">取消选择</v-btn>
      </div>

      <v-table>
        <thead>
          <tr>
            <th style="width: 40px;">
              <v-checkbox
                :model-value="allSelected"
                :indeterminate="someSelected"
                hide-details
                density="compact"
                @update:model-value="toggleSelectAll"
              />
            </th>
            <th>ID</th>
            <th>标题</th>
            <th>分类</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center pa-4">
              <v-progress-circular indeterminate color="primary" />
            </td>
          </tr>
          <tr v-else-if="articles.length === 0">
            <td colspan="7" class="text-center pa-4 text-grey">暂无数据</td>
          </tr>
          <tr v-for="article in articles" :key="article.id" v-else>
            <td>
              <v-checkbox
                :model-value="selectedIds.includes(article.id)"
                hide-details
                density="compact"
                @update:model-value="toggleSelect(article.id)"
              />
            </td>
            <td>{{ article.id }}</td>
            <td>
              <div class="d-flex align-center" style="gap: 6px;">
                <v-icon v-if="article.isTop === 1" size="14" color="warning" title="置顶">mdi-pin</v-icon>
                {{ article.title }}
              </div>
            </td>
            <td>{{ article.categoryName || '-' }}</td>
            <td>
              <v-chip :color="article.status === 1 ? 'success' : 'default'" size="small">
                {{ article.status === 1 ? '已发布' : '草稿' }}
              </v-chip>
            </td>
            <td>{{ formatDate(article.createTime) }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" :to="'/admin/articles/edit/' + article.id">编辑</v-btn>
              <v-btn size="small" variant="text" :color="article.isTop === 1 ? 'warning' : 'default'" @click="toggleTop(article)" :title="article.isTop === 1 ? '取消置顶' : '置顶'">{{ article.isTop === 1 ? '取消置顶' : '置顶' }}</v-btn>
              <v-btn size="small" variant="text" color="error" @click="confirmDelete(article)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

      <!-- 分页 -->
      <div class="d-flex justify-center pa-4">
        <v-pagination v-model="pagination.page" :length="pagination.totalPages" :total-visible="7" @update:model-value="loadArticles" />
      </div>
    </v-card>

    <!-- 批量删除确认 -->
    <v-dialog v-model="batchDeleteDialog" max-width="400">
      <v-card>
        <v-card-title>批量删除确认</v-card-title>
        <v-card-text>确定删除选中的 {{ selectedIds.length }} 篇文章？此操作不可撤销。</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="batchDeleteDialog = false">取消</v-btn>
          <v-btn color="error" :loading="batchLoading" @click="doBatchDelete">确认删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title>确认删除</v-card-title>
        <v-card-text>
          确定要删除文章《{{ deleteTarget && deleteTarget.title }}》吗？此操作不可撤销。
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">取消</v-btn>
          <v-btn color="error" :loading="deleteLoading" @click="doDelete">确认删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { adminGetArticles, deleteArticle } from '../../api/article.js'
import { adminGetCategories } from '../../api/category.js'
import request from '../../api/request.js'

export default {
  name: 'ArticleListView',
  data: function() {
    return {
      loading: false,
      articles: [],
      categories: [],
      // 筛选条件
      filters: {
        keyword: '',
        categoryId: null,
        status: null
      },
      // 状态选项（与后端一致：0=草稿，1=已发布）
      statusOptions: [
        { label: '已发布', value: 1 },
        { label: '草稿', value: 0 }
      ],
      // 分页信息
      pagination: {
        page: 1,
        pageSize: 10,
        totalPages: 1
      },
      // 删除相关
      deleteDialog: false,
      deleteLoading: false,
      deleteTarget: null,
      // 批量操作
      selectedIds: [],
      batchLoading: false,
      batchDeleteDialog: false,
      // 导入导出
      importing: false,
      importSnackbar: { show: false, text: '', color: 'success' }
    }
  },
  computed: {
    allSelected: function() {
      return this.articles.length > 0 && this.selectedIds.length === this.articles.length
    },
    someSelected: function() {
      return this.selectedIds.length > 0 && this.selectedIds.length < this.articles.length
    }
  },
  mounted: function() {
    this.loadCategories()
    this.loadArticles()
  },
  methods: {
    // 加载分类列表（用于筛选下拉）
    loadCategories: function() {
      var self = this
      adminGetCategories()
        .then(function(data) {
          self.categories = data || []
        })
        .catch(function(err) {
          console.error('加载分类失败:', err)
        })
    },
    // 加载文章列表
    loadArticles: function() {
      var self = this
      self.loading = true
      self.selectedIds = []
      var params = {
        pageNum: self.pagination.page,
        pageSize: self.pagination.pageSize
      }
      if (self.filters.keyword) params.keyword = self.filters.keyword
      if (self.filters.categoryId) params.categoryId = self.filters.categoryId
      if (self.filters.status) params.status = self.filters.status

      adminGetArticles(params)
        .then(function(data) {
          self.articles = data.list || data.records || []
          self.pagination.totalPages = data.totalPages || Math.ceil((data.total || 0) / self.pagination.pageSize) || 1
        })
        .catch(function(err) {
          console.error('加载文章失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    // 重置筛选条件
    resetFilters: function() {
      this.filters.keyword = ''
      this.filters.categoryId = null
      this.filters.status = null
      this.pagination.page = 1
      this.loadArticles()
    },
    // 弹出删除确认框
    confirmDelete: function(article) {
      this.deleteTarget = article
      this.deleteDialog = true
    },
    // 执行删除
    doDelete: function() {
      var self = this
      if (!self.deleteTarget) return
      self.deleteLoading = true
      deleteArticle(self.deleteTarget.id)
        .then(function() {
          self.deleteDialog = false
          self.deleteTarget = null
          self.loadArticles()
        })
        .catch(function(err) {
          console.error('删除文章失败:', err)
        })
        .finally(function() {
          self.deleteLoading = false
        })
    },
    // 格式化日期
    formatDate: function(dateStr) {
      if (!dateStr) return '-'
      var d = new Date(dateStr)
      return d.getFullYear() + '-' +
        String(d.getMonth() + 1).padStart(2, '0') + '-' +
        String(d.getDate()).padStart(2, '0')
    },
    // 置顶切换
    toggleTop: function(article) {
      var self = this
      request({ method: 'put', url: '/api/admin/articles/' + article.id + '/top' })
        .then(function() { self.loadArticles() })
        .catch(function(err) { console.error('置顶操作失败:', err) })
    },
    // 批量选择
    toggleSelect: function(id) {
      var idx = this.selectedIds.indexOf(id)
      if (idx >= 0) this.selectedIds.splice(idx, 1)
      else this.selectedIds.push(id)
    },
    toggleSelectAll: function(val) {
      if (val) this.selectedIds = this.articles.map(function(a) { return a.id })
      else this.selectedIds = []
    },
    // 批量发布
    batchPublish: function() {
      var self = this
      self.batchLoading = true
      request({ method: 'put', url: '/api/admin/articles/batch-status', data: { ids: self.selectedIds, status: 1 } })
        .then(function() { self.selectedIds = []; self.loadArticles() })
        .catch(function() {})
        .finally(function() { self.batchLoading = false })
    },
    // 批量转草稿
    batchDraft: function() {
      var self = this
      self.batchLoading = true
      request({ method: 'put', url: '/api/admin/articles/batch-status', data: { ids: self.selectedIds, status: 0 } })
        .then(function() { self.selectedIds = []; self.loadArticles() })
        .catch(function() {})
        .finally(function() { self.batchLoading = false })
    },
    // 批量删除确认
    batchDeleteConfirm: function() {
      this.batchDeleteDialog = true
    },
    // 执行批量删除
    doBatchDelete: function() {
      var self = this
      self.batchLoading = true
      request({ method: 'delete', url: '/api/admin/articles/batch', data: { ids: self.selectedIds } })
        .then(function() { self.batchDeleteDialog = false; self.selectedIds = []; self.loadArticles() })
        .catch(function() {})
        .finally(function() { self.batchLoading = false })
    },
    // 触发导入文件选择
    triggerImport: function() {
      this.$refs.importInput.click()
    },
    // 处理导入
    handleImport: function(event) {
      var self = this
      var file = event.target.files[0]
      if (!file) return
      self.importing = true
      var formData = new FormData()
      formData.append('file', file)
      request({
        method: 'post',
        url: '/api/admin/articles/import',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
      })
        .then(function(msg) {
          self.importSnackbar = { show: true, text: msg || '导入成功', color: 'success' }
          self.loadArticles()
        })
        .catch(function(err) {
          self.importSnackbar = { show: true, text: '导入失败：' + (err.message || '请检查文件格式'), color: 'error' }
        })
        .finally(function() {
          self.importing = false
          event.target.value = ''
        })
    },
    // 导出文章
    exportArticles: function() {
      // 直接跳转下载链接（带 token）
      var token = localStorage.getItem('blog_token') || ''
      var url = '/api/admin/articles/export'
      // 创建隐藏 a 标签触发下载
      var a = document.createElement('a')
      a.href = url
      a.setAttribute('download', 'articles.zip')
      // 通过 fetch 下载（携带 Authorization 头）
      fetch(url, { headers: { 'Authorization': token } })
        .then(function(res) { return res.blob() })
        .then(function(blob) {
          var blobUrl = URL.createObjectURL(blob)
          a.href = blobUrl
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          URL.revokeObjectURL(blobUrl)
        })
        .catch(function(err) { console.error('导出失败:', err) })
    }
  }
}
</script>
