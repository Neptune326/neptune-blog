<template>
  <v-app style="background: #f8f9fa;">
    <v-app-bar color="white" elevation="0" style="border-bottom: 1px solid #e8eaed;">
      <v-container style="max-width: 1100px; display: flex; align-items: center; padding: 0 16px;">
        <v-btn icon variant="text" @click="$router.back()" style="color: #5f6368;">
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
        <router-link to="/" class="d-flex align-center text-decoration-none ml-2" style="gap: 6px;">
          <v-icon color="primary" size="22">mdi-pencil-circle</v-icon>
          <span style="font-size: 16px; font-weight: 600; color: #202124;">我的博客</span>
        </router-link>
        <v-spacer />
      </v-container>
    </v-app-bar>

    <v-main>
      <v-container style="max-width: 860px; padding: 32px 16px;">

        <!-- 搜索框区域 -->
        <div class="mb-8">
          <h1 class="mb-6" style="font-size: 24px; font-weight: 600; color: #202124;">搜索文章</h1>

          <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; overflow: visible;">
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
                  color: #202124;
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
        </div>

        <!-- 搜索结果提示 -->
        <div v-if="searched && !loading" class="mb-4" style="color: #5f6368; font-size: 14px;">
          <span v-if="total > 0">
            找到 <strong style="color: #1a73e8;">{{ total }}</strong> 篇与
            "<strong style="color: #202124;">{{ lastKeyword }}</strong>" 相关的文章
          </span>
          <span v-else>
            未找到与 "<strong style="color: #202124;">{{ lastKeyword }}</strong>" 相关的文章
          </span>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="d-flex justify-center py-12">
          <v-progress-circular indeterminate color="primary" size="40" />
        </div>

        <!-- 搜索结果 -->
        <template v-else-if="searched">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
          />

          <div v-if="articles.length === 0" class="text-center py-16">
            <v-icon size="64" color="grey-lighten-2">mdi-file-search-outline</v-icon>
            <p class="mt-4 text-body-1" style="color: #80868b;">没有找到相关文章</p>
            <p class="text-body-2" style="color: #9aa0a6;">试试其他关键词</p>
          </div>

          <Pagination
            v-if="articles.length > 0"
            :total="total"
            :pages="pages"
            :page-num="pageNum"
            @page-change="onPageChange"
          />
        </template>

        <!-- 初始状态 -->
        <div v-else class="text-center py-16">
          <v-icon size="64" color="grey-lighten-2">mdi-text-search</v-icon>
          <p class="mt-4 text-body-1" style="color: #80868b;">输入关键词开始搜索</p>
        </div>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import ArticleCard from '../../components/frontend/ArticleCard.vue'
import Pagination from '../../components/frontend/Pagination.vue'
import { getArticles } from '../../api/article.js'

export default {
  name: 'SearchView',
  components: { ArticleCard, Pagination },
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
      searched: false
    }
  },
  mounted: function() {
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
      self.loadResults()
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
