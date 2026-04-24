<template>
  <v-app class="front-shell">
    <FrontendNavBar variant="back" container-max-width="1200px" />

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <v-row>
          <v-col cols="12" md="8">
            <div class="d-flex align-center mb-6" style="gap: 12px;">
              <div style="width: 40px; height: 40px; background: color-mix(in srgb, var(--front-accent) 12%, transparent); border-radius: 10px; display: flex; align-items: center; justify-content: center;">
                <v-icon color="primary" size="22">mdi-folder-outline</v-icon>
              </div>
              <div>
                <h1 class="front-title" style="font-size: 20px; font-weight: 700; margin: 0;">{{ categoryName || '分类文章' }}</h1>
                <p class="front-muted" style="font-size: 13px; margin: 2px 0 0;">共 {{ total }} 篇文章</p>
              </div>
            </div>

            <div v-if="loading" class="d-flex justify-center py-12">
              <v-progress-circular indeterminate color="primary" size="40" />
            </div>

            <template v-else>
              <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-folder-open-outline</v-icon>
                <p class="mt-4 front-muted">该分类下暂无文章</p>
              </div>
              <Pagination :total="total" :pages="pages" :page-num="pageNum" @page-change="onPageChange" />
            </template>
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
import { getCategoryArticles } from '@/api/category.js'

export default {
  name: 'CategoryView',
  components: { ArticleCard, Pagination, FrontendNavBar, BlogSidebar },
  data: function() {
    return {
      categoryName: '',
      articles: [],
      total: 0,
      pages: 1,
      pageNum: 1,
      pageSize: 10,
      loading: false
    }
  },
  mounted: function() {
    this.loadArticles()
  },
  methods: {
    loadArticles: function() {
      var self = this
      var id = self.$route.params.id
      self.loading = true
      getCategoryArticles(id, { pageNum: self.pageNum, pageSize: self.pageSize })
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
          if (self.articles.length > 0 && self.articles[0].categoryName) {
            self.categoryName = self.articles[0].categoryName
          }
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
    onPageChange: function(page) {
      this.pageNum = page
      this.loadArticles()
    }
  }
}
</script>
