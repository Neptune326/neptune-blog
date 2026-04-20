<template>
  <v-app :style="animeMode ? 'background: #0a0e1a;' : 'background: #f0f4ff;'" style="min-height: 100vh; overflow-x: hidden;">

    <!-- 顶部导航 -->
    <nav :style="animeMode
      ? 'background: rgba(10,14,26,0.85); border-bottom: 1px solid rgba(255,255,255,0.06);'
      : 'background: rgba(255,255,255,0.9); border-bottom: 1px solid rgba(99,102,241,0.12); box-shadow: 0 1px 12px rgba(99,102,241,0.08);'"
      style="
        position: fixed; top: 0; left: 0; right: 0; z-index: 100;
        padding: 16px 32px;
        display: flex; align-items: center; justify-content: space-between;
        backdrop-filter: blur(12px);
      ">
      <!-- Logo -->
      <div class="d-flex align-center" style="gap: 10px;">
        <div style="
          width: 36px; height: 36px;
          background: linear-gradient(135deg, #6366f1, #8b5cf6);
          border-radius: 10px;
          display: flex; align-items: center; justify-content: center;
        ">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
        </div>
        <span :style="animeMode ? 'color: white;' : 'color: #1e1b4b;'" style="font-size: 18px; font-weight: 700; letter-spacing: -0.3px;">我的博客</span>
      </div>

      <!-- 导航链接 -->
      <div class="d-none d-sm-flex align-center" style="gap: 4px;">
        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          :style="animeMode
            ? 'color: rgba(255,255,255,0.7);'
            : 'color: #4b5563;'"
          style="
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
        :style="animeMode ? 'color: white;' : 'color: #4b5563;'"
        style="background: none; border: none; cursor: pointer; padding: 4px;"
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
          <path v-if="!mobileMenu" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          <path v-else d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
    </nav>

    <!-- 移动端菜单 -->
    <div v-if="mobileMenu" :style="animeMode
      ? 'background: rgba(10,14,26,0.97); border-bottom: 1px solid rgba(255,255,255,0.08);'
      : 'background: rgba(255,255,255,0.97); border-bottom: 1px solid rgba(99,102,241,0.1);'"
      style="position: fixed; top: 69px; left: 0; right: 0; z-index: 99; padding: 8px 0;">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        @click="mobileMenu = false"
        :style="animeMode ? 'color: rgba(255,255,255,0.8);' : 'color: #374151;'"
        style="display: block; padding: 12px 24px; text-decoration: none; font-size: 15px;"
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
      <!-- ===== 动漫模式：背景图轮播 ===== -->
      <template v-if="animeMode">
        <div style="position: absolute; inset: 0; z-index: 0;">
          <transition name="bg-fade">
            <img
              :key="currentBgIndex"
              :src="bgImages[currentBgIndex]"
              style="position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; transition: opacity 1s;"
            />
          </transition>
          <!-- 暗色遮罩 -->
          <div style="position: absolute; inset: 0; background: rgba(10,14,26,0.55);"></div>
        </div>
        <!-- 背景切换指示点 -->
        <div style="position: absolute; bottom: 80px; left: 50%; transform: translateX(-50%); display: flex; gap: 8px; z-index: 2;">
          <button
            v-for="(_, i) in bgImages"
            :key="i"
            @click="currentBgIndex = i"
            :style="i === currentBgIndex
              ? 'width: 20px; background: white; opacity: 1;'
              : 'width: 8px; background: rgba(255,255,255,0.5); opacity: 0.6;'"
            style="height: 8px; border: none; border-radius: 4px; cursor: pointer; transition: all 0.3s; padding: 0;"
          ></button>
        </div>
      </template>

      <!-- ===== 亮色模式：渐变背景 ===== -->
      <template v-else>
        <!-- 主渐变背景 -->
        <div style="
          position: absolute; inset: 0;
          background: linear-gradient(135deg, #f0f4ff 0%, #faf5ff 40%, #f0fdf4 100%);
          pointer-events: none;
        "></div>
        <!-- 彩色光晕 -->
        <div style="
          position: absolute; inset: 0;
          background:
            radial-gradient(ellipse 70% 50% at 15% 40%, rgba(99,102,241,0.12) 0%, transparent 60%),
            radial-gradient(ellipse 50% 40% at 85% 60%, rgba(139,92,246,0.1) 0%, transparent 60%),
            radial-gradient(ellipse 40% 35% at 50% 90%, rgba(16,185,129,0.08) 0%, transparent 60%);
          pointer-events: none;
        "></div>
        <!-- 装饰圆圈 -->
        <div style="position: absolute; top: 15%; right: 8%; width: 300px; height: 300px; border-radius: 50%; background: linear-gradient(135deg, rgba(99,102,241,0.08), rgba(139,92,246,0.06)); pointer-events: none;"></div>
        <div style="position: absolute; bottom: 20%; left: 5%; width: 200px; height: 200px; border-radius: 50%; background: linear-gradient(135deg, rgba(16,185,129,0.07), rgba(99,102,241,0.05)); pointer-events: none;"></div>
        <!-- 点阵背景 -->
        <div style="
          position: absolute; inset: 0;
          background-image: radial-gradient(circle, rgba(99,102,241,0.12) 1px, transparent 1px);
          background-size: 32px 32px;
          pointer-events: none;
          opacity: 0.6;
        "></div>
      </template>

      <!-- 主内容 -->
      <div style="position: relative; z-index: 1; text-align: center; max-width: 800px;">

        <!-- 标签 -->
        <div :style="animeMode
          ? 'background: rgba(99,102,241,0.15); border: 1px solid rgba(99,102,241,0.35); color: #a5b4fc;'
          : 'background: rgba(99,102,241,0.08); border: 1px solid rgba(99,102,241,0.2); color: #6366f1;'"
          style="
            display: inline-flex; align-items: center; gap: 8px;
            border-radius: 20px;
            padding: 6px 16px;
            margin-bottom: 32px;
            font-size: 13px;
            font-weight: 500;
          ">
          <span style="width: 6px; height: 6px; background: #6366f1; border-radius: 50%; animation: pulse 2s infinite;"></span>
          技术博客 · 持续更新中
        </div>

        <!-- 主标题 -->
        <h1 :style="animeMode
          ? 'background: linear-gradient(135deg, #ffffff 0%, rgba(255,255,255,0.75) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;'
          : 'background: linear-gradient(135deg, #1e1b4b 0%, #4338ca 60%, #7c3aed 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;'"
          style="
            font-size: clamp(36px, 6vw, 72px);
            font-weight: 800;
            line-height: 1.1;
            letter-spacing: -2px;
            margin: 0 0 24px;
          ">
          记录技术成长<br/>
          <span style="
            background: linear-gradient(135deg, #6366f1, #8b5cf6, #ec4899);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          ">分享开发心得</span>
        </h1>

        <!-- 副标题 -->
        <p :style="animeMode ? 'color: rgba(255,255,255,0.55);' : 'color: #6b7280;'"
          style="
            font-size: clamp(16px, 2vw, 20px);
            line-height: 1.7;
            margin: 0 0 48px;
            max-width: 560px;
            margin-left: auto;
            margin-right: auto;
          ">
          探索
          <span :style="animeMode ? 'color: #a5b4fc;' : 'color: #6366f1;'" style="font-weight: 600;">{{ currentTech }}</span>
          <span :style="animeMode ? 'color: rgba(255,255,255,0.25);' : 'color: #d1d5db;'"> | </span>
          的无限可能，用代码构建更好的世界
        </p>

        <!-- CTA 按钮 -->
        <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;">
          <router-link to="/articles" style="text-decoration: none;">
            <button style="
              background: linear-gradient(135deg, #6366f1, #8b5cf6);
              color: white;
              border: none;
              border-radius: 12px;
              padding: 14px 32px;
              font-size: 16px;
              font-weight: 600;
              cursor: pointer;
              display: flex; align-items: center; gap: 8px;
              transition: all 0.2s;
              box-shadow: 0 4px 20px rgba(99,102,241,0.4);
            " class="cta-btn-primary">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
              浏览文章
            </button>
          </router-link>

          <router-link to="/about" style="text-decoration: none;">
            <button :style="animeMode
              ? 'background: rgba(255,255,255,0.06); color: rgba(255,255,255,0.85); border: 1px solid rgba(255,255,255,0.15);'
              : 'background: white; color: #4b5563; border: 1px solid rgba(99,102,241,0.2); box-shadow: 0 2px 8px rgba(99,102,241,0.08);'"
              style="
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
          <svg width="24" height="24" viewBox="0 0 24 24" :fill="animeMode ? 'rgba(255,255,255,0.3)' : 'rgba(99,102,241,0.4)'">
            <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/>
          </svg>
        </div>
      </div>
    </section>

    <!-- 统计数字区 -->
    <section :style="animeMode
      ? 'border-top: 1px solid rgba(255,255,255,0.06); border-bottom: 1px solid rgba(255,255,255,0.06);'
      : 'border-top: 1px solid rgba(99,102,241,0.1); border-bottom: 1px solid rgba(99,102,241,0.1); background: white;'"
      style="padding: 60px 24px;">
      <div style="max-width: 900px; margin: 0 auto; display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 32px; text-align: center;">
        <div v-for="stat in siteStats" :key="stat.label">
          <div :style="animeMode ? 'color: white;' : 'background: linear-gradient(135deg, #6366f1, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;'"
            style="font-size: 40px; font-weight: 800; line-height: 1; margin-bottom: 8px;">
            {{ stat.value }}<span style="color: #6366f1;">+</span>
          </div>
          <div :style="animeMode ? 'color: rgba(255,255,255,0.4);' : 'color: #9ca3af;'" style="font-size: 14px; font-weight: 500;">{{ stat.label }}</div>
        </div>
      </div>
    </section>

    <!-- 最新文章预览 -->
    <section :style="animeMode ? '' : 'background: #f8f7ff;'" style="padding: 80px 24px;">
      <div style="max-width: 1100px; margin: 0 auto;">
        <div style="text-align: center; margin-bottom: 48px;">
          <div style="font-size: 12px; font-weight: 600; color: #6366f1; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 12px;">LATEST POSTS</div>
          <h2 :style="animeMode ? 'color: white;' : 'color: #1e1b4b;'" style="font-size: 32px; font-weight: 700; margin: 0;">最新文章</h2>
        </div>

        <div v-if="loadingArticles" style="text-align: center; padding: 40px;">
          <div style="
            width: 36px; height: 36px;
            border: 3px solid rgba(99,102,241,0.15);
            border-top-color: #6366f1;
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
            <div :style="animeMode
              ? 'background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08);'
              : 'background: white; border: 1px solid rgba(99,102,241,0.1); box-shadow: 0 2px 12px rgba(99,102,241,0.06);'"
              style="
                border-radius: 16px;
                overflow: hidden;
                transition: all 0.25s;
                cursor: pointer;
              " class="article-card">
              <!-- 封面 -->
              <div v-if="article.coverUrl" style="height: 160px; overflow: hidden;">
                <img :src="article.coverUrl" style="width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s;" class="article-cover-img" />
              </div>
              <div v-else :style="{
                height: '4px',
                background: 'linear-gradient(90deg, ' + getCategoryColor(article.categoryName) + ')'
              }"></div>

              <div style="padding: 20px;">
                <div v-if="article.categoryName" :style="animeMode
                  ? 'color: #a5b4fc; background: rgba(99,102,241,0.15);'
                  : 'color: #6366f1; background: rgba(99,102,241,0.08);'"
                  style="display: inline-block; font-size: 11px; font-weight: 600; border-radius: 6px; padding: 3px 8px; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 0.5px;">
                  {{ article.categoryName }}
                </div>

                <h3 :style="animeMode ? 'color: rgba(255,255,255,0.9);' : 'color: #1e1b4b;'"
                  style="font-size: 16px; font-weight: 600; line-height: 1.4; margin: 0 0 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                  {{ article.title }}
                </h3>

                <p :style="animeMode ? 'color: rgba(255,255,255,0.4);' : 'color: #9ca3af;'"
                  style="font-size: 13px; line-height: 1.6; margin: 0 0 16px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                  {{ article.summary }}
                </p>

                <div style="display: flex; align-items: center; justify-content: space-between;">
                  <span :style="animeMode ? 'color: rgba(255,255,255,0.3);' : 'color: #d1d5db;'" style="font-size: 12px;">
                    {{ formatDate(article.createTime) }}
                  </span>
                  <div :style="animeMode ? 'color: rgba(255,255,255,0.3);' : 'color: #d1d5db;'" style="display: flex; align-items: center; gap: 4px; font-size: 12px;">
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
            <button :style="animeMode
              ? 'color: rgba(255,255,255,0.6); border: 1px solid rgba(255,255,255,0.15); background: none;'
              : 'color: #6366f1; border: 1px solid rgba(99,102,241,0.25); background: white; box-shadow: 0 2px 8px rgba(99,102,241,0.08);'"
              style="
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
    <section :style="animeMode
      ? 'border-top: 1px solid rgba(255,255,255,0.06);'
      : 'border-top: 1px solid rgba(99,102,241,0.1); background: white;'"
      style="padding: 60px 24px;">
      <div style="max-width: 800px; margin: 0 auto; text-align: center;">
        <h2 :style="animeMode ? 'color: white;' : 'color: #1e1b4b;'" style="font-size: 24px; font-weight: 700; margin: 0 0 32px;">技术栈</h2>
        <div style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center;">
          <span
            v-for="tech in techStack"
            :key="tech.name"
            :style="{
              background: 'rgba(' + tech.rgb + ', 0.1)',
              border: '1px solid rgba(' + tech.rgb + ', 0.22)',
              color: animeMode ? 'rgba(' + tech.rgb + ', 0.9)' : 'rgb(' + tech.rgb + ')',
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
    <footer :style="animeMode
      ? 'border-top: 1px solid rgba(255,255,255,0.06);'
      : 'border-top: 1px solid rgba(99,102,241,0.1); background: #f8f7ff;'"
      style="padding: 32px 24px; text-align: center;">
      <div :style="animeMode ? 'color: rgba(255,255,255,0.25);' : 'color: #9ca3af;'" style="font-size: 13px; margin-bottom: 10px;">
        © {{ new Date().getFullYear() }} 我的博客 · 用 ❤️ 和代码构建
      </div>
      <!-- 运行时间 + 访客计数 -->
      <SiteRuntime class="mb-3" />
      <div style="display: flex; justify-content: center; gap: 20px; margin-top: 12px; flex-wrap: wrap;">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to"
          :style="animeMode ? 'color: rgba(255,255,255,0.3);' : 'color: #d1d5db;'"
          style="font-size: 12px; text-decoration: none; transition: color 0.15s;"
          class="footer-link">{{ item.label }}</router-link>
      </div>
    </footer>

  </v-app>
</template>

<script>
import { getArticles } from '@/api/article.js'
import request from '@/api/request.js'
import SiteRuntime from '@/components/frontend/SiteRuntime.vue'

// 默认二次元背景图（使用 picsum 的动漫风格占位，实际可替换为真实二次元图源）
var DEFAULT_ANIME_IMAGES = [
  'https://picsum.photos/seed/anime1/1920/1080',
  'https://picsum.photos/seed/anime2/1920/1080',
  'https://picsum.photos/seed/anime3/1920/1080',
  'https://picsum.photos/seed/anime4/1920/1080',
  'https://picsum.photos/seed/anime5/1920/1080'
]

export default {
  name: 'LandingView',
  components: { SiteRuntime },
  data: function() {
    return {
      mobileMenu: false,
      loadingArticles: false,
      latestArticles: [],
      // 动漫主题
      animeMode: false,
      bgImages: DEFAULT_ANIME_IMAGES.slice(),
      currentBgIndex: 0,
      bgTimer: null,
      siteStats: [
        { label: '篇技术文章', value: 0 },
        { label: '个知识分类', value: 0 },
        { label: '个技术标签', value: 0 },
        { label: '次阅读', value: 0 }
      ],
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
        { name: 'MyBatis-Plus', rgb: '99, 102, 241' },
        { name: 'MySQL', rgb: '0, 114, 177' },
        { name: 'Vue 3', rgb: '65, 184, 131' },
        { name: 'Vuetify', rgb: '99, 102, 241' },
        { name: 'TypeScript', rgb: '49, 120, 198' },
        { name: 'Docker', rgb: '0, 150, 237' },
        { name: 'Sa-Token', rgb: '139, 92, 246' },
        { name: 'Tailwind CSS', rgb: '6, 182, 212' },
        { name: 'Git', rgb: '240, 80, 50' },
        { name: 'Linux', rgb: '234, 179, 8' }
      ]
    }
  },
  mounted: function() {
    this.loadLatestArticles()
    this.loadStats()
    this.loadAnimeConfig()
    this.startTechRotation()
  },
  beforeUnmount: function() {
    if (this.techTimer) clearInterval(this.techTimer)
    if (this.bgTimer) clearInterval(this.bgTimer)
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
        })
        .catch(function() {
          self.siteStats[0].value = 6
          self.siteStats[1].value = 3
          self.siteStats[2].value = 8
          self.siteStats[3].value = 8119
        })
    },
    loadAnimeConfig: function() {
      var self = this
      // 读取公开的前台站点配置（不需要鉴权）
      request({ method: 'get', url: '/api/site-config' })
        .then(function(data) {
          self.animeMode = data && data.anime_theme_enabled === 'true'
          if (data && data.gallery_images) {
            try {
              var imgs = JSON.parse(data.gallery_images)
              if (Array.isArray(imgs) && imgs.length > 0) {
                self.bgImages = imgs
              }
            } catch (e) {}
          }
          if (self.animeMode) {
            self.startBgRotation()
          }
        })
        .catch(function() {})
    },
    startTechRotation: function() {
      var self = this
      self.techTimer = setInterval(function() {
        self.currentTechIndex = (self.currentTechIndex + 1) % self.techWords.length
        self.currentTech = self.techWords[self.currentTechIndex]
      }, 2000)
    },
    startBgRotation: function() {
      var self = this
      if (self.bgTimer) clearInterval(self.bgTimer)
      if (self.bgImages.length <= 1) return
      self.bgTimer = setInterval(function() {
        self.currentBgIndex = (self.currentBgIndex + 1) % self.bgImages.length
      }, 5000)
    },
    getCategoryColor: function(categoryName) {
      var colors = {
        '前端开发': '#6366f1, #8b5cf6',
        '后端开发': '#10b981, #059669',
        '工具效率': '#f59e0b, #d97706',
        '读书笔记': '#ec4899, #db2777'
      }
      return colors[categoryName] || '#6366f1, #8b5cf6'
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
  background: rgba(99,102,241,0.1) !important;
  color: #6366f1 !important;
}
.cta-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(99,102,241,0.5) !important;
}
.cta-btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(99,102,241,0.15) !important;
  border-color: rgba(99,102,241,0.4) !important;
}
.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(99,102,241,0.15) !important;
  border-color: rgba(99,102,241,0.2) !important;
}
.article-card:hover .article-cover-img {
  transform: scale(1.05);
}
.more-btn:hover {
  background: rgba(99,102,241,0.06) !important;
  border-color: rgba(99,102,241,0.4) !important;
  color: #6366f1 !important;
}
.footer-link:hover {
  color: #6366f1 !important;
}

/* 背景图淡入淡出 */
.bg-fade-enter-active,
.bg-fade-leave-active {
  transition: opacity 1s ease;
}
.bg-fade-enter-from,
.bg-fade-leave-to {
  opacity: 0;
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


