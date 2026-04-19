<template>
  <v-app style="background: #f8f9fa;">
    <!-- 阅读进度条 + 回到顶部 -->
    <ReadingProgress />

    <!-- 顶部导航栏 -->
    <v-app-bar color="white" elevation="0" style="border-bottom: 1px solid #e8eaed;">
      <v-container style="max-width: 1200px; display: flex; align-items: center; padding: 0 16px;">
        <v-btn icon variant="text" @click="$router.back()" style="color: #5f6368;">
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
        <router-link to="/" class="d-flex align-center text-decoration-none ml-2" style="gap: 6px;">
          <v-icon color="primary" size="22">mdi-pencil-circle</v-icon>
          <span style="font-size: 16px; font-weight: 600; color: #202124;">我的博客</span>
        </router-link>
        <v-spacer />
        <!-- 分享按钮 -->
        <v-btn
          icon
          variant="text"
          @click="copyLink"
          style="color: #5f6368;"
          title="复制链接"
        >
          <v-icon size="20">mdi-share-variant-outline</v-icon>
        </v-btn>
      </v-container>
    </v-app-bar>

    <!-- 复制成功提示 -->
    <v-snackbar
      v-model="snackbar.show"
      :timeout="2000"
      location="top"
      color="success"
      rounded="lg"
    >
      {{ snackbar.text }}
    </v-snackbar>

    <v-main>
      <v-container style="max-width: 1200px; padding: 32px 16px;">
        <!-- 加载骨架 -->
        <div v-if="loading">
          <v-row>
            <v-col cols="12" md="8">
              <div style="background: white; border: 1px solid #e8eaed; border-radius: 12px; overflow: hidden;">
                <div class="skeleton" style="height: 300px;" />
                <div style="padding: 32px;">
                  <div class="skeleton" style="height: 14px; width: 120px; border-radius: 4px; margin-bottom: 16px;" />
                  <div class="skeleton" style="height: 32px; width: 80%; border-radius: 4px; margin-bottom: 10px;" />
                  <div class="skeleton" style="height: 32px; width: 60%; border-radius: 4px; margin-bottom: 24px;" />
                  <div v-for="i in 6" :key="i" class="skeleton" style="height: 14px; border-radius: 4px; margin-bottom: 8px;" :style="{ width: (70 + Math.random() * 30) + '%' }" />
                </div>
              </div>
            </v-col>
            <v-col cols="12" md="4" class="d-none d-md-block">
              <div style="background: white; border: 1px solid #e8eaed; border-radius: 12px; padding: 16px;">
                <div class="skeleton" style="height: 14px; width: 60px; border-radius: 4px; margin-bottom: 12px;" />
                <div v-for="i in 5" :key="i" class="skeleton" style="height: 12px; border-radius: 4px; margin-bottom: 8px;" :style="{ width: (50 + i * 8) + '%' }" />
              </div>
            </v-col>
          </v-row>
        </div>

        <v-row v-else-if="article">
          <!-- 文章主体 -->
          <v-col cols="12" md="8">
            <v-card elevation="0" rounded="lg" class="mb-6" style="border: 1px solid #e8eaed;">
              <!-- 封面图 -->
              <v-img
                v-if="article.coverUrl"
                :src="article.coverUrl"
                height="320"
                cover
                class="rounded-t-lg"
              />

              <div class="pa-6 pa-md-8">
                <!-- 分类 + 标签 -->
                <div class="d-flex flex-wrap mb-4" style="gap: 8px;">
                  <v-chip
                    v-if="article.categoryName"
                    color="primary"
                    variant="tonal"
                    size="small"
                    prepend-icon="mdi-folder-outline"
                    :to="'/category/' + article.categoryId"
                    style="cursor: pointer;"
                  >
                    {{ article.categoryName }}
                  </v-chip>
                  <v-chip
                    v-for="tag in article.tags"
                    :key="tag.id"
                    variant="outlined"
                    color="primary"
                    size="small"
                    :to="'/tag/' + tag.id"
                    style="cursor: pointer;"
                    class="tag-chip"
                  >
                    {{ tag.name }}
                  </v-chip>
                </div>

                <!-- 标题 -->
                <h1 class="mb-4" style="font-size: 28px; font-weight: 700; color: #202124; line-height: 1.35; letter-spacing: -0.3px;">
                  {{ article.title }}
                </h1>

                <!-- 元信息 -->
                <div
                  class="d-flex align-center flex-wrap mb-6"
                  style="gap: 16px; color: #80868b; font-size: 13px; border-bottom: 1px solid #e8eaed; padding-bottom: 20px;"
                >
                  <span class="d-flex align-center" style="gap: 4px;">
                    <v-icon size="15">mdi-calendar-outline</v-icon>
                    {{ formatDate(article.createTime) }}
                  </span>
                  <span class="d-flex align-center" style="gap: 4px;">
                    <v-icon size="15">mdi-eye-outline</v-icon>
                    {{ article.viewCount }} 次阅读
                  </span>
                  <span class="d-flex align-center" style="gap: 4px;">
                    <v-icon size="15">mdi-clock-outline</v-icon>
                    约 {{ readingTime }} 分钟阅读
                  </span>
                  <v-spacer />
                  <!-- 分享按钮组 -->
                  <div class="d-flex align-center" style="gap: 6px;">
                    <span style="font-size: 12px; color: #9aa0a6;">分享：</span>
                    <button @click="shareWeibo" title="分享到微博" style="background:none;border:none;cursor:pointer;padding:4px;border-radius:4px;color:#e6162d;font-size:12px;transition:background 0.15s;" class="share-btn">微博</button>
                    <button @click="copyLink" title="复制链接" style="background:none;border:none;cursor:pointer;padding:4px;border-radius:4px;color:#5f6368;font-size:12px;transition:background 0.15s;" class="share-btn">
                      <v-icon size="14">mdi-link-variant</v-icon>
                    </button>
                  </div>
                </div>

                <!-- Markdown 正文 -->
                <div class="markdown-body" ref="articleContent" v-html="renderedContent" />

                <!-- 文章底部：点赞 + 收藏 + 打印 -->
                <div class="d-flex align-center justify-center my-6 flex-wrap" style="gap: 12px;">
                  <button
                    @click="toggleLike"
                    :disabled="likeLoading"
                    style="
                      display: flex; align-items: center; gap: 8px;
                      padding: 10px 24px;
                      border-radius: 24px;
                      border: 2px solid;
                      cursor: pointer;
                      font-size: 15px;
                      font-weight: 600;
                      transition: all 0.2s;
                      background: none;
                    "
                    :style="liked
                      ? 'border-color: #ea4335; color: #ea4335; background: #fce8e6;'
                      : 'border-color: #e8eaed; color: #5f6368; background: white;'"
                  >
                    <svg width="18" height="18" viewBox="0 0 24 24" :fill="liked ? '#ea4335' : 'none'" :stroke="liked ? '#ea4335' : '#5f6368'" stroke-width="2">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                    </svg>
                    {{ liked ? '已点赞' : '点个赞' }}
                    <span style="font-size: 13px; opacity: 0.8;">{{ likeCount }}</span>
                  </button>

                  <!-- 收藏按钮 -->
                  <ArticleFavorite
                    v-if="article"
                    :article-id="article.id"
                    :article-title="article.title"
                  />

                  <!-- 打印/PDF -->
                  <button
                    @click="printArticle"
                    style="
                      display: flex; align-items: center; gap: 8px;
                      padding: 10px 20px;
                      border-radius: 24px;
                      border: 2px solid #e8eaed;
                      cursor: pointer;
                      font-size: 15px;
                      font-weight: 600;
                      transition: all 0.2s;
                      background: white;
                      color: #5f6368;
                    "
                  >
                    <v-icon size="18">mdi-printer-outline</v-icon>
                    打印
                  </button>
                </div>

                <!-- 版权声明 -->
                <div
                  style="
                    margin-top: 32px;
                    padding: 16px;
                    background: #f8f9fa;
                    border-left: 3px solid #1a73e8;
                    border-radius: 0 8px 8px 0;
                    font-size: 13px;
                    color: #5f6368;
                    line-height: 1.6;
                  "
                >
                  <div style="font-weight: 600; color: #3c4043; margin-bottom: 4px;">版权声明</div>
                  本文采用 CC BY-NC-SA 4.0 协议，转载请注明出处。
                </div>

                <!-- 上一篇 / 下一篇 -->
                <div
                  v-if="prevArticle || nextArticle"
                  style="
                    margin-top: 24px;
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 12px;
                  "
                >
                  <router-link
                    v-if="prevArticle"
                    :to="'/article/' + prevArticle.id"
                    style="
                      text-decoration: none;
                      padding: 12px 16px;
                      background: white;
                      border: 1px solid #e8eaed;
                      border-radius: 10px;
                      transition: all 0.2s;
                    "
                    class="prev-next-link"
                  >
                    <div style="font-size: 11px; color: #9aa0a6; margin-bottom: 4px;">
                      ← 上一篇
                    </div>
                    <div style="font-size: 13px; color: #3c4043; font-weight: 500; line-height: 1.4; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                      {{ prevArticle.title }}
                    </div>
                  </router-link>
                  <div v-else></div>

                  <router-link
                    v-if="nextArticle"
                    :to="'/article/' + nextArticle.id"
                    style="
                      text-decoration: none;
                      padding: 12px 16px;
                      background: white;
                      border: 1px solid #e8eaed;
                      border-radius: 10px;
                      text-align: right;
                      transition: all 0.2s;
                    "
                    class="prev-next-link"
                  >
                    <div style="font-size: 11px; color: #9aa0a6; margin-bottom: 4px;">
                      下一篇 →
                    </div>
                    <div style="font-size: 13px; color: #3c4043; font-weight: 500; line-height: 1.4; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                      {{ nextArticle.title }}
                    </div>
                  </router-link>
                </div>
              </div>
            </v-card>

                <!-- 相关文章推荐 -->
                <div v-if="relatedArticles.length > 0" style="margin-top: 24px;">
                  <div style="font-size: 14px; font-weight: 600; color: #5f6368; margin-bottom: 12px; display: flex; align-items: center; gap: 6px;">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="#5f6368"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/></svg>
                    相关文章
                  </div>
                  <div style="display: flex; flex-direction: column; gap: 8px;">
                    <router-link
                      v-for="rel in relatedArticles"
                      :key="rel.id"
                      :to="'/article/' + rel.id"
                      style="
                        display: flex; align-items: center; gap: 10px;
                        padding: 10px 12px;
                        background: #f8f9fa;
                        border-radius: 8px;
                        text-decoration: none;
                        transition: all 0.15s;
                        border: 1px solid transparent;
                      "
                      class="related-link"
                    >
                      <div style="flex: 1; min-width: 0;">
                        <div style="font-size: 13px; font-weight: 500; color: #202124; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                          {{ rel.title }}
                        </div>
                        <div style="font-size: 11px; color: #9aa0a6; margin-top: 2px;">
                          {{ rel.createTime ? String(rel.createTime).substring(0, 10) : '' }}
                        </div>
                      </div>
                    </router-link>
                  </div>
                </div>


            <!-- 评论区 -->
            <v-card elevation="0" rounded="lg" style="border: 1px solid #e8eaed;">
              <div class="pa-6">
                <div class="d-flex align-center mb-5" style="gap: 8px;">
                  <v-icon color="primary">mdi-comment-text-outline</v-icon>
                  <span style="font-size: 18px; font-weight: 600; color: #202124;">
                    评论
                    <span style="color: #80868b; font-size: 14px; font-weight: 400;">（{{ comments.length }}）</span>
                  </span>
                </div>

                <CommentList
                  :comments="comments"
                  :article-id="$route.params.id"
                  @reply-submitted="loadComments"
                />

                <v-divider class="my-6" style="border-color: #e8eaed;" />

                <div class="mb-4" style="font-size: 16px; font-weight: 600; color: #202124;">发表评论</div>

                <!-- 评论提交成功提示 -->
                <v-alert
                  v-if="commentSuccess"
                  type="success"
                  variant="tonal"
                  rounded="lg"
                  class="mb-4"
                  density="compact"
                  closable
                  @click:close="commentSuccess = false"
                >
                  评论提交成功！审核通过后将显示。
                </v-alert>

                <v-form ref="commentFormRef" @submit.prevent="handleSubmitComment">
                  <v-row dense>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="commentForm.nickname"
                        label="昵称"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-account-outline"
                        :rules="[function(v) { return !!v || '请填写昵称' }]"
                      />
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="commentForm.email"
                        label="邮箱（可选）"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-email-outline"
                      />
                    </v-col>
                    <v-col cols="12">
                      <div style="position: relative;">
                        <v-textarea
                          v-model="commentForm.content"
                          label="写下你的评论..."
                          variant="outlined"
                          rows="4"
                          :rules="[function(v) { return !!v || '请填写评论内容' }]"
                        />
                        <!-- Emoji 选择器 -->
                        <div style="position: absolute; bottom: 12px; left: 12px; z-index: 10;">
                          <EmojiPicker @select="insertEmoji" />
                        </div>
                      </div>
                    </v-col>
                  </v-row>
                  <div class="d-flex justify-end">
                    <v-btn
                      type="submit"
                      color="primary"
                      :loading="submitting"
                      prepend-icon="mdi-send"
                      style="min-width: 120px;"
                    >
                      提交评论
                    </v-btn>
                  </div>
                </v-form>
              </div>
            </v-card>
          </v-col>

          <!-- 右侧：目录 -->
          <v-col cols="12" md="4" class="d-none d-md-block">
            <TableOfContents :content="renderedContent" />
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { marked } from 'marked'
import CommentList from '../../components/frontend/CommentList.vue'
import ReadingProgress from '../../components/frontend/ReadingProgress.vue'
import TableOfContents from '../../components/frontend/TableOfContents.vue'
import ArticleFavorite from '../../components/frontend/ArticleFavorite.vue'
import Breadcrumb from '../../components/frontend/Breadcrumb.vue'
import ImageLightbox from '../../components/frontend/ImageLightbox.vue'
import EmojiPicker from '../../components/frontend/EmojiPicker.vue'
import { getArticleById } from '../../api/article.js'
import { getArticleComments, submitComment } from '../../api/comment.js'
import { smoothScrollToTop } from '../../utils/smoothScroll.js'
import request from '../../api/request.js'

