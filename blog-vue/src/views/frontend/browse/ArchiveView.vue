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
      <v-container style="max-width: 760px; padding: 32px 16px;">

        <!-- 页面标题 -->
        <div class="d-flex align-center mb-8" style="gap: 12px;">
          <div
            style="
              width: 40px; height: 40px;
              background: #e8f0fe;
              border-radius: 10px;
              display: flex; align-items: center; justify-content: center;
            "
          >
            <v-icon color="primary" size="22">mdi-archive-outline</v-icon>
          </div>
          <div>
            <h1 style="font-size: 22px; font-weight: 700; color: #202124; margin: 0;">文章归档</h1>
            <p style="font-size: 13px; color: #80868b; margin: 2px 0 0;">共 {{ totalCount }} 篇文章</p>
          </div>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="d-flex justify-center py-12">
          <v-progress-circular indeterminate color="primary" size="40" />
        </div>

        <!-- 归档内容 -->
        <template v-else>
          <div v-if="archiveList.length === 0" class="text-center py-16">
            <v-icon size="64" color="grey-lighten-2">mdi-archive-outline</v-icon>
            <p class="mt-4" style="color: #80868b;">暂无归档数据</p>
          </div>

          <!-- 按年分组 -->
          <div v-for="yearGroup in archiveList" :key="yearGroup.year" class="mb-8">
            <!-- 年份标题 -->
            <div
              class="d-flex align-center mb-4"
              style="gap: 12px;"
            >
              <div
                style="
                  background: #1a73e8;
                  color: white;
                  font-size: 15px;
                  font-weight: 700;
                  padding: 4px 14px;
                  border-radius: 20px;
                  letter-spacing: 0.5px;
                "
              >
                {{ yearGroup.year }}
              </div>
              <span style="color: #80868b; font-size: 13px;">{{ yearGroup.count }} 篇</span>
              <div style="flex: 1; height: 1px; background: #e8eaed;"></div>
            </div>

            <!-- 按月分组 -->
            <div v-for="monthGroup in yearGroup.months" :key="monthGroup.month" class="mb-5 ml-2">
              <!-- 月份标题 -->
              <div
                class="d-flex align-center mb-3"
                style="gap: 8px; color: #5f6368; font-size: 13px; font-weight: 500;"
              >
                <v-icon size="14" color="grey">mdi-calendar-month-outline</v-icon>
                {{ monthGroup.month }} 月
                <span style="color: #9aa0a6;">· {{ monthGroup.articles.length }} 篇</span>
              </div>

              <!-- 文章列表 -->
              <div class="ml-4">
                <div
                  v-for="article in monthGroup.articles"
                  :key="article.id"
                  class="d-flex align-center mb-2"
                  style="gap: 12px;"
                >
                  <!-- 时间点 -->
                  <div style="position: relative; display: flex; align-items: center;">
                    <div
                      style="
                        width: 8px; height: 8px;
                        background: #1a73e8;
                        border-radius: 50%;
                        flex-shrink: 0;
                      "
                    ></div>
                  </div>

                  <!-- 日期 -->
                  <span
                    style="
                      font-size: 12px;
                      color: #9aa0a6;
                      min-width: 48px;
                      font-family: 'Roboto Mono', monospace;
                    "
                  >
                    {{ formatDay(article.createTime) }}
                  </span>

                  <!-- 文章标题 -->
                  <router-link
                    :to="'/article/' + article.id"
                    style="
                      color: #202124;
                      font-size: 14px;
                      text-decoration: none;
                      transition: color 0.2s;
                      flex: 1;
                      white-space: nowrap;
                      overflow: hidden;
                      text-overflow: ellipsis;
                    "
                    class="archive-link"
                  >
                    {{ article.title }}
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </template>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { getArchive } from '@/api/article.js'

export default {
  name: 'ArchiveView',
  data: function() {
    return {
      archiveList: [],
      totalCount: 0,
      loading: false
    }
  },
  mounted: function() {
    this.loadArchive()
  },
  methods: {
    loadArchive: function() {
      var self = this
      self.loading = true
      getArchive()
        .then(function(data) {
          if (Array.isArray(data)) {
            self.totalCount = data.length
            self.archiveList = self.groupByYearMonth(data)
          } else if (data && Array.isArray(data.list)) {
            self.totalCount = data.total || data.list.length
            self.archiveList = self.groupByYearMonth(data.list)
          } else {
            self.archiveList = []
          }
        })
        .catch(function(err) {
          console.error('加载归档失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    groupByYearMonth: function(articles) {
      var yearMap = {}
      articles.forEach(function(article) {
        if (!article.createTime) return
        var date = new Date(article.createTime)
        var year = date.getFullYear()
        var month = date.getMonth() + 1
        if (!yearMap[year]) yearMap[year] = { year: year, count: 0, monthMap: {} }
        yearMap[year].count++
        if (!yearMap[year].monthMap[month]) yearMap[year].monthMap[month] = { month: month, articles: [] }
        yearMap[year].monthMap[month].articles.push(article)
      })
      var result = Object.values(yearMap).sort(function(a, b) { return b.year - a.year })
      result.forEach(function(yearGroup) {
        yearGroup.months = Object.values(yearGroup.monthMap).sort(function(a, b) { return b.month - a.month })
        delete yearGroup.monthMap
      })
      return result
    },
    formatDay: function(dateStr) {
      if (!dateStr) return ''
      var date = new Date(dateStr)
      return String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.archive-link:hover {
  color: #1a73e8 !important;
}
</style>


