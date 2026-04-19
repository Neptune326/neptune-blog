<template>
  <teleport to="body">
    <transition name="lightbox-fade">
      <div
        v-if="visible"
        style="
          position: fixed; inset: 0; z-index: 99998;
          background: rgba(0,0,0,0.92);
          display: flex; align-items: center; justify-content: center;
          cursor: zoom-out;
        "
        @click.self="close"
        @keydown.esc="close"
      >
        <!-- 关闭按钮 -->
        <button @click="close" style="
          position: absolute; top: 16px; right: 20px;
          background: rgba(255,255,255,0.12); border: none; border-radius: 50%;
          width: 40px; height: 40px; cursor: pointer; color: white;
          display: flex; align-items: center; justify-content: center;
          transition: background 0.2s; z-index: 1;
        " @mouseenter="$event.target.style.background='rgba(255,255,255,0.22)'"
           @mouseleave="$event.target.style.background='rgba(255,255,255,0.12)'">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>

        <!-- 上一张 -->
        <button v-if="images.length > 1" @click.stop="prev" style="
          position: absolute; left: 16px; top: 50%; transform: translateY(-50%);
          background: rgba(255,255,255,0.12); border: none; border-radius: 50%;
          width: 44px; height: 44px; cursor: pointer; color: white;
          display: flex; align-items: center; justify-content: center;
          transition: background 0.2s;
        " @mouseenter="$event.target.style.background='rgba(255,255,255,0.22)'"
           @mouseleave="$event.target.style.background='rgba(255,255,255,0.12)'">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="white"><path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/></svg>
        </button>

        <!-- 图片 -->
        <div style="max-width: 90vw; max-height: 90vh; position: relative;" @click.stop>
          <img
            :src="currentSrc"
            :style="{
              maxWidth: '90vw',
              maxHeight: '90vh',
              objectFit: 'contain',
              borderRadius: '8px',
              transform: 'scale(' + scale + ')',
              transition: 'transform 0.2s',
              cursor: scale > 1 ? 'grab' : 'zoom-in',
              display: 'block'
            }"
            @click.stop="toggleZoom"
            @wheel.prevent="onWheel"
            draggable="false"
          />
          <!-- 图片计数 -->
          <div v-if="images.length > 1" style="
            position: absolute; bottom: -32px; left: 50%; transform: translateX(-50%);
            color: rgba(255,255,255,0.6); font-size: 13px; white-space: nowrap;
          ">{{ currentIndex + 1 }} / {{ images.length }}</div>
        </div>

        <!-- 下一张 -->
        <button v-if="images.length > 1" @click.stop="next" style="
          position: absolute; right: 16px; top: 50%; transform: translateY(-50%);
          background: rgba(255,255,255,0.12); border: none; border-radius: 50%;
          width: 44px; height: 44px; cursor: pointer; color: white;
          display: flex; align-items: center; justify-content: center;
          transition: background 0.2s;
        " @mouseenter="$event.target.style.background='rgba(255,255,255,0.22)'"
           @mouseleave="$event.target.style.background='rgba(255,255,255,0.12)'">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="white"><path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/></svg>
        </button>
      </div>
    </transition>
  </teleport>
</template>

<script>
export default {
  name: 'ImageLightbox',
  data: function() {
    return {
      visible: false,
      images: [],
      currentIndex: 0,
      scale: 1
    }
  },
  computed: {
    currentSrc: function() {
      return this.images[this.currentIndex] || ''
    }
  },
  mounted: function() {
    // 监听文章内容区图片点击
    document.addEventListener('click', this.onImgClick)
    document.addEventListener('keydown', this.onKeydown)
  },
  beforeUnmount: function() {
    document.removeEventListener('click', this.onImgClick)
    document.removeEventListener('keydown', this.onKeydown)
  },
  methods: {
    onImgClick: function(e) {
      var target = e.target
      if (target.tagName !== 'IMG') return
      // 只处理 markdown-body 内的图片
      if (!target.closest('.markdown-body')) return
      e.preventDefault()
      // 收集同一文章内所有图片
      var container = target.closest('.markdown-body')
      var imgs = Array.from(container.querySelectorAll('img')).map(function(img) { return img.src })
      this.images = imgs
      this.currentIndex = imgs.indexOf(target.src)
      if (this.currentIndex < 0) this.currentIndex = 0
      this.scale = 1
      this.visible = true
      document.body.style.overflow = 'hidden'
    },
    onKeydown: function(e) {
      if (!this.visible) return
      if (e.key === 'Escape') this.close()
      if (e.key === 'ArrowLeft') this.prev()
      if (e.key === 'ArrowRight') this.next()
    },
    close: function() {
      this.visible = false
      this.scale = 1
      document.body.style.overflow = ''
    },
    prev: function() {
      this.scale = 1
      this.currentIndex = (this.currentIndex - 1 + this.images.length) % this.images.length
    },
    next: function() {
      this.scale = 1
      this.currentIndex = (this.currentIndex + 1) % this.images.length
    },
    toggleZoom: function() {
      this.scale = this.scale > 1 ? 1 : 2
    },
    onWheel: function(e) {
      var delta = e.deltaY > 0 ? -0.2 : 0.2
      this.scale = Math.min(4, Math.max(0.5, this.scale + delta))
    }
  }
}
</script>

<style>
.lightbox-fade-enter-active, .lightbox-fade-leave-active {
  transition: opacity 0.2s ease;
}
.lightbox-fade-enter-from, .lightbox-fade-leave-to {
  opacity: 0;
}
</style>
