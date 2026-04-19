<template>
  <div style="position: relative; display: inline-block;">
    <!-- 触发按钮 -->
    <button
      type="button"
      @click.stop="open = !open"
      style="
        background: none; border: 1px solid #e8eaed; border-radius: 8px;
        padding: 6px 10px; cursor: pointer; font-size: 16px;
        transition: all 0.15s; color: #5f6368;
        display: flex; align-items: center; gap: 4px;
      "
      title="插入表情"
    >
      😊
      <svg width="10" height="10" viewBox="0 0 24 24" fill="#9aa0a6">
        <path d="M7 10l5 5 5-5z"/>
      </svg>
    </button>

    <!-- 表情面板 -->
    <div
      v-if="open"
      v-click-outside="closePanel"
      style="
        position: absolute; bottom: calc(100% + 8px); left: 0;
        background: white; border: 1px solid #e8eaed; border-radius: 12px;
        padding: 10px; z-index: 1000;
        box-shadow: 0 8px 24px rgba(0,0,0,0.12);
        width: 280px;
      "
      @click.stop
    >
      <!-- 分类标签 -->
      <div style="display: flex; gap: 4px; margin-bottom: 8px; flex-wrap: wrap;">
        <button
          v-for="cat in categories"
          :key="cat.name"
          @click="activeCategory = cat.name"
          :style="activeCategory === cat.name
            ? 'background: #e8f0fe; color: #1a73e8; border-color: #1a73e8;'
            : 'background: #f8f9fa; color: #5f6368; border-color: #e8eaed;'"
          style="border: 1px solid; border-radius: 6px; padding: 2px 8px; font-size: 11px; cursor: pointer; font-weight: 500; transition: all 0.15s;"
        >{{ cat.label }}</button>
      </div>

      <!-- 表情网格 -->
      <div style="display: grid; grid-template-columns: repeat(8, 1fr); gap: 2px; max-height: 180px; overflow-y: auto;">
        <button
          v-for="emoji in currentEmojis"
          :key="emoji"
          @click="select(emoji)"
          style="
            background: none; border: none; border-radius: 6px;
            padding: 4px; font-size: 20px; cursor: pointer;
            transition: background 0.1s; line-height: 1;
          "
          @mouseenter="$event.target.style.background='#f1f3f4'"
          @mouseleave="$event.target.style.background='none'"
          :title="emoji"
        >{{ emoji }}</button>
      </div>
    </div>
  </div>
</template>

<script>
var EMOJI_DATA = {
  '常用': ['😊','😂','🤣','❤️','😍','🥰','😘','😭','😅','😁','🙏','👍','🎉','🔥','✨','💯','🤔','😎','🥺','😢','😡','🤯','🤩','😴','🤗','😏','🙄','😤','🥳','🤭'],
  '表情': ['😀','😃','😄','😆','😉','😋','😛','😝','😜','🤪','🤨','🧐','🤓','😒','😞','😔','😟','😕','🙁','☹️','😣','😖','😫','😩','🥱','😤','😠','😈','👿','💀'],
  '手势': ['👋','🤚','🖐️','✋','🖖','👌','🤌','🤏','✌️','🤞','🤟','🤘','🤙','👈','👉','👆','🖕','👇','☝️','👍','👎','✊','👊','🤛','🤜','👏','🙌','🫶','🤲','🤝'],
  '动物': ['🐶','🐱','🐭','🐹','🐰','🦊','🐻','🐼','🐨','🐯','🦁','🐮','🐷','🐸','🐵','🙈','🙉','🙊','🐔','🐧','🐦','🦆','🦅','🦉','🦇','🐺','🐗','🐴','🦄','🐝'],
  '食物': ['🍎','🍊','🍋','🍇','🍓','🫐','🍈','🍒','🍑','🥭','🍍','🥥','🥝','🍅','🍆','🥑','🥦','🥬','🥒','🌶️','🫑','🧄','🧅','🥔','🍠','🥐','🥖','🍞','🥨','🧀'],
  '符号': ['❤️','🧡','💛','💚','💙','💜','🖤','🤍','🤎','💔','❣️','💕','💞','💓','💗','💖','💘','💝','💟','☮️','✝️','☯️','🕉️','✡️','🔯','🕎','☦️','⛎','♈','♉']
}

export default {
  name: 'EmojiPicker',
  emits: ['select'],
  directives: {
    'click-outside': {
      mounted: function(el, binding) {
        el._clickOutside = function(e) {
          if (!el.contains(e.target)) binding.value()
        }
        document.addEventListener('click', el._clickOutside)
      },
      unmounted: function(el) {
        document.removeEventListener('click', el._clickOutside)
      }
    }
  },
  data: function() {
    return {
      open: false,
      activeCategory: '常用',
      categories: Object.keys(EMOJI_DATA).map(function(k) { return { name: k, label: k } })
    }
  },
  computed: {
    currentEmojis: function() {
      return EMOJI_DATA[this.activeCategory] || []
    }
  },
  methods: {
    select: function(emoji) {
      this.$emit('select', emoji)
      this.open = false
    },
    closePanel: function() {
      this.open = false
    }
  }
}
</script>
