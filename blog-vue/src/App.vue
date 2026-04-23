<template>
  <v-app>
    <!-- 主内容区（带路由过渡动画） -->
    <router-view v-slot="{ Component }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>

    <ThemeAmbientLayer
      v-if="isFrontend"
      :enabled="frontendThemeEnabled && frontendAmbientEnabled"
      :theme="frontendTheme"
    />

    <!-- 粒子飘落特效（仅前台，可通过系统配置控制） -->
    <ParticleCanvas
      v-if="isFrontend && particleEnabled"
      :type="particleType"
      :count="particleCount"
    />

    <!-- 鼠标点击特效（仅前台） -->
    <ClickEffect v-if="isFrontend && clickEffectEnabled === 'true'" />
    <!-- 彩色粒子点击特效 -->
    <ColorClickEffect v-if="isFrontend && clickEffectEnabled === 'color'" />

    <!-- 图片灯箱（全局，自动监听 markdown-body 内图片点击） -->
    <ImageLightbox v-if="isFrontend" />

    <!-- 鼠标轨迹特效（仅前台，可配置开关） -->
    <MouseTrail v-if="isFrontend && mouseTrailEnabled" />

    <!-- 音乐播放器（仅前台，有歌单时显示） -->
    <MusicPlayer v-if="isFrontend" :playlist="musicPlaylist" />

    <!-- Live2D 看板娘（仅前台，可通过系统配置控制） -->
    <Live2DWidget :enabled="isFrontend && live2dEnabled" />

    <!-- 彩带礼花（特殊日期自动触发） -->
    <Confetti v-if="isFrontend" />

    <ThemeSwitcher
      v-if="isFrontend"
      :enabled="frontendThemeEnabled && frontendThemeSwitcherEnabled"
      :theme="frontendTheme"
      @theme-change="setFrontendTheme"
    />
  </v-app>
</template>

<script>
import request from './api/request.js'
import ParticleCanvas from './components/frontend/ParticleCanvas.vue'
import ClickEffect from './components/frontend/ClickEffect.vue'
import ImageLightbox from './components/frontend/ImageLightbox.vue'
import MouseTrail from './components/frontend/MouseTrail.vue'
import MusicPlayer from './components/frontend/MusicPlayer.vue'
import Confetti from './components/frontend/Confetti.vue'
import ColorClickEffect from './components/frontend/ColorClickEffect.vue'
import Live2DWidget from './components/frontend/Live2DWidget.vue'
import ThemeAmbientLayer from './components/frontend/ThemeAmbientLayer.vue'
import ThemeSwitcher from './components/frontend/ThemeSwitcher.vue'

export default {
  name: 'App',
  components: { ParticleCanvas, ClickEffect, ImageLightbox, MouseTrail, MusicPlayer, Confetti, ColorClickEffect, Live2DWidget, ThemeAmbientLayer, ThemeSwitcher },
  data: function() {
    return {
      particleEnabled: false,
      particleType: 'sakura',
      particleCount: 25,
      clickEffectEnabled: 'true',
      live2dEnabled: false,
      mouseTrailEnabled: false,
      musicPlaylist: [],
      frontendThemeEnabled: true,
      frontendThemeSwitcherEnabled: true,
      frontendAmbientEnabled: true,
      frontendTheme: 'sakura'
    }
  },
  computed: {
    isFrontend: function() {
      return this.$route && !this.$route.path.startsWith('/admin')
    }
  },
  mounted: function() {
    this.applyFrontendTheme(this.getStoredTheme() || this.frontendTheme)
    this.loadEffectConfig()
    // 复制保护：复制文章内容时追加版权声明
    document.addEventListener('copy', function(e) {
      var selection = window.getSelection()
      if (!selection || selection.toString().length < 30) return
      if (!window.location.pathname.startsWith('/article/')) return
      var original = selection.toString()
      var notice = '\n\n---\n版权声明：本文采用 CC BY-NC-SA 4.0 协议，转载请注明出处。\n原文链接：' + window.location.href
      if (e.clipboardData) {
        e.clipboardData.setData('text/plain', original + notice)
        e.preventDefault()
      }
    })
  },
  methods: {
    getStoredTheme: function() {
      try {
        var theme = localStorage.getItem('frontend_theme')
        return this.isValidTheme(theme) ? theme : ''
      } catch (e) {
        return ''
      }
    },
    isValidTheme: function(theme) {
      return ['classic', 'sakura', 'neon', 'starry'].indexOf(theme) >= 0
    },
    setFrontendTheme: function(theme) {
      if (!this.isValidTheme(theme)) return
      this.frontendTheme = theme
      try { localStorage.setItem('frontend_theme', theme) } catch (e) {}
      this.applyFrontendTheme(theme)
    },
    applyFrontendTheme: function(theme) {
      document.body.setAttribute('data-front-theme', this.isValidTheme(theme) ? theme : 'sakura')
    },
    loadEffectConfig: function() {
      var self = this
      request({ method: 'get', url: '/api/site-config' })
        .then(function(data) {
          if (!data) return
          self.particleEnabled = data.particle_enabled === 'true'
          self.particleType = data.particle_type || 'sakura'
          self.particleCount = parseInt(data.particle_count || '25')
          self.clickEffectEnabled = data.click_effect_enabled || 'none'
          self.live2dEnabled = data.live2d_enabled === 'true'
          self.mouseTrailEnabled = data.mouse_trail_enabled === 'true'
          self.frontendThemeEnabled = data.frontend_theme_enabled !== 'false'
          self.frontendThemeSwitcherEnabled = data.frontend_theme_switcher_enabled !== 'false'
          self.frontendAmbientEnabled = data.frontend_ambient_enabled !== 'false'
          var defaultTheme = self.isValidTheme(data.frontend_theme_default) ? data.frontend_theme_default : 'sakura'
          self.frontendTheme = self.frontendThemeEnabled ? (self.getStoredTheme() || defaultTheme) : 'classic'
          self.applyFrontendTheme(self.frontendTheme)
          // 音乐播放列表
          if (data.music_playlist) {
            try {
              var pl = JSON.parse(data.music_playlist)
              self.musicPlaylist = Array.isArray(pl) ? pl : []
            } catch (e) {}
          }
        })
        .catch(function() {
          self.clickEffectEnabled = 'true'
          self.particleEnabled = true
        })
    }
  }
}
</script>

