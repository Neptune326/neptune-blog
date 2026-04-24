<template>
  <!-- 悬浮音乐播放器：支持直链 url，或通过 Meting 解析 网易云/QQ 等（server+id） -->
  <div
    v-if="playlist.length > 0"
    class="music-player-wrap"
    :style="expanded ? 'width: 280px;' : 'width: 52px;'"
  >
    <div v-if="!expanded" class="mp-collapsed">
      <button type="button" class="mp-icon-btn" @click="expanded = true">
        <v-icon size="20" color="primary">mdi-music-note</v-icon>
      </button>
    </div>

    <div v-else class="mp-expanded">
      <div class="d-flex align-center justify-space-between mb-2">
        <span class="mp-title">🎵 音乐播放器</span>
        <button type="button" class="mp-close" @click="expanded = false">
          <v-icon size="16">mdi-chevron-down</v-icon>
        </button>
      </div>

      <div v-if="resolveError" class="mp-err text-caption mb-2">{{ resolveError }}</div>

      <div style="margin-bottom: 10px;">
        <div class="mp-line-name">{{ displayName }}</div>
        <div class="mp-line-artist">{{ displayArtist }}</div>
      </div>

      <div style="margin-bottom: 10px;">
        <div class="mp-progress-bg" @click="seek">
          <div :style="{ width: progress + '%' }" class="mp-progress-fill" />
        </div>
        <div class="d-flex justify-space-between mt-1 mp-time">
          <span>{{ formatTime(currentTime) }}</span>
          <span>{{ formatTime(duration) }}</span>
        </div>
      </div>

      <div class="d-flex align-center justify-center" style="gap: 8px;">
        <button type="button" class="mp-ctrl" @click="prev">
          <v-icon size="18">mdi-skip-previous</v-icon>
        </button>
        <button
          type="button"
          class="mp-play"
          :disabled="loadingResolve"
          @click="togglePlay"
        >
          <v-icon v-if="loadingResolve" size="18" class="mp-spin">mdi-loading</v-icon>
          <v-icon v-else size="18" color="white">{{ playing ? 'mdi-pause' : 'mdi-play' }}</v-icon>
        </button>
        <button type="button" class="mp-ctrl" @click="next">
          <v-icon size="18">mdi-skip-next</v-icon>
        </button>
        <button
          type="button"
          class="mp-ctrl"
          :style="loop ? 'color: #1a73e8;' : 'color: #9aa0a6;'"
          @click="toggleLoop"
        >
          <v-icon size="16">{{ loop ? 'mdi-repeat-once' : 'mdi-repeat' }}</v-icon>
        </button>
      </div>

      <div class="mp-list">
        <div
          v-for="(song, idx) in playlist"
          :key="idx"
          class="mp-row"
          :class="{ 'mp-row--active': idx === currentIndex }"
          @click="playSong(idx)"
        >
          <v-icon size="12" :color="idx === currentIndex ? 'primary' : 'grey'">
            {{ idx === currentIndex && playing ? 'mdi-volume-high' : 'mdi-music-note' }}
          </v-icon>
          <span class="mp-row-text">{{ rowLabel(song, idx) }}</span>
        </div>
      </div>
    </div>

    <audio ref="audio" @timeupdate="onTimeUpdate" @ended="onEnded" @loadedmetadata="onLoaded" />
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MusicPlayer',
  props: {
    playlist: { type: Array, default: function() { return [] } }
  },
  data: function() {
    return {
      expanded: false,
      playing: false,
      loop: false,
      currentIndex: 0,
      currentTime: 0,
      duration: 0,
      loadingResolve: false,
      resolveError: '',
      resolvedMeta: {}
    }
  },
  computed: {
    currentSong: function() {
      return this.playlist[this.currentIndex] || { name: '', artist: '' }
    },
    displayName: function() {
      var m = this.resolvedMeta[this.currentIndex]
      if (m && m.name) return m.name
      if (this.currentSong.name) return this.currentSong.name
      if (this.currentSong.server && this.currentSong.id) {
        return (this.currentSong.server === 'netease' ? '网易' : this.currentSong.server === 'tencent' ? 'QQ' : this.currentSong.server) + ' #' + this.currentSong.id
      }
      return '暂无歌曲'
    },
    displayArtist: function() {
      var m = this.resolvedMeta[this.currentIndex]
      if (m && m.artist) return m.artist
      return this.currentSong.artist || '未知艺术家'
    },
    progress: function() {
      return this.duration > 0 ? (this.currentTime / this.duration) * 100 : 0
    }
  },
  watch: {
    playlist: {
      handler: function() {
        this.resolvedMeta = {}
        this.resolveError = ''
        this.currentIndex = 0
        var self = this
        this.$nextTick(function() { self.loadSong() })
      },
      deep: true
    },
    currentIndex: function() {
      this.loadSong()
    }
  },
  mounted: function() {
    if (this.playlist.length > 0) this.loadSong()
  },
  methods: {
    rowLabel: function(song, idx) {
      var m = this.resolvedMeta[idx]
      if (m && m.name) return m.name
      if (song.name) return song.name
      if (song.url) return (song.name || '直链') + ''
      if (song.server && song.id) {
        var tag = song.server === 'netease' ? '网易' : song.server === 'tencent' ? 'QQ' : song.server
        return tag + ' · ' + String(song.id)
      }
      return '未命名'
    },
    loadSong: function() {
      var self = this
      var audio = this.$refs.audio
      if (!audio || this.playlist.length === 0) return
      var song = this.playlist[this.currentIndex]
      if (!song) return
      this.resolveError = ''

      if (song.url) {
        audio.src = song.url
        if (this.playing) {
          audio.play().catch(function() { self.playing = false })
        }
        return
      }
      if (song.server && song.id) {
        this.loadingResolve = true
        axios
          .get('/api/meting/resolve', {
            params: { server: String(song.server).trim(), id: String(song.id).trim() }
          })
          .then(function(res) {
            var body = res.data
            if (!body || body.code !== 200) {
              throw new Error((body && body.message) || '解析失败')
            }
            var data = body.data
            if (!data || !data.url) throw new Error('未返回播放地址')
            self.resolvedMeta = Object.assign({}, self.resolvedMeta, {
              [self.currentIndex]: {
                name: data.name || song.name,
                artist: data.artist || song.artist,
                url: data.url
              }
            })
            audio.src = data.url
            if (self.playing) {
              audio.play().catch(function() { self.playing = false })
            }
          })
          .catch(function(e) {
            self.resolveError = (e && e.message) ? e.message : '网络错误或暂无法解析'
            self.playing = false
          })
          .finally(function() {
            self.loadingResolve = false
          })
        return
      }
      this.resolveError = '请配置 url，或同时配置 server 与 id（网易云/QQ 等）'
    },
    togglePlay: function() {
      var self = this
      var audio = this.$refs.audio
      if (!audio) return
      if (this.loadingResolve) return
      if (this.playing) {
        audio.pause()
        this.playing = false
        return
      }
      this.playing = true
      if (!audio.src) {
        this.loadSong()
        return
      }
      audio.play().then(function() {}).catch(function() { self.playing = false })
    },
    prev: function() {
      this.currentIndex = (this.currentIndex - 1 + this.playlist.length) % this.playlist.length
    },
    next: function() {
      this.currentIndex = (this.currentIndex + 1) % this.playlist.length
    },
    playSong: function(idx) {
      this.playing = true
      this.currentIndex = idx
    },
    toggleLoop: function() {
      this.loop = !this.loop
      if (this.$refs.audio) this.$refs.audio.loop = this.loop
    },
    onTimeUpdate: function() {
      this.currentTime = this.$refs.audio ? this.$refs.audio.currentTime : 0
    },
    onLoaded: function() {
      this.duration = this.$refs.audio ? this.$refs.audio.duration : 0
    },
    onEnded: function() {
      if (!this.loop) this.next()
    },
    seek: function(e) {
      var audio = this.$refs.audio
      if (!audio || !this.duration) return
      var rect = e.currentTarget.getBoundingClientRect()
      var ratio = (e.clientX - rect.left) / rect.width
      audio.currentTime = ratio * this.duration
    },
    formatTime: function(s) {
      if (!s || isNaN(s)) return '0:00'
      var m = Math.floor(s / 60)
      var sec = Math.floor(s % 60)
      return m + ':' + (sec < 10 ? '0' : '') + sec
    }
  }
}
</script>

