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
          <p class="mb-2"><strong>如何配置：</strong>进入 <router-link to="/admin/sys-config">系统设置</router-link>，在「背景音乐」中填写 JSON 播放列表；可选填写「Meting API 根地址」后保存。</p>
          <p class="mb-2"><strong>数据格式：</strong><code>music_playlist</code> 为 JSON 数组。每一项二选一：</p>
          <ul class="ml-4 mb-2">
            <li><strong>直链：</strong><code>name</code>（建议）、<code>artist</code>（可选）、<code>url</code>（mp3 等可直连地址，浏览器能访问即可）</li>
            <li><strong>平台解析（网易 / QQ 等）：</strong><code>server</code>、<code>id</code> 必填；<code>name</code>、<code>artist</code> 可选（解析成功后会以接口返回为准补充）</li>
          </ul>
          <p class="mb-1 text-body-2"><code>server</code> 常用取值：<code>netease</code>（网易云）、<code>tencent</code>（QQ 音乐）、<code>kugou</code> 等。歌曲 <code>id</code> 为各平台曲目的数字 ID（可从分享链接或网页参数中取得）。</p>
          <p class="mb-2 text-body-2">解析依赖 <strong>Meting 兼容 API</strong>（与开源 <a href="https://github.com/metowolf/Meting" target="_blank" rel="noopener">Meting</a> / <a href="https://github.com/injahow/meting-api" target="_blank" rel="noopener">meting-api</a> 同族）。默认使用公共实例，不稳定时可自建服务，并在系统设置中填写「Meting API 根地址」。</p>
          <p class="mb-1 text-body-2 font-weight-medium">直链示例</p>
          <v-sheet color="grey-lighten-4" class="pa-3 rounded text-body-2 font-mono text-break mb-2">
            [{"name":"示例曲","artist":"示例","url":"https://example.com/music.mp3"}]
          </v-sheet>
          <p class="mb-1 text-body-2 font-weight-medium">网易云 / QQ 音乐（server + id）示例</p>
          <v-sheet color="grey-lighten-4" class="pa-3 rounded text-body-2 font-mono text-break">
            [{"server":"netease","id":"0000000","name":"显示名"} , {"server":"tencent","id":"000000","name":"QQ曲"}]
          </v-sheet>
          <p class="text-caption text-medium-emphasis mt-2 mb-0">
            若配置为空数组 <code>[]</code>，播放器不会显示。直链须注意跨域与 HTTPS 混源。仅使用平台解析时，请确保服务器能访问所填 Meting API。
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
          <v-icon start color="teal" size="22">mdi-gamepad-outline</v-icon>
          趣味彩蛋与小签
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <p class="mb-2">在 <strong>系统设置 → 功能开关</strong> 中可分别开启：</p>
          <ul class="ml-4 mb-2">
            <li>
              <strong>Konami 键盘彩蛋（<code>easter_konami_enabled</code>）：</strong>访客在前台用键盘按顺序输入
              <strong>上、上、下、下、左、右、左、右、B、A</strong>（与经典 Konami 码一致）时，会短暂放礼花并出现提示。若系统已开启「减少动效」则只显示文字提示、不播放礼花动画。
            </li>
            <li>
              <strong>侧栏开发者今日小签（<code>dev_fortune_enabled</code>）：</strong>在带侧栏的页面（如文章列表）显示一张小卡片，内容为按日期随机的「宜 / 忌」幽默短句，无第三方接口。
            </li>
          </ul>
          <p class="mb-0 text-body-2">关闭任一项后，对应行为立即在前台不再出现（需刷新页面）。</p>
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
