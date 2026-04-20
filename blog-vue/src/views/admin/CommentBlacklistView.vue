<template>
  <div>
    <div class="mb-6 d-flex align-center justify-space-between">
      <div>
        <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">评论黑名单</h1>
        <p style="font-size: 13px; color: #80868b; margin: 0;">屏蔽指定 IP 或关键词，自动拒绝违规评论</p>
      </div>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openAdd">添加规则</v-btn>
    </div>

    <!-- IP 黑名单 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
      <div class="pa-5">
        <div class="d-flex align-center mb-3" style="gap: 8px;">
          <v-icon color="error" size="18">mdi-ip-network-outline</v-icon>
          <span style="font-size: 14px; font-weight: 600; color: #202124;">IP 黑名单</span>
          <v-chip size="x-small" color="error" variant="tonal">{{ ipList.length }}</v-chip>
        </div>
        <div v-if="ipList.length === 0" style="color: #9aa0a6; font-size: 13px;">暂无屏蔽 IP</div>
        <div style="display: flex; flex-wrap: wrap; gap: 8px;">
          <v-chip
            v-for="(ip, idx) in ipList"
            :key="'ip-' + idx"
            color="error"
            variant="tonal"
            size="small"
            closable
            @click:close="removeIp(idx)"
          >{{ ip }}</v-chip>
        </div>
      </div>
    </v-card>

    <!-- 关键词黑名单 -->
    <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
      <div class="pa-5">
        <div class="d-flex align-center mb-3" style="gap: 8px;">
          <v-icon color="warning" size="18">mdi-text-box-remove-outline</v-icon>
          <span style="font-size: 14px; font-weight: 600; color: #202124;">关键词黑名单</span>
          <v-chip size="x-small" color="warning" variant="tonal">{{ keywordList.length }}</v-chip>
        </div>
        <div v-if="keywordList.length === 0" style="color: #9aa0a6; font-size: 13px;">暂无屏蔽关键词</div>
        <div style="display: flex; flex-wrap: wrap; gap: 8px;">
          <v-chip
            v-for="(kw, idx) in keywordList"
            :key="'kw-' + idx"
            color="warning"
            variant="tonal"
            size="small"
            closable
            @click:close="removeKeyword(idx)"
          >{{ kw }}</v-chip>
        </div>
      </div>
    </v-card>

    <!-- 保存按钮 -->
    <div class="d-flex justify-end">
      <v-btn color="primary" :loading="saving" prepend-icon="mdi-content-save-outline" @click="save">保存黑名单</v-btn>
    </div>

    <!-- 添加规则对话框 -->
    <v-dialog v-model="addDialog" max-width="420">
      <v-card rounded="xl">
        <div class="pa-6">
          <div style="font-size: 16px; font-weight: 600; color: #202124; margin-bottom: 16px;">添加黑名单规则</div>
          <v-btn-toggle v-model="addType" density="compact" variant="outlined" color="primary" class="mb-4">
            <v-btn value="ip" size="small">IP 地址</v-btn>
            <v-btn value="keyword" size="small">关键词</v-btn>
          </v-btn-toggle>
          <v-text-field
            v-model="addValue"
            :label="addType === 'ip' ? '输入 IP 地址（如 192.168.1.1）' : '输入屏蔽关键词'"
            variant="outlined"
            density="comfortable"
            @keyup.enter="doAdd"
          />
          <div class="d-flex justify-end mt-2" style="gap: 8px;">
            <v-btn variant="text" @click="addDialog = false">取消</v-btn>
            <v-btn color="primary" @click="doAdd">添加</v-btn>
          </div>
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { getSysConfig, updateSysConfig } from '../../api/sysConfig.js'

export default {
  name: 'CommentBlacklistView',
  data: function() {
    return {
      ipList: [],
      keywordList: [],
      saving: false,
      addDialog: false,
      addType: 'ip',
      addValue: ''
    }
  },
  mounted: function() {
    this.load()
  },
  methods: {
    load: function() {
      var self = this
      getSysConfig().then(function(data) {
        if (!data) return
        try {
          var bl = JSON.parse(data.comment_blacklist || '[]')
          self.ipList = bl.filter(function(x) { return x.type === 'ip' }).map(function(x) { return x.value })
          self.keywordList = bl.filter(function(x) { return x.type === 'keyword' }).map(function(x) { return x.value })
        } catch (e) {}
      }).catch(function() {})
    },
    openAdd: function() {
      this.addValue = ''
      this.addType = 'ip'
      this.addDialog = true
    },
    doAdd: function() {
      var val = this.addValue.trim()
      if (!val) return
      if (this.addType === 'ip') {
        if (!this.ipList.includes(val)) this.ipList.push(val)
      } else {
        if (!this.keywordList.includes(val)) this.keywordList.push(val)
      }
      this.addValue = ''
      this.addDialog = false
    },
    removeIp: function(idx) { this.ipList.splice(idx, 1) },
    removeKeyword: function(idx) { this.keywordList.splice(idx, 1) },
    save: function() {
      var self = this
      self.saving = true
      var bl = []
      self.ipList.forEach(function(v) { bl.push({ type: 'ip', value: v }) })
      self.keywordList.forEach(function(v) { bl.push({ type: 'keyword', value: v }) })
      updateSysConfig({ comment_blacklist: JSON.stringify(bl) })
        .then(function() { self.$toast.success('黑名单已保存') })
        .catch(function() {})
        .finally(function() { self.saving = false })
    }
  }
}
</script>
