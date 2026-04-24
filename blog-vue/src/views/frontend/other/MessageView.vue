<template>
  <v-app style="background: var(--bg-primary);">
    <ReadingProgress />
    <FrontendNavBar variant="back" container-max-width="1100px" />

    <v-main>
      <v-container style="max-width: 760px; padding: 32px 16px;">
        <!-- 页面标题 -->
        <div class="d-flex align-center mb-6" style="gap: 12px;">
          <div style="width: 40px; height: 40px; background: #e8f0fe; border-radius: 10px; display: flex; align-items: center; justify-content: center;">
            <v-icon color="primary" size="22">mdi-message-text-outline</v-icon>
          </div>
          <div>
            <h1 style="font-size: 22px; font-weight: 700; color: #202124; margin: 0;">留言板</h1>
            <p style="font-size: 13px; color: #80868b; margin: 2px 0 0;">欢迎留下你的足迹 ✨</p>
          </div>
        </div>

        <!-- 留言表单 -->
        <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 24px;">
          <div class="pa-5">
            <div style="font-size: 15px; font-weight: 600; color: #202124; margin-bottom: 16px;">发表留言</div>

            <v-alert v-if="submitSuccess" type="success" variant="tonal" rounded="lg" density="compact" class="mb-4" closable @click:close="submitSuccess = false">
              留言提交成功！审核通过后将显示。
            </v-alert>

            <v-form ref="formRef" @submit.prevent="submitMessage">
              <v-row dense>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.nickname"
                    label="昵称"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-account-outline"
                    :rules="[function(v) { return !!v || '请填写昵称' }]"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="form.email"
                    label="邮箱（可选）"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-email-outline"
                  />
                </v-col>
                <v-col cols="12">
                  <v-textarea
                    v-model="form.content"
                    label="留言内容..."
                    variant="outlined"
                    rows="3"
                    :rules="[function(v) { return !!v || '请填写留言内容' }]"
                  />
                </v-col>
              </v-row>
              <div class="d-flex justify-end">
                <v-btn type="submit" color="primary" :loading="submitting" prepend-icon="mdi-send">
                  提交留言
                </v-btn>
              </div>
            </v-form>
          </div>
        </v-card>

        <!-- 留言列表 -->
        <div v-if="loading" class="d-flex justify-center py-8">
          <v-progress-circular indeterminate color="primary" />
        </div>

        <template v-else>
          <div v-if="messages.length === 0" class="text-center py-12" style="color: #80868b;">
            <v-icon size="48" color="grey-lighten-2">mdi-message-outline</v-icon>
            <p class="mt-3">还没有留言，来第一个留言吧！</p>
          </div>

          <div v-for="msg in messages" :key="msg.id" class="d-flex mb-4" style="gap: 12px;">
            <v-avatar :color="avatarColor(msg.nickname)" size="40" style="flex-shrink: 0;">
              <span class="text-white font-weight-bold text-body-2">{{ msg.nickname.charAt(0).toUpperCase() }}</span>
            </v-avatar>
            <div style="flex: 1;">
              <div class="d-flex align-center mb-1" style="gap: 8px;">
                <span style="font-size: 14px; font-weight: 600; color: #202124;">{{ msg.nickname }}</span>
                <span style="font-size: 12px; color: #9aa0a6;">{{ formatDate(msg.createTime) }}</span>
              </div>
              <div
                style="
                  background: white;
                  border: 1px solid #e8eaed;
                  border-radius: 0 12px 12px 12px;
                  padding: 10px 14px;
                  font-size: 14px;
                  color: #3c4043;
                  line-height: 1.6;
                "
              >
                {{ msg.content }}
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="pages > 1" class="d-flex justify-center mt-4">
            <v-pagination v-model="pageNum" :length="pages" :total-visible="7" @update:model-value="loadMessages" />
          </div>
        </template>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import ReadingProgress from '@/components/frontend/ReadingProgress.vue'
import FrontendNavBar from '@/components/frontend/FrontendNavBar.vue'
import request from '@/api/request.js'

export default {
  name: 'MessageView',
  components: { ReadingProgress, FrontendNavBar },
  data: function() {
    return {
      messages: [],
      loading: false,
      submitting: false,
      submitSuccess: false,
      pageNum: 1,
      pages: 1,
      form: { nickname: '', email: '', content: '' }
    }
  },
  mounted: function() {
    this.loadMessages()
  },
  methods: {
    loadMessages: function() {
      var self = this
      self.loading = true
      request({ method: 'get', url: '/api/messages', params: { pageNum: self.pageNum, pageSize: 20 } })
        .then(function(data) {
          self.messages = data.list || []
          self.pages = data.pages || 1
        })
        .catch(function() {})
        .finally(function() { self.loading = false })
    },
    submitMessage: function() {
      var self = this
      if (!self.form.nickname || !self.form.content) return
      self.submitting = true
      request({ method: 'post', url: '/api/messages', data: self.form })
        .then(function() {
          self.submitSuccess = true
          self.form = { nickname: '', email: '', content: '' }
          if (self.$refs.formRef) self.$refs.formRef.reset()
        })
        .catch(function(err) { console.error('提交留言失败:', err) })
        .finally(function() { self.submitting = false })
    },
    avatarColor: function(nickname) {
      var colors = ['#1a73e8', '#34a853', '#ea4335', '#fbbc04', '#9c27b0', '#00bcd4']
      return colors[nickname.charCodeAt(0) % colors.length]
    },
    formatDate: function(t) {
      if (!t) return ''
      return String(t).substring(0, 10)
    }
  }
}
</script>


