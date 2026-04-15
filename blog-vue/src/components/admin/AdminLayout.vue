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
      navItems: [
        { title: '仪表盘', icon: 'mdi-view-dashboard-outline', to: '/admin/dashboard' },
        { title: '文章管理', icon: 'mdi-file-document-outline', to: '/admin/articles' },
        { title: '分类管理', icon: 'mdi-folder-outline', to: '/admin/categories' },
        { title: '标签管理', icon: 'mdi-tag-outline', to: '/admin/tags' },
        { title: '评论管理', icon: 'mdi-comment-outline', to: '/admin/comments' }
      ]
    }
  },
  computed: {
    currentTitle: function() {
      var map = {
        '/admin/dashboard': '仪表盘',
        '/admin/articles': '文章管理',
        '/admin/articles/edit': '新建文章',
        '/admin/categories': '分类管理',
        '/admin/tags': '标签管理',
        '/admin/comments': '评论管理'
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
    }
  }
}
</script>
