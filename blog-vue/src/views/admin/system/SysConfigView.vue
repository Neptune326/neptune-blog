<template>
  <div>
    <div class="mb-6">
      <h1 style="font-size: 20px; font-weight: 700; color: #202124; margin: 0 0 4px;">系统设置</h1>
      <p style="font-size: 13px; color: #80868b; margin: 0;">管理博客系统的全局配置项。</p>
    </div>

    <div v-if="loading" class="d-flex justify-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else>
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
                hint="锁定后需等待该时长才能重新登录"
                persistent-hint
              />
            </v-col>
          </v-row>
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="success" size="20">mdi-toggle-switch-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">功能开关</span>
          </div>

          <div class="d-flex flex-column" style="gap: 16px;">
            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">评论审核</div>
                <div class="setting-desc">开启后评论需要管理员审核才能显示。</div>
              </div>
              <v-switch
                v-model="form.comment_audit_enabled"
                color="primary"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">动漫主题首页</div>
                <div class="setting-desc">开启后首页切换为暗色动漫风格，并使用画廊背景轮播。</div>
              </div>
              <v-switch
                v-model="form.anime_theme_enabled"
                color="deep-purple"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="setting-row">
              <div class="setting-title" style="margin-bottom: 8px;">鼠标点击特效</div>
              <v-btn-toggle v-model="form.click_effect_enabled" density="compact" variant="outlined" color="pink">
                <v-btn value="none" size="small">关闭</v-btn>
                <v-btn value="true" size="small">Emoji 粒子</v-btn>
                <v-btn value="color" size="small">彩色粒子</v-btn>
              </v-btn-toggle>
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">鼠标轨迹特效</div>
                <div class="setting-desc">鼠标移动时显示轻量粒子轨迹。</div>
              </div>
              <v-switch
                v-model="form.mouse_trail_enabled"
                color="pink"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="setting-row">
              <div class="d-flex align-center justify-space-between mb-2">
                <div>
                  <div class="setting-title">粒子飘落特效</div>
                  <div class="setting-desc">前台全屏飘落动画，可选樱花、雪花或星星。</div>
                </div>
                <v-switch
                  v-model="form.particle_enabled"
                  color="pink"
                  hide-details
                  true-value="true"
                  false-value="false"
                />
              </div>
              <div v-if="form.particle_enabled === 'true'" class="d-flex flex-wrap align-center" style="gap: 8px; margin-top: 8px;">
                <span class="setting-desc" style="margin-top: 0;">类型：</span>
                <v-btn-toggle v-model="form.particle_type" density="compact" variant="outlined" color="pink">
                  <v-btn value="sakura" size="small">樱花</v-btn>
                  <v-btn value="snow" size="small">雪花</v-btn>
                  <v-btn value="star" size="small">星星</v-btn>
                </v-btn-toggle>
                <v-text-field
                  v-model="form.particle_count"
                  type="number"
                  min="5"
                  max="80"
                  density="compact"
                  variant="outlined"
                  hide-details
                  style="max-width: 100px;"
                  label="数量"
                />
              </div>
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">Konami 键盘彩蛋</div>
                <div class="setting-desc">前台依次输入上上下下左右左右 BA 时触发提示与短礼花。</div>
              </div>
              <v-switch
                v-model="form.easter_konami_enabled"
                color="teal"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">侧栏开发者今日小签</div>
                <div class="setting-desc">在含侧栏的前台页面展示今日宜忌短签。</div>
              </div>
              <v-switch
                v-model="form.dev_fortune_enabled"
                color="teal"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>
          </div>
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="pink" size="20">mdi-palette-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">前台主题装扮</span>
          </div>

          <div class="d-flex flex-column" style="gap: 16px;">
            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">沉浸主题</div>
                <div class="setting-desc">前台页面统一使用主题变量和氛围层。</div>
              </div>
              <v-switch
                v-model="form.frontend_theme_enabled"
                color="pink"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">访客主题切换按钮</div>
                <div class="setting-desc">允许访客在前台自行切换主题并保存在本地。</div>
              </div>
              <v-switch
                v-model="form.frontend_theme_switcher_enabled"
                color="pink"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="d-flex align-center justify-space-between setting-row">
              <div>
                <div class="setting-title">主题氛围层</div>
                <div class="setting-desc">显示轻量背景纹理和主题装饰，移动端会自动降级。</div>
              </div>
              <v-switch
                v-model="form.frontend_ambient_enabled"
                color="pink"
                hide-details
                true-value="true"
                false-value="false"
              />
            </div>

            <div class="setting-row">
              <div class="setting-title" style="margin-bottom: 8px;">默认主题</div>
              <v-btn-toggle v-model="form.frontend_theme_default" density="compact" variant="outlined" color="pink">
                <v-btn value="classic" size="small">经典</v-btn>
                <v-btn value="sakura" size="small">樱花</v-btn>
                <v-btn value="neon" size="small">霓虹</v-btn>
                <v-btn value="starry" size="small">星夜</v-btn>
              </v-btn-toggle>
            </div>
          </div>
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-1" style="gap: 8px;">
            <v-icon color="primary" size="20">mdi-music-note</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">背景音乐</span>
          </div>
          <p style="font-size: 12px; color: #80868b; margin: 0 0 12px;">
            支持直链 <code>url</code>，或通过 <strong>server + id</strong> 交给 Meting 兼容接口解析。为空 <code>[]</code> 时前台不展示播放器。
          </p>
          <v-text-field
            v-model="form.meting_api_base"
            class="mb-3"
            label="Meting API 根地址（可选）"
            variant="outlined"
            density="comfortable"
            prepend-inner-icon="mdi-api"
            placeholder="https://api.injahow.cn/meting/"
            hint="留空则使用默认值；若公共接口不可用可自建后填写"
            persistent-hint
          />
          <v-textarea
            v-model="form.music_playlist"
            label="音乐播放列表（JSON）"
            variant="outlined"
            density="comfortable"
            rows="6"
            placeholder='[{"name":"曲名","url":"https://..."}] 或 [{"server":"netease","id":"歌曲id","name":"可选"}]'
            class="font-mono text-body-2"
            hint="必须是合法 JSON 数组；每项至少包含 url，或同时包含 server 和 id"
            persistent-hint
          />
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-4" style="gap: 8px;">
            <v-icon color="primary" size="20">mdi-robot-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">AI 写作助手</span>
          </div>

          <div class="d-flex align-center justify-space-between mb-4 setting-row">
            <div>
              <div class="setting-title">启用 AI 写作</div>
              <div class="setting-desc">开启后后台文章编辑页可调用 OpenAI 兼容接口。</div>
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
              <v-text-field
                v-model="form.ai_base_url"
                label="Base URL"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-link-variant"
                placeholder="https://api.openai.com"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.ai_model"
                label="模型名称"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-cube-outline"
                placeholder="填写服务商提供的模型名称"
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="form.ai_api_key"
                label="API Key"
                variant="outlined"
                density="comfortable"
                type="password"
                prepend-inner-icon="mdi-key-outline"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.ai_temperature"
                label="温度"
                variant="outlined"
                density="comfortable"
                type="number"
                min="0"
                max="2"
                step="0.1"
                prepend-inner-icon="mdi-thermometer"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.ai_max_tokens"
                label="最大输出 Token"
                variant="outlined"
                density="comfortable"
                type="number"
                min="128"
                max="8000"
                prepend-inner-icon="mdi-counter"
              />
            </v-col>
          </v-row>
        </div>
      </v-card>

      <v-card elevation="0" rounded="xl" style="border: 1px solid #e8eaed; margin-bottom: 20px;">
        <div class="pa-5">
          <div class="d-flex align-center mb-1" style="gap: 8px;">
            <v-icon color="deep-purple" size="20">mdi-image-multiple-outline</v-icon>
            <span style="font-size: 15px; font-weight: 600; color: #202124;">首页背景画廊</span>
          </div>
          <p style="font-size: 12px; color: #80868b; margin: 0 0 16px;">上传图片作为动漫主题首页的背景轮播图，建议尺寸 1920x1080。</p>

          <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 12px; margin-bottom: 16px;">
            <div
              v-for="(img, idx) in galleryImages"
              :key="idx"
              style="position: relative; border-radius: 8px; overflow: hidden; aspect-ratio: 16/9; background: #f1f3f4;"
            >
              <img :src="img" style="width: 100%; height: 100%; object-fit: cover;" />
              <button
                type="button"
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

            <label class="upload-zone">
              <input type="file" accept="image/*" multiple style="display: none;" @change="handleGalleryUpload" :disabled="uploading" />
              <v-icon :color="uploading ? 'grey' : 'primary'" size="22">{{ uploading ? 'mdi-loading' : 'mdi-plus' }}</v-icon>
              <span style="font-size: 11px; color: #9aa0a6;">{{ uploading ? '上传中...' : '添加图片' }}</span>
            </label>
          </div>

          <p style="font-size: 11px; color: #bdc1c6; margin: 0;">
            共 {{ galleryImages.length }} 张图片。未上传时将使用默认背景。
          </p>
        </div>
      </v-card>

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
import { getSysConfig, updateSysConfig } from '@/api/sysConfig.js'
import { uploadImage } from '@/api/upload.js'

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
        comment_audit_enabled: 'true',
        anime_theme_enabled: 'false',
        gallery_images: '[]',
        click_effect_enabled: 'true',
        mouse_trail_enabled: 'false',
        particle_enabled: 'true',
        particle_type: 'sakura',
        particle_count: '25',
        easter_konami_enabled: 'true',
        dev_fortune_enabled: 'true',
        frontend_theme_enabled: 'true',
        frontend_theme_switcher_enabled: 'true',
        frontend_theme_default: 'sakura',
        frontend_ambient_enabled: 'true',
        music_playlist: '[]',
        meting_api_base: '',
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
      getSysConfig()
        .then(function(data) {
          if (!data) return
          Object.keys(self.form).forEach(function(key) {
            if (data[key] !== undefined) {
              self.form[key] = data[key]
            }
          })
          try {
            var images = JSON.parse(self.form.gallery_images || '[]')
            self.galleryImages = Array.isArray(images) ? images : []
          } catch (e) {
            self.galleryImages = []
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
      self.form.gallery_images = JSON.stringify(self.galleryImages)
      if (self.form.music_playlist && self.form.music_playlist.trim()) {
        try {
          var playlist = JSON.parse(self.form.music_playlist)
          if (!Array.isArray(playlist)) {
            throw new Error('音乐播放列表必须是数组')
          }
          for (var i = 0; i < playlist.length; i++) {
            var item = playlist[i]
            if (!item || typeof item !== 'object') {
              throw new Error('播放列表中的每一项都必须是对象')
            }
            var hasUrl = item.url != null && String(item.url).trim() !== ''
            var hasServer = item.server != null && String(item.server).trim() !== '' &&
              item.id != null && String(item.id).trim() !== ''
            if (!hasUrl && !hasServer) {
              throw new Error('每项必须包含 url，或同时包含 server 和 id')
            }
          }
        } catch (e) {
          self.$toast.error(e.message || '背景音乐 JSON 不合法')
          return
        }
      }
      if (self.form.meting_api_base && String(self.form.meting_api_base).trim() !== '') {
        var base = String(self.form.meting_api_base).trim()
        if (!/^https?:\/\//i.test(base)) {
          self.$toast.error('Meting API 根地址必须以 http:// 或 https:// 开头')
          return
        }
      }
      self.saving = true
      updateSysConfig(self.form)
        .then(function() {
          self.$toast.success('设置已保存')
          self.loadConfig()
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

.upload-zone {
  aspect-ratio: 16/9;
  border: 2px dashed #e8eaed;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafafa;
  gap: 4px;
}

.upload-zone:hover {
  border-color: #1a73e8 !important;
  background: #f0f7ff !important;
}
</style>
