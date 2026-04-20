<template>
  <v-app style="background: #f8f9fa;">
    <ReadingProgress />

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
      <v-container style="max-width: 820px; padding: 32px 16px;">
        <div v-if="loading" class="d-flex justify-center py-12">
          <v-progress-circular indeterminate color="primary" size="40" />
        </div>

        <v-card v-else elevation="0" rounded="lg" style="border: 1px solid #e8eaed;">
          <!-- 头部装饰 -->
          <div style="
            height: 120px;
            background: linear-gradient(135deg, #1a73e8 0%, #34a853 100%);
            border-radius: 12px 12px 0 0;
            position: relative;
          ">
            <!-- 头像 -->
            <div style="
              position: absolute;
              bottom: -40px; left: 32px;
              width: 80px; height: 80px;
              background: white;
              border-radius: 50%;
              border: 4px solid white;
              display: flex; align-items: center; justify-content: center;
              font-size: 32px;
              box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            ">
              {{ authorInitial }}
            </div>
          </div>

          <div class="pa-6 pa-md-8" style="padding-top: 56px !important;">
            <!-- 作者名 -->
            <h1 style="font-size: 22px; font-weight: 700; color: #202124; margin: 0 0 4px;">
              {{ authorName }}
            </h1>
            <p style="font-size: 14px; color: #80868b; margin: 0 0 24px;">{{ blogDescription }}</p>

            <v-divider style="margin-bottom: 24px;" />

            <!-- 关于我内容（Markdown 渲染） -->
            <div class="markdown-body" v-html="renderedContent" />
          </div>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { marked } from 'marked'
import ReadingProgress from '@/components/frontend/ReadingProgress.vue'
import request from '@/api/request.js'

export default {
  name: 'AboutView',
  components: { ReadingProgress },
  data: function() {
    return {
      loading: false,
      content: '',
      renderedContent: '',
      authorName: '博主',
      authorInitial: '博',
      blogDescription: '记录技术成长，分享开发心得'
    }
  },
  mounted: function() {
    this.loadAbout()
  },
  methods: {
    loadAbout: function() {
      var self = this
      self.loading = true
      // 从系统配置读取关于我内容（前台公开接口）
      request({ method: 'get', url: '/api/about' })
        .then(function(data) {
          self.content = data.content || ''
          self.authorName = data.authorName || '博主'
          self.blogDescription = data.description || ''
          self.authorInitial = self.authorName.charAt(0)
          self.renderedContent = marked(self.content)
        })
        .catch(function() {
          // 降级显示默认内容
          self.renderedContent = marked('# 关于我\n\n欢迎来到我的博客！')
        })
        .finally(function() {
          self.loading = false
        })
    }
  }
}
</script>


