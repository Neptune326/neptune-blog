<template>
  <!-- 评论列表组件 —— Google 风格 -->
  <div>
    <div v-if="comments.length === 0" class="text-center py-8" style="color: #80868b;">
      <v-icon size="40" color="grey-lighten-1">mdi-comment-outline</v-icon>
      <p class="mt-2 text-body-2">暂无评论，来发表第一条评论吧</p>
    </div>

    <div v-else>
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="d-flex mb-4"
        style="gap: 12px;"
      >
        <!-- 头像 -->
        <v-avatar
          :color="avatarColor(comment.nickname)"
          size="40"
          style="flex-shrink: 0;"
        >
          <span class="text-white text-body-2 font-weight-bold">
            {{ comment.nickname.charAt(0).toUpperCase() }}
          </span>
        </v-avatar>

        <!-- 评论内容 -->
        <div style="flex: 1; min-width: 0;">
          <div class="d-flex align-center mb-1" style="gap: 8px;">
            <span class="font-weight-medium text-body-2" style="color: #202124;">
              {{ comment.nickname }}
            </span>
            <span class="text-caption" style="color: #80868b;">
              {{ formatDate(comment.createTime) }}
            </span>
          </div>
          <div
            class="text-body-2"
            style="color: #3c4043; line-height: 1.6; word-break: break-word;"
          >
            {{ comment.content }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentList',
  props: {
    comments: { type: Array, default: function() { return [] } }
  },
  methods: {
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 10)
    },
    // 根据昵称生成头像颜色
    avatarColor: function(nickname) {
      var colors = ['#1a73e8', '#34a853', '#ea4335', '#fbbc04', '#9c27b0', '#00bcd4', '#ff5722']
      var index = nickname ? nickname.charCodeAt(0) % colors.length : 0
      return colors[index]
    }
  }
}
</script>
