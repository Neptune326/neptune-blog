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
        <!-- 分类标题 -->
        <div class="d-flex align-center mb-6" style="gap: 12px;">
          <div style="width: 40px; height: 40px; background: #e8f0fe; border-radius: 10px; display: flex; align-items: center; justify-content: center;">
            <v-icon color="primary" size="22">mdi-folder-outline</v-icon>
          </div>
          <div>
            <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0;">{{ categoryName || '分类文章' }}</h1>
            <p style="font-size: 13px; color: #80868b; margin: 2px 0 0;">共 {{ total }} 篇文章</p>
          </div>
        </div>

        <div v-if="loading" class="d-flex justify-center py-12">
          <v-progress-circular indeterminate color="primary" size="40" />
        </div>

        <template v-else>
          <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
          <div v-if="articles.length === 0" class="text-center py-16">
            <v-icon size="64" color="grey-lighten-2">mdi-folder-open-outline</v-icon>
            <p class="mt-4" style="color: #80868b;">该分类下暂无文章</p>
          </div>
          <Pagination :total="total" :pages="pages" :page-num="pageNum" @page-change="onPageChange" />
        </template>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import ArticleCard from '../../components/frontend/ArticleCard.vue'
import Pagination from '../../components/frontend/Pagination.vue'
import { getCategoryArticles } from '../../api/category.js'

export default {
  name: 'CategoryView',
  components: { ArticleCard, Pagination },
  data: function() {
    return {
      categoryName: '',   // 分类名称
      articles: [],       // 文章列表
      total: 0,           // 总条数
      pages: 1,           // 总页数
      pageNum: 1,         // 当前页码
      pageSize: 10,       // 每页条数
      loading: false      // 加载状态
    }
  },
  mounted: function() {
    // 页面挂载后加载该分类下的文章
    this.loadArticles()
  },
  methods: {
    // 加载分类文章列表
    loadArticles: function() {
      var self = this
      var id = self.$route.params.id
      self.loading = true
      getCategoryArticles(id, { pageNum: self.pageNum, pageSize: self.pageSize })
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
          // 从第一篇文章中取分类名称
          if (self.articles.length > 0 && self.articles[0].categoryName) {
            self.categoryName = self.articles[0].categoryName
          }
          // 接口可能直接返回分类名
          if (data.categoryName) {
            self.categoryName = data.categoryName
          }
        })
        .catch(function(err) {
          console.error('加载分类文章失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    // 翻页回调
    onPageChange: function(page) {
      this.pageNum = page
      this.loadArticles()
    }
  }
}
</script>
