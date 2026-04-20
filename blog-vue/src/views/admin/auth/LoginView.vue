<template>
  <v-app style="background: #f8f9fa;">
    <v-main>
      <div class="d-flex align-center justify-center" style="min-height: 100vh; padding: 24px;">
        <div style="width: 100%; max-width: 420px;">

          <!-- Logo 区域 -->
          <div class="text-center mb-8">
            <div style="
              width: 56px; height: 56px;
              background: #1a73e8;
              border-radius: 16px;
              display: flex; align-items: center; justify-content: center;
              margin: 0 auto 16px;
              box-shadow: 0 4px 12px rgba(26,115,232,0.3);
            ">
              <v-icon color="white" size="30">mdi-pencil</v-icon>
            </div>
            <h1 style="font-size: 24px; font-weight: 700; color: #202124; margin: 0 0 6px;">
              博客管理后台
            </h1>
            <p style="font-size: 14px; color: #80868b; margin: 0;">
              请使用管理员账号登录
            </p>
          </div>

          <!-- 登录卡片 -->
          <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; padding: 32px;">
            <v-form @submit.prevent="handleLogin">
              <div class="mb-4">
                <label style="font-size: 13px; font-weight: 500; color: #3c4043; display: block; margin-bottom: 6px;">用户名</label>
                <v-text-field
                  v-model="form.username"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-account-outline"
                  placeholder="请输入用户名"
                  hide-details="auto"
                  :rules="[function(v) { return !!v || '请输入用户名' }]"
                />
              </div>

              <div class="mb-6">
                <label style="font-size: 13px; font-weight: 500; color: #3c4043; display: block; margin-bottom: 6px;">密码</label>
                <v-text-field
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-lock-outline"
                  :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                  placeholder="请输入密码"
                  hide-details="auto"
                  :rules="[function(v) { return !!v || '请输入密码' }]"
                  @click:append-inner="showPassword = !showPassword"
                />
              </div>

              <v-alert
                v-if="errorMsg"
                type="error"
                variant="tonal"
                rounded="lg"
                class="mb-4"
                density="compact"
                prepend-icon="mdi-alert-circle-outline"
              >
                {{ errorMsg }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                :loading="loading"
                style="font-size: 15px; font-weight: 600; height: 48px;"
              >
                登录
              </v-btn>
            </v-form>
          </v-card>

          <p class="text-center mt-4" style="font-size: 12px; color: #9aa0a6;">
            默认账号：admin / admin123
          </p>
        </div>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import { login } from '../../../api/auth.js'
import { useAuthStore } from '../../../store/auth.js'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginView',
  setup: function() {
    var router = useRouter()
    var authStore = useAuthStore()
    return { router, authStore }
  },
  data: function() {
    return {
      form: { username: '', password: '' },
      loading: false,
      errorMsg: '',
      showPassword: false
    }
  },
  methods: {
    handleLogin: function() {
      var self = this
      if (!self.form.username || !self.form.password) {
        self.errorMsg = '请填写用户名和密码'
        return
      }
      self.loading = true
      self.errorMsg = ''
      login(self.form)
        .then(function(token) {
          self.authStore.setAuth(token)
          self.router.push('/admin/dashboard')
        })
        .catch(function(err) {
          self.errorMsg = err.message || '用户名或密码错误'
        })
        .finally(function() {
          self.loading = false
        })
    }
  }
}
</script>

