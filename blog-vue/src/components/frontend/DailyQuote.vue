<template>
  <!-- 每日一言卡片 -->
  <div
    v-if="quote"
    style="
      background: linear-gradient(135deg, #e8f0fe 0%, #f3e8fd 100%);
      border-radius: 12px;
      padding: 16px 20px;
      margin-bottom: 16px;
      position: relative;
      overflow: hidden;
    "
  >
    <!-- 装饰引号 -->
    <div style="
      position: absolute; top: -8px; left: 12px;
      font-size: 60px; color: #1a73e8; opacity: 0.15;
      font-family: Georgia, serif; line-height: 1;
      pointer-events: none;
    ">"</div>

    <div style="position: relative; z-index: 1;">
      <p style="
        font-size: 14px; color: #3c4043;
        line-height: 1.7; margin: 0 0 8px;
        font-style: italic;
      ">
        {{ quote.hitokoto }}
      </p>
      <div style="font-size: 12px; color: #80868b; text-align: right;">
        —— {{ quote.from || '佚名' }}
        <span v-if="quote.from_who"> · {{ quote.from_who }}</span>
      </div>
    </div>

    <!-- 刷新按钮 -->
    <button
      @click="loadQuote"
      :disabled="loading"
      style="
        position: absolute; top: 10px; right: 12px;
        background: none; border: none; cursor: pointer;
        color: #80868b; padding: 4px;
        transition: transform 0.3s;
      "
      :style="loading ? 'transform: rotate(360deg);' : ''"
      title="换一句"
    >
      <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
        <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
      </svg>
    </button>
  </div>
</template>

<script>
export default {
  name: 'DailyQuote',
  data: function() {
    return {
      quote: null,
      loading: false
    }
  },
  mounted: function() {
    this.loadQuote()
  },
  methods: {
    loadQuote: function() {
      var self = this
      self.loading = true
      // 使用一言 API（hitokoto.cn）
      fetch('https://v1.hitokoto.cn/?c=i&c=k&encode=json')
        .then(function(res) { return res.json() })
        .then(function(data) {
          self.quote = data
        })
        .catch(function() {
          // 接口失败时使用本地备用名言
          var fallbacks = [
            { hitokoto: '代码是写给人看的，顺便让机器执行。', from: 'Harold Abelson' },
            { hitokoto: '简单是可靠性的前提。', from: 'Edsger W. Dijkstra' },
            { hitokoto: '先让它工作，再让它正确，最后让它快。', from: 'Kent Beck' },
            { hitokoto: '好的代码是最好的文档。', from: 'Steve McConnell' }
          ]
          self.quote = fallbacks[Math.floor(Math.random() * fallbacks.length)]
        })
        .finally(function() {
          self.loading = false
        })
    }
  }
}
</script>
