<template>
  <div>
    <v-app-bar
      class="frontend-nav-bar"
      elevation="0"
      :style="{
        background: 'var(--bg-card)',
        borderBottom: '1px solid var(--border-color)',
        color: 'var(--text-primary)'
      }"
    >
      <v-container
        :style="{
          maxWidth: containerMaxWidth,
          display: 'flex',
          alignItems: 'center',
          padding: '0 16px'
        }"
      >
        <template v-if="variant === 'back'">
          <v-btn icon variant="text" style="color: var(--text-secondary);" @click="goBack">
            <v-icon>mdi-arrow-left</v-icon>
          </v-btn>
          <router-link to="/" class="d-flex align-center text-decoration-none ml-2" style="gap: 6px;">
            <v-icon color="primary" size="22">mdi-pencil-circle</v-icon>
            <span
              class="navbar-title"
              style="font-size: 16px; font-weight: 600; color: var(--text-primary);"
            >我的博客</span>
          </router-link>
          <v-spacer />
          <slot name="actions" />
        </template>

        <template v-else>
          <router-link to="/" class="d-flex align-center text-decoration-none" style="gap: 8px;">
            <v-icon color="primary" size="28">mdi-pencil-circle</v-icon>
            <span
              class="navbar-title d-none d-sm-inline"
              style="font-size: 20px; font-weight: 600; color: var(--text-primary); letter-spacing: -0.3px;"
            >我的博客</span>
          </router-link>
          <v-spacer />
          <div class="d-none d-sm-flex align-center" style="gap: 4px;">
            <v-btn
              variant="text"
              to="/"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/') }"
              style="min-width: auto;"
            >首页</v-btn>
            <v-btn
              variant="text"
              to="/articles"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/articles') }"
              style="min-width: auto;"
            >文章</v-btn>
            <v-btn
              variant="text"
              to="/archive"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/archive') }"
              style="min-width: auto;"
            >归档</v-btn>
            <v-btn
              variant="text"
              to="/about"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/about') }"
              style="min-width: auto;"
            >关于</v-btn>
            <v-btn
              variant="text"
              to="/message"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/message') }"
              style="min-width: auto;"
            >留言</v-btn>
            <v-btn
              variant="text"
              to="/search"
              class="nav-link"
              :class="{ 'nav-link--active': isPathActive('/search') }"
              style="min-width: auto;"
            >
              <v-icon size="18" class="mr-1">mdi-magnify</v-icon>搜索
            </v-btn>
            <DarkModeToggle class="ml-1" />
          </div>
          <v-btn
            icon
            variant="text"
            class="d-flex d-sm-none"
            style="color: var(--text-secondary);"
            @click="mobileMenu = !mobileMenu"
          >
            <v-icon>{{ mobileMenu ? 'mdi-close' : 'mdi-menu' }}</v-icon>
          </v-btn>
        </template>
      </v-container>
    </v-app-bar>

    <div
      v-if="variant === 'main' && mobileMenu"
      class="frontend-nav-mobile d-sm-none nav-mobile-panel"
      style="
        position: fixed; top: 64px; left: 0; right: 0;
        z-index: 100; padding: 8px 0;
        background: var(--bg-card);
        border-bottom: 1px solid var(--border-color);
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
      "
    >
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="mobile-nav-link d-flex align-center"
        :class="{ 'mobile-nav-link--active': $route.path === item.to }"
        style="gap: 10px; padding: 12px 20px; text-decoration: none; font-size: 15px; font-weight: 500;"
        @click="mobileMenu = false"
      >
        <v-icon size="18" color="primary">{{ item.icon }}</v-icon>
        <span style="color: var(--text-primary);">{{ item.label }}</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import DarkModeToggle from '@/components/frontend/DarkModeToggle.vue'

export default {
  name: 'FrontendNavBar',
  components: { DarkModeToggle },
  props: {
    variant: {
      type: String,
      default: 'main',
      validator: function(v) { return v === 'main' || v === 'back' }
    },
    containerMaxWidth: {
      type: String,
      default: '1200px'
    }
  },
  data: function() {
    return {
      mobileMenu: false,
      navItems: [
        { to: '/', label: '首页', icon: 'mdi-home-outline' },
        { to: '/articles', label: '文章', icon: 'mdi-file-document-outline' },
        { to: '/archive', label: '归档', icon: 'mdi-archive-outline' },
        { to: '/about', label: '关于', icon: 'mdi-account-circle-outline' },
        { to: '/message', label: '留言', icon: 'mdi-message-text-outline' },
        { to: '/search', label: '搜索', icon: 'mdi-magnify' }
      ]
    }
  },
  methods: {
    goBack: function() {
      this.$router.back()
    },
    isPathActive: function(path) {
      return this.$route && this.$route.path === path
    }
  }
}
</script>

<style scoped>
.mobile-nav-link {
  color: var(--text-primary) !important;
  transition: background 0.15s;
}
.mobile-nav-link:hover,
.mobile-nav-link:focus-visible {
  background: var(--bg-hover);
}
.mobile-nav-link--active {
  background: rgba(26, 115, 232, 0.1) !important;
  border-left: 3px solid #1a73e8;
}
</style>
<style>
/* 暗色下移动端当前项（非 scoped 以便命中 body） */
body.dark-mode .nav-mobile-panel .mobile-nav-link--active {
  background: rgba(138, 180, 248, 0.12) !important;
  border-left-color: #8ab4f8;
}
</style>
