<template>
  <div>
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">关于我</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">编辑前台「关于我」页面内容（支持 Markdown）</p>
    </div>

    <div v-if="loading" class="d-flex justify-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else>
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 16px;">
        <div class="pa-5">
          <MdEditor v-model="content" :theme="'light'" style="height: 500px;" />
        </div>
      </v-card>

      <div class="d-flex justify-end" style="gap: 12px;">
        <v-btn
          color="primary"
          :loading="saving"
          prepend-icon="mdi-content-save-outline"
          @click="save"
        >
          保存
        </v-btn>
      </div>

      
    </template>
  </div>
</template>

<script>
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { getSysConfig, updateSysConfig } from '../../api/sysConfig.js'

export default {
  name: 'AboutEditView',
  components: { MdEditor },
  data: function() {
    return {
      loading: false,
      saving: false,
      
      content: ''
    }
  },
  mounted: function() {
    this.loadContent()
  },
  methods: {
    loadContent: function() {
      var self = this
      self.loading = true
      getSysConfig()
        .then(function(data) {
          self.content = (data && data.about_content) ? data.about_content : '# 关于我\n\n'
        })
        .catch(function() {})
        .finally(function() { self.loading = false })
    },
    save: function() {
      var self = this
      self.saving = true
      updateSysConfig({ about_content: self.content })
        .then(function() { self.$toast.success('保存成功') })
        .catch(function(err) { console.error('保存失败:', err) })
        .finally(function() { self.saving = false })
    }
  }
}
</script>


