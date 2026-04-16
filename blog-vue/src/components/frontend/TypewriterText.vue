<template>
  <!-- 打字机效果文字组件 -->
  <span>{{ displayText }}<span class="cursor" :class="{ blink: !typing }">|</span></span>
</template>

<script>
export default {
  name: 'TypewriterText',
  props: {
    // 要打字的文字数组，循环播放
    texts: { type: Array, default: function() { return ['Hello World'] } },
    // 打字速度（ms/字）
    typeSpeed: { type: Number, default: 100 },
    // 删除速度（ms/字）
    deleteSpeed: { type: Number, default: 50 },
    // 每段文字停留时间（ms）
    pauseTime: { type: Number, default: 2000 }
  },
  data: function() {
    return {
      displayText: '',
      typing: true,
      currentIndex: 0,
      timer: null
    }
  },
  mounted: function() {
    this.startTyping()
  },
  beforeUnmount: function() {
    if (this.timer) clearTimeout(this.timer)
  },
  methods: {
    startTyping: function() {
      var self = this
      var currentText = self.texts[self.currentIndex]

      // 打字阶段
      function typeChar(i) {
        if (i <= currentText.length) {
          self.displayText = currentText.substring(0, i)
          self.typing = true
          self.timer = setTimeout(function() { typeChar(i + 1) }, self.typeSpeed)
        } else {
          // 打完，停留一段时间后开始删除
          self.typing = false
          self.timer = setTimeout(deleteChar, self.pauseTime)
        }
      }

      // 删除阶段
      function deleteChar() {
        var len = self.displayText.length
        if (len > 0) {
          self.displayText = self.displayText.substring(0, len - 1)
          self.typing = true
          self.timer = setTimeout(deleteChar, self.deleteSpeed)
        } else {
          // 删完，切换到下一段文字
          self.currentIndex = (self.currentIndex + 1) % self.texts.length
          self.timer = setTimeout(self.startTyping, 300)
        }
      }

      typeChar(0)
    }
  }
}
</script>

<style scoped>
.cursor {
  display: inline-block;
  color: #1a73e8;
  font-weight: 300;
  margin-left: 1px;
}
.cursor.blink {
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>
