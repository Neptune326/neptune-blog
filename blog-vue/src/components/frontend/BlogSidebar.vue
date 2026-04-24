<template>
  <div class="blog-sidebar" style="display: flex; flex-direction: column; gap: 16px;">

    <DailyQuote />

    <WeatherWidget />

    <!-- 博主简介卡片 -->
    <div class="sidebar-card blog-sidebar-section" style="padding: 20px; text-align: center;">
      <div
        style="
          width: 72px; height: 72px;
          background: linear-gradient(135deg, #1a73e8, #34a853);
          border-radius: 50%;
          margin: 0 auto 12px;
          display: flex; align-items: center; justify-content: center;
          font-size: 28px;
          color: white;
          font-weight: 700;
        "
      >
        博
      </div>
      <div class="sidebar-profile-title" style="font-size: 16px; font-weight: 700; margin-bottom: 4px;">
        我的博客
      </div>
      <div class="sidebar-profile-desc" style="font-size: 13px; line-height: 1.6; margin-bottom: 10px;">
        记录技术成长，分享开发心得
      </div>
      <div
        v-if="visitStreak > 0"
        class="visit-streak-line"
        style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;"
      >
        已连续来访 <strong style="color: var(--link-color);">{{ visitStreak }}</strong> 天
      </div>
      <div
        style="
          display: flex;
          justify-content: space-around;
          padding-top: 14px;
          border-top: 1px solid var(--border-color);
        "
      >
        <div style="text-align: center;">
          <div class="stat-num stat-num--blue">{{ stats.articles }}</div>
          <div class="stat-label">文章</div>
        </div>
        <div style="text-align: center;">
          <div class="stat-num stat-num--green">{{ stats.categories }}</div>
          <div class="stat-label">分类</div>
        </div>
        <div style="text-align: center;">
          <div class="stat-num stat-num--orange">{{ stats.tags }}</div>
          <div class="stat-label">标签</div>
        </div>
      </div>
    </div>

    <!-- 分类列表 -->
    <div class="sidebar-card blog-sidebar-section" style="padding: 16px 20px;">
      <div class="sidebar-section-title">
        <svg class="sidebar-section-icon" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
          <path d="M10 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/>
        </svg>
        分类
      </div>
      <div v-if="categories.length === 0" class="sidebar-muted" style="font-size: 13px;">加载中...</div>
      <router-link
        v-for="cat in categories"
        :key="cat.id"
        :to="'/category/' + cat.id"
        class="sidebar-cat-link"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 7px 0;
          text-decoration: none;
          border-bottom: 1px solid var(--bg-hover);
          transition: color 0.15s;
        "
      >
        <span class="sidebar-cat-text" style="font-size: 14px; display: flex; align-items: center; gap: 6px;">
          <span class="sidebar-cat-dot"></span>
          {{ cat.name }}
        </span>
        <span class="sidebar-cat-count">
          {{ cat.articleCount || 0 }}
        </span>
      </router-link>
    </div>

    <!-- 标签云 -->
    <div class="sidebar-card blog-sidebar-section" style="padding: 16px 20px;">
      <div class="sidebar-section-title">
        <svg class="sidebar-section-icon" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
          <path d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z"/>
        </svg>
        标签云
      </div>
      <div v-if="tags.length === 0" class="sidebar-muted" style="font-size: 13px;">加载中...</div>
      <div style="display: flex; flex-wrap: wrap; gap: 6px; align-items: center;">
        <router-link
          v-for="tag in tags"
          :key="tag.id"
          :to="'/tag/' + tag.id"
          :style="tagChipStyle(tag.articleCount)"
          class="sidebar-tag-link"
        >
          # {{ tag.name }}
          <span style="font-size: 10px; opacity: 0.7; margin-left: 2px;">{{ tag.articleCount }}</span>
        </router-link>
      </div>
    </div>

    <!-- 随机文章 -->
    <div class="sidebar-card blog-sidebar-section" style="padding: 16px 20px; text-align: center;">
      <div class="sidebar-muted" style="font-size: 13px; margin-bottom: 10px;">✨ 随机发现一篇好文章</div>
      <v-btn
        color="primary"
        variant="tonal"
        prepend-icon="mdi-shuffle-variant"
        block
        @click="goRandom"
        style="font-size: 13px;"
      >随机阅读</v-btn>
    </div>

    <!-- 最新文章 -->
    <div
      v-if="recentArticles.length > 0"
      class="sidebar-card blog-sidebar-section"
      style="padding: 16px 20px;"
    >
      <div class="sidebar-section-title">
        <svg class="sidebar-section-icon" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
          <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
        </svg>
        最新文章
      </div>
      <div
        v-for="article in recentArticles"
        :key="article.id"
        class="sidebar-list-row"
      >
        <router-link
          :to="'/article/' + article.id"
          class="sidebar-article-link"
          style="text-decoration: none; font-size: 13px; line-height: 1.5; display: block; transition: color 0.15s;"
        >
          {{ article.title }}
        </router-link>
        <div class="sidebar-muted" style="font-size: 11px; margin-top: 3px;">
          {{ formatDate(article.createTime) }}
        </div>
      </div>
    </div>

    <!-- 热门文章 -->
    <div
      v-if="hotArticles.length > 0"
      class="sidebar-card blog-sidebar-section"
      style="padding: 16px 20px;"
    >
      <div class="sidebar-section-title">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="#f29900"><path d="M13.5 0.67s.74 2.65.74 4.8c0 2.06-1.35 3.73-3.41 3.73-2.07 0-3.63-1.67-3.63-3.73l.03-.36C5.21 7.51 4 10.62 4 14c0 4.42 3.58 8 8 8s8-3.58 8-8C20 8.61 17.41 3.8 13.5 0.67zM11.71 19c-1.78 0-3.22-1.4-3.22-3.14 0-1.62 1.05-2.76 2.81-3.12 1.77-.36 3.6-1.21 4.62-2.58.39 1.29.59 2.65.59 4.04 0 2.65-2.15 4.8-4.8 4.8z"/></svg>
        热门文章
      </div>
      <div
        v-for="(article, idx) in hotArticles"
        :key="article.id"
        class="sidebar-list-row"
        style="display: flex; align-items: flex-start; gap: 8px;"
      >
        <span :class="['hot-rank', idx < 3 ? 'hot-rank--top' : 'hot-rank--rest']">{{ idx + 1 }}</span>
        <div style="flex: 1; min-width: 0;">
          <router-link
            :to="'/article/' + article.id"
            class="sidebar-article-link article-link-ellipsis"
            style="text-decoration: none; font-size: 13px; line-height: 1.5; display: block; transition: color 0.15s;"
          >{{ article.title }}</router-link>
          <div class="sidebar-muted" style="font-size: 11px; margin-top: 2px; display: flex; align-items: center; gap: 4px;">
            <svg width="10" height="10" viewBox="0 0 24 24" fill="currentColor"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
            {{ article.viewCount || 0 }}
          </div>
        </div>
      </div>
    </div>

    <!-- 友情链接 -->
    <div
      v-if="friendLinks.length > 0"
      class="sidebar-card blog-sidebar-section"
      style="padding: 16px 20px;"
    >
      <div class="sidebar-section-title">
        <svg class="sidebar-section-icon" width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"/></svg>
        友情链接
      </div>
      <div style="display: flex; flex-wrap: wrap; gap: 6px;">
        <a
          v-for="link in friendLinks"
          :key="link.id"
          :href="link.url"
          target="_blank"
          rel="noopener noreferrer"
          class="friend-link"
        >{{ link.name }}</a>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategories } from '@/api/category.js'
