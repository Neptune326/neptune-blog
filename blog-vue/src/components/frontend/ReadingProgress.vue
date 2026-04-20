<template>
  <!-- 阅读进度条 + 回到顶部按钮 -->
  <div>
    <!-- 顶部进度条 -->
    <div
      style="
        position: fixed;
        top: 0; left: 0;
        height: 3px;
        background: linear-gradient(90deg, #1a73e8, #34a853);
        z-index: 9998;
        transition: width 0.1s linear;
        border-radius: 0 2px 2px 0;
      "
      :style="{ width: progress + '%' }"
    />

    <!-- 回到顶部按钮 -->
    <transition name="fade-up">
      <button
        v-if="showBackTop"
        @click="scrollToTop"
        style="
          position: fixed;
          bottom: 32px; right: 32px;
          width: 44px; height: 44px;
          background: #1a73e8;
          color: white;
          border: none;
          border-radius: 50%;
          cursor: pointer;
          display: flex; align-items: center; justify-content: center;
          box-shadow: 0 4px 12px rgba(26,115,232,0.35);
          z-index: 9997;
          transition: all 0.2s;
        "
        @mouseenter="hovered = true"
        @mouseleave="hovered = false"
        :style="hovered ? 'transform: translateY(-3px); box-shadow: 0 6px 16px rgba(26,115,232,0.45);' : ''"
        title="回到顶部"
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
          <path d="M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z"/>
        </svg>
      </button>
    </transition>
  </div>
</template>

<script>
import { smoothScrollToTop } from '@/utils/smoothScroll.js'

export default {
  name: 'ReadingProgress',
  data: function() {
    return {
      progress: 0,
      showBackTop: false,
      hovered: false
    }
  },
  mounted: function() {
    window.addEventListener('scroll', this.onScroll, { passive: true })
    // 兼容 Vuetify 滚动容器
    var container = document.scrollingElement || document.documentElement
    container.addEventListener('scroll', this.onScroll, { passive: true })
  },
  beforeUnmount: function() {
    window.removeEventListener('scroll', this.onScroll)
    var container = document.scrollingElement || document.documentElement
    container.removeEventListener('scroll', this.onScroll)
  },
  methods: {
    onScroll: function() {
      var container = document.scrollingElement || document.documentElement
      var scrollTop = container.scrollTop || window.scrollY || 0
      var docHeight = container.scrollHeight - container.clientHeight
      this.progress = docHeight > 0 ? Math.min(100, (scrollTop / docHeight) * 100) : 0
      this.showBackTop = scrollTop > 400
    },
    scrollToTop: function() {
      smoothScrollToTop(600)
    }
  }
}
</script>

<style scoped>
.fade-up-enter-active,
.fade-up-leave-active {
  transition: opacity 0.25s, transform 0.25s;
}
.fade-up-enter-from,
.fade-up-leave-to {
  opacity: 0;
  transform: translateY(12px);
}
</style>

