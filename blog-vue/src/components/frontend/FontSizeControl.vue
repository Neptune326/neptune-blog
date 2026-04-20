<template>
  <!-- 字体大小调节 -->
  <div class="d-flex align-center" style="gap: 4px;">
    <button @click="decrease" :disabled="size <= minSize" title="缩小字体"
      style="background: none; border: 1px solid #e8eaed; border-radius: 6px; width: 28px; height: 28px; cursor: pointer; font-size: 13px; font-weight: 700; color: #5f6368; display: flex; align-items: center; justify-content: center; transition: all 0.15s;"
      class="font-btn">A-</button>
    <span style="font-size: 12px; color: #80868b; min-width: 28px; text-align: center;">{{ size }}</span>
    <button @click="increase" :disabled="size >= maxSize" title="放大字体"
      style="background: none; border: 1px solid #e8eaed; border-radius: 6px; width: 28px; height: 28px; cursor: pointer; font-size: 15px; font-weight: 700; color: #5f6368; display: flex; align-items: center; justify-content: center; transition: all 0.15s;"
      class="font-btn">A+</button>
  </div>
</template>

<script>
var DEFAULT_SIZE = 16
var MIN_SIZE = 13
var MAX_SIZE = 22
var STORAGE_KEY = 'blog_font_size'

export default {
  name: 'FontSizeControl',
  data: function() {
    return {
      size: DEFAULT_SIZE,
      minSize: MIN_SIZE,
      maxSize: MAX_SIZE
    }
  },
  mounted: function() {
    var saved = parseInt(localStorage.getItem(STORAGE_KEY))
    if (saved && saved >= MIN_SIZE && saved <= MAX_SIZE) {
      this.size = saved
    }
    this.apply()
  },
  methods: {
    increase: function() {
      if (this.size < MAX_SIZE) {
        this.size += 1
        this.save()
      }
    },
    decrease: function() {
      if (this.size > MIN_SIZE) {
        this.size -= 1
        this.save()
      }
    },
    save: function() {
      localStorage.setItem(STORAGE_KEY, String(this.size))
      this.apply()
    },
    apply: function() {
      // 只影响文章正文区域
      document.documentElement.style.setProperty('--article-font-size', this.size + 'px')
    }
  }
}
</script>

<style scoped>
.font-btn:hover:not(:disabled) {
  background: #f1f3f4 !important;
  border-color: #dadce0 !important;
}
.font-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
