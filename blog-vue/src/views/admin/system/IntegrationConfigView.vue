<template>
  <div>
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">集成设置</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">管理第三方服务与站点集成参数。</p>
    </div>

    <div v-if="loading" class="d-flex justify-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else>
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="primary" size="20">mdi-web</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">SEO 与站点地址</span>
          </div>
          <v-text-field
            v-model="form.site_base_url"
            label="站点基础地址"
            variant="outlined"
            density="comfortable"
            prepend-inner-icon="mdi-link-variant"
            hint="用于 sitemap/rss/robots 输出地址，如 https://blog.example.com"
            persistent-hint
          />
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-1" style="gap: 8px;">
            <v-icon color="indigo" size="20">mdi-music-note</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">Meting 音乐集成</span>
          </div>
          <v-text-field
            v-model="form.meting_api_base"
            class="mb-3"
            label="Meting API 根地址"
            variant="outlined"
            density="comfortable"
            prepend-inner-icon="mdi-api"
          />
          <v-textarea
            v-model="form.music_playlist"
            label="音乐播放列表（JSON）"
            variant="outlined"
            density="comfortable"
            rows="6"
          />
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="deep-purple" size="20">mdi-robot-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">AI 写作集成</span>
          </div>

          <div class="d-flex align-center justify-space-between mb-4 setting-row">
            <div>
              <div class="setting-title">启用 AI 写作</div>
              <div class="setting-desc">开启后后台编辑页可调用 AI 助手。</div>
            </div>
            <v-switch
              v-model="form.ai_enabled"
              color="primary"
              hide-details
              true-value="true"
              false-value="false"
            />
          </div>

          <v-row dense>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.ai_base_url" label="Base URL" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.ai_model" label="模型名称" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12">
              <v-text-field v-model="form.ai_api_key" type="password" label="API Key" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.ai_temperature" type="number" label="温度" min="0" max="2" step="0.1" variant="outlined" density="comfortable" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field v-model="form.ai_max_tokens" type="number" label="最大输出 Token" min="128" max="8000" variant="outlined" density="comfortable" />
            </v-col>
          </v-row>
        </div>
      </v-card>

      <div class="d-flex justify-end" style="gap: 12px;">
        <v-btn variant="outlined" prepend-icon="mdi-refresh" @click="loadConfig" :disabled="saving">重置</v-btn>
        <v-btn color="primary" variant="tonal" :loading="saving" prepend-icon="mdi-content-save-outline" @click="saveConfig">保存设置</v-btn>
      </div>
    </template>
  </div>
</template>

<script>
import { getSysConfig, updateSysConfig } from '@/api/sysConfig.js'

export default {
  name: 'IntegrationConfigView',
  data: function() {
    return {
      loading: false,
      saving: false,
      form: {
        site_base_url: 'http://localhost:8080',
        meting_api_base: '',
        music_playlist: '[]',
        ai_enabled: 'false',
        ai_base_url: 'https://api.openai.com',
        ai_model: '',
        ai_api_key: '',
        ai_temperature: '0.7',
        ai_max_tokens: '1200'
      }
    }
  },
  mounted: function() {
    this.loadConfig()
  },
  methods: {
    loadConfig: function() {
      var self = this
      self.loading = true
      getSysConfig().then(function(data) {
        if (!data) return
        Object.keys(self.form).forEach(function(key) {
          if (data[key] !== undefined) self.form[key] = data[key]
        })
      }).finally(function() {
        self.loading = false
      })
    },
    saveConfig: function() {
      var self = this
      if (self.form.site_base_url && !/^https?:\/\//i.test(self.form.site_base_url)) {
        self.$toast.error('站点基础地址必须以 http:// 或 https:// 开头')
        return
      }
      self.saving = true
      updateSysConfig(self.form).then(function() {
        self.$toast.success('集成设置已保存')
        self.loadConfig()
      }).finally(function() {
        self.saving = false
      })
    }
  }
}
</script>

<style scoped>
.setting-row {
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 10px;
}
.setting-title {
  font-size: 14px;
  font-weight: 500;
  color: #202124;
}
.setting-desc {
  font-size: 12px;
  color: #80868b;
  margin-top: 2px;
}
</style>
