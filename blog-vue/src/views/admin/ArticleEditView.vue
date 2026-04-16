<template>
  <div>
    <v-card class="pa-6">
      <v-card-title class="text-h6 mb-4">
        {{ isEdit ? '编辑文章' : '新建文章' }}
      </v-card-title>

      <v-form ref="formRef" @submit.prevent="handleSubmit">
        <!-- 标题 -->
        <v-text-field
          v-model="form.title"
          label="文章标题"
          variant="outlined"
          class="mb-4"
          :rules="[function(v) { return !!v || '请输入文章标题' }]"
        />

        <v-row>
          <!-- 分类选择 -->
          <v-col cols="12" sm="6">
            <v-select
              v-model="form.categoryId"
              :items="categories"
              item-title="name"
              item-value="id"
              label="分类"
              variant="outlined"
              clearable
            />
          </v-col>

          <!-- 发布状态 -->
          <v-col cols="12" sm="4">
            <v-select
              v-model="form.status"
              :items="statusOptions"
              item-title="label"
              item-value="value"
              label="发布状态"
              variant="outlined"
            />
          </v-col>

          <!-- 定时发布 -->
          <v-col cols="12" sm="4">
            <v-text-field
              v-model="form.publishTime"
              label="定时发布时间（可选）"
              variant="outlined"
              type="datetime-local"
              density="comfortable"
              prepend-inner-icon="mdi-clock-outline"
              hint="设置后将在指定时间自动发布"
              persistent-hint
              clearable
            />
          </v-col>
        </v-row>

        <!-- 标签多选 -->
        <v-combobox
          v-model="form.tagIds"
          :items="tags"
          item-title="name"
          item-value="id"
          label="标签（可多选）"
          variant="outlined"
          multiple
          chips
          closable-chips
          class="mb-4"
          return-object
        />

        <!-- 摘要 -->
        <v-textarea
          v-model="form.summary"
          label="文章摘要"
          variant="outlined"
          rows="3"
          class="mb-4"
        />

        <!-- 封面图：支持 URL 输入 + 本地上传 -->
        <div class="mb-4">
          <div style="font-size: 13px; color: #5f6368; margin-bottom: 8px;">封面图片</div>
          <div class="d-flex align-start" style="gap: 12px;">
            <v-text-field
              v-model="form.coverUrl"
              label="封面图片 URL"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-image-outline"
              hide-details
              style="flex: 1;"
            />
            <v-btn
              variant="outlined"
              color="primary"
              prepend-icon="mdi-upload"
              :loading="uploading"
              @click="triggerUpload"
              style="height: 48px; white-space: nowrap;"
            >
              上传图片
            </v-btn>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none;"
              @change="handleFileChange"
            />
          </div>
          <!-- 封面预览 -->
          <div v-if="form.coverUrl" class="mt-2">
            <img
              :src="form.coverUrl"
              style="max-height: 120px; border-radius: 8px; border: 1px solid #e8eaed; object-fit: cover;"
              @error="form.coverUrl = ''"
            />
          </div>
        </div>

        <!-- Markdown 编辑器 -->
        <div class="mb-4">
          <div class="text-body-2 text-grey mb-2">文章内容（Markdown）</div>
          <MdEditor
            v-model="form.content"
            :theme="'light'"
            style="height: 500px"
          />
        </div>

        <!-- 操作按钮 -->
        <div class="d-flex justify-end gap-3 mt-4">
          <v-btn variant="outlined" @click="goBack">取消</v-btn>
          <v-btn
            color="primary"
            type="submit"
            :loading="submitting"
          >
            {{ isEdit ? '保存修改' : '发布文章' }}
          </v-btn>
        </div>
      </v-form>

      <!-- 错误提示 -->
      <v-alert v-if="errorMsg" type="error" class="mt-4">{{ errorMsg }}</v-alert>
    </v-card>
  </div>
</template>