import { getTags } from '@/api/tag.js'
import { getArticles } from '@/api/article.js'
import request from '@/api/request.js'
import DailyQuote from './DailyQuote.vue'
import WeatherWidget from './WeatherWidget.vue'
import { useRouter } from 'vue-router'
import { syncVisitStreak } from '@/utils/visitStreak.js'

export default {
  name: 'BlogSidebar',
  components: { DailyQuote, WeatherWidget },
  setup: function() {
    var router = useRouter()
    return { router }
  },
  data: function() {
    return {
      categories: [],
      tags: [],
      recentArticles: [],
      hotArticles: [],
      friendLinks: [],
      stats: { articles: 0, categories: 0, tags: 0 },
      visitStreak: 0
    }
  },
  mounted: function() {
    var r = syncVisitStreak()
    this.visitStreak = r.streak
    this.loadData()
  },
  methods: {
    loadData: function() {
      var self = this
      getCategories()
        .then(function(data) {
          self.categories = (data || []).slice(0, 8)
          self.stats.categories = (data || []).length
        })
        .catch(function() {})

      getTags()
        .then(function(data) {
          self.tags = (data || []).slice(0, 20)
          self.stats.tags = (data || []).length
        })
        .catch(function() {})

      getArticles({ pageNum: 1, pageSize: 5 })
        .then(function(data) {
          self.recentArticles = (data && data.list) ? data.list.slice(0, 5) : []
          self.stats.articles = (data && data.total) ? data.total : 0
        })
        .catch(function() {})

      getArticles({ pageNum: 1, pageSize: 5, orderBy: 'view_count' })
        .then(function(data) {
          var list = (data && data.list) ? data.list.slice(0, 5) : []
          list.sort(function(a, b) { return (b.viewCount || 0) - (a.viewCount || 0) })
          self.hotArticles = list
        })
        .catch(function() {})

      request({ method: 'get', url: '/api/friend-links' })
        .then(function(data) {
          self.friendLinks = data || []
        })
        .catch(function() {})
    },
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    },
    goRandom: function() {
      var self = this
      var list = self.recentArticles.concat(self.hotArticles)
      var seen = {}
      var unique = list.filter(function(a) {
        if (seen[a.id]) return false
        seen[a.id] = true
        return true
      })
      if (unique.length === 0) return
      var pick = unique[Math.floor(Math.random() * unique.length)]
      self.router.push('/article/' + pick.id)
    },
    getTagFontSize: function(count) {
      var counts = this.tags.map(function(t) { return t.articleCount || 1 })
      var max = Math.max.apply(null, counts)
      var min = Math.min.apply(null, counts)
      if (max === min) return 13
      var ratio = ((count || 1) - min) / (max - min)
      return Math.round(11 + ratio * 8)
    },
    tagHeatRatio: function(count) {
      var counts = this.tags.map(function(t) { return t.articleCount || 1 })
      var max = Math.max.apply(null, counts)
      var min = Math.min.apply(null, counts)
      if (max === min) return 0.5
      return ((count || 1) - min) / (max - min)
    },
    tagChipStyle: function(count) {
      var ratio = this.tagHeatRatio(count)
      var color = 'var(--text-secondary)'
      if (ratio > 0.7) color = 'var(--link-color)'
      else if (ratio > 0.4) color = '#34a853'
      return {
        textDecoration: 'none',
        fontSize: this.getTagFontSize(count) + 'px',
        padding: '2px 8px',
        borderRadius: '12px',
        border: '1px solid var(--border-color)',
        color: color,
        background: 'var(--bg-hover)',
        transition: 'all 0.15s',
        whiteSpace: 'nowrap',
        lineHeight: '1.8'
      }
    }
  }
}
</script>