<style>
/* 全局 Google 风格基础样式 */
* {
  box-sizing: border-box;
}

/* CSS 变量：亮色主题默认值 */
:root {
  --bg-primary: #f8f9fa;
  --bg-card: #ffffff;
  --bg-hover: #f1f3f4;
  --text-primary: #202124;
  --text-secondary: #5f6368;
  --text-muted: #80868b;
  --border-color: #e8eaed;
  --link-color: #1a73e8;
  --front-bg: #f8fafc;
  --front-surface: rgba(255,255,255,0.82);
  --front-card: rgba(255,255,255,0.9);
  --front-text: #172033;
  --front-text-soft: #5e6678;
  --front-muted: #8a93a6;
  --front-border: rgba(125,145,190,0.2);
  --front-accent: #ec4899;
  --front-accent-2: #38bdf8;
  --front-shadow: 0 18px 45px rgba(88, 92, 130, 0.12);
  --front-radius: 16px;
}

body[data-front-theme="classic"] {
  --front-bg: #f8f9fa;
  --front-surface: rgba(255,255,255,0.9);
  --front-card: #ffffff;
  --front-text: #202124;
  --front-text-soft: #5f6368;
  --front-muted: #80868b;
  --front-border: #e8eaed;
  --front-accent: #1a73e8;
  --front-accent-2: #34a853;
  --front-shadow: 0 12px 32px rgba(26,115,232,0.1);
}

body[data-front-theme="sakura"] {
  --front-bg: #fff7fb;
  --front-surface: rgba(255,255,255,0.78);
  --front-card: rgba(255,255,255,0.86);
  --front-text: #251827;
  --front-text-soft: #735a71;
  --front-muted: #9d829a;
  --front-border: rgba(244,114,182,0.24);
  --front-accent: #ec4899;
  --front-accent-2: #38bdf8;
  --front-shadow: 0 20px 48px rgba(236,72,153,0.16);
}

body[data-front-theme="neon"] {
  --front-bg: #0f172a;
  --front-surface: rgba(15,23,42,0.82);
  --front-card: rgba(15,23,42,0.78);
  --front-text: #f8fafc;
  --front-text-soft: #cbd5e1;
  --front-muted: #94a3b8;
  --front-border: rgba(34,211,238,0.26);
  --front-accent: #22d3ee;
  --front-accent-2: #d946ef;
  --front-shadow: 0 22px 56px rgba(34,211,238,0.18);
}

body[data-front-theme="starry"] {
  --front-bg: #0b1020;
  --front-surface: rgba(15,23,42,0.8);
  --front-card: rgba(20,28,50,0.78);
  --front-text: #f8fafc;
  --front-text-soft: #cbd5e1;
  --front-muted: #94a3b8;
  --front-border: rgba(147,197,253,0.24);
  --front-accent: #60a5fa;
  --front-accent-2: #facc15;
  --front-shadow: 0 22px 56px rgba(96,165,250,0.18);
}

