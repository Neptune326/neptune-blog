<template>
  <!-- 文章收藏按钮 -->
  <button
    @click="toggle"
    :title="isFavorited ? '取消收藏' : '收藏文章'"
    style="
      display: flex; align-items: center; gap: 8px;
      padding: 10px 24px;
      border-radius: 24px;
      border: 2px solid;
      cursor: pointer;
      font-size: 15px;
      font-weight: 600;
      transition: all 0.2s;
      background: none;
    "
    :style="isFavorited
      ? 'border-color: #f59e0b; color: #f59e0b; background: #fffbeb;'
      : 'border-color: #e8eaed; color: #5f6368; background: white;'"
  >
    <svg width="18" height="18" viewBox="0 0 24 24"
      :fill="isFavorited ? '#f59e0b' : 'none'"
      :stroke="isFavorited ? '#f59e0b' : '#5f6368'"
      stroke-width="2">
      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
    </svg>
    {{ isFavorited ? '已收藏' : '收藏' }}
  </button>
</template>

<script>
var STORAGE_KEY = 'blog_favorites'

export default {
  name: 'ArticleFavorite',
  props: {
    articleId: { type: [Number, String], required: true },
    articleTitle: { type: String, default: '' }
  },
  data: function() {
    return {
      isFavorited: false
    }
  },
  mounted: function() {
    this.checkFavorite()
  },
  watch: {
    articleId: function() {
      this.checkFavorite()
    }
  },
  methods: {
    getFavorites: function() {
      try {
        return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
      } catch (e) { return [] }
    },
    checkFavorite: function() {
      var id = String(this.articleId)
      var favs = this.getFavorites()
      this.isFavorited = favs.some(function(f) { return String(f.id) === id })
    },
    toggle: function() {
      var id = String(this.articleId)
      var favs = this.getFavorites()
      var idx = favs.findIndex(function(f) { return String(f.id) === id })
      if (idx >= 0) {
        favs.splice(idx, 1)
        this.isFavorited = false
      } else {
        favs.unshift({ id: id, title: this.articleTitle, time: Date.now() })
        if (favs.length > 50) favs = favs.slice(0, 50)
        this.isFavorited = true
      }
      try { localStorage.setItem(STORAGE_KEY, JSON.stringify(favs)) } catch (e) {}
    }
  }
}
</script>
