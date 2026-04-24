<template>
  <div
    v-if="goodText && badText"
    class="dev-fortune"
    style="
      border-radius: 12px;
      padding: 12px 16px;
      margin-bottom: 16px;
      border: 1px solid var(--border-color);
      background: var(--bg-card);
    "
  >
    <div style="font-size: 11px; color: var(--text-muted); margin-bottom: 8px; font-weight: 600; letter-spacing: 0.5px;">开发者今日小签</div>
    <div style="font-size: 13px; color: var(--text-primary); line-height: 1.55; margin-bottom: 6px;">
      <span style="color: #0f9d58; font-weight: 600;">宜</span> · {{ goodText }}
    </div>
    <div style="font-size: 13px; color: var(--text-primary); line-height: 1.55;">
      <span style="color: #d93025; font-weight: 600;">忌</span> · {{ badText }}
    </div>
  </div>
</template>

<script>
var GOODS = [
  '写可读的变量名',
  '先跑通再优化',
  '给函数写一句注释',
  '提交前看一眼 diff',
  '喝杯热水再继续',
  '把错误信息读完整',
  '把大需求拆成小块',
  '先写用例再写实现',
  '顺手关掉未用的 import',
  '读文档而不是猜',
  '给边界条件多测一条',
  '保存前再检查一遍路径'
]

var BADS = [
  '周五下午大重构',
  '在深夜合并主干',
  '以为「马上就好」',
  '复制粘贴不命名',
  '在群里吵架构',
  '没备份就动生产',
  '「先上线再修」成常态',
  '盲信一行命令',
  '忽视 linter 的黄色波浪',
  '需求口头传达不走文档',
  '同时开十个分支',
  '在焦虑时改发布脚本'
]

function daySeed() {
  var d = new Date()
  return d.getFullYear() * 10000 + (d.getMonth() + 1) * 100 + d.getDate()
}

function nextRand(seed) {
  return (seed * 1103515245 + 12345) % 2147483647
}

function pickAt(seed, arr) {
  if (!arr || !arr.length) return ''
  var x = nextRand(seed)
  var idx = Math.abs(x) % arr.length
  return arr[idx]
}

export default {
  name: 'DevFortune',
  data: function() {
    return {
      goodText: '',
      badText: ''
    }
  },
  mounted: function() {
    this.refresh()
  },
  methods: {
    refresh: function() {
      var s0 = daySeed()
      this.goodText = pickAt(s0, GOODS)
      this.badText = pickAt(nextRand(s0), BADS)
    }
  }
}
</script>
