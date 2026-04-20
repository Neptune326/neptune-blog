<template>
  <div>
    <div v-if="comments.length === 0" class="text-center py-8" style="color: #80868b;">
      <v-icon size="40" color="grey-lighten-1">mdi-comment-outline</v-icon>
      <p class="mt-2 text-body-2">暂无评论，来发表第一条评论吧</p>
    </div>

    <div v-else>
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="mb-5"
      >
        <!-- 顶级评论 -->
        <div class="d-flex" style="gap: 12px;">
          <v-avatar :color="avatarColor(comment.nickname)" size="38" style="flex-shrink: 0;">
            <span class="text-white text-body-2 font-weight-bold">
              {{ comment.nickname.charAt(0).toUpperCase() }}
            </span>
          </v-avatar>

          <div style="flex: 1; min-width: 0;">
            <div class="d-flex align-center justify-space-between mb-1">
              <div class="d-flex align-center" style="gap: 8px;">
                <span class="font-weight-medium text-body-2" style="color: #202124;">{{ comment.nickname }}</span>
                <span class="text-caption" style="color: #80868b;">{{ formatDate(comment.createTime) }}</span>
              </div>
              <!-- 回复按钮 -->
              <button
                @click="toggleReply(comment.id)"
                style="
                  background: none; border: none; cursor: pointer;
                  font-size: 12px; color: #80868b;
                  display: flex; align-items: center; gap: 3px;
                  padding: 2px 6px; border-radius: 4px;
                  transition: all 0.15s;
                "
                class="reply-btn"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z"/>
                </svg>
                回复
              </button>
            </div>

            <div class="text-body-2" style="color: #3c4043; line-height: 1.6; word-break: break-word;">
              {{ comment.content }}
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="mt-3">
              <div
                style="
                  background: #f8f9fa;
                  border-radius: 10px;
                  padding: 12px;
                  border: 1px solid #e8eaed;
                "
              >
                <div style="font-size: 12px; color: #80868b; margin-bottom: 8px;">
                  回复 <strong style="color: #1a73e8;">{{ comment.nickname }}</strong>
                </div>
                <textarea
                  v-model="replyContent"
                  placeholder="写下你的回复..."
                  style="
                    width: 100%; border: 1px solid #e8eaed; border-radius: 8px;
                    padding: 8px 12px; font-size: 13px; resize: none;
                    outline: none; font-family: inherit; background: white;
                    transition: border-color 0.15s;
                  "
                  rows="3"
                  @focus="$event.target.style.borderColor = '#1a73e8'"
                  @blur="$event.target.style.borderColor = '#e8eaed'"
                />
                <div class="d-flex justify-end mt-2" style="gap: 8px;">
                  <button
                    @click="cancelReply"
                    style="
                      background: none; border: 1px solid #e8eaed;
                      border-radius: 6px; padding: 4px 12px;
                      font-size: 12px; cursor: pointer; color: #5f6368;
                    "
                  >取消</button>
                  <button
                    @click="submitReply(comment)"
                    :disabled="!replyContent.trim() || replySubmitting"
                    style="
                      background: #1a73e8; color: white;
                      border: none; border-radius: 6px;
                      padding: 4px 12px; font-size: 12px; cursor: pointer;
                      opacity: 1; transition: opacity 0.15s;
                    "
                    :style="(!replyContent.trim() || replySubmitting) ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                  >
                    {{ replySubmitting ? '提交中...' : '提交回复' }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 子评论（回复列表） -->
            <div
              v-if="comment.children && comment.children.length > 0"
              class="mt-3"
              style="
                background: #f8f9fa;
                border-radius: 10px;
                padding: 12px 16px;
                border-left: 3px solid #e8eaed;
              "
            >
              <div
                v-for="child in comment.children"
                :key="child.id"
                class="d-flex mb-3"
                style="gap: 10px;"
              >
                <v-avatar :color="avatarColor(child.nickname)" size="28" style="flex-shrink: 0;">
                  <span class="text-white" style="font-size: 11px; font-weight: 700;">
                    {{ child.nickname.charAt(0).toUpperCase() }}
                  </span>
                </v-avatar>
                <div style="flex: 1; min-width: 0;">
                  <div class="d-flex align-center mb-1" style="gap: 6px;">
                    <span style="font-size: 13px; font-weight: 600; color: #202124;">{{ child.nickname }}</span>
                    <span v-if="child.replyTo" style="font-size: 12px; color: #80868b;">
                      回复 <span style="color: #1a73e8;">@{{ child.replyTo }}</span>
                    </span>
                    <span style="font-size: 11px; color: #9aa0a6;">{{ formatDate(child.createTime) }}</span>
                  </div>
                  <div style="font-size: 13px; color: #3c4043; line-height: 1.6; word-break: break-word;">
                    {{ child.content }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <v-divider v-if="comments.indexOf(comment) < comments.length - 1" class="mt-4" style="border-color: #f1f3f4;" />
      </div>
    </div>
  </div>
</template>

<script>
import { submitComment } from '@/api/comment.js'

export default {
  name: 'CommentList',
  props: {
    comments: { type: Array, default: function() { return [] } },
    articleId: { type: [Number, String], default: null }
  },
  emits: ['reply-submitted'],
  data: function() {
    return {
      replyingTo: null,
      replyContent: '',
      replySubmitting: false
    }
  },
  methods: {
    formatDate: function(dateStr) {
      if (!dateStr) return ''
      return String(dateStr).substring(0, 10)
    },
    avatarColor: function(nickname) {
      var colors = ['#1a73e8', '#34a853', '#ea4335', '#fbbc04', '#9c27b0', '#00bcd4', '#ff5722']
      var index = nickname ? nickname.charCodeAt(0) % colors.length : 0
      return colors[index]
    },
    toggleReply: function(commentId) {
      if (this.replyingTo === commentId) {
        this.cancelReply()
      } else {
        this.replyingTo = commentId
        this.replyContent = ''
      }
    },
    cancelReply: function() {
      this.replyingTo = null
      this.replyContent = ''
    },
    submitReply: function(parentComment) {
      var self = this
      if (!self.replyContent.trim()) return
      // 从 localStorage 读取上次填写的昵称
      var nickname = localStorage.getItem('comment_nickname') || ''
      if (!nickname) {
        nickname = prompt('请输入你的昵称：')
        if (!nickname) return
        localStorage.setItem('comment_nickname', nickname)
      }
      self.replySubmitting = true
      submitComment({
        articleId: Number(self.articleId),
        parentId: parentComment.id,
        replyTo: parentComment.nickname,
        nickname: nickname,
        content: self.replyContent.trim()
      })
        .then(function() {
          self.cancelReply()
          self.$emit('reply-submitted')
        })
        .catch(function(err) {
          console.error('提交回复失败:', err)
        })
        .finally(function() {
          self.replySubmitting = false
        })
    }
  }
}
</script>

<style scoped>
.reply-btn:hover {
  background: #f1f3f4;
  color: #1a73e8;
}
</style>

