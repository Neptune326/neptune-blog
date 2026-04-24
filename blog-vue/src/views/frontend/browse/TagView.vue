<template>
  <v-app class="front-shell">
    <FrontendNavBar variant="back" container-max-width="1200px" />

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <v-row>
          <v-col cols="12" md="8">
            <div class="d-flex align-center mb-6" style="gap: 12px;">
              <div style="width: 40px; height: 40px; background: color-mix(in srgb, var(--front-accent-2) 14%, transparent); border-radius: 10px; display: flex; align-items: center; justify-content: center;">
                <v-icon color="warning" size="22">mdi-tag-outline</v-icon>
              </div>
              <div>
                <h1 class="front-title" style="font-size: 20px; font-weight: 700; margin: 0;">
                  <v-chip color="primary" variant="tonal" size="small" class="mr-1">{{ tagName }}</v-chip>
                  标签文章
                </h1>
                <p class="front-muted" style="font-size: 13px; margin: 2px 0 0;">共 {{ total }} 篇文章</p>
              </div>
            </div>

            <div v-if="loading" class="d-flex justify-center py-12">
              <v-progress-circular indeterminate color="primary" size="40" />
            </div>

            <template v-else>
              <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
              <div v-if="articles.length === 0" class="text-center py-16">
                <v-icon size="64" color="grey-lighten-2">mdi-tag-outline</v-icon>
                <p class="mt-4 front-muted">该标签下暂无文章</p>
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
import { getTagArticles } from '@/api/tag.js'

export default {
  name: 'TagView',
  components: { ArticleCard, Pagination, FrontendNavBar, BlogSidebar },
  data: function() {
    return {
      tagName: '',
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
      getTagArticles(id, { pageNum: self.pageNum, pageSize: self.pageSize })
        .then(function(data) {
          self.articles = data.list || []
          self.total = data.total || 0
          self.pages = data.pages || 1
          if (data.tagName) {
            self.tagName = data.tagName
          }
        })
        .catch(function(err) {
          console.error('加载标签文章失败:', err)
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
