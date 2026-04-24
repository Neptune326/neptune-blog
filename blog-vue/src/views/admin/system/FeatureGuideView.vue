<template>
  <div class="feature-guide">
    <div class="mb-6">
      <h1 class="text-h5 font-weight-bold mb-1">前台集成功能说明</h1>
      <p class="text-body-2 text-medium-emphasis mb-0">
        以下能力已在博客前台接入；大部分在 <strong>系统设置</strong> 中配置，保存后访客刷新前台即可生效。
      </p>
    </div>

    <v-expansion-panels variant="accordion" class="mb-4">
      <v-expansion-panel>
        <v-expansion-panel-title class="font-weight-medium">
          <v-icon start color="primary" size="22">mdi-music-circle-outline</v-icon>
          音乐播放器
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <p class="mb-2"><strong>是否已实现：</strong>已实现。前台右下角出现悬浮播放器（有歌单时显示）。</p>
          <p class="mb-2"><strong>如何配置：</strong>进入 <router-link to="/admin/sys-config">系统设置</router-link>，在「背景音乐」中填写 JSON 播放列表并保存。</p>
          <p class="mb-2"><strong>数据格式：</strong><code>music_playlist</code> 为 JSON 数组，每项包含：</p>
          <ul class="ml-4 mb-2">
            <li><code>name</code>：曲名（必填）</li>
            <li><code>artist</code>：艺术家（可选）</li>
            <li><code>url</code>：音频直链地址（必填，需浏览器可直接访问的 mp3 等）</li>
          </ul>
          <v-sheet color="grey-lighten-4" class="pa-3 rounded text-body-2 font-mono text-break">
            [{"name":"示例曲","artist":"示例","url":"https://example.com/music.mp3"}]
          </v-sheet>
          <p class="text-caption text-medium-emphasis mt-2 mb-0">
            若配置为空数组 <code>[]</code>，播放器不会显示。注意跨域与 HTTPS 混源时浏览器可能拦截音频。
          </p>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-title class="font-weight-medium">
          <v-icon start color="pink" size="22">mdi-weather-partly-snowy-rainy</v-icon>
          粒子飘落 / 点击特效 / 鼠标轨迹
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <p class="mb-2">在 <strong>系统设置 → 功能开关</strong> 中可分别开启：</p>
          <ul class="ml-4 mb-2">
            <li><strong>鼠标点击特效：</strong>无 / Emoji 粒子 / 彩色粒子</li>
            <li><strong>鼠标轨迹特效：</strong>移动时彩色轨迹</li>
            <li><strong>粒子飘落：</strong>樱花 / 雪花 / 星星，并可调整数量（粒子数量）</li>
          </ul>
          <p class="mb-0 text-body-2">
            若访客系统开启「减少动态效果」，前台会自动关闭粒子、礼花、轨迹、点击特效等重动画，仅保留音乐与图片灯箱等。
          </p>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-title class="font-weight-medium">
          <v-icon start color="deep-purple" size="22">mdi-image-multiple-outline</v-icon>
          动漫主题首页与背景画廊
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <p class="mb-2">开启「动漫主题首页」后，访问 <code>/</code> 落地页为暗色动漫风格；背景轮播图在 <strong>首页背景画廊</strong> 中上传。</p>
          <p class="mb-0">未上传图片时使用默认背景。建议横图约 1920×1080。</p>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-title class="font-weight-medium">
          <v-icon start color="amber-darken-2" size="22">mdi-theme-light-dark</v-icon>
          访客端深色模式与阅读体验
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <p class="mb-2">在文章列表、详情等带完整顶栏的页面，访客可通过 <strong>深色 / 浅色</strong> 切换主题；偏好会保存在浏览器本地。</p>
          <p class="mb-0">文章页支持目录、字号、图片灯箱、复制内容时追加版权说明等（随页面功能展示）。</p>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-title class="font-weight-medium">
          <v-icon start color="success" size="22">mdi-party-popper</v-icon>
          其他前台小功能
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <ul class="ml-4 mb-0">
            <li><strong>礼花：</strong>特定日期自动短暂全屏礼花（元旦、情人节等）。</li>
            <li><strong>连续访问：</strong>访客连续多日访问会在达成 7 / 30 天时出现轻提示；侧栏可显示连续天数（有侧栏的页面）。</li>
            <li><strong>图片灯箱：</strong>正文中图片可点击放大浏览。</li>
          </ul>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <v-alert type="info" variant="tonal" density="comfortable" rounded="lg" class="text-body-2">
      修改 <code>music_playlist</code>、特效开关等后，若前台未变化，请强制刷新（Ctrl+F5）或清除缓存后再试。
    </v-alert>
  </div>
</template>

<script>
export default {
  name: 'FeatureGuideView'
}
</script>

<style scoped>
.feature-guide :deep(.v-expansion-panel-title) {
  min-height: 52px;
}
.text-break {
  word-break: break-all;
  white-space: pre-wrap;
}
</style>