<script>
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { adminGetArticleById, createArticle, updateArticle } from '../../api/article.js'
import { adminGetCategories } from '../../api/category.js'
import { adminGetTags } from '../../api/tag.js'
import { uploadImage } from '../../api/upload.js'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'ArticleEditView',
  components: { MdEditor },
  setup: function() {
    var route = useRoute()
    var router = useRouter()
    return { route, router }
  },
  data: function() {
    return {
      submitting: false,
      uploading: false,
      errorMsg: '',
      categories: [],
      tags: [],
      // 状态选项（与后端一致：0=草稿，1=已发布）
      statusOptions: [
        { label: '草稿', value: 0 },
        { label: '已发布', value: 1 }
      ],
      // 表单数据
      form: {
        title: '',
        categoryId: null,
        tagIds: [],
        summary: '',
        coverUrl: '',
        content: '',
        status: 0,
        publishTime: null
      }
    }
  },
  computed: {
    // 是否为编辑模式（URL 中有 id 参数）
    isEdit: function() {
      return !!this.route.params.id
    }
  },
  mounted: function() {
    this.loadCategories()
    this.loadTags()
    if (this.isEdit) {
      this.loadArticle()
    }
  },
  methods: {
    // 加载分类列表
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
    // 加载标签列表
    loadTags: function() {
      var self = this
      adminGetTags()
        .then(function(data) {
          self.tags = data || []
        })
        .catch(function(err) {
          console.error('加载标签失败:', err)
        })
    },
    // 加载文章详情（编辑模式）
    loadArticle: function() {
      var self = this
      adminGetArticleById(self.route.params.id)
        .then(function(data) {
          self.form.title = data.title || ''
          self.form.categoryId = data.categoryId || null
          self.form.summary = data.summary || ''
          self.form.coverUrl = data.coverUrl || ''
          self.form.content = data.content || ''
          self.form.status = data.status !== undefined ? data.status : 0
          // 回填标签（后端返回标签对象数组）
          self.form.tagIds = data.tags || []
        })
        .catch(function(err) {
          console.error('加载文章失败:', err)
          self.errorMsg = '加载文章失败'
        })
    },
    // 提交表单
    handleSubmit: function() {
      var self = this
      if (!self.form.title) {
        self.errorMsg = '请输入文章标题'
        return
      }
      self.submitting = true
      self.errorMsg = ''

      // 处理标签 ID 列表（兼容对象和字符串两种情况）
      var tagIds = (self.form.tagIds || []).map(function(t) {
        return typeof t === 'object' ? t.id : t
      }).filter(function(id) { return !!id })

      var payload = {
        title: self.form.title,
        categoryId: self.form.categoryId,
        tagIds: tagIds,
        summary: self.form.summary,
        coverUrl: self.form.coverUrl,
        content: self.form.content,
        status: self.form.status,
        publishTime: self.form.publishTime || null
      }

      var promise = self.isEdit
        ? updateArticle(self.route.params.id, payload)
        : createArticle(payload)

      promise
        .then(function() {
          self.router.push('/admin/articles')
        })
        .catch(function(err) {
          self.errorMsg = err.message || '保存失败，请重试'
        })
        .finally(function() {
          self.submitting = false
        })
    },
    // 返回列表页
    goBack: function() {
      this.router.push('/admin/articles')
    },
    triggerUpload: function() {
      this.$refs.fileInput.click()
    },
    handleFileChange: function(event) {
      var self = this
      var file = event.target.files[0]
      if (!file) return
      self.uploading = true
      uploadImage(file)
        .then(function(url) {
          self.form.coverUrl = url
        })
        .catch(function(err) {
          self.errorMsg = '图片上传失败：' + (err.message || '请重试')
        })
        .finally(function() {
          self.uploading = false
          // 清空 input，允许重复上传同一文件
          event.target.value = ''
        })
    }
  }
}
</script>
