<template>
  <!-- 悬浮音乐播放器 -->
  <div
    v-if="playlist.length > 0"
    style="
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 9200;
      background: white;
      border: 1px solid #e8eaed;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.12);
      overflow: hidden;
      transition: all 0.3s ease;
    "
    :style="expanded ? 'width: 280px;' : 'width: 52px;'"
  >
    <!-- 收起状态：只显示播放按钮 -->
    <div v-if="!expanded" style="padding: 8px; display: flex; justify-content: center;">
      <button @click="expanded = true" style="background: none; border: none; cursor: pointer; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: background 0.15s;"
        @mouseenter="$event.target.style.background='#f1f3f4'" @mouseleave="$event.target.style.background='none'">
        <v-icon size="20" color="primary">mdi-music-note</v-icon>
      </button>
    </div>

    <!-- 展开状态 -->
    <div v-else style="padding: 12px;">
      <!-- 标题栏 -->
      <div class="d-flex align-center justify-space-between mb-2">
        <span style="font-size: 12px; font-weight: 600; color: #202124;">🎵 音乐播放器</span>
        <button @click="expanded = false" style="background: none; border: none; cursor: pointer; color: #80868b; padding: 2px;">
          <v-icon size="16">mdi-chevron-down</v-icon>
        </button>
      </div>

      <!-- 当前歌曲 -->
      <div style="margin-bottom: 10px;">
        <div style="font-size: 13px; font-weight: 500; color: #202124; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
          {{ currentSong.name }}
        </div>
        <div style="font-size: 11px; color: #80868b; margin-top: 2px;">{{ currentSong.artist || '未知艺术家' }}</div>
      </div>

      <!-- 进度条 -->
      <div style="margin-bottom: 10px;">
        <div style="height: 3px; background: #e8eaed; border-radius: 2px; cursor: pointer; position: relative;" @click="seek">
          <div :style="{ width: progress + '%' }" style="height: 100%; background: #1a73e8; border-radius: 2px; transition: width 0.1s;"></div>
        </div>
        <div class="d-flex justify-space-between mt-1" style="font-size: 10px; color: #9aa0a6;">
          <span>{{ formatTime(currentTime) }}</span>
          <span>{{ formatTime(duration) }}</span>
        </div>
      </div>

      <!-- 控制按钮 -->
      <div class="d-flex align-center justify-center" style="gap: 8px;">
        <button @click="prev" style="background: none; border: none; cursor: pointer; color: #5f6368; padding: 4px;">
          <v-icon size="18">mdi-skip-previous</v-icon>
        </button>
        <button @click="togglePlay" style="background: #1a73e8; border: none; cursor: pointer; color: white; width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center;">
          <v-icon size="18" color="white">{{ playing ? 'mdi-pause' : 'mdi-play' }}</v-icon>
        </button>
        <button @click="next" style="background: none; border: none; cursor: pointer; color: #5f6368; padding: 4px;">
          <v-icon size="18">mdi-skip-next</v-icon>
        </button>
        <button @click="toggleLoop" style="background: none; border: none; cursor: pointer; padding: 4px;" :style="loop ? 'color: #1a73e8;' : 'color: #9aa0a6;'">
          <v-icon size="16">{{ loop ? 'mdi-repeat-once' : 'mdi-repeat' }}</v-icon>
        </button>
      </div>

      <!-- 播放列表 -->
      <div style="margin-top: 10px; max-height: 120px; overflow-y: auto;">
        <div
          v-for="(song, idx) in playlist"
          :key="idx"
          @click="playSong(idx)"
          style="padding: 5px 6px; border-radius: 6px; cursor: pointer; font-size: 12px; transition: background 0.1s; display: flex; align-items: center; gap: 6px;"
          :style="idx === currentIndex ? 'background: #e8f0fe; color: #1a73e8;' : 'color: #3c4043;'"
          @mouseenter="$event.currentTarget.style.background = idx === currentIndex ? '#e8f0fe' : '#f8f9fa'"
          @mouseleave="$event.currentTarget.style.background = idx === currentIndex ? '#e8f0fe' : 'transparent'"
        >
          <v-icon size="12" :color="idx === currentIndex ? 'primary' : 'grey'">{{ idx === currentIndex && playing ? 'mdi-volume-high' : 'mdi-music-note' }}</v-icon>
          <span style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ song.name }}</span>
        </div>
      </div>
    </div>

    <!-- 隐藏的 audio 元素 -->
    <audio ref="audio" @timeupdate="onTimeUpdate" @ended="onEnded" @loadedmetadata="onLoaded" />
  </div>
</template>

<script>
export default {
  name: 'MusicPlayer',
  props: {
    // 播放列表：[{ name, artist, url }]
    playlist: { type: Array, default: function() { return [] } }
  },
  data: function() {
    return {
      expanded: false,
      playing: false,
      loop: false,
      currentIndex: 0,
      currentTime: 0,
      duration: 0
    }
  },
  computed: {
    currentSong: function() {
      return this.playlist[this.currentIndex] || { name: '暂无歌曲', artist: '' }
    },
    progress: function() {
      return this.duration > 0 ? (this.currentTime / this.duration) * 100 : 0
    }
  },
  watch: {
    currentIndex: function() {
      this.loadSong()
    }
  },
  mounted: function() {
    if (this.playlist.length > 0) this.loadSong()
  },
  methods: {
    loadSong: function() {
      var audio = this.$refs.audio
      if (!audio || !this.currentSong.url) return
      audio.src = this.currentSong.url
      if (this.playing) audio.play().catch(function() {})
    },
    togglePlay: function() {
      var audio = this.$refs.audio
      if (!audio) return
      if (this.playing) {
        audio.pause()
        this.playing = false
      } else {
        audio.play().then(function() {}).catch(function() {})
        this.playing = true
      }
    },
    prev: function() {
      this.currentIndex = (this.currentIndex - 1 + this.playlist.length) % this.playlist.length
    },
    next: function() {
      this.currentIndex = (this.currentIndex + 1) % this.playlist.length
    },
    playSong: function(idx) {
      this.currentIndex = idx
      this.playing = true
      var self = this
      this.$nextTick(function() {
        self.$refs.audio && self.$refs.audio.play().catch(function() {})
      })
    },
    toggleLoop: function() {
      this.loop = !this.loop
      this.$refs.audio && (this.$refs.audio.loop = this.loop)
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
      return m + ':' + String(sec).padStart(2, '0')
    }
  }
}
</script>
