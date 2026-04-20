<template>
  <!-- 文章目录（TOC）—— 右侧固定 -->
  <div
    v-if="headings.length > 0"
    style="
      position: sticky;
      top: 80px;
      max-height: calc(100vh - 120px);
      overflow-y: auto;
      padding: 16px;
      background: white;
      border: 1px solid #e8eaed;
      border-radius: 12px;
    "
  >
    <div
      style="
        font-size: 12px;
        font-weight: 600;
        color: #80868b;
        text-transform: uppercase;
        letter-spacing: 0.8px;
        margin-bottom: 12px;
        display: flex;
        align-items: center;
        gap: 6px;
      "
    >
      <svg width="14" height="14" viewBox="0 0 24 24" fill="#80868b">
        <path d="M3 9h14V7H3v2zm0 4h14v-2H3v2zm0 4h14v-2H3v2zm16 0h2v-2h-2v2zm0-10v2h2V7h-2zm0 6h2v-2h-2v2z"/>
      </svg>
      目录
    </div>

    <nav>
      <a
        v-for="heading in headings"
        :key="heading.id"
        :href="'#' + heading.id"
        @click.prevent="scrollTo(heading.id)"
        :style="{
          display: 'block',
          padding: '4px 0 4px ' + (heading.level - 1) * 12 + 'px',
          fontSize: heading.level === 1 ? '13px' : '12px',
          fontWeight: heading.level <= 2 ? '500' : '400',
          color: activeId === heading.id ? '#1a73e8' : '#5f6368',
          textDecoration: 'none',
          borderLeft: activeId === heading.id ? '2px solid #1a73e8' : '2px solid transparent',
          paddingLeft: (heading.level - 1) * 12 + 8 + 'px',
          transition: 'all 0.15s',
          lineHeight: '1.5',
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          maxWidth: '180px'
        }"
      >
        {{ heading.text }}
      </a>
    </nav>
  </div>
</template>

<script>
import { smoothScrollToElement } from '@/utils/smoothScroll.js'

export default {
  name: 'TableOfContents',
  props: {
    content: { type: String, default: '' }
  },
  data: function() {
    return {
      headings: [],
      activeId: '',
      observer: null
    }
  },
  watch: {
    content: function(val) {
      if (val) {
        this.$nextTick(this.extractHeadings)
      }
    }
  },
  mounted: function() {
    if (this.content) {
      this.$nextTick(this.extractHeadings)
    }
  },
  beforeUnmount: function() {
    if (this.observer) this.observer.disconnect()
  },
  methods: {
    extractHeadings: function() {
      var self = this
      var articleEl = document.querySelector('.markdown-body')
      if (!articleEl) return

      var els = articleEl.querySelectorAll('h1, h2, h3, h4')
      self.headings = []

      els.forEach(function(el, index) {
        if (!el.id) {
          el.id = 'heading-' + index
        }
        self.headings.push({
          id: el.id,
          text: el.textContent,
          level: parseInt(el.tagName.replace('H', ''))
        })
      })

      if (self.observer) self.observer.disconnect()
      self.observer = new IntersectionObserver(
        function(entries) {
          entries.forEach(function(entry) {
            if (entry.isIntersecting) {
              self.activeId = entry.target.id
            }
          })
        },
        { rootMargin: '-64px 0px -70% 0px', threshold: 0 }
      )

      els.forEach(function(el) {
        self.observer.observe(el)
      })
    },
    scrollTo: function(id) {
      // 使用自定义缓动动画，兼容 Vuetify 滚动容器
      smoothScrollToElement(id, 80, 500)
    }
  }
}
</script>