<style scoped>
.music-player-wrap {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9200;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-color, #e8eaed);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
  overflow: hidden;
  transition: width 0.3s ease, background 0.2s, border-color 0.2s;
  color: var(--text-primary, #202124);
}
.mp-collapsed { padding: 8px; display: flex; justify-content: center; }
.mp-icon-btn {
  background: none; border: none; cursor: pointer; width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  transition: background 0.15s;
  color: inherit;
}
.mp-icon-btn:hover { background: var(--bg-hover, #f1f3f4); }
.mp-expanded { padding: 12px; }
.mp-title { font-size: 12px; font-weight: 600; }
.mp-close {
  background: none; border: none; cursor: pointer; color: var(--text-muted, #80868b); padding: 2px;
}
.mp-line-name { font-size: 13px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.mp-line-artist { font-size: 11px; color: var(--text-secondary, #80868b); margin-top: 2px; }
.mp-progress-bg {
  height: 3px; background: var(--bg-hover, #e8eaed); border-radius: 2px; cursor: pointer; position: relative;
}
.mp-progress-fill {
  height: 100%; background: #1a73e8; border-radius: 2px; transition: width 0.1s;
}
.mp-time { font-size: 10px; color: var(--text-muted, #9aa0a6); }
.mp-ctrl { background: none; border: none; cursor: pointer; color: var(--text-secondary, #5f6368); padding: 4px; }
.mp-play {
  background: #1a73e8; border: none; cursor: pointer; color: white; width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}
.mp-play:disabled { opacity: 0.6; }
.mp-err { color: #c62828; }
.mp-list { margin-top: 10px; max-height: 120px; overflow-y: auto; }
.mp-row {
  padding: 5px 6px; border-radius: 6px; cursor: pointer; font-size: 12px;
  display: flex; align-items: center; gap: 6px; color: var(--text-primary, #3c4043);
  transition: background 0.1s;
}
.mp-row:hover { background: var(--bg-hover, #f8f9fa); }
.mp-row--active { background: rgba(26, 115, 232, 0.12); color: #1a73e8; }
.mp-row-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; min-width: 0; }
.mp-spin { animation: mp-rot 0.8s linear infinite; }
@keyframes mp-rot { to { transform: rotate(360deg); } }
</style>
