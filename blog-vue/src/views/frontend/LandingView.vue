<template>
  <v-app style="background: #0a0e1a; min-height: 100vh; overflow-x: hidden;">

    <!-- 顶部导航 -->
    <nav style="
      position: fixed; top: 0; left: 0; right: 0; z-index: 100;
      padding: 16px 32px;
      display: flex; align-items: center; justify-content: space-between;
      background: rgba(10, 14, 26, 0.85);
      backdrop-filter: blur(12px);
      border-bottom: 1px solid rgba(255,255,255,0.06);
    ">
      <!-- Logo -->
      <div class="d-flex align-center" style="gap: 10px;">
        <div style="
          width: 36px; height: 36px;
          background: linear-gradient(135deg, #1a73e8, #34a853);
          border-radius: 10px;
          display: flex; align-items: center; justify-content: center;
        ">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
        </div>
        <span style="font-size: 18px; font-weight: 700; color: white; letter-spacing: -0.3px;">我的博客</span>
      </div>

      <!-- 导航链接 -->
      <div class="d-none d-sm-flex align-center" style="gap: 4px;">
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          style="
            color: rgba(255,255,255,0.7);
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            padding: 6px 14px;
            border-radius: 20px;
            transition: all 0.2s;
          "
          class="nav-item"
        >{{ item.label }}</router-link>
      </div>

      <!-- 移动端菜单 -->
      <button
        class="d-flex d-sm-none"
        @click="mobileMenu = !mobileMenu"
        style="background: none; border: none; cursor: pointer; color: white; padding: 4px;"
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="white">
          <path v-if="!mobileMenu" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          <path v-else d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
    </nav>

    <!-- 移动端菜单 -->
    <div v-if="mobileMenu" style="
      position: fixed; top: 69px; left: 0; right: 0; z-index: 99;
      background: rgba(10,14,26,0.97);
      border-bottom: 1px solid rgba(255,255,255,0.08);
      padding: 8px 0;
    ">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        @click="mobileMenu = false"
        style="
          display: block; padding: 12px 24px;
          color: rgba(255,255,255,0.8);
          text-decoration: none; font-size: 15px;
        "
      >{{ item.label }}</router-link>
    </div>

    <!-- Hero 区域 -->
    <section style="
      min-height: 100vh;
      display: flex; align-items: center; justify-content: center;
      position: relative;
      padding: 100px 24px 60px;
      overflow: hidden;
    ">
      <!-- 背景粒子/光晕效果 -->
      <div style="
        position: absolute; inset: 0;
        background:
          radial-gradient(ellipse 80% 60% at 20% 40%, rgba(26,115,232,0.15) 0%, transparent 60%),
          radial-gradient(ellipse 60% 50% at 80% 60%, rgba(52,168,83,0.1) 0%, transparent 60%),
          radial-gradient(ellipse 40% 40% at 50% 80%, rgba(147,52,230,0.08) 0%, transparent 60%);
        pointer-events: none;
      "></div>

      <!-- 网格背景 -->
      <div style="
        position: absolute; inset: 0;
        background-image:
          linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
          linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
        background-size: 60px 60px;
        pointer-events: none;
      "></div>

      <!-- 主内容 -->
      <div style="position: relative; z-index: 1; text-align: center; max-width: 800px;">

        <!-- 标签 -->
        <div style="
          display: inline-flex; align-items: center; gap: 8px;
          background: rgba(26,115,232,0.15);
          border: 1px solid rgba(26,115,232,0.3);
          border-radius: 20px;
          padding: 6px 16px;
          margin-bottom: 32px;
          font-size: 13px;
          color: #8ab4f8;
          font-weight: 500;
        ">
          <span style="width: 6px; height: 6px; background: #1a73e8; border-radius: 50%; animation: pulse 2s infinite;"></span>
          技术博客 · 持续更新中
        </div>

        <!-- 主标题 -->
        <h1 style="
          font-size: clamp(36px, 6vw, 72px);
          font-weight: 800;
          line-height: 1.1;
          letter-spacing: -2px;
          margin: 0 0 24px;
          background: linear-gradient(135deg, #ffffff 0%, rgba(255,255,255,0.7) 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        ">
          记录技术成长<br/>
          <span style="
            background: linear-gradient(135deg, #1a73e8, #34a853);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          ">分享开发心得</span>
        </h1>

        <!-- 副标题（打字机效果） -->
        <p style="
          font-size: clamp(16px, 2vw, 20px);
          color: rgba(255,255,255,0.5);
          line-height: 1.7;
          margin: 0 0 48px;
          max-width: 560px;
          margin-left: auto;
          margin-right: auto;
        ">
          探索
          <span style="color: #8ab4f8;">{{ currentTech }}</span>
          <span style="color: rgba(255,255,255,0.3);">|</span>
          的无限可能，用代码构建更好的世界
        </p>

        <!-- CTA 按钮 -->
        <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;">
          <router-link to="/articles" style="text-decoration: none;">
            <button style="
              background: linear-gradient(135deg, #1a73e8, #1557b0);
              color: white;
              border: none;
              border-radius: 12px;
              padding: 14px 32px;
              font-size: 16px;
              font-weight: 600;
              cursor: pointer;
              display: flex; align-items: center; gap: 8px;
              transition: all 0.2s;
              box-shadow: 0 4px 20px rgba(26,115,232,0.4);
            " class="cta-btn-primary">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
              浏览文章
            </button>
          </router-link>

          <router-link to="/about" style="text-decoration: none;">
            <button style="
              background: rgba(255,255,255,0.06);
              color: rgba(255,255,255,0.85);
              border: 1px solid rgba(255,255,255,0.15);
              border-radius: 12px;
              padding: 14px 32px;
              font-size: 16px;
              font-weight: 600;
              cursor: pointer;
              display: flex; align-items: center; gap: 8px;
              transition: all 0.2s;
              backdrop-filter: blur(8px);
            " class="cta-btn-secondary">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/>
              </svg>
              了解博主
            </button>
          </router-link>
        </div>

        <!-- 滚动提示 -->
        <div style="margin-top: 64px; animation: bounce 2s infinite;" class="d-none d-sm-block">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="rgba(255,255,255,0.3)">
            <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/>
          </svg>
        </div>
      </div>
    </section>

    <!-- 统计数字区 -->
    <section style="
      padding: 60px 24px;
      border-top: 1px solid rgba(255,255,255,0.06);
      border-bottom: 1px solid rgba(255,255,255,0.06);
    ">
      <div style="max-width: 900px; margin: 0 auto; display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 32px; text-align: center;">
        <div v-for="stat in siteStats" :key="stat.label">
          <div style="font-size: 40px; font-weight: 800; color: white; line-height: 1; margin-bottom: 8px;">
            {{ stat.value }}<span style="color: #1a73e8;">+</span>
          </div>
          <div style="font-size: 14px; color: rgba(255,255,255,0.4); font-weight: 500;">{{ stat.label }}</div>
        </div>
      </div>
    </section>

    <!-- 最新文章预览 -->
    <section style="padding: 80px 24px;">
      <div style="max-width: 1100px; margin: 0 auto;">
        <div style="text-align: center; margin-bottom: 48px;">
          <div style="
            font-size: 12px; font-weight: 600; color: #1a73e8;
            text-transform: uppercase; letter-spacing: 2px; margin-bottom: 12px;
          ">LATEST POSTS</div>
          <h2 style="font-size: 32px; font-weight: 700; color: white; margin: 0;">最新文章</h2>
        </div>

        <div v-if="loadingArticles" style="text-align: center; padding: 40px;">
          <div style="
            width: 36px; height: 36px;
            border: 3px solid rgba(255,255,255,0.1);
            border-top-color: #1a73e8;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
            margin: 0 auto;
          "></div>
        </div>

        <div v-else style="display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px;">
          <router-link
            v-for="article in latestArticles"
            :key="article.id"
            :to="'/article/' + article.id"
            style="text-decoration: none;"
          >
            <div style="
              background: rgba(255,255,255,0.04);
              border: 1px solid rgba(255,255,255,0.08);
              border-radius: 16px;
              overflow: hidden;
              transition: all 0.25s;
              cursor: pointer;
            " class="article-card-dark">
              <!-- 封面 -->
              <div v-if="article.coverUrl" style="height: 160px; overflow: hidden;">
                <img :src="article.coverUrl" style="width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s;" class="article-cover-img" />
              </div>
              <!-- 无封面时的彩色占位 -->
              <div v-else :style="{
                height: '6px',
                background: 'linear-gradient(90deg, ' + getCategoryColor(article.categoryName) + ')'
              }"></div>

              <div style="padding: 20px;">
                <!-- 分类 -->
                <div v-if="article.categoryName" style="
                  display: inline-block;
                  font-size: 11px; font-weight: 600;
                  color: #8ab4f8;
                  background: rgba(26,115,232,0.15);
                  border-radius: 6px;
                  padding: 3px 8px;
                  margin-bottom: 10px;
                  text-transform: uppercase;
                  letter-spacing: 0.5px;
                ">{{ article.categoryName }}</div>

                <!-- 标题 -->
                <h3 style="
                  font-size: 16px; font-weight: 600;
                  color: rgba(255,255,255,0.9);
                  line-height: 1.4;
                  margin: 0 0 10px;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                ">{{ article.title }}</h3>

                <!-- 摘要 -->
                <p style="
                  font-size: 13px;
                  color: rgba(255,255,255,0.4);
                  line-height: 1.6;
                  margin: 0 0 16px;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                ">{{ article.summary }}</p>

                <!-- 底部信息 -->
                <div style="display: flex; align-items: center; justify-content: space-between;">
                  <span style="font-size: 12px; color: rgba(255,255,255,0.3);">
                    {{ formatDate(article.createTime) }}
                  </span>
                  <div style="display: flex; align-items: center; gap: 4px; font-size: 12px; color: rgba(255,255,255,0.3);">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                    {{ article.viewCount || 0 }}
                  </div>
                </div>
              </div>
            </div>
          </router-link>
        </div>

        <!-- 查看更多 -->
        <div style="text-align: center; margin-top: 48px;">
          <router-link to="/articles" style="text-decoration: none;">
            <button style="
              background: none;
              color: rgba(255,255,255,0.6);
              border: 1px solid rgba(255,255,255,0.15);
              border-radius: 10px;
              padding: 12px 32px;
              font-size: 14px;
              font-weight: 500;
              cursor: pointer;
              transition: all 0.2s;
              display: inline-flex; align-items: center; gap: 8px;
            " class="more-btn">
              查看全部文章
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/></svg>
            </button>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 技术标签云 -->
    <section style="
      padding: 60px 24px;
      border-top: 1px solid rgba(255,255,255,0.06);
    ">
      <div style="max-width: 800px; margin: 0 auto; text-align: center;">
        <h2 style="font-size: 24px; font-weight: 700; color: white; margin: 0 0 32px;">技术栈</h2>
        <div style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center;">
          <span
            v-for="tech in techStack"
            :key="tech.name"
            :style="{
              background: 'rgba(' + tech.rgb + ', 0.12)',
              border: '1px solid rgba(' + tech.rgb + ', 0.25)',
              color: 'rgba(' + tech.rgb + ', 1)',
              borderRadius: '8px',
              padding: '6px 14px',
              fontSize: '13px',
              fontWeight: '600',
              cursor: 'default'
            }"
          >{{ tech.name }}</span>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer style="
      padding: 32px 24px;
      border-top: 1px solid rgba(255,255,255,0.06);
      text-align: center;
    ">
      <div style="font-size: 13px; color: rgba(255,255,255,0.25);">
        © {{ new Date().getFullYear() }} 我的博客 · 用 ❤️ 和代码构建
      </div>
      <div style="display: flex; justify-content: center; gap: 20px; margin-top: 12px; flex-wrap: wrap;">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to"
          style="color: rgba(255,255,255,0.3); font-size: 12px; text-decoration: none; transition: color 0.15s;"
          class="footer-link">{{ item.label }}</router-link>
      </div>
    </footer>

  </v-app>
</template>

<script>
import { getArticles } from '../../api/article.js'
import request from '../../api/request.js'

export default {
  name: 'LandingView',
  data: function() {
    return {
      mobileMenu: false,
      loadingArticles: false,
      latestArticles: [],
      siteStats: [
        { label: '篇技术文章', value: 0 },
        { label: '个知识分类', value: 0 },
        { label: '个技术标签', value: 0 },
        { label: '次阅读', value: 0 }
      ],
      // 打字机效果
      techWords: ['Vue 3', 'Spring Boot', 'MySQL', 'Docker', 'TypeScript', 'Java'],
      currentTechIndex: 0,
      currentTech: 'Vue 3',
      techTimer: null,
      navItems: [
        { to: '/articles', label: '文章' },
        { to: '/archive', label: '归档' },
        { to: '/about', label: '关于' },
        { to: '/message', label: '留言' }
      ],
      techStack: [
        { name: 'Java', rgb: '234, 67, 53' },
        { name: 'Spring Boot', rgb: '52, 168, 83' },
        { name: 'MyBatis-Plus', rgb: '26, 115, 232' },
        { name: 'MySQL', rgb: '0, 114, 177' },
        { name: 'Vue 3', rgb: '65, 184, 131' },
        { name: 'Vuetify', rgb: '26, 115, 232' },
        { name: 'TypeScript', rgb: '49, 120, 198' },
        { name: 'Docker', rgb: '0, 150, 237' },
        { name: 'Sa-Token', rgb: '147, 52, 230' },
        { name: 'Tailwind CSS', rgb: '6, 182, 212' },
        { name: 'Git', rgb: '240, 80, 50' },
        { name: 'Linux', rgb: '255, 193, 7' }
      ]
    }
  },
  mounted: function() {
    this.loadLatestArticles()
    this.loadStats()
    this.startTechRotation()
  },
  beforeUnmount: function() {
    if (this.techTimer) clearInterval(this.techTimer)
  },
  methods: {
    loadLatestArticles: function() {
      var self = this
      self.loadingArticles = true
      getArticles({ pageNum: 1, pageSize: 6 })
        .then(function(data) {
          self.latestArticles = (data && data.list) ? data.list.slice(0, 6) : []
        })
        .catch(function() {})
        .finally(function() { self.loadingArticles = false })
    },
    loadStats: function() {
      var self = this
      request({ method: 'get', url: '/api/admin/dashboard' })
        .then(function(data) {
          self.siteStats[0].value = data.articleCount || 0
          self.siteStats[1].value = data.categoryCount || 0
          self.siteStats[2].value = data.tagCount || 0
          // 总阅读数从文章列表估算
        })
        .catch(function() {
          // mock 数据
          self.siteStats[0].value = 6
          self.siteStats[1].value = 3
          self.siteStats[2].value = 8
          self.siteStats[3].value = 8119
        })
    },
    startTechRotation: function() {
      var self = this
      self.techTimer = setInterval(function() {
        self.currentTechIndex = (self.currentTechIndex + 1) % self.techWords.length
        self.currentTech = self.techWords[self.currentTechIndex]
      }, 2000)
    },
    getCategoryColor: function(categoryName) {
      var colors = {
        '前端开发': '#1a73e8, #4285f4',
        '后端开发': '#34a853, #0f9d58',
        '工具效率': '#f29900, #fbbc04',
        '读书笔记': '#9334e6, #ab47bc'
      }
      return colors[categoryName] || '#1a73e8, #34a853'
    },
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return String(dateStr).substring(0, 10)
    }
  }
}
</script>

<style scoped>
.nav-item:hover {
  background: rgba(255,255,255,0.08) !important;
  color: white !important;
}
.cta-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(26,115,232,0.5) !important;
}
.cta-btn-secondary:hover {
  background: rgba(255,255,255,0.1) !important;
  border-color: rgba(255,255,255,0.3) !important;
  color: white !important;
}
.article-card-dark:hover {
  background: rgba(255,255,255,0.07) !important;
  border-color: rgba(255,255,255,0.15) !important;
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.3);
}
.article-card-dark:hover .article-cover-img {
  transform: scale(1.05);
}
.more-btn:hover {
  background: rgba(255,255,255,0.06) !important;
  color: white !important;
  border-color: rgba(255,255,255,0.3) !important;
}
.footer-link:hover {
  color: rgba(255,255,255,0.7) !important;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(8px); }
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
