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
import { getFavoriteStatus, toggleFavorite } from '@/api/favorite.js'

export default {
  name: 'ArticleFavorite',
  props: {
    articleId: { type: [Number, String], required: true },
    articleTitle: { type: String, default: '' }
  },
  data: function() {
    return {
      isFavorited: false,
      favoriteCount: 0
    }
  },
  mounted: function() {
    this.loadStatus()
  },
  watch: {
    articleId: function() {
      this.loadStatus()
    }
  },
  methods: {
    loadStatus: function() {
      var self = this
      getFavoriteStatus(self.articleId).then(function(data) {
        self.isFavorited = !!data.favorited
        self.favoriteCount = Number(data.favoriteCount || 0)
      }).catch(function() {})
    },
    toggle: function() {
      var self = this
      toggleFavorite(self.articleId).then(function(data) {
        self.isFavorited = !!data.favorited
        self.favoriteCount = Number(data.favoriteCount || 0)
      }).catch(function() {})
    }
  }
}
</script>
