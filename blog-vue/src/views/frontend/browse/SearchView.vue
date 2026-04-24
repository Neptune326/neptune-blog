<template>
  <v-app class="front-shell">
    <FrontendNavBar variant="back" container-max-width="1200px" />

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <v-row>
          <v-col cols="12" md="8">
            <div class="mb-8">
              <h1 class="mb-6 front-title" style="font-size: 28px; font-weight: 800;">搜索文章</h1>

              <v-card elevation="0" rounded="xl" class="front-card" style="overflow: visible;">
                <div class="pa-4 d-flex align-center" style="gap: 12px;">
                  <v-icon color="grey" size="22">mdi-magnify</v-icon>
                  <input
                    v-model="keyword"
                    type="text"
                    placeholder="输入关键词搜索文章..."
                    style="
                      flex: 1;
                      border: none;
                      outline: none;
                      font-size: 16px;
                      color: var(--front-text);
                      background: transparent;
                      font-family: inherit;
                    "
                    @keyup.enter="doSearch"
                  />
                  <v-btn
                    v-if="keyword"
                    icon
                    variant="text"
                    size="small"
                    @click="onClear"
                    style="color: #80868b;"
                  >
                    <v-icon size="18">mdi-close</v-icon>
                  </v-btn>
                  <v-btn
                    color="primary"
                    :loading="loading"
                    @click="doSearch"
                    style="min-width: 80px;"
                  >
                    搜索
                  </v-btn>
                </div>
              </v-card>

              <div v-if="!searched && searchHistory.length > 0" class="mt-4">
                <div class="d-flex align-center justify-space-between mb-2">
                  <span class="front-muted" style="font-size: 13px; font-weight: 500;">搜索历史</span>
                  <v-btn size="x-small" variant="text" color="grey" @click="clearHistory">清除</v-btn>
                </div>
                <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                  <v-chip
                    v-for="h in searchHistory"
                    :key="h"
                    size="small"
                    variant="outlined"
                    color="primary"
                    closable
                    @click="useHistory(h)"
                    @click:close="removeHistory(h)"
                    style="cursor: pointer;"
                  >
                    {{ h }}
                  </v-chip>
                </div>
              </div>

              <div v-if="!searched" class="mt-4">
                <div class="mb-2 front-muted" style="font-size: 13px; font-weight: 500;">热门搜索</div>
                <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                  <v-chip
                    v-for="hot in hotKeywords"
                    :key="hot"
                    size="small"
                    variant="tonal"
                    color="primary"
                    prepend-icon="mdi-fire"
                    @click="useHistory(hot)"
                    style="cursor: pointer;"
                  >
                    {{ hot }}
                  </v-chip>
                </div>
              </div>
            </div>

            <div v-if="searched && !loading" class="mb-4 front-soft" style="font-size: 14px;">
              <span v-if="total > 0">
                找到 <strong style="color: var(--front-accent);">{{ total }}</strong> 篇与
                "<strong class="front-title">{{ lastKeyword }}</strong>" 相关的文章
              </span>
              <span v-else>
                未找到与 "<strong class="front-title">{{ lastKeyword }}</strong>" 相关的文章
              </span>
            </div>

            <div v-if="loading" class="d-flex justify-center py-12">
              <v-progress-circular indeterminate color="primary" size="40" />
            </div>

            <template v-else-if="searched">
              <ArticleCard
                v-for="article in articles"
                :key="article.id"
                :article="article"
              />

              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-file-search-outline</v-icon>
                <p class="mt-4 text-body-1 front-muted">没有找到相关文章</p>
                <p class="text-body-2 front-soft">试试其他关键词</p>
              </div>

              <Pagination
                v-if="articles.length > 0"
                :total="total"
                :pages="pages"
                :page-num="pageNum"
                @page-change="onPageChange"
              />
            </template>

            <div v-else class="text-center py-16">
              <v-icon size="64" color="grey-lighten-2">mdi-text-search</v-icon>
              <p class="mt-4 text-body-1 front-muted">输入关键词开始搜索</p>
            </div>
          </v-col>

          <v-col cols="12" md="4" class="d-none d-md-block">
            <BlogSidebar />
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import ArticleCard from '@/components/frontend/ArticleCard.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import FrontendNavBar from '@/components/frontend/FrontendNavBar.vue'
import BlogSidebar from '@/components/frontend/BlogSidebar.vue'
import { getArticles } from '@/api/article.js'

export default {
  name: 'SearchView',
  components: { ArticleCard, Pagination, FrontendNavBar, BlogSidebar },
  data: function() {
    return {
      keyword: '',
      lastKeyword: '',
      articles: [],
      total: 0,
      pages: 1,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      searched: false,
      searchHistory: [],
      hotKeywords: ['Vue 3', 'Spring Boot', 'MySQL', 'Docker', 'TypeScript', 'Git']
    }
  },
  mounted: function() {
    try {
      var history = localStorage.getItem('blog_search_history')
      this.searchHistory = history ? JSON.parse(history) : []
    } catch (e) {
      this.searchHistory = []
    }

    var q = this.$route.query.keyword
    if (q) {
      this.keyword = q
      this.doSearch()
    }
  },
  methods: {
    doSearch: function() {
      var self = this
      if (!self.keyword || !self.keyword.trim()) return
      self.pageNum = 1
      self.lastKeyword = self.keyword.trim()
      var keyword = self.lastKeyword
      var history = self.searchHistory.filter(function(item) { return item !== keyword })
      history.unshift(keyword)
      if (history.length > 10) {
        history = history.slice(0, 10)
      }
      self.searchHistory = history
      try { localStorage.setItem('blog_search_history', JSON.stringify(history)) } catch (e) {}
      self.loadResults()
    },
    useHistory: function(keyword) {
      this.keyword = keyword
      this.doSearch()
    },
    removeHistory: function(keyword) {
      this.searchHistory = this.searchHistory.filter(function(item) { return item !== keyword })
      try { localStorage.setItem('blog_search_history', JSON.stringify(this.searchHistory)) } catch (e) {}
    },
    clearHistory: function() {
      this.searchHistory = []
      try { localStorage.removeItem('blog_search_history') } catch (e) {}
    },
    loadResults: function() {
      var self = this
      self.loading = true
      self.searched = true
      getArticles({ keyword: self.lastKeyword, pageNum: self.pageNum, pageSize: self.pageSize })
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
        })
        .catch(function(err) {
          console.error('搜索失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    onPageChange: function(page) {
      this.pageNum = page
      this.loadResults()
    },
    onClear: function() {
      this.keyword = ''
      this.articles = []
      this.total = 0
      this.pages = 1
      this.pageNum = 1
      this.searched = false
      this.lastKeyword = ''
    }
  }
}
</script>
