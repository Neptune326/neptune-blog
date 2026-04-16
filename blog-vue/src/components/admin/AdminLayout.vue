<template>
  <v-app style="background: #f8f9fa;">
    <!-- 左侧导航栏 —— Google 风格 -->
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      permanent
      color="white"
      style="border-right: 1px solid #e8eaed;"
    >
      <!-- Logo 区域 -->
      <div
        class="d-flex align-center pa-4"
        style="gap: 10px; min-height: 64px; border-bottom: 1px solid #e8eaed;"
      >
        <div
          style="
            width: 36px; height: 36px;
            background: #1a73e8;
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
          "
        >
          <v-icon color="white" size="20">mdi-pencil</v-icon>
        </div>
        <span
          v-if="!rail"
          style="font-size: 15px; font-weight: 700; color: #202124; white-space: nowrap;"
        >
          博客管理
        </span>
        <v-spacer v-if="!rail" />
        <v-btn
          v-if="!rail"
          icon
          variant="text"
          size="small"
          @click="rail = true"
          style="color: #80868b;"
        >
          <v-icon size="18">mdi-chevron-left</v-icon>
        </v-btn>
      </div>

      <!-- 展开按钮（收起状态） -->
      <div v-if="rail" class="d-flex justify-center pa-2">
        <v-btn
          icon
          variant="text"
          size="small"
          @click="rail = false"
          style="color: #80868b;"
        >
          <v-icon size="18">mdi-chevron-right</v-icon>
        </v-btn>
      </div>

      <!-- 导航菜单 -->
      <v-list density="compact" nav class="pa-2">
        <v-list-item
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          :value="item.to"
          rounded="lg"
          class="mb-1"
          style="min-height: 44px;"
          active-color="primary"
        >
          <template #prepend>
            <v-icon :size="20" style="margin-right: 12px;">{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title
            style="font-size: 14px; font-weight: 500;"
          >
            {{ item.title }}
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- 顶部工具栏 -->
    <v-app-bar
      color="white"
      elevation="0"
      style="border-bottom: 1px solid #e8eaed;"
    >
      <v-app-bar-title>
        <span style="font-size: 16px; font-weight: 600; color: #202124;">
          {{ currentTitle }}
        </span>
      </v-app-bar-title>
      <v-spacer />

      <!-- 修改密码 -->
      <v-btn
        icon
        variant="text"
        @click="pwdDialog = true"
        style="color: #5f6368;"
        title="修改密码"
        class="mr-1"
      >
        <v-icon size="20">mdi-lock-reset</v-icon>
      </v-btn>

      <!-- 退出登录 -->
      <v-btn
        variant="outlined"
        color="error"
        size="small"
        prepend-icon="mdi-logout"
        @click="handleLogout"
        class="mr-3"
        style="font-size: 13px;"
      >
        退出登录
      </v-btn>
    </v-app-bar>

    <!-- 修改密码弹窗 -->
    <v-dialog v-model="pwdDialog" max-width="420">
      <v-card rounded="xl">
        <div class="pa-6">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 20px;">修改密码</div>
          <v-form ref="pwdFormRef" @submit.prevent="handleChangePwd">
            <v-text-field
              v-model="pwdForm.oldPassword"
              label="旧密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-outline"
              class="mb-3"
              :rules="[function(v) { return !!v || '请输入旧密码' }]"
            />
            <v-text-field
              v-model="pwdForm.newPassword"
              label="新密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-check-outline"
              class="mb-3"
              :rules="[
                function(v) { return !!v || '请输入新密码' },
                function(v) { return v.length >= 6 || '密码至少 6 位' }
              ]"
            />
            <v-text-field
              v-model="pwdForm.confirmPassword"
              label="确认新密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-check-outline"
              :rules="[
                function(v) { return !!v || '请确认新密码' },
                function(v) { return v === pwdForm.newPassword || '两次密码不一致' }
              ]"
            />
            <v-alert v-if="pwdError" type="error" variant="tonal" density="compact" rounded="lg" class="mb-3">
              {{ pwdError }}
            </v-alert>
            <div class="d-flex justify-end" style="gap: 8px;">
              <v-btn variant="text" @click="pwdDialog = false">取消</v-btn>
              <v-btn color="primary" type="submit" :loading="pwdLoading">确认修改</v-btn>
            </div>
          </v-form>
        </div>
      </v-card>
    </v-dialog>

    <!-- 内容区 -->
    <v-main style="background: #f8f9fa;">
      <v-container fluid style="padding: 24px;">
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { logout } from '../../api/auth.js'
import request from '../../api/request.js'
import { useAuthStore } from '../../store/auth.js'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'AdminLayout',
  setup: function() {
    var router = useRouter()
    var route = useRoute()
    var authStore = useAuthStore()
    return { router, route, authStore }
  },
  data: function() {
    return {
      drawer: true,
      rail: false,
      pwdDialog: false,
      pwdLoading: false,
      pwdError: '',
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      navItems: [
        { title: '仪表盘', icon: 'mdi-view-dashboard-outline', to: '/admin/dashboard' },
        { title: '文章管理', icon: 'mdi-file-document-outline', to: '/admin/articles' },
        { title: '文章系列', icon: 'mdi-book-open-variant-outline', to: '/admin/series' },
        { title: '分类管理', icon: 'mdi-folder-outline', to: '/admin/categories' },
        { title: '标签管理', icon: 'mdi-tag-outline', to: '/admin/tags' },
        { title: '评论管理', icon: 'mdi-comment-outline', to: '/admin/comments' },
        { title: '留言管理', icon: 'mdi-message-text-outline', to: '/admin/messages' },
        { title: '操作日志', icon: 'mdi-clipboard-text-clock-outline', to: '/admin/logs/operation' },
        { title: '登录日志', icon: 'mdi-login-variant', to: '/admin/logs/login' },
        { title: '关于我', icon: 'mdi-account-circle-outline', to: '/admin/about' },
        { title: '管理员账号', icon: 'mdi-account-key-outline', to: '/admin/admin-users' },
        { title: '系统设置', icon: 'mdi-cog-outline', to: '/admin/sys-config' }
      ]
    }
  },
  computed: {
    currentTitle: function() {
      var map = {
        '/admin/dashboard': '仪表盘',
        '/admin/articles': '文章管理',
        '/admin/articles/edit': '新建文章',
        '/admin/series': '文章系列',
        '/admin/categories': '分类管理',
        '/admin/tags': '标签管理',
        '/admin/comments': '评论管理',
        '/admin/messages': '留言管理',
        '/admin/logs/operation': '操作日志',
        '/admin/logs/login': '登录日志',
        '/admin/about': '关于我',
        '/admin/admin-users': '管理员账号',
        '/admin/sys-config': '系统设置'
      }
      return map[this.route.path] || '博客管理'
    }
  },
  methods: {
    handleLogout: function() {
      var self = this
      logout().catch(function() {}).finally(function() {
        self.authStore.clearAuth()
        self.router.push('/admin/login')
      })
    },
    handleChangePwd: function() {
      var self = this
      if (self.pwdForm.newPassword !== self.pwdForm.confirmPassword) {
        self.pwdError = '两次密码不一致'
        return
      }
      self.pwdLoading = true
      self.pwdError = ''
      request({
        method: 'post',
        url: '/api/admin/auth/change-password',
        data: { oldPassword: self.pwdForm.oldPassword, newPassword: self.pwdForm.newPassword }
      })
        .then(function() {
          self.pwdDialog = false
          self.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
          // 修改成功后重新登录
          self.authStore.clearAuth()
          self.router.push('/admin/login')
        })
        .catch(function(err) {
          self.pwdError = err.message || '修改失败，请检查旧密码是否正确'
        })
        .finally(function() {
          self.pwdLoading = false
        })
    }
  }
}
</script>
