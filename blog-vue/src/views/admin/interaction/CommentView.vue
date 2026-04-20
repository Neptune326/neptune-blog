<template>
  <div>
    <!-- 状态筛选 -->
    <v-card class="mb-4 pa-4">
      <v-row align="center">
        <v-col cols="12" sm="4">
          <v-select
            v-model="filters.status"
            :items="statusOptions"
            item-title="label"
            item-value="value"
            label="评论状态"
            variant="outlined"
            density="compact"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="2">
          <v-btn color="primary" @click="loadComments">筛选</v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- 评论列表 -->
    <v-card>
      <v-table>
        <thead>
          <tr>
            <th>ID</th>
            <th>文章ID</th>
            <th>昵称</th>
            <th>内容</th>
            <th>状态</th>
            <th>时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center pa-4">
              <v-progress-circular indeterminate color="primary" />
            </td>
          </tr>
          <tr v-else-if="comments.length === 0">
            <td colspan="7" class="text-center pa-4 text-grey">暂无评论</td>
          </tr>
          <tr v-for="comment in comments" :key="comment.id" v-else>
            <td>{{ comment.id }}</td>
            <td>{{ comment.articleId }}</td>
            <td>{{ comment.nickname || comment.authorName || '-' }}</td>
            <td class="comment-content">{{ comment.content }}</td>
            <td>
              <v-chip :color="statusColor(comment.status)" size="small">
                {{ statusLabel(comment.status) }}
              </v-chip>
            </td>
            <td>{{ formatDate(comment.createTime) }}</td>
            <td>
              <!-- 待审核（status=0）时显示通过/拒绝按钮 -->
              <template v-if="comment.status === 0">
                <v-btn
                  size="small"
                  variant="text"
                  color="success"
                  :loading="actionLoading === comment.id + '_approve'"
                  @click="handleApprove(comment)"
                >通过</v-btn>
                <v-btn
                  size="small"
                  variant="text"
                  color="warning"
                  :loading="actionLoading === comment.id + '_reject'"
                  @click="handleReject(comment)"
                >拒绝</v-btn>
              </template>
              <v-btn
                size="small"
                variant="text"
                color="error"
                :loading="actionLoading === comment.id + '_delete'"
                @click="confirmDelete(comment)"
              >删除</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

      <!-- 分页 -->
      <TablePagination
        :total="pagination.total"
        :page="pagination.page"
        :page-size="pagination.pageSize"
        @change="pagination.page = $event; loadComments()"
      />
    </v-card>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title>确认删除</v-card-title>
        <v-card-text>确定要删除该评论吗？此操作不可撤销。</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">取消</v-btn>
          <v-btn color="error" :loading="deleteLoading" @click="doDelete">确认删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {
  adminGetComments,
  approveComment,
  rejectComment,
  deleteComment
} from '@/api/comment.js'
import TablePagination from '@/components/admin/TablePagination.vue'

export default {
  name: 'CommentView',
  components: { TablePagination },
  data: function() {
    return {
      loading: false,
      comments: [],
      // 筛选条件
      filters: { status: null },
      // 状态选项（与后端一致：0=待审核，1=已通过，2=已拒绝）
      statusOptions: [
        { label: '全部', value: null },
        { label: '待审核', value: 0 },
        { label: '已通过', value: 1 },
        { label: '已拒绝', value: 2 }
      ],
      // 分页信息
      pagination: {
        page: 1,
        pageSize: 10,
        totalPages: 1,
        total: 0
      },
      // 操作 loading 标识（格式：id_action）
      actionLoading: '',
      // 删除弹窗
      deleteDialog: false,
      deleteLoading: false,
      deleteTarget: null
    }
  },
  mounted: function() {
    this.loadComments()
  },
  methods: {
    // 加载评论列表
    loadComments: function() {
      var self = this
      self.loading = true
      var params = {
        pageNum: self.pagination.page,
        pageSize: self.pagination.pageSize
      }
      if (self.filters.status !== null && self.filters.status !== undefined) {
        params.status = self.filters.status
      }

      adminGetComments(params)
        .then(function(data) {
          self.comments = data.list || data.records || []
          self.pagination.total = data.total || 0
          self.pagination.totalPages = data.totalPages || Math.ceil((data.total || 0) / self.pagination.pageSize) || 1
        })
        .catch(function(err) {
          console.error('加载评论失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    // 审核通过
    handleApprove: function(comment) {
      var self = this
      self.actionLoading = comment.id + '_approve'
      approveComment(comment.id)
        .then(function() {
          self.$toast.success('审核通过')
          self.loadComments()
        })
        .catch(function(err) { console.error('审核通过失败:', err) })
        .finally(function() { self.actionLoading = '' })
    },
    // 拒绝评论
    handleReject: function(comment) {
      var self = this
      self.actionLoading = comment.id + '_reject'
      rejectComment(comment.id)
        .then(function() {
          self.$toast.success('已拒绝')
          self.loadComments()
        })
        .catch(function(err) { console.error('拒绝评论失败:', err) })
        .finally(function() { self.actionLoading = '' })
    },
    // 弹出删除确认框
    confirmDelete: function(comment) {
      this.deleteTarget = comment
      this.deleteDialog = true
    },
    // 执行删除
    doDelete: function() {
      var self = this
      if (!self.deleteTarget) return
      self.deleteLoading = true
      deleteComment(self.deleteTarget.id)
        .then(function() {
          self.deleteDialog = false
          self.deleteTarget = null
          self.$toast.success('评论已删除')
          self.loadComments()
        })
        .catch(function(err) {
          console.error('删除评论失败:', err)
        })
        .finally(function() {
          self.deleteLoading = false
        })
    },
    // 状态对应颜色（0=待审核，1=已通过，2=已拒绝）
    statusColor: function(status) {
      var map = { 0: 'warning', 1: 'success', 2: 'error' }
      return map[status] !== undefined ? map[status] : 'default'
    },
    // 状态对应文字
    statusLabel: function(status) {
      var map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
      return map[status] !== undefined ? map[status] : String(status)
    },
    // 格式化日期
    formatDate: function(dateStr) {
      if (!dateStr) return '-'
      var d = new Date(dateStr)
      return d.getFullYear() + '-' +
        String(d.getMonth() + 1).padStart(2, '0') + '-' +
        String(d.getDate()).padStart(2, '0') + ' ' +
        String(d.getHours()).padStart(2, '0') + ':' +
        String(d.getMinutes()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
/* 评论内容列限制最大宽度，超长截断 */
.comment-content {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>


