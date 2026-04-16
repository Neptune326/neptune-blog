<template>
  <!-- 博客侧边栏 -->
  <div style="display: flex; flex-direction: column; gap: 16px;">

    <!-- 每日一言 -->
    <DailyQuote />

    <!-- 博主简介卡片 -->
    <div
      style="
        background: white;
        border: 1px solid #e8eaed;
        border-radius: 12px;
        padding: 20px;
        text-align: center;
      "
    >
      <!-- 头像 -->
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
      <div style="font-size: 16px; font-weight: 700; color: #202124; margin-bottom: 4px;">
        我的博客
      </div>
      <div style="font-size: 13px; color: #80868b; line-height: 1.6; margin-bottom: 14px;">
        记录技术成长，分享开发心得
      </div>
      <!-- 统计数字 -->
      <div
        style="
          display: flex;
          justify-content: space-around;
          padding-top: 14px;
          border-top: 1px solid #f1f3f4;
        "
      >
        <div style="text-align: center;">
          <div style="font-size: 18px; font-weight: 700; color: #1a73e8;">{{ stats.articles }}</div>
          <div style="font-size: 11px; color: #80868b; margin-top: 2px;">文章</div>
        </div>
        <div style="text-align: center;">
          <div style="font-size: 18px; font-weight: 700; color: #34a853;">{{ stats.categories }}</div>
          <div style="font-size: 11px; color: #80868b; margin-top: 2px;">分类</div>
        </div>
        <div style="text-align: center;">
          <div style="font-size: 18px; font-weight: 700; color: #f29900;">{{ stats.tags }}</div>
          <div style="font-size: 11px; color: #80868b; margin-top: 2px;">标签</div>
        </div>
      </div>
    </div>

    <!-- 分类列表 -->
    <div
      style="
        background: white;
        border: 1px solid #e8eaed;
        border-radius: 12px;
        padding: 16px 20px;
      "
    >
      <div
        style="
          font-size: 13px; font-weight: 600; color: #80868b;
          text-transform: uppercase; letter-spacing: 0.8px;
          margin-bottom: 12px;
          display: flex; align-items: center; gap: 6px;
        "
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="#80868b">
          <path d="M10 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/>
        </svg>
        分类
      </div>
      <div v-if="categories.length === 0" style="color: #9aa0a6; font-size: 13px;">加载中...</div>
      <router-link
        v-for="cat in categories"
        :key="cat.id"
        :to="'/category/' + cat.id"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 7px 0;
          text-decoration: none;
          border-bottom: 1px solid #f8f9fa;
          transition: color 0.15s;
        "
        class="sidebar-cat-link"
      >
        <span style="font-size: 14px; color: #3c4043; display: flex; align-items: center; gap: 6px;">
          <span style="width: 6px; height: 6px; background: #1a73e8; border-radius: 50%; display: inline-block; flex-shrink: 0;"></span>
          {{ cat.name }}
        </span>
        <span
          style="
            font-size: 11px; color: white;
            background: #e8f0fe; color: #1a73e8;
            padding: 1px 7px; border-radius: 10px;
            font-weight: 600;
          "
        >
          {{ cat.articleCount || 0 }}
        </span>
      </router-link>
    </div>

    <!-- 标签云 -->
    <div
      style="
        background: white;
        border: 1px solid #e8eaed;
        border-radius: 12px;
        padding: 16px 20px;
      "
    >
      <div
        style="
          font-size: 13px; font-weight: 600; color: #80868b;
          text-transform: uppercase; letter-spacing: 0.8px;
          margin-bottom: 12px;
          display: flex; align-items: center; gap: 6px;
        "
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="#80868b">
          <path d="M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z"/>
        </svg>
        标签云
      </div>
      <div v-if="tags.length === 0" style="color: #9aa0a6; font-size: 13px;">加载中...</div>
      <div style="display: flex; flex-wrap: wrap; gap: 6px;">
        <router-link
          v-for="tag in tags"
          :key="tag.id"
          :to="'/tag/' + tag.id"
          style="
            text-decoration: none;
            font-size: 12px;
            padding: 3px 10px;
            border-radius: 12px;
            border: 1px solid #e8eaed;
            color: #5f6368;
            background: #f8f9fa;
            transition: all 0.15s;
            white-space: nowrap;
          "
          class="sidebar-tag-link"
        >
          # {{ tag.name }}
          <span style="color: #9aa0a6; font-size: 10px; margin-left: 2px;">{{ tag.articleCount }}</span>
        </router-link>
      </div>
    </div>

    <!-- 最新文章 -->
    <div
      v-if="recentArticles.length > 0"
      style="
        background: white;
        border: 1px solid #e8eaed;
        border-radius: 12px;
        padding: 16px 20px;
      "
    >
      <div
        style="
          font-size: 13px; font-weight: 600; color: #80868b;
          text-transform: uppercase; letter-spacing: 0.8px;
          margin-bottom: 12px;
          display: flex; align-items: center; gap: 6px;
        "
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="#80868b">
          <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
        </svg>
        最新文章
      </div>
      <div
        v-for="article in recentArticles"
        :key="article.id"
        style="margin-bottom: 10px; padding-bottom: 10px; border-bottom: 1px solid #f8f9fa;"
      >
        <router-link
          :to="'/article/' + article.id"
          style="
            text-decoration: none;
            font-size: 13px;
            color: #3c4043;
            line-height: 1.5;
            display: block;
            transition: color 0.15s;
          "
          class="sidebar-article-link"
        >
          {{ article.title }}
        </router-link>
        <div style="font-size: 11px; color: #9aa0a6; margin-top: 3px;">
          {{ formatDate(article.createTime) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategories } from '../../api/category.js'
import { getTags } from '../../api/tag.js'
import { getArticles } from '../../api/article.js'
import DailyQuote from './DailyQuote.vue'
import TypewriterText from './TypewriterText.vue'

export default {
  name: 'BlogSidebar',
  components: { DailyQuote, TypewriterText },
  data: function() {
    return {
      categories: [],
      tags: [],
      recentArticles: [],
      stats: { articles: 0, categories: 0, tags: 0 }
    }
  },
  mounted: function() {
    this.loadData()
  },
  methods: {
    loadData: function() {
      var self = this
      // 加载分类
      getCategories()
        .then(function(data) {
          self.categories = (data || []).slice(0, 8)
          self.stats.categories = (data || []).length
        })
        .catch(function() {})

      // 加载标签
      getTags()
        .then(function(data) {
          self.tags = (data || []).slice(0, 20)
          self.stats.tags = (data || []).length
        })
        .catch(function() {})

      // 加载最新文章
      getArticles({ pageNum: 1, pageSize: 5 })
        .then(function(data) {
          self.recentArticles = (data && data.list) ? data.list.slice(0, 5) : []
          self.stats.articles = (data && data.total) ? data.total : 0
        })
        .catch(function() {})
    },
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    }
  }
}
</script>

<style scoped>
.sidebar-cat-link:hover span:first-child {
  color: #1a73e8 !important;
}
.sidebar-tag-link:hover {
  background: #e8f0fe !important;
  color: #1a73e8 !important;
  border-color: #1a73e8 !important;
}
.sidebar-article-link:hover {
  color: #1a73e8 !important;
}
</style>
