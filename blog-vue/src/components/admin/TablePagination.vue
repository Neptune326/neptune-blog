<template>
  <!-- 后台列表通用分页栏：右对齐，显示总数、上下页、页码 -->
  <div class="d-flex align-center justify-end pa-3" style="gap: 8px; border-top: 1px solid #f1f3f4; flex-wrap: wrap;">
    <span style="font-size: 13px; color: #80868b; margin-right: 8px;">
      共 {{ total }} 条
    </span>

    <!-- 上一页 -->
    <v-btn
      icon
      variant="text"
      size="small"
      :disabled="page <= 1"
      @click="$emit('change', page - 1)"
    >
      <v-icon size="18">mdi-chevron-left</v-icon>
    </v-btn>

    <!-- 页码按钮 -->
    <template v-for="p in pageList" :key="p">
      <span v-if="p === '...'" style="color: #9aa0a6; padding: 0 4px;">…</span>
      <v-btn
        v-else
        :variant="p === page ? 'flat' : 'text'"
        :color="p === page ? 'primary' : 'default'"
        size="small"
        style="min-width: 32px; height: 32px;"
        @click="$emit('change', p)"
      >{{ p }}</v-btn>
    </template>

    <!-- 下一页 -->
    <v-btn
      icon
      variant="text"
      size="small"
      :disabled="page >= totalPages"
      @click="$emit('change', page + 1)"
    >
      <v-icon size="18">mdi-chevron-right</v-icon>
    </v-btn>

    <span style="font-size: 13px; color: #80868b; margin-left: 4px;">
      {{ page }} / {{ totalPages }} 页
    </span>
  </div>
</template>

<script>
export default {
  name: 'TablePagination',
  props: {
    total:      { type: Number, default: 0 },
    page:       { type: Number, default: 1 },
    pageSize:   { type: Number, default: 10 }
  },
  emits: ['change'],
  computed: {
    totalPages: function() {
      return Math.max(1, Math.ceil(this.total / this.pageSize))
    },
    pageList: function() {
      var total = this.totalPages
      var cur = this.page
      if (total <= 7) {
        return Array.from({ length: total }, function(_, i) { return i + 1 })
      }
      var pages = []
      pages.push(1)
      if (cur > 3) pages.push('...')
      for (var i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) {
        pages.push(i)
      }
      if (cur < total - 2) pages.push('...')
      pages.push(total)
      return pages
    }
  }
}
</script>