<style scoped>
.sidebar-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
}
.sidebar-profile-title {
  color: var(--text-primary);
}
.sidebar-profile-desc {
  color: var(--text-secondary);
}
.stat-num {
  font-size: 18px;
  font-weight: 700;
}
.stat-num--blue { color: #1a73e8; }
.stat-num--green { color: #34a853; }
.stat-num--orange { color: #f29900; }
.stat-label {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}
.sidebar-section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.8px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.sidebar-section-icon {
  color: var(--text-muted);
  flex-shrink: 0;
}
.sidebar-muted {
  color: var(--text-muted);
}
.sidebar-cat-dot {
  width: 6px;
  height: 6px;
  background: var(--link-color);
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}
.sidebar-cat-text {
  color: var(--text-primary);
}
.sidebar-cat-count {
  font-size: 11px;
  color: var(--link-color);
  background: var(--bg-hover);
  padding: 1px 7px;
  border-radius: 10px;
  font-weight: 600;
  border: 1px solid var(--border-color);
}
.sidebar-cat-link:hover .sidebar-cat-text {
  color: var(--link-color) !important;
}
.sidebar-list-row {
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--bg-hover);
}
.sidebar-article-link {
  color: var(--text-primary) !important;
}
.sidebar-article-link:hover {
  color: var(--link-color) !important;
}
.article-link-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hot-rank {
  min-width: 18px;
  height: 18px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 1px;
}
.hot-rank--top {
  background: #ea4335;
  color: white;
}
.hot-rank--rest {
  background: var(--bg-hover);
  color: var(--text-muted);
  border: 1px solid var(--border-color);
}
.sidebar-tag-link:hover {
  background: var(--bg-primary) !important;
  color: var(--link-color) !important;
  border-color: var(--link-color) !important;
}
.friend-link {
  font-size: 12px;
  color: var(--link-color);
  text-decoration: none;
  padding: 3px 10px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  background: var(--bg-hover);
  transition: all 0.15s;
}
.friend-link:hover {
  background: var(--bg-primary) !important;
  border-color: var(--link-color) !important;
}
</style>

<!-- 暗色下统计数字略提亮度 -->
<style scoped>
body.dark-mode .stat-num--blue { color: #8ab4f8; }
body.dark-mode .stat-num--green { color: #81c995; }
body.dark-mode .stat-num--orange { color: #fdd663; }
</style>
