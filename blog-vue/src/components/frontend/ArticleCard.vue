<template>
  <!-- 文章卡片 —— Google Material Design 3 风格 -->
  <v-card
    class="mb-4 card-clickable article-card-root"
    rounded="lg"
    elevation="0"
    @click="goDetail"
  >
    <!-- 封面图 -->
    <v-img
      v-if="article.coverUrl"
      :src="article.coverUrl"
      height="220"
      cover
      class="rounded-t-lg"
    >
      <!-- 分类标签 + 置顶标识 浮层 -->
      <template #default>
        <div class="pa-3 d-flex align-center justify-space-between">
          <v-chip
            v-if="article.categoryName"
            size="small"
            color="primary"
            style="background: rgba(26,115,232,0.9); backdrop-filter: blur(4px);"
          >
            {{ article.categoryName }}
          </v-chip>
          <v-chip
            v-if="article.isTop === 1"
            size="small"
            color="warning"
            style="background: rgba(242,153,0,0.9); backdrop-filter: blur(4px);"
            prepend-icon="mdi-pin"
          >
            置顶
          </v-chip>
        </div>
      </template>
    </v-img>

    <v-card-text class="pa-5">
      <!-- 无封面时显示分类 + 置顶 -->
      <div v-if="!article.coverUrl" class="mb-2 d-flex align-center justify-space-between">
        <v-chip v-if="article.categoryName" size="small" color="primary" variant="tonal">
          {{ article.categoryName }}
        </v-chip>
        <v-chip v-if="article.isTop === 1" size="small" color="warning" variant="tonal" prepend-icon="mdi-pin">
          置顶
        </v-chip>
      </div>

      <!-- 标题 -->
      <h3
        class="text-h6 font-weight-bold mb-2 line-clamp-2 article-card-title"
      >
        {{ article.title }}
      </h3>

      <!-- 摘要 -->
      <p
        v-if="article.summary"
        class="text-body-2 line-clamp-3 mb-3 article-card-summary"
      >
        {{ article.summary }}
      </p>

      <!-- 底部：时间 + 阅读数 + 评论数 + 字数 + 标签 -->
      <div class="d-flex align-center justify-space-between flex-wrap" style="gap: 8px;">
        <!-- 左侧：元信息 -->
        <div class="d-flex align-center flex-wrap article-card-meta" style="gap: 10px; font-size: 12px;">
          <span class="d-flex align-center" style="gap: 3px;">
            <v-icon size="13" color="grey">mdi-calendar-outline</v-icon>
            {{ formatDate(article.createTime) }}
          </span>
          <span v-if="article.viewCount !== undefined" class="d-flex align-center" style="gap: 3px;">
            <v-icon size="13" color="grey">mdi-eye-outline</v-icon>
            {{ article.viewCount }}
          </span>
          <span v-if="article.commentCount !== undefined" class="d-flex align-center" style="gap: 3px;">
            <v-icon size="13" color="grey">mdi-comment-outline</v-icon>
            {{ article.commentCount }}
          </span>
          <span v-if="wordCount > 0" class="d-flex align-center" style="gap: 3px;">
            <v-icon size="13" color="grey">mdi-text</v-icon>
            {{ wordCount }} 字
          </span>
          <span v-if="readingTime > 0" class="d-flex align-center" style="gap: 3px;">
            <v-icon size="13" color="grey">mdi-clock-outline</v-icon>
            {{ readingTime }} 分钟
          </span>
        </div>

        <!-- 右侧：标签 -->
        <div class="d-flex flex-wrap" style="gap: 4px;">
          <v-chip
            v-for="tag in (article.tags || []).slice(0, 3)"
            :key="tag.id"
            size="x-small"
            variant="outlined"
            color="primary"
            class="tag-chip"
          >
            {{ tag.name }}
          </v-chip>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'ArticleCard',
  props: {
    article: { type: Object, required: true }
  },
  computed: {
    wordCount: function() {
      var content = this.article.summary || ''
      return content.replace(/\s/g, '').length
    },
    readingTime: function() {
      var wc = this.wordCount
      return wc > 0 ? Math.max(1, Math.ceil(wc / 300)) : 0
    }
  },
  setup: function(props) {
    var router = useRouter()

    function goDetail() {
      router.push('/article/' + props.article.id)
    }

    function formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    }

    return { goDetail, formatDate }
  }
}
</script>

<style scoped>
.article-card-root {
  border: 1px solid var(--border-color, #e8eaed) !important;
}
.article-card-title {
  color: var(--text-primary, #202124);
  line-height: 1.4;
}
.article-card-summary {
  color: var(--text-secondary, #5f6368);
  line-height: 1.6;
}
.article-card-meta {
  color: var(--text-muted, #80868b);
}
</style>
