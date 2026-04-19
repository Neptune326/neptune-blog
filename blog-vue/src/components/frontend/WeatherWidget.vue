<template>
  <!-- 天气组件 —— 调用 wttr.in 免费天气 API，无需 key -->
  <div
    v-if="weather"
    style="
      background: linear-gradient(135deg, #e0f2fe, #f0f9ff);
      border: 1px solid #bae6fd;
      border-radius: 12px;
      padding: 14px 16px;
      display: flex;
      align-items: center;
      gap: 12px;
    "
  >
    <!-- 天气图标 -->
    <div style="font-size: 32px; line-height: 1; flex-shrink: 0;">{{ weather.icon }}</div>
    <!-- 信息 -->
    <div style="flex: 1; min-width: 0;">
      <div style="display: flex; align-items: baseline; gap: 6px; margin-bottom: 2px;">
        <span style="font-size: 22px; font-weight: 700; color: #0369a1; line-height: 1;">{{ weather.temp }}°</span>
        <span style="font-size: 12px; color: #0284c7; font-weight: 500;">{{ weather.desc }}</span>
      </div>
      <div style="font-size: 11px; color: #7dd3fc; display: flex; align-items: center; gap: 4px;">
        <span>📍 {{ weather.city }}</span>
        <span>·</span>
        <span>💧 {{ weather.humidity }}%</span>
        <span>·</span>
        <span>🌬️ {{ weather.wind }}</span>
      </div>
    </div>
    <!-- 刷新按钮 -->
    <button
      @click="loadWeather"
      :disabled="loading"
      style="background: none; border: none; cursor: pointer; color: #7dd3fc; padding: 4px; border-radius: 6px; transition: color 0.15s; flex-shrink: 0;"
      title="刷新天气"
    >
      <svg :style="loading ? 'animation: spin 1s linear infinite;' : ''" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
        <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
      </svg>
    </button>
  </div>

  <!-- 加载中 -->
  <div v-else-if="loading" style="background: #f0f9ff; border: 1px solid #bae6fd; border-radius: 12px; padding: 14px 16px; display: flex; align-items: center; gap: 8px;">
    <div style="width: 20px; height: 20px; border: 2px solid #bae6fd; border-top-color: #0284c7; border-radius: 50%; animation: spin 0.8s linear infinite;"></div>
    <span style="font-size: 13px; color: #7dd3fc;">获取天气中...</span>
  </div>

  <!-- 失败时不显示 -->
</template>

<script>
// 天气图标映射（wttr.in 天气代码 → emoji）
var WEATHER_ICONS = {
  '晴': '☀️', '多云': '⛅', '阴': '☁️', '小雨': '🌦️', '中雨': '🌧️',
  '大雨': '⛈️', '雷阵雨': '⛈️', '小雪': '🌨️', '中雪': '❄️', '大雪': '🌨️',
  '雾': '🌫️', '霾': '😷', '沙尘': '🌪️', '冰雹': '🌨️'
}

function getIcon(desc) {
  for (var key in WEATHER_ICONS) {
    if (desc && desc.indexOf(key) >= 0) return WEATHER_ICONS[key]
  }
  return '🌤️'
}

export default {
  name: 'WeatherWidget',
  data: function() {
    return {
      weather: null,
      loading: false,
      city: '自动定位'
    }
  },
  mounted: function() {
    this.loadWeather()
  },
  methods: {
    loadWeather: function() {
      var self = this
      self.loading = true
      // 使用 wttr.in JSON API，format=j1，无需 API key
      // 先尝试获取用户城市（通过 IP 定位）
      fetch('https://wttr.in/?format=j1&lang=zh', {
        signal: AbortSignal.timeout ? AbortSignal.timeout(5000) : undefined
      })
        .then(function(res) { return res.json() })
        .then(function(data) {
          var current = data.current_condition && data.current_condition[0]
          var area = data.nearest_area && data.nearest_area[0]
          if (!current) throw new Error('no data')

          var desc = (current.lang_zh && current.lang_zh[0] && current.lang_zh[0].value) ||
                     (current.weatherDesc && current.weatherDesc[0] && current.weatherDesc[0].value) || '未知'
          var cityName = (area && area.areaName && area.areaName[0] && area.areaName[0].value) || '未知'

          self.weather = {
            temp: current.temp_C || '--',
            desc: desc,
            icon: getIcon(desc),
            city: cityName,
            humidity: current.humidity || '--',
            wind: (current.windspeedKmph || '--') + 'km/h'
          }
        })
        .catch(function() {
          // 网络失败时显示默认数据
          self.weather = {
            temp: '--',
            desc: '天气获取失败',
            icon: '🌤️',
            city: '未知',
            humidity: '--',
            wind: '--'
          }
        })
        .finally(function() {
          self.loading = false
        })
    }
  }
}
</script>
