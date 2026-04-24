<template>
  <v-card
    class="mb-4 card-clickable front-card article-card-modern"
    rounded="xl"
    elevation="0"
    @click="goDetail"
  >
    <v-img
      v-if="article.coverUrl"
      :src="article.coverUrl"
      height="220"
      cover
      class="rounded-t-xl article-cover"
    >
      <template #default>
        <div class="pa-3 d-flex align-center justify-space-between">
          <v-chip
            v-if="article.categoryName"
            size="small"
            color="primary"
            class="article-chip"
          >
            {{ article.categoryName }}
          </v-chip>
          <v-chip
            v-if="article.isTop === 1"
            size="small"
            color="warning"
            class="article-chip"
            prepend-icon="mdi-pin"
          >
            置顶
          </v-chip>
        </div>
      </template>
    </v-img>

    <v-card-text class="pa-5">
      <div v-if="!article.coverUrl" class="mb-2 d-flex align-center justify-space-between">
        <v-chip v-if="article.categoryName" size="small" color="primary" variant="tonal">
          {{ article.categoryName }}
        </v-chip>
        <v-chip v-if="article.isTop === 1" size="small" color="warning" variant="tonal" prepend-icon="mdi-pin">
          置顶
        </v-chip>
      </div>

      <h3 class="text-h6 font-weight-bold mb-2 line-clamp-2 article-card-title">
        {{ article.title }}
      </h3>

      <p
        v-if="article.summary"
        class="text-body-2 line-clamp-3 mb-3 article-card-summary"
      >
        {{ article.summary }}
      </p>

      <div class="d-flex align-center justify-space-between flex-wrap" style="gap: 8px;">
        <div class="d-flex align-center flex-wrap article-meta">
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
      var count = this.wordCount
      return count > 0 ? Math.max(1, Math.ceil(count / 300)) : 0
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
.article-card-modern {
  overflow: hidden;
}

.article-card-modern:hover {
  transform: translateY(-4px);
}

.article-cover :deep(img) {
  transition: transform 0.35s ease;
}

.article-card-modern:hover .article-cover :deep(img) {
  transform: scale(1.04);
}

.article-chip {
  background: color-mix(in srgb, var(--front-accent) 84%, transparent) !important;
  color: white !important;
  backdrop-filter: blur(10px);
}

.article-card-title {
  color: var(--front-text);
  line-height: 1.4;
}

.article-card-summary {
  color: var(--front-text-soft);
  line-height: 1.7;
}

.article-meta {
  gap: 10px;
  color: var(--front-muted);
  font-size: 12px;
}
</style>
