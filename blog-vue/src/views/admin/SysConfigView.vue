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

            <!-- 动漫主题首页开关 -->
            <div class="d-flex align-center justify-space-between" style="padding: 12px 16px; background: #f8f9fa; border-radius: 10px;">
              <div>
                <div style="font-size: 14px; font-weight: 500; color: #202124;">动漫主题首页</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">开启后首页切换为暗色动漫风格，背景图从画廊轮播</div>
              </div>
              <v-switch
                v-model="form.anime_theme_enabled"
                color="deep-purple"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <!-- 鼠标点击特效 -->
            <div class="d-flex align-center justify-space-between" style="padding: 12px 16px; background: #f8f9fa; border-radius: 10px;">
              <div>
                <div style="font-size: 14px; font-weight: 500; color: #202124;">鼠标点击特效</div>
                <div style="font-size: 12px; color: #80868b; margin-top: 2px;">点击页面时飘出 Emoji 粒子动画</div>
              </div>
              <v-switch
                v-model="form.click_effect_enabled"
                color="pink"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <!-- 粒子飘落特效 -->
            <div style="padding: 12px 16px; background: #f8f9fa; border-radius: 10px;">
              <div class="d-flex align-center justify-space-between mb-2">
                <div>
                  <div style="font-size: 14px; font-weight: 500; color: #202124;">粒子飘落特效</div>
                  <div style="font-size: 12px; color: #80868b; margin-top: 2px;">全屏飘落动画（樱花 / 雪花 / 星星）</div>
                </div>
                <v-switch
                  v-model="form.particle_enabled"
                  color="pink"
                  hide-details
                  true-value="true"
                  false-value="false"
                />
              </div>
              <!-- 粒子类型选择 -->
              <div v-if="form.particle_enabled === 'true'" class="d-flex align-center" style="gap: 8px; margin-top: 8px;">
                <span style="font-size: 12px; color: #80868b;">类型：</span>
                <v-btn-toggle v-model="form.particle_type" density="compact" variant="outlined" color="pink">
                  <v-btn value="sakura" size="small">🌸 樱花</v-btn>
                  <v-btn value="snow" size="small">❄️ 雪花</v-btn>
                  <v-btn value="star" size="small">⭐ 星星</v-btn>
                </v-btn-toggle>
              </div>
            </div>
          </div>
        </div>
      </v-card>

      <!-- 画廊管理 -->
      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-1" style="gap: 8px;">
            <v-icon color="deep-purple" size="20">mdi-image-multiple-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">首页背景画廊</span>
          </div>
          <p style="font-size: 12px; color: #80868b; margin: 0 0 16px;">上传图片作为动漫主题首页的背景轮播图，建议尺寸 1920×1080</p>

          <!-- 图片网格 -->
          <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 12px; margin-bottom: 16px;">
            <!-- 已有图片 -->
            <div
              v-for="(img, idx) in galleryImages"
              :key="idx"
              style="position: relative; border-radius: 8px; overflow: hidden; aspect-ratio: 16/9; background: #f1f3f4;"
            >
              <img :src="img" style="width: 100%; height: 100%; object-fit: cover;" />
              <button
                @click="removeGalleryImage(idx)"
                style="
                  position: absolute; top: 4px; right: 4px;
                  width: 22px; height: 22px;
                  background: rgba(0,0,0,0.55);
                  border: none; border-radius: 50%;
                  cursor: pointer;
                  display: flex; align-items: center; justify-content: center;
                  color: white;
                "
              >
                <v-icon size="13" color="white">mdi-close</v-icon>
              </button>
            </div>

            <!-- 上传按钮 -->
            <label
              style="
                aspect-ratio: 16/9;
                border: 2px dashed #e8eaed;
                border-radius: 8px;
                display: flex; flex-direction: column; align-items: center; justify-content: center;
                cursor: pointer;
                transition: all 0.2s;
                background: #fafafa;
                gap: 4px;
              "
              class="upload-zone"
            >
              <input type="file" accept="image/*" multiple style="display: none;" @change="handleGalleryUpload" :disabled="uploading" />
              <v-icon :color="uploading ? 'grey' : 'primary'" size="22">{{ uploading ? 'mdi-loading' : 'mdi-plus' }}</v-icon>
              <span style="font-size: 11px; color: #9aa0a6;">{{ uploading ? '上传中...' : '添加图片' }}</span>
            </label>
          </div>

          <p style="font-size: 11px; color: #bdc1c6; margin: 0;">
            共 {{ galleryImages.length }} 张图片。未上传图片时将使用默认背景图。
          </p>
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
    </template>
  </div>
</template>

<script>
import { getSysConfig, updateSysConfig } from '../../api/sysConfig.js'
import { uploadImage } from '../../api/upload.js'

export default {
  name: 'SysConfigView',
  data: function() {
    return {
      loading: false,
      saving: false,
      uploading: false,
      galleryImages: [],
      form: {
        blog_name: '',
        blog_author: '',
        blog_description: '',
        login_max_fail_count: '5',
        login_lock_duration: '10',
        live2d_enabled: 'true',
        comment_audit_enabled: 'true',
        anime_theme_enabled: 'false',
        gallery_images: '[]',
        click_effect_enabled: 'true',
        particle_enabled: 'true',
        particle_type: 'sakura'
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
            // 解析画廊图片
            try {
              var imgs = JSON.parse(self.form.gallery_images || '[]')
              self.galleryImages = Array.isArray(imgs) ? imgs : []
            } catch (e) {
              self.galleryImages = []
            }
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
      // 将画廊图片同步到 form
      self.form.gallery_images = JSON.stringify(self.galleryImages)
      self.saving = true
      updateSysConfig(self.form)
        .then(function() {
          self.$toast.success('设置已保存')
        })
        .catch(function(err) {
          console.error('保存系统配置失败:', err)
        })
        .finally(function() {
          self.saving = false
        })
    },
    handleGalleryUpload: function(event) {
      var self = this
      var files = Array.from(event.target.files || [])
      if (!files.length) return
      self.uploading = true
      // 逐个上传
      var promises = files.map(function(file) {
        return uploadImage(file).then(function(url) {
          self.galleryImages.push(url)
        })
      })
      Promise.all(promises)
        .catch(function(err) {
          console.error('上传图片失败:', err)
        })
        .finally(function() {
          self.uploading = false
          // 清空 input，允许重复选同一文件
          event.target.value = ''
        })
    },
    removeGalleryImage: function(idx) {
      this.galleryImages.splice(idx, 1)
    }
  }
}
</script>

<style scoped>
.upload-zone:hover {
  border-color: #1a73e8 !important;
  background: #f0f7ff !important;
}
</style>
