<template>
  <div>
    <!-- 草稿恢复提示条 -->
    <div v-if="draftRestored" style="
      background: #e8f5e9; border: 1px solid #a5d6a7; border-radius: 8px;
      padding: 10px 16px; margin-bottom: 16px;
      display: flex; align-items: center; justify-content: space-between;
      font-size: 13px; color: #2e7d32;
    ">
      <div style="display: flex; align-items: center; gap: 8px;">
        <v-icon size="16" color="success">mdi-restore</v-icon>
        已自动恢复上次未保存的草稿（{{ draftSavedAt }}）
      </div>
      <button @click="clearDraftBanner" style="background: none; border: none; cursor: pointer; color: #2e7d32; font-size: 12px; padding: 2px 8px; border-radius: 4px; transition: background 0.15s;" @mouseenter="$event.target.style.background='rgba(0,0,0,0.06)'" @mouseleave="$event.target.style.background='none'">
        知道了
      </button>
    </div>

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
          <div class="d-flex align-center justify-space-between mb-2">
            <div class="text-body-2 text-grey">文章内容（Markdown）</div>
            <div v-if="autoSaveIndicator" style="font-size: 12px; color: #9aa0a6; display: flex; align-items: center; gap: 4px;">
              <v-icon size="13" color="grey">mdi-content-save-outline</v-icon>
              已自动保存
            </div>
          </div>
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

var DRAFT_KEY = 'article_new_draft'

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
      statusOptions: [
        { label: '草稿', value: 0 },
        { label: '已发布', value: 1 }
      ],
      form: {
        title: '',
        categoryId: null,
        tagIds: [],
        summary: '',
        coverUrl: '',
        content: '',
        status: 0,
        publishTime: null
      },
      // 草稿相关
      draftRestored: false,
      draftSavedAt: '',
      autoSaveIndicator: false,
      _debounceTimer: null
    }
  },
  computed: {
    isEdit: function() {
      return !!this.route.params.id
    }
  },
  watch: {
    // 监听 form 变化，防抖 800ms 后自动保存草稿（仅新建模式）
    form: {
      deep: true,
      handler: function() {
        if (this.isEdit) return
        var self = this
        if (self._debounceTimer) clearTimeout(self._debounceTimer)
        self._debounceTimer = setTimeout(function() {
          self._saveDraft()
        }, 800)
      }
    }
  },
  mounted: function() {
    this.loadCategories()
    this.loadTags()
    if (this.isEdit) {
      this.loadArticle()
    } else {
      // 新建模式：自动静默恢复草稿
      this._restoreDraft()
    }
  },
  beforeUnmount: function() {
    // 离开页面时立即保存一次（仅新建模式且有内容）
    if (!this.isEdit) {
      this._saveDraft()
    }
    if (this._debounceTimer) clearTimeout(this._debounceTimer)
  },
  methods: {
    loadCategories: function() {
      var self = this
      adminGetCategories().then(function(data) { self.categories = data || [] }).catch(function() {})
    },
    loadTags: function() {
      var self = this
      adminGetTags().then(function(data) { self.tags = data || [] }).catch(function() {})
    },
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
          self.form.tagIds = data.tags || []
        })
        .catch(function(err) {
          console.error('加载文章失败:', err)
          self.errorMsg = '加载文章失败'
        })
    },
    handleSubmit: function() {
      var self = this
      if (!self.form.title) {
        self.errorMsg = '请输入文章标题'
        return
      }
      self.submitting = true
      self.errorMsg = ''

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
          // 发布成功后清除草稿
          try { localStorage.removeItem(DRAFT_KEY) } catch (e) {}
          self.router.push('/admin/articles')
        })
        .catch(function(err) {
          self.errorMsg = err.message || '保存失败，请重试'
        })
        .finally(function() {
          self.submitting = false
        })
    },
    goBack: function() {
      this.router.push('/admin/articles')
    },
    // 内部：保存草稿到 localStorage
    _saveDraft: function() {
      if (!this.form.title && !this.form.content) return
      try {
        var now = new Date()
        var timeStr = now.getHours() + ':' +
          String(now.getMinutes()).padStart(2, '0') + ':' +
          String(now.getSeconds()).padStart(2, '0')
        localStorage.setItem(DRAFT_KEY, JSON.stringify({
          title: this.form.title,
          content: this.form.content,
          summary: this.form.summary,
          coverUrl: this.form.coverUrl,
          categoryId: this.form.categoryId,
          status: this.form.status,
          savedAt: timeStr
        }))
        // 短暂显示"已自动保存"
        var self = this
        self.autoSaveIndicator = true
        setTimeout(function() { self.autoSaveIndicator = false }, 1500)
      } catch (e) {}
    },
    // 内部：静默恢复草稿（不弹窗）
    _restoreDraft: function() {
      try {
        var raw = localStorage.getItem(DRAFT_KEY)
        if (!raw) return
        var data = JSON.parse(raw)
        if (!data.title && !data.content) return
        // 直接恢复，不询问
        this.form.title = data.title || ''
        this.form.content = data.content || ''
        this.form.summary = data.summary || ''
        this.form.coverUrl = data.coverUrl || ''
        if (data.categoryId) this.form.categoryId = data.categoryId
        if (data.status !== undefined) this.form.status = data.status
        this.draftSavedAt = data.savedAt || ''
        this.draftRestored = true
        // 恢复后立即删除，避免下次点新建还显示旧内容
        // 后续编辑会重新触发 watch 保存新草稿
        localStorage.removeItem(DRAFT_KEY)
      } catch (e) {}
    },
    clearDraftBanner: function() {
      this.draftRestored = false
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
          event.target.value = ''
        })
    }
  }
}
</script>
