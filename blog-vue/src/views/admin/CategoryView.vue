<template>
  <div>
    <!-- 新建按钮 -->
    <div class="d-flex justify-end mb-3">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateDialog">新建分类</v-btn>
    </div>

    <!-- 分类列表 -->
    <v-card>
      <v-table>
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>描述</th>
            <th>文章数</th>
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
          <tr v-else-if="categories.length === 0">
            <td colspan="6" class="text-center pa-4 text-grey">暂无分类</td>
          </tr>
          <tr v-for="cat in categories" :key="cat.id" v-else>
            <td>{{ cat.id }}</td>
            <td>{{ cat.name }}</td>
            <td>{{ cat.description || '-' }}</td>
            <td>{{ cat.articleCount || 0 }}</td>
            <td>{{ formatDate(cat.createdAt) }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" @click="openEditDialog(cat)">编辑</v-btn>
              <v-btn size="small" variant="text" color="error" @click="confirmDelete(cat)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- 新建/编辑弹窗 -->
    <v-dialog v-model="formDialog" max-width="500">
      <v-card>
        <v-card-title>{{ editTarget ? '编辑分类' : '新建分类' }}</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="formData.name"
            label="分类名称"
            variant="outlined"
            class="mb-3"
            :rules="[function(v) { return !!v || '请输入分类名称' }]"
          />
          <v-textarea
            v-model="formData.description"
            label="分类描述"
            variant="outlined"
            rows="3"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="formDialog = false">取消</v-btn>
          <v-btn color="primary" :loading="formLoading" @click="submitForm">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title>确认删除</v-card-title>
        <v-card-text>
          确定要删除分类「{{ deleteTarget && deleteTarget.name }}」吗？
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
import {
  adminGetCategories,
  adminCreateCategory,
  adminUpdateCategory,
  adminDeleteCategory
} from '../../api/category.js'

export default {
  name: 'AdminCategoryView',
  data: function() {
    return {
      loading: false,
      categories: [],
      // 表单弹窗
      formDialog: false,
      formLoading: false,
      editTarget: null,
      formData: { name: '', description: '' },
      // 删除弹窗
      deleteDialog: false,
      deleteLoading: false,
      deleteTarget: null
    }
  },
  mounted: function() {
    this.loadCategories()
  },
  methods: {
    // 加载分类列表
    loadCategories: function() {
      var self = this
      self.loading = true
      adminGetCategories()
        .then(function(data) {
          self.categories = data || []
        })
        .catch(function(err) {
          console.error('加载分类失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    // 打开新建弹窗
    openCreateDialog: function() {
      this.editTarget = null
      this.formData = { name: '', description: '' }
      this.formDialog = true
    },
    // 打开编辑弹窗，回填数据
    openEditDialog: function(cat) {
      this.editTarget = cat
      this.formData = { name: cat.name, description: cat.description || '' }
      this.formDialog = true
    },
    // 提交新建或编辑
    submitForm: function() {
      var self = this
      if (!self.formData.name) return
      self.formLoading = true
      var isEdit = !!self.editTarget
      var promise = isEdit
        ? adminUpdateCategory(self.editTarget.id, self.formData)
        : adminCreateCategory(self.formData)
      promise
        .then(function() {
          self.formDialog = false
          self.$toast.success(isEdit ? '分类已更新' : '分类创建成功')
          self.loadCategories()
        })
        .catch(function(err) {
          console.error('保存分类失败:', err)
        })
        .finally(function() {
          self.formLoading = false
        })
    },
    // 弹出删除确认框
    confirmDelete: function(cat) {
      this.deleteTarget = cat
      this.deleteDialog = true
    },
    // 执行删除
    doDelete: function() {
      var self = this
      if (!self.deleteTarget) return
      self.deleteLoading = true
      adminDeleteCategory(self.deleteTarget.id)
        .then(function() {
          self.deleteDialog = false
          self.deleteTarget = null
          self.$toast.success('分类已删除')
          self.loadCategories()
        })
        .catch(function(err) {
          console.error('删除分类失败:', err)
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