export default {
  name: 'ArticleDetailView',
  components: { CommentList, ReadingProgress, TableOfContents, ArticleFavorite, Breadcrumb, ImageLightbox, EmojiPicker },
  data: function() {
    return {
      article: null,
      renderedContent: '',
      comments: [],
      relatedArticles: [],
      liked: false,
      likeCount: 0,
      likeLoading: false,
      loading: false,
      submitting: false,
      commentSuccess: false,
      commentForm: { nickname: '', email: '', content: '' },
      prevArticle: null,
      nextArticle: null,
      snackbar: { show: false, text: '' }
    }
  },
  computed: {
    // 预估阅读时间（按 300 字/分钟）
    readingTime: function() {
      if (!this.article || !this.article.content) return 1
      var words = this.article.content.length
      return Math.max(1, Math.ceil(words / 300))
    }
  },
  mounted: function() {
    this.loadArticle()
    this.loadComments()
  },
  watch: {
    // 路由变化时重新加载（从上一篇/下一篇跳转）
    '$route.params.id': function() {
      this.loadArticle()
      this.loadComments()
      smoothScrollToTop(400)
    },
    // 内容渲染完成后添加代码复制按钮
    renderedContent: function() {
      var self = this
      self.$nextTick(function() {
        self.addCopyButtons()
      })
    }
  },
  methods: {
    loadArticle: function() {
      var self = this
      var id = self.$route.params.id
      self.loading = true
      self.article = null
      getArticleById(id)
        .then(function(data) {
          self.article = data
          var renderer = new marked.Renderer()
          var headingCount = {}
          renderer.heading = function(text, level) {
            var slug = text.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-').replace(/^-|-$/g, '')
            headingCount[slug] = (headingCount[slug] || 0) + 1
            var id = headingCount[slug] > 1 ? slug + '-' + headingCount[slug] : slug
            return '<h' + level + ' id="' + id + '">' + text + '</h' + level + '>'
          }
          self.renderedContent = marked(data.content || '', { renderer: renderer })
          // 加载相关文章
          self.loadRelated(id)
          // 加载点赞状态
          self.loadLikeStatus(id)
        })
        .catch(function(err) {
          console.error('加载文章失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    loadRelated: function(id) {
      var self = this
      request({ method: 'get', url: '/api/articles/' + id + '/related', params: { limit: 5 } })
        .then(function(data) {
          self.relatedArticles = data || []
        })
        .catch(function() {})
    },
    loadLikeStatus: function(id) {
      var self = this
      request({ method: 'get', url: '/api/articles/' + id + '/like' })
        .then(function(data) {
          self.liked = data.liked || false
          self.likeCount = data.likeCount || 0
        })
        .catch(function() {})
    },
    toggleLike: function() {
      var self = this
      var id = self.$route.params.id
      self.likeLoading = true
      request({ method: 'post', url: '/api/articles/' + id + '/like' })
        .then(function(data) {
          self.liked = data.liked
          self.likeCount = data.likeCount
        })
        .catch(function() {})
        .finally(function() { self.likeLoading = false })
    },
    loadComments: function() {
      var self = this
      var id = self.$route.params.id
      getArticleComments(id, { pageNum: 1, pageSize: 100 })
        .then(function(data) {
          self.comments = data.list || []
        })
        .catch(function() {})
    },
    handleSubmitComment: function() {
      var self = this
      if (!self.commentForm.nickname || !self.commentForm.content) return
      self.submitting = true
      var id = self.$route.params.id
      submitComment({
        articleId: Number(id),
        nickname: self.commentForm.nickname,
        email: self.commentForm.email,
        content: self.commentForm.content
      })
        .then(function() {
          self.commentSuccess = true
          self.commentForm = { nickname: '', email: '', content: '' }
          // 重置表单校验状态，清除提示语
          self.$nextTick(function() {
            if (self.$refs.commentFormRef) {
              self.$refs.commentFormRef.reset()
            }
          })
        })
        .catch(function(err) {
          console.error('提交评论失败:', err)
        })
        .finally(function() {
          self.submitting = false
        })
    },
    // 为代码块添加复制按钮
    addCopyButtons: function() {
      var articleEl = this.$refs.articleContent
      if (!articleEl) return
      var preEls = articleEl.querySelectorAll('pre')
      preEls.forEach(function(pre) {
        if (pre.querySelector('.copy-btn')) return
        var btn = document.createElement('button')
        btn.className = 'copy-btn'
        btn.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/></svg> 复制'
        btn.style.cssText = 'position:absolute;top:8px;right:8px;background:rgba(255,255,255,0.1);color:#e8eaed;border:1px solid rgba(255,255,255,0.2);border-radius:6px;padding:3px 8px;font-size:11px;cursor:pointer;display:flex;align-items:center;gap:4px;transition:all 0.15s;'
        btn.addEventListener('mouseenter', function() { btn.style.background = 'rgba(255,255,255,0.2)' })
        btn.addEventListener('mouseleave', function() { btn.style.background = 'rgba(255,255,255,0.1)' })
        btn.addEventListener('click', function() {
          var code = pre.querySelector('code')
          var text = code ? code.textContent : pre.textContent
          navigator.clipboard.writeText(text).then(function() {
            btn.innerHTML = '✓ 已复制'
            btn.style.color = '#34a853'
            setTimeout(function() {
              btn.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/></svg> 复制'
              btn.style.color = '#e8eaed'
            }, 2000)
          })
        })
        pre.style.position = 'relative'
        pre.appendChild(btn)
      })
    },
    copyLink: function() {
      var self = this
      navigator.clipboard.writeText(window.location.href).then(function() {
        self.snackbar.text = '链接已复制到剪贴板'
        self.snackbar.show = true
      })
    },
    shareWeibo: function() {
      var url = encodeURIComponent(window.location.href)
      var title = encodeURIComponent(this.article ? this.article.title : '')
      window.open('https://service.weibo.com/share/share.php?url=' + url + '&title=' + title, '_blank')
    },
    // 打印文章
    printArticle: function() {
      window.print()
    },
    // 插入 Emoji 到评论框
    insertEmoji: function(emoji) {
      this.commentForm.content = (this.commentForm.content || '') + emoji
    },
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    }
  }
}
</script>

<style scoped>
.prev-next-link:hover {
  border-color: #1a73e8 !important;
  background: #f8fbff !important;
}
.related-link:hover {
  background: #e8f0fe !important;
  border-color: #1a73e8 !important;
}

.skeleton {
  background: linear-gradient(90deg, #f1f3f4 25%, #e8eaed 50%, #f1f3f4 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
