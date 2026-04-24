<template>
<<<<<<< HEAD
  <v-app class="front-shell">
    <!-- 阅读进度条 + 回到顶部 -->
    <ReadingProgress />

    <!-- 顶部导航栏 -->
    <v-app-bar elevation="0" class="front-app-bar">
      <v-container style="max-width: 1200px; display: flex; align-items: center; padding: 0 16px;">
        <!-- Logo -->
        <router-link to="/" class="d-flex align-center text-decoration-none" style="gap: 8px;">
          <v-icon color="primary" size="28">mdi-pencil-circle</v-icon>
          <span class="front-title" style="font-size: 20px; font-weight: 700; letter-spacing: 0;">
            我的博客
          </span>
        </router-link>

        <v-spacer />

        <!-- 桌面端导航 -->
        <div class="d-none d-sm-flex align-center" style="gap: 4px;">
          <v-btn variant="text" to="/" class="nav-link" style="min-width: auto;">首页</v-btn>
          <v-btn variant="text" to="/articles" class="nav-link" style="min-width: auto;">文章</v-btn>
          <v-btn variant="text" to="/archive" class="nav-link" style="min-width: auto;">归档</v-btn>
          <v-btn variant="text" to="/about" class="nav-link" style="min-width: auto;">关于</v-btn>
          <v-btn variant="text" to="/message" class="nav-link" style="min-width: auto;">留言</v-btn>
          <v-btn variant="text" to="/search" class="nav-link" style="min-width: auto;">
            <v-icon size="18" class="mr-1">mdi-magnify</v-icon>搜索
          </v-btn>
          <DarkModeToggle class="ml-1" />
        </div>

        <!-- 移动端汉堡菜单 -->
        <v-btn
          icon
          variant="text"
          class="d-flex d-sm-none"
          @click="mobileMenu = !mobileMenu"
          style="color: #5f6368;"
        >
          <v-icon>{{ mobileMenu ? 'mdi-close' : 'mdi-menu' }}</v-icon>
        </v-btn>
      </v-container>
    </v-app-bar>

    <!-- 移动端菜单下拉 -->
    <div
      v-if="mobileMenu"
      style="
        position: fixed; top: 64px; left: 0; right: 0;
        z-index: 100;
        padding: 8px 0;
      "
      class="front-nav-mobile"
    >
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        @click="mobileMenu = false"
        style="
          display: flex; align-items: center; gap: 10px;
          padding: 12px 20px;
          text-decoration: none;
          font-size: 15px;
          font-weight: 500;
          transition: background 0.15s;
        "
        class="mobile-nav-link"
      >
        <v-icon size="18" color="primary">{{ item.icon }}</v-icon>
        {{ item.label }}
      </router-link>
    </div>
=======
  <v-app style="background: var(--bg-primary);">
    <!-- 阅读进度条 + 回到顶部 -->
    <ReadingProgress />

    <FrontendNavBar variant="main" container-max-width="1200px" />
>>>>>>> cab4ca2899de7ec70ce6a25b534fbe02e1d9ca49

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <v-row>
          <!-- 主内容区 -->
          <v-col cols="12" md="8">
            <!-- 排序切换 -->
            <div class="d-flex align-center justify-space-between mb-4" style="gap: 8px;">
<<<<<<< HEAD
              <div class="front-soft" style="font-size: 14px;">
                共 <strong class="front-title">{{ total }}</strong> 篇文章
=======
              <div class="text-secondary-ctx" style="font-size: 14px;">
                共 <strong class="text-primary-ctx">{{ total }}</strong> 篇文章
>>>>>>> cab4ca2899de7ec70ce6a25b534fbe02e1d9ca49
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

            <!-- 骨架屏 -->
            <ArticleSkeleton v-if="loading" :count="3" />

            <template v-else>
              <!-- 无文章提示 -->
              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-file-document-outline</v-icon>
                <p class="mt-4 text-body-1 text-muted-ctx">暂无文章，敬请期待</p>
              </div>

              <!-- 文章卡片列表 -->
              <ArticleCard
                v-for="article in articles"
                :key="article.id"
                :article="article"
              />

              <!-- 分页 -->
              <Pagination
                :total="total"
                :pages="pages"
                :page-num="pageNum"
                @page-change="onPageChange"
              />
            </template>
          </v-col>

          <!-- 侧边栏（桌面端） -->
          <v-col cols="12" md="4" class="d-none d-md-block">
            <BlogSidebar />
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <!-- 底部 -->
<<<<<<< HEAD
    <v-footer class="front-footer" style="padding: 20px 16px;">
      <v-container style="max-width: 1200px; text-align: center;">
        <div class="front-muted" style="font-size: 13px; margin-bottom: 8px;">
=======
    <v-footer class="front-footer" style="background: var(--bg-card) !important; border-top: 1px solid var(--border-color); padding: 20px 16px;">
      <v-container style="max-width: 1200px; text-align: center;">
        <div class="text-muted-ctx" style="font-size: 13px; margin-bottom: 8px;">
>>>>>>> cab4ca2899de7ec70ce6a25b534fbe02e1d9ca49
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
import { getArticles } from '@/api/article.js'
import { smoothScrollToTop } from '@/utils/smoothScroll.js'
import SiteRuntime from '@/components/frontend/SiteRuntime.vue'

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
    loadArticles: function() {
      var self = this
      self.loading = true
      var params = { pageNum: self.pageNum, pageSize: self.pageSize }
      if (self.sortBy === 'hot') params.orderBy = 'view_count'
      getArticles(params)
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
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
    }
  }
}
</script>

<style scoped>
<<<<<<< HEAD
.mobile-nav-link:hover {
  background: color-mix(in srgb, var(--front-accent) 10%, transparent);
}
.mobile-nav-link {
  color: var(--front-text) !important;
=======
.text-primary-ctx {
  color: var(--text-primary);
}
.text-secondary-ctx {
  color: var(--text-secondary);
}
.text-muted-ctx {
  color: var(--text-muted);
}
.footer-sub-link {
  color: var(--text-secondary);
  font-size: 12px;
  text-decoration: none;
  opacity: 0.9;
}
.footer-sub-link:hover {
  color: var(--link-color);
>>>>>>> cab4ca2899de7ec70ce6a25b534fbe02e1d9ca49
}
</style>