/* 暗色主题变量覆盖 */
body.dark-mode {
  --bg-primary: #202124;
  --bg-card: #292a2d;
  --bg-hover: #35363a;
  --text-primary: #e8eaed;
  --text-secondary: #9aa0a6;
  --text-muted: #80868b;
  --border-color: #3c4043;
  --link-color: #8ab4f8;
}

body {
  font-family: 'Google Sans', 'Roboto', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.front-shell {
  min-height: 100vh;
  background:
    linear-gradient(135deg, color-mix(in srgb, var(--front-bg) 92%, white), var(--front-bg)) !important;
  color: var(--front-text);
  position: relative;
}

.front-shell .v-main,
.front-shell .v-container,
.front-shell .v-row,
.front-shell .v-col {
  position: relative;
  z-index: 1;
}

.front-app-bar {
  background: color-mix(in srgb, var(--front-surface) 92%, transparent) !important;
  border-bottom: 1px solid var(--front-border) !important;
  backdrop-filter: blur(18px);
  color: var(--front-text) !important;
}

.front-card {
  background: var(--front-card) !important;
  border: 1px solid var(--front-border) !important;
  box-shadow: var(--front-shadow) !important;
  backdrop-filter: blur(18px);
}

.front-card:hover {
  border-color: color-mix(in srgb, var(--front-accent) 42%, var(--front-border)) !important;
}

.front-title {
  color: var(--front-text) !important;
}

.front-muted {
  color: var(--front-muted) !important;
}

.front-soft {
  color: var(--front-text-soft) !important;
}

.front-footer {
  background: color-mix(in srgb, var(--front-surface) 88%, transparent) !important;
  border-top: 1px solid var(--front-border) !important;
  backdrop-filter: blur(16px);
}

.front-nav-mobile {
  background: color-mix(in srgb, var(--front-surface) 94%, transparent) !important;
  border-bottom: 1px solid var(--front-border) !important;
  box-shadow: var(--front-shadow);
  backdrop-filter: blur(18px);
}

.front-gradient-text {
  color: var(--front-accent);
  background: linear-gradient(135deg, var(--front-accent), var(--front-accent-2));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 链接样式 */
a {
  color: #1a73e8;
  text-decoration: none;
  transition: color 0.2s;
}
a:hover {
  color: #1557b0;
}

/* 卡片悬停效果 */
.v-card {
  transition: box-shadow 0.2s ease, transform 0.2s ease !important;
}
.v-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.12), 0 1px 3px rgba(0,0,0,0.08) !important;
}

/* 可点击卡片 */
.card-clickable {
  cursor: pointer;
}
.card-clickable:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(26,115,232,0.12), 0 2px 6px rgba(0,0,0,0.08) !important;
}

