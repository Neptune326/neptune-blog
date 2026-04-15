<template>
  <v-app>
    <!-- Mock 模式提示条 -->
    <div
      v-if="isMockMode"
      style="
        position: fixed;
        top: 0; left: 0; right: 0;
        z-index: 9999;
        background: #f29900;
        color: white;
        text-align: center;
        font-size: 13px;
        font-weight: 500;
        padding: 6px 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
      "
    >
      <span>⚡ Mock 模式</span>
      <span style="opacity: 0.85; font-weight: 400;">后端服务未启动，当前展示模拟数据。启动后端后刷新页面即可切换到真实数据。</span>
      <button
        @click="dismissBanner"
        style="
          background: rgba(255,255,255,0.25);
          border: none;
          color: white;
          cursor: pointer;
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          margin-left: 8px;
        "
      >
        知道了
      </button>
    </div>

    <!-- 检测中：全屏加载 -->
    <div
      v-if="!ready"
      style="
        position: fixed; inset: 0;
        background: #f8f9fa;
        display: flex; align-items: center; justify-content: center;
        z-index: 9990;
      "
    >
      <div style="text-align: center;">
        <div style="
          width: 40px; height: 40px;
          border: 3px solid #e8eaed;
          border-top-color: #1a73e8;
          border-radius: 50%;
          animation: spin 0.8s linear infinite;
          margin: 0 auto 12px;
        "></div>
        <div style="font-size: 13px; color: #80868b;">正在连接服务...</div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div v-if="ready" :style="isMockMode ? 'padding-top: 36px;' : ''">
      <router-view />
    </div>
  </v-app>
</template>

<script>
import { detectBackend, isMockMode } from './api/request.js'

export default {
  name: 'App',
  data: function() {
    return {
      showMockBanner: false,
      bannerDismissed: false,
      ready: false
    }
  },
  mounted: function() {
    var self = this
    // 先检测后端，确定 mock 模式后再渲染页面（避免组件先发请求再确定 mock 状态）
    detectBackend().then(function(backendOk) {
      self.showMockBanner = !backendOk
      self.ready = true
    })
  },
  computed: {
    isMockMode: function() {
      return this.showMockBanner && !this.bannerDismissed
    }
  },
  methods: {
    dismissBanner: function() {
      this.bannerDismissed = true
    }
  }
}
</script>

<style>
/* 全局 Google 风格基础样式 */
* {
  box-sizing: border-box;
}

body {
  font-family: 'Google Sans', 'Roboto', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  background-color: #f8f9fa;
  color: #202124;
  -webkit-font-smoothing: antialiased;
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
  font-size: 16px;
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
</style>
