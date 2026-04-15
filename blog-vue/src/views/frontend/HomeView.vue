<template>
  <v-app style="background: #f8f9fa;">
    <!-- 阅读进度条 + 回到顶部 -->
    <ReadingProgress />

    <!-- 顶部导航栏 -->
    <v-app-bar color="white" elevation="0" style="border-bottom: 1px solid #e8eaed;">
      <v-container style="max-width: 1200px; display: flex; align-items: center; padding: 0 16px;">
        <!-- Logo -->
        <router-link to="/" class="d-flex align-center text-decoration-none" style="gap: 8px;">
          <v-icon color="primary" size="28">mdi-pencil-circle</v-icon>
          <span style="font-size: 20px; font-weight: 600; color: #202124; letter-spacing: -0.3px;">
            我的博客
          </span>
        </router-link>

        <v-spacer />

        <!-- 桌面端导航 -->
        <div class="d-none d-sm-flex align-center" style="gap: 4px;">
          <v-btn variant="text" to="/" class="nav-link" style="min-width: auto;">首页</v-btn>
          <v-btn variant="text" to="/archive" class="nav-link" style="min-width: auto;">归档</v-btn>
          <v-btn variant="text" to="/search" class="nav-link" style="min-width: auto;">
            <v-icon size="18" class="mr-1">mdi-magnify</v-icon>搜索
          </v-btn>
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
        background: white;
        border-bottom: 1px solid #e8eaed;
        z-index: 100;
        padding: 8px 0;
        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
      "
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
          color: #3c4043;
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

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <v-row>
          <!-- 主内容区 -->
          <v-col cols="12" md="8">
            <!-- 骨架屏 -->
            <ArticleSkeleton v-if="loading" :count="3" />

            <template v-else>
              <!-- 无文章提示 -->
              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-file-document-outline</v-icon>
                <p class="mt-4 text-body-1" style="color: #80868b;">暂无文章，敬请期待</p>
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
    <v-footer color="white" style="border-top: 1px solid #e8eaed; padding: 20px 16px;">
      <v-container style="max-width: 1200px; text-align: center;">
        <div style="color: #80868b; font-size: 13px; margin-bottom: 6px;">
          © {{ new Date().getFullYear() }} 我的博客 · 用 ❤️ 构建
        </div>
        <div style="display: flex; justify-content: center; gap: 16px; flex-wrap: wrap;">
          <router-link to="/" style="color: #9aa0a6; font-size: 12px; text-decoration: none;">首页</router-link>
          <router-link to="/archive" style="color: #9aa0a6; font-size: 12px; text-decoration: none;">归档</router-link>
          <router-link to="/search" style="color: #9aa0a6; font-size: 12px; text-decoration: none;">搜索</router-link>
        </div>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script>
import ArticleCard from '../../components/frontend/ArticleCard.vue'
import Pagination from '../../components/frontend/Pagination.vue'
import ArticleSkeleton from '../../components/frontend/ArticleSkeleton.vue'
import BlogSidebar from '../../components/frontend/BlogSidebar.vue'
import ReadingProgress from '../../components/frontend/ReadingProgress.vue'
import { getArticles } from '../../api/article.js'
import { smoothScrollToTop } from '../../utils/smoothScroll.js'

export default {
  name: 'HomeView',
  components: { ArticleCard, Pagination, ArticleSkeleton, BlogSidebar, ReadingProgress },
  data: function() {
    return {
      articles: [],
      total: 0,
      pages: 1,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      mobileMenu: false,
      navItems: [
        { to: '/', label: '首页', icon: 'mdi-home-outline' },
        { to: '/archive', label: '归档', icon: 'mdi-archive-outline' },
        { to: '/search', label: '搜索', icon: 'mdi-magnify' }
      ]
    }
  },
  mounted: function() {
    this.loadArticles()
  },
  methods: {
    loadArticles: function() {
      var self = this
      self.loading = true
      getArticles({ pageNum: self.pageNum, pageSize: self.pageSize })
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
    onPageChange: function(page) {
      this.pageNum = page
      this.loadArticles()
      smoothScrollToTop(400)
    }
  }
}
</script>

<style scoped>
.mobile-nav-link:hover {
  background: #f8f9fa;
}
</style>