/* Markdown 内容样式 */
.markdown-body {
  line-height: 1.8;
  color: #202124;
  font-size: var(--article-font-size, 16px);
}
.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4 {
  color: #202124;
  font-weight: 600;
  margin-top: 1.5em;
  margin-bottom: 0.5em;
}
.markdown-body h1 { font-size: 1.8em; border-bottom: 2px solid #e8eaed; padding-bottom: 0.3em; }
.markdown-body h2 { font-size: 1.4em; border-bottom: 1px solid #e8eaed; padding-bottom: 0.2em; }
.markdown-body p { margin-bottom: 1em; }
.markdown-body code {
  background: #f1f3f4;
  color: #c62828;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  font-family: 'Roboto Mono', 'Courier New', monospace;
}
.markdown-body pre {
  background: #f8f9fa;
  border: 1px solid #e8eaed;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
  margin-bottom: 1em;
}
.markdown-body pre code {
  background: none;
  color: #202124;
  padding: 0;
}
.markdown-body blockquote {
  border-left: 4px solid #1a73e8;
  margin: 0 0 1em;
  padding: 8px 16px;
  background: #e8f0fe;
  border-radius: 0 8px 8px 0;
  color: #3c4043;
}
.markdown-body ul,
.markdown-body ol {
  padding-left: 1.5em;
  margin-bottom: 1em;
}
.markdown-body li { margin-bottom: 0.3em; }
.markdown-body a { color: #1a73e8; }
.markdown-body img {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}
.markdown-body table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1em;
}
.markdown-body th,
.markdown-body td {
  border: 1px solid #e8eaed;
  padding: 8px 12px;
  text-align: left;
}
.markdown-body th {
  background: #f1f3f4;
  font-weight: 600;
}
.markdown-body tr:nth-child(even) td {
  background: #f8f9fa;
}

/* 文字截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 导航栏链接 */
.nav-link {
  color: #3c4043 !important;
  font-weight: 500;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background 0.2s, color 0.2s;
}
.nav-link:hover {
  background: #e8f0fe;
  color: #1a73e8 !important;
}
.nav-link.active {
  color: #1a73e8 !important;
  background: #e8f0fe;
}

/* 标签云样式 */
.tag-chip {
  transition: all 0.2s;
}
.tag-chip:hover {
  background: #e8f0fe !important;
  color: #1a73e8 !important;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: #f1f3f4;
}
::-webkit-scrollbar-thumb {
  background: #bdc1c6;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #80868b;
}

/* 加载旋转动画 */
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 页面路由切换过渡 */
.page-fade-enter-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* 全局平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 标题锚点偏移，避免被固定导航栏遮挡 */
.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4 {
  scroll-margin-top: 80px;
}

/* 打印样式 */
@media print {
  .v-app-bar, .v-navigation-drawer, .v-footer,
  .ReadingProgress, .TableOfContents,
  .v-btn, button:not(.print-keep),
  .v-snackbar, .v-alert,
  [class*="sidebar"], [class*="related"],
  [class*="comment"] {
    display: none !important;
  }
  .v-main { padding: 0 !important; }
  .markdown-body { font-size: 14px; line-height: 1.8; }
  body { background: white !important; }
}

/* ===== 暗色主题全局覆盖 ===== */
body.dark-mode .v-app-bar {
  background: #292a2d !important;
  border-bottom-color: #3c4043 !important;
}
body.dark-mode .v-card {
  background: #292a2d !important;
  border-color: #3c4043 !important;
}
body.dark-mode .v-footer {
  background: #292a2d !important;
  border-top-color: #3c4043 !important;
}
body.dark-mode .v-table {
  background: #292a2d !important;
  color: #e8eaed !important;
}
body.dark-mode .v-table thead tr {
  background: #35363a !important;
}
body.dark-mode .v-table tbody tr:hover {
  background: #35363a !important;
}
body.dark-mode .markdown-body {
  color: #e8eaed;
}
body.dark-mode .markdown-body code {
  background: #35363a;
  color: #f28b82;
}
body.dark-mode .markdown-body pre {
  background: #35363a;
  border-color: #3c4043;
}
body.dark-mode .markdown-body pre code {
  color: #e8eaed;
}
body.dark-mode .markdown-body blockquote {
  background: #35363a;
  border-left-color: #8ab4f8;
  color: #9aa0a6;
}
body.dark-mode .markdown-body th {
  background: #35363a;
}
body.dark-mode .markdown-body td,
body.dark-mode .markdown-body th {
  border-color: #3c4043;
}
body.dark-mode .markdown-body tr:nth-child(even) td {
  background: #35363a;
}
body.dark-mode a {
  color: #8ab4f8;
}
body.dark-mode a:hover {
  color: #aecbfa;
}
/* 前台文章列表页暗色 */
body.dark-mode .v-app[style*="background: #f8f9fa"] {
  background: #202124 !important;
}
/* 侧边栏暗色 */
body.dark-mode .v-navigation-drawer {
  background: #292a2d !important;
  border-right-color: #3c4043 !important;
}

.front-shell .markdown-body,
.front-shell .markdown-body h1,
.front-shell .markdown-body h2,
.front-shell .markdown-body h3,
.front-shell .markdown-body h4 {
  color: var(--front-text);
}

.front-shell .markdown-body pre,
.front-shell .markdown-body code,
.front-shell .markdown-body th,
.front-shell .markdown-body tr:nth-child(even) td {
  background: color-mix(in srgb, var(--front-accent) 8%, var(--front-card));
}

.front-shell .markdown-body pre,
.front-shell .markdown-body td,
.front-shell .markdown-body th,
.front-shell .markdown-body h1,
.front-shell .markdown-body h2 {
  border-color: var(--front-border);
}

.front-shell .markdown-body blockquote {
  background: color-mix(in srgb, var(--front-accent) 9%, transparent);
  border-left-color: var(--front-accent);
  color: var(--front-text-soft);
}

.front-shell .nav-link {
  color: var(--front-text-soft) !important;
}

.front-shell .nav-link:hover,
.front-shell .nav-link.active {
  background: color-mix(in srgb, var(--front-accent) 12%, transparent);
  color: var(--front-accent) !important;
}

.front-shell .tag-chip:hover {
  background: color-mix(in srgb, var(--front-accent) 12%, transparent) !important;
  color: var(--front-accent) !important;
}
</style>
