<template>
  <nav style="display: flex; align-items: center; gap: 4px; font-size: 13px; color: #80868b; flex-wrap: wrap;">
    <router-link to="/" style="color: #80868b; text-decoration: none; transition: color 0.15s;" class="bc-link">首页</router-link>
    <template v-for="(item, idx) in items" :key="idx">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="#bdc1c6" style="flex-shrink: 0;">
        <path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
      </svg>
      <router-link
        v-if="item.to && idx < items.length - 1"
        :to="item.to"
        style="color: #80868b; text-decoration: none; transition: color 0.15s;"
        class="bc-link"
      >{{ item.label }}</router-link>
      <span v-else :style="idx === items.length - 1 ? 'color: #3c4043; font-weight: 500;' : 'color: #80868b;'">
        {{ item.label }}
      </span>
    </template>
  </nav>
</template>

<script>
export default {
  name: 'Breadcrumb',
  props: {
    items: { type: Array, default: function() { return [] } }
    // items: [{ label: '前端开发', to: '/category/1' }, { label: '文章标题' }]
  }
}
</script>

<style scoped>
.bc-link:hover { color: #1a73e8 !important; }
</style>
