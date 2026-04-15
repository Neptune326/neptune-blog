<template>
  <!-- 文章卡片骨架屏 -->
  <div
    v-for="i in count"
    :key="i"
    style="
      background: white;
      border: 1px solid #e8eaed;
      border-radius: 12px;
      margin-bottom: 16px;
      overflow: hidden;
    "
  >
    <!-- 封面骨架 -->
    <div class="skeleton" style="height: 180px; width: 100%;" />

    <div style="padding: 20px;">
      <!-- 分类骨架 -->
      <div class="skeleton" style="height: 22px; width: 70px; border-radius: 11px; margin-bottom: 12px;" />
      <!-- 标题骨架 -->
      <div class="skeleton" style="height: 20px; width: 85%; border-radius: 4px; margin-bottom: 8px;" />
      <div class="skeleton" style="height: 20px; width: 60%; border-radius: 4px; margin-bottom: 14px;" />
      <!-- 摘要骨架 -->
      <div class="skeleton" style="height: 14px; width: 100%; border-radius: 4px; margin-bottom: 6px;" />
      <div class="skeleton" style="height: 14px; width: 90%; border-radius: 4px; margin-bottom: 6px;" />
      <div class="skeleton" style="height: 14px; width: 75%; border-radius: 4px; margin-bottom: 16px;" />
      <!-- 底部信息骨架 -->
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div style="display: flex; gap: 12px;">
          <div class="skeleton" style="height: 14px; width: 80px; border-radius: 4px;" />
          <div class="skeleton" style="height: 14px; width: 60px; border-radius: 4px;" />
        </div>
        <div style="display: flex; gap: 6px;">
          <div class="skeleton" style="height: 20px; width: 50px; border-radius: 10px;" />
          <div class="skeleton" style="height: 20px; width: 50px; border-radius: 10px;" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArticleSkeleton',
  props: {
    count: { type: Number, default: 3 }
  }
}
</script>

<style scoped>
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
