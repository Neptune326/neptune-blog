<template>
  <div class="d-flex justify-center mt-6 mb-2">
    <v-pagination
      v-model="currentPage"
      :length="pages"
      :total-visible="7"
      color="primary"
      rounded="lg"
      @update:model-value="onPageChange"
    />
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    total: { type: Number, default: 0 },
    pages: { type: Number, default: 1 },
    pageNum: { type: Number, default: 1 }
  },
  emits: ['page-change'],
  data: function() {
    return { currentPage: this.pageNum }
  },
  watch: {
    pageNum: function(val) { this.currentPage = val }
  },
  methods: {
    onPageChange: function(page) {
      this.$emit('page-change', page)
    }
  }
}
</script>
