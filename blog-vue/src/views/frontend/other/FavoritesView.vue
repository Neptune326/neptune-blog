<template>
  <div class="container" style="max-width: 980px; margin: 24px auto; padding: 0 16px;">
    <h1 style="font-size: 28px; margin-bottom: 16px;">我的收藏</h1>
    <div v-if="loading">加载中...</div>
    <div v-else-if="!list.length" style="color: #80868b;">暂无收藏文章</div>
    <div v-else>
      <article v-for="item in list" :key="item.id" style="border: 1px solid #e8eaed; border-radius: 10px; padding: 14px; margin-bottom: 12px;">
        <router-link :to="'/article/' + item.id" style="font-size: 18px; color: #1a73e8; text-decoration: none;">
          {{ item.title }}
        </router-link>
        <p style="margin: 8px 0 0; color: #5f6368;">{{ item.summary }}</p>
      </article>
    </div>
  </div>
</template>

<script>
import { getMyFavorites } from '@/api/favorite.js'

export default {
  name: 'FavoritesView',
  data: function() {
    return { loading: false, list: [] }
  },
  mounted: function() {
    var self = this
    self.loading = true
    getMyFavorites().then(function(data) {
      self.list = data || []
    }).finally(function() {
      self.loading = false
    })
  }
}
</script>
