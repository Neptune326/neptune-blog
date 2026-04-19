<template>
  <div>
    <!-- 新建按钮 -->
    <div class="d-flex justify-end mb-3">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreateDialog">新建标签</v-btn>
    </div>

    <!-- 标签列表 -->
    <v-card>
      <v-table>
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>文章数</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="text-center pa-4">
              <v-progress-circular indeterminate color="primary" />
            </td>
          </tr>
          <tr v-else-if="tags.length === 0">
            <td colspan="5" class="text-center pa-4 text-grey">暂无标签</td>
          </tr>
          <tr v-for="tag in tags" :key="tag.id" v-else>
            <td>{{ tag.id }}</td>
            <td>
              <v-chip size="small" color="primary" variant="outlined">{{ tag.name }}</v-chip>
            </td>
            <td>{{ tag.articleCount || 0 }}</td>
            <td>{{ formatDate(tag.createdAt) }}</td>
            <td>
              <v-btn size="small" variant="text" color="primary" @click="openEditDialog(tag)">编辑</v-btn>
              <v-btn size="small" variant="text" color="error" @click="confirmDelete(tag)">删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- 新建/编辑弹窗 -->
    <v-dialog v-model="formDialog" max-width="400">
      <v-card>
        <v-card-title>{{ editTarget ? '编辑标签' : '新建标签' }}</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="formData.name"
            label="标签名称"
            variant="outlined"
            :rules="[function(v) { return !!v || '请输入标签名称' }]"
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
          确定要删除标签「{{ deleteTarget && deleteTarget.name }}」吗？
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
  adminGetTags,
  adminCreateTag,
  adminUpdateTag,
  adminDeleteTag
} from '../../api/tag.js'

export default {
  name: 'AdminTagView',
  data: function() {
    return {
      loading: false,
      tags: [],
      // 表单弹窗
      formDialog: false,
      formLoading: false,
      editTarget: null,
      formData: { name: '' },
      // 删除弹窗
      deleteDialog: false,
      deleteLoading: false,
      deleteTarget: null
    }
  },
  mounted: function() {
    this.loadTags()
  },
  methods: {
    // 加载标签列表
    loadTags: function() {
      var self = this
      self.loading = true
      adminGetTags()
        .then(function(data) {
          self.tags = data || []
        })
        .catch(function(err) {
          console.error('加载标签失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    // 打开新建弹窗
    openCreateDialog: function() {
      this.editTarget = null
      this.formData = { name: '' }
      this.formDialog = true
    },
    // 打开编辑弹窗，回填数据
    openEditDialog: function(tag) {
      this.editTarget = tag
      this.formData = { name: tag.name }
      this.formDialog = true
    },
    // 提交新建或编辑
    submitForm: function() {
      var self = this
      if (!self.formData.name) return
      self.formLoading = true
      var isEdit = !!self.editTarget
      var promise = isEdit
        ? adminUpdateTag(self.editTarget.id, self.formData)
        : adminCreateTag(self.formData)
      promise
        .then(function() {
          self.formDialog = false
          self.$toast.success(isEdit ? '标签已更新' : '标签创建成功')
          self.loadTags()
        })
        .catch(function(err) {
          console.error('保存标签失败:', err)
        })
        .finally(function() {
          self.formLoading = false
        })
    },
    // 弹出删除确认框
    confirmDelete: function(tag) {
      this.deleteTarget = tag
      this.deleteDialog = true
    },
    // 执行删除
    doDelete: function() {
      var self = this
      if (!self.deleteTarget) return
      self.deleteLoading = true
      adminDeleteTag(self.deleteTarget.id)
        .then(function() {
          self.deleteDialog = false
          self.deleteTarget = null
          self.$toast.success('标签已删除')
          self.loadTags()
        })
        .catch(function(err) {
          console.error('删除标签失败:', err)
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
