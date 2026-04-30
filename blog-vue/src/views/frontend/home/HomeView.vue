<template>
  <v-app class="front-shell">
    <ReadingProgress />
    <FrontendNavBar variant="main" container-max-width="1200px" />

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <div class="d-flex flex-wrap mb-4" style="gap: 8px;">
          <v-btn size="small" variant="tonal" prepend-icon="mdi-format-list-bulleted" @click="scrollToSection('latest-section')">最新</v-btn>
          <v-btn size="small" variant="tonal" prepend-icon="mdi-fire" @click="scrollToSection('hot-section')">热门</v-btn>
          <v-btn size="small" variant="tonal" prepend-icon="mdi-star-outline" @click="scrollToSection('recommend-section')">推荐</v-btn>
        </div>
        <v-row>
          <v-col cols="12" md="8">
            <div id="latest-section" />
            <div class="d-flex align-center justify-space-between mb-4" style="gap: 8px;">
              <div class="front-soft" style="font-size: 14px;">
                共 <strong class="front-title">{{ total }}</strong> 篇文章
              </div>
              <div class="d-flex" style="gap: 4px;">
                <v-btn
                  :variant="sortBy === 'latest' ? 'flat' : 'text'"
                  :color="sortBy === 'latest' ? 'primary' : 'default'"
                  size="small"
                  prepend-icon="mdi-clock-outline"
                  @click="setSort('latest')"
                  style="font-size: 12px;"
                >最新</v-btn>
                <v-btn
                  :variant="sortBy === 'hot' ? 'flat' : 'text'"
                  :color="sortBy === 'hot' ? 'primary' : 'default'"
                  size="small"
                  prepend-icon="mdi-fire"
                  @click="setSort('hot')"
                  style="font-size: 12px;"
                >最热</v-btn>
              </div>
            </div>
            <div id="hot-section" />

            <ArticleSkeleton v-if="loading" :count="3" />

            <template v-else>
              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-file-document-outline</v-icon>
                <p class="mt-4 text-body-1 front-muted">暂无文章，敬请期待</p>
              </div>

              <ArticleCard
                v-for="article in articles"
                :key="article.id"
                :article="article"
              />

              <Pagination
                :total="total"
                :pages="pages"
                :page-num="pageNum"
                @page-change="onPageChange"
              />
            </template>
          </v-col>

          <v-col cols="12" md="4" class="d-none d-md-block">
            <div id="recommend-section" />
            <BlogSidebar />
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <v-footer class="front-footer" style="padding: 20px 16px;">
      <v-container style="max-width: 1200px; text-align: center;">
        <div class="front-muted" style="font-size: 13px; margin-bottom: 8px;">
          © {{ new Date().getFullYear() }} 我的博客 · 用 ❤️ 构建
        </div>
        <SiteRuntime class="mb-2" />
        <div style="display: flex; justify-content: center; gap: 16px; flex-wrap: wrap; margin-top: 8px;">
          <router-link to="/" class="footer-sub-link">首页</router-link>
          <router-link to="/archive" class="footer-sub-link">归档</router-link>
          <router-link to="/search" class="footer-sub-link">搜索</router-link>
        </div>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script>
import ArticleCard from '@/components/frontend/ArticleCard.vue'
import Pagination from '@/components/frontend/Pagination.vue'
import ArticleSkeleton from '@/components/frontend/ArticleSkeleton.vue'
import BlogSidebar from '@/components/frontend/BlogSidebar.vue'
import ReadingProgress from '@/components/frontend/ReadingProgress.vue'
import FrontendNavBar from '@/components/frontend/FrontendNavBar.vue'
import SiteRuntime from '@/components/frontend/SiteRuntime.vue'
import { getArticles } from '@/api/article.js'
import { smoothScrollToTop } from '@/utils/smoothScroll.js'

export default {
  name: 'HomeView',
  components: { ArticleCard, Pagination, ArticleSkeleton, BlogSidebar, ReadingProgress, FrontendNavBar, SiteRuntime },
  data: function() {
    return {
      articles: [],
      total: 0,
      pages: 1,
      pageNum: 1,
      pageSize: 10,
      sortBy: 'latest',
      loading: false
    }
  },
  mounted: function() {
    this.loadArticles()
  },
  methods: {
    getCacheKey: function() {
      return 'home_articles_cache_v1_' + this.sortBy + '_' + this.pageNum + '_' + this.pageSize
    },
    readCache: function() {
      try {
        var raw = sessionStorage.getItem(this.getCacheKey())
        if (!raw) return null
        var data = JSON.parse(raw)
        if (!data || !data.expireAt || data.expireAt < Date.now()) {
          return null
        }
        return data.payload || null
      } catch (e) {
        return null
      }
    },
    writeCache: function(payload) {
      try {
        sessionStorage.setItem(this.getCacheKey(), JSON.stringify({
          expireAt: Date.now() + 60 * 1000,
          payload: payload
        }))
      } catch (e) {}
    },
    loadArticles: function() {
      var self = this
      self.loading = true
      var params = { pageNum: self.pageNum, pageSize: self.pageSize }
      if (self.sortBy === 'hot') {
        params.orderBy = 'view_count'
      }
      var cache = self.readCache()
      if (cache) {
        self.articles = cache.list || []
        self.total = cache.total || 0
        self.pages = cache.pages || 1
        self.loading = false
      }
      getArticles(params)
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
          self.writeCache({
            list: self.articles,
            total: self.total,
            pages: self.pages
          })
        })
        .catch(function(err) {
          console.error('加载文章失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    setSort: function(sort) {
      this.sortBy = sort
      this.pageNum = 1
      this.loadArticles()
    },
    onPageChange: function(page) {
      this.pageNum = page
      this.loadArticles()
      smoothScrollToTop(400)
    },
    scrollToSection: function(sectionId) {
      if (sectionId === 'hot-section' && this.sortBy !== 'hot') {
        this.setSort('hot')
      }
      if (sectionId === 'latest-section' && this.sortBy !== 'latest') {
        this.setSort('latest')
      }
      var el = document.getElementById(sectionId)
      if (!el) return
      el.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }
}
</script>

<style scoped>
.footer-sub-link {
  color: var(--front-text-soft);
  font-size: 12px;
  text-decoration: none;
  opacity: 0.9;
}

.footer-sub-link:hover {
  color: var(--front-accent);
}
</style>
