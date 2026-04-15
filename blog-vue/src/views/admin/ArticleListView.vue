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

    <!-- 新建按钮 -->
    <div class="d-flex justify-end mb-3">
      <v-btn color="primary" prepend-icon="mdi-plus" :to="'/admin/articles/edit'">新建文章</v-btn>
    </div>

    <!-- 文章列表表格 -->
    <v-card>
      <v-table>
        <thead>
          <tr>
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
            <td colspan="6" class="text-center pa-4">
              <v-progress-circular indeterminate color="primary" />
            </td>
          </tr>
          <tr v-else-if="articles.length === 0">
            <td colspan="6" class="text-center pa-4 text-grey">暂无数据</td>
          </tr>
          <tr v-for="article in articles" :key="article.id" v-else>
            <td>{{ article.id }}</td>
            <td>{{ article.title }}</td>
            <td>{{ article.categoryName || '-' }}</td>
            <td>
              <v-chip
                :color="article.status === 1 ? 'success' : 'default'"
                size="small"
              >
                {{ article.status === 1 ? '已发布' : '草稿' }}
              </v-chip>
            </td>
            <td>{{ formatDate(article.createTime) }}</td>
            <td>
              <v-btn
                size="small"
                variant="text"
                color="primary"
                :to="'/admin/articles/edit/' + article.id"
              >编辑</v-btn>
              <v-btn
                size="small"
                variant="text"
                color="error"
                @click="confirmDelete(article)"
              >删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

      <!-- 分页 -->
      <div class="d-flex justify-center pa-4">
        <v-pagination
          v-model="pagination.page"
          :length="pagination.totalPages"
          :total-visible="7"
          @update:model-value="loadArticles"
        />
      </div>
    </v-card>

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
      deleteTarget: null
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
    }
  }
}
</script>
