<template>
  <div v-if="enabled" class="theme-switcher">
    <v-menu v-model="menu" location="top end" :close-on-content-click="false">
      <template #activator="{ props }">
        <v-tooltip text="主题装扮" location="left">
          <template #activator="{ props: tooltipProps }">
            <v-btn
              v-bind="{ ...props, ...tooltipProps }"
              icon
              color="primary"
              class="theme-switcher__button"
              elevation="8"
            >
              <v-icon size="22">mdi-palette-outline</v-icon>
            </v-btn>
          </template>
        </v-tooltip>
      </template>

      <div class="theme-switcher__panel">
        <div class="theme-switcher__title">主题装扮</div>
        <button
          v-for="item in themes"
          :key="item.value"
          class="theme-switcher__item"
          :class="{ 'theme-switcher__item--active': item.value === theme }"
          type="button"
          @click="selectTheme(item.value)"
        >
          <span class="theme-switcher__swatch" :class="'theme-switcher__swatch--' + item.value"></span>
          <span>{{ item.label }}</span>
          <v-icon v-if="item.value === theme" size="16" color="primary">mdi-check</v-icon>
        </button>
      </div>
    </v-menu>
  </div>
</template>

<script>
export default {
  name: 'ThemeSwitcher',
  props: {
    enabled: { type: Boolean, default: false },
    theme: { type: String, default: 'sakura' }
  },
  emits: ['theme-change'],
  data: function() {
    return {
      menu: false,
      themes: [
        { label: '经典', value: 'classic' },
        { label: '樱花', value: 'sakura' },
        { label: '霓虹', value: 'neon' },
        { label: '星夜', value: 'starry' }
      ]
    }
  },
  methods: {
    selectTheme: function(theme) {
      this.$emit('theme-change', theme)
      this.menu = false
    }
  }
}
</script>

<style scoped>
.theme-switcher {
  position: fixed;
  right: 22px;
  bottom: 94px;
  z-index: 9300;
}

.theme-switcher__button {
  width: 46px !important;
  height: 46px !important;
  backdrop-filter: blur(14px);
  background: color-mix(in srgb, var(--front-card) 72%, var(--front-accent)) !important;
  border: 1px solid var(--front-border);
}

.theme-switcher__panel {
  width: 188px;
  padding: 10px;
  border: 1px solid var(--front-border);
  border-radius: 14px;
  background: color-mix(in srgb, var(--front-card) 86%, transparent);
  backdrop-filter: blur(18px);
  box-shadow: var(--front-shadow);
}

.theme-switcher__title {
  padding: 4px 6px 8px;
  color: var(--front-text);
  font-size: 13px;
  font-weight: 700;
}

.theme-switcher__item {
  width: 100%;
  min-height: 36px;
  display: flex;
  align-items: center;
  gap: 9px;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: var(--front-text);
  cursor: pointer;
  font: inherit;
  font-size: 13px;
  padding: 7px 8px;
  text-align: left;
}

.theme-switcher__item:hover,
.theme-switcher__item--active {
  background: color-mix(in srgb, var(--front-accent) 12%, transparent);
}

.theme-switcher__swatch {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.5);
  box-shadow: inset 0 0 0 1px rgba(0,0,0,0.08);
  flex: 0 0 auto;
}

.theme-switcher__swatch--classic {
  background: linear-gradient(135deg, #ffffff, #1a73e8);
}

.theme-switcher__swatch--sakura {
  background: linear-gradient(135deg, #fff1f2, #f472b6, #38bdf8);
}

.theme-switcher__swatch--neon {
  background: linear-gradient(135deg, #0f172a, #22d3ee, #d946ef);
}

.theme-switcher__swatch--starry {
  background: linear-gradient(135deg, #020617, #2563eb, #facc15);
}

@media (max-width: 600px) {
  .theme-switcher {
    right: 16px;
    bottom: 76px;
  }
}
</style>
