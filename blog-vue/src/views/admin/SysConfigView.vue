<template>
  <div>
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">系统设置</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">管理博客系统的全局配置项</p>
    </div>

    <div v-if="loading" class="d-flex justify-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else>
      <!-- 博客基本信息 -->
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="primary" size="20">mdi-information-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">博客基本信息</span>
          </div>
          <v-row dense>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.blog_name"
                label="博客名称"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-pencil-circle-outline"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.blog_author"
                label="博主名称"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-account-outline"
              />
            </v-col>
            <v-col cols="12">
              <v-textarea
                v-model="form.blog_description"
                label="博客描述"
                variant="outlined"
                density="comfortable"
                rows="2"
                prepend-inner-icon="mdi-text-box-outline"
              />
            </v-col>
          </v-row>
        </div>
      </v-card>

      <!-- 安全设置 -->
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="error" size="20">mdi-shield-lock-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">安全设置</span>
          </div>
          <v-row dense>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.login_max_fail_count"
                label="登录最大失败次数"
                variant="outlined"
                density="comfortable"
                type="number"
                :min="1"
                :max="20"
                prepend-inner-icon="mdi-lock-alert-outline"
                hint="超出次数后 IP 将被临时锁定"
                persistent-hint
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.login_lock_duration"
                label="登录锁定时长（分钟）"
                variant="outlined"
                density="comfortable"
                type="number"
                :min="1"
                :max="1440"
                prepend-inner-icon="mdi-timer-lock-outline"
                hint="锁定后需等待此时长才能重新登录"
                persistent-hint
              />
            </v-col>
          </v-row>
        </div>
      </v-card>

      <!-- 功能开关 -->
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="success" size="20">mdi-toggle-switch-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">功能开关</span>
          </div>

          <div class="d-flex flex-column" style="gap: 16px;">
            <!-- Live2D 开关 -->
            <div class="d-flex align-center justify-space-between" style="padding: 12px 16px; background: #f8f9fa; border-radius: 10px;">
              <div>
                <div style="font-size: 14px; font-weight: 500; color: #202124;">Live2D 看板娘</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">在前台页面左下角显示 Live2D 动态角色</div>
              </div>
              <v-switch
                v-model="form.live2d_enabled"
                color="primary"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <!-- 评论审核开关 -->
            <div class="d-flex align-center justify-space-between" style="padding: 12px 16px; background: #f8f9fa; border-radius: 10px;">
              <div>
                <div style="font-size: 14px; font-weight: 500; color: #202124;">评论审核</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">开启后评论需要管理员审核才能显示</div>
              </div>
              <v-switch
                v-model="form.comment_audit_enabled"
                color="primary"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>
          </div>
        </div>
      </v-card>

      <!-- 保存按钮 -->
      <div class="d-flex justify-end" style="gap: 12px;">
        <v-btn variant="outlined" @click="loadConfig" :disabled="saving">重置</v-btn>
        <v-btn
          color="primary"
          :loading="saving"
          prepend-icon="mdi-content-save-outline"
          @click="saveConfig"
        >
          保存设置
        </v-btn>
      </div>

      <!-- 保存成功提示 -->
      <v-snackbar v-model="snackbar" color="success" timeout="2000" location="top" rounded="lg">
        设置已保存
      </v-snackbar>
    </template>
  </div>
</template>

<script>
import { getSysConfig, updateSysConfig } from '../../api/sysConfig.js'

export default {
  name: 'SysConfigView',
  data: function() {
    return {
      loading: false,
      saving: false,
      snackbar: false,
      form: {
        blog_name: '',
        blog_author: '',
        blog_description: '',
        login_max_fail_count: '5',
        login_lock_duration: '10',
        live2d_enabled: 'true',
        comment_audit_enabled: 'true'
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
      getSysConfig()
        .then(function(data) {
          if (data) {
            Object.keys(self.form).forEach(function(key) {
              if (data[key] !== undefined) {
                self.form[key] = data[key]
              }
            })
          }
        })
        .catch(function(err) {
          console.error('加载系统配置失败:', err)
        })
        .finally(function() {
          self.loading = false
        })
    },
    saveConfig: function() {
      var self = this
      self.saving = true
      updateSysConfig(self.form)
        .then(function() {
          self.snackbar = true
        })
        .catch(function(err) {
          console.error('保存系统配置失败:', err)
        })
        .finally(function() {
          self.saving = false
        })
    }
  }
}
</script>
