<template>
  <div class="sidebar-card blog-sidebar-section" style="padding: 16px 20px;">
    <div class="sidebar-section-title">邮箱订阅</div>
    <div class="sidebar-muted" style="font-size: 12px; margin-bottom: 10px;">订阅后可第一时间获取文章更新。</div>
    <v-text-field
      v-model="email"
      variant="outlined"
      density="compact"
      label="你的邮箱"
      hide-details
    />
    <v-btn color="primary" variant="tonal" block class="mt-2" :loading="loading" @click="submit">
      立即订阅
    </v-btn>
  </div>
</template>

<script>
import request from '@/api/request.js'

export default {
  name: 'SubscribeCard',
  data: function() {
    return { email: '', loading: false }
  },
  methods: {
    submit: function() {
      var self = this
      if (!self.email) {
        self.$toast.error('请输入邮箱')
        return
      }
      self.loading = true
      request({ method: 'post', url: '/api/subscribers', data: { email: self.email } })
        .then(function() {
          self.$toast.success('订阅成功')
          self.email = ''
        })
        .catch(function(err) {
          self.$toast.error(err.message || '订阅失败')
        })
        .finally(function() {
          self.loading = false
        })
    }
  }
}
</script>
