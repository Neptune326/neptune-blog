<template>
  <div
    v-if="enabled && theme !== 'classic'"
    class="theme-ambient"
    :class="'theme-ambient--' + theme"
    aria-hidden="true"
  >
    <div class="theme-ambient__pattern"></div>
    <div class="theme-ambient__flow theme-ambient__flow--one"></div>
    <div class="theme-ambient__flow theme-ambient__flow--two"></div>
    <span
      v-for="item in particles"
      :key="item"
      class="theme-ambient__particle"
      :style="{ '--i': item }"
    ></span>
  </div>
</template>

<script>
export default {
  name: 'ThemeAmbientLayer',
  props: {
    enabled: { type: Boolean, default: false },
    theme: { type: String, default: 'sakura' }
  },
  computed: {
    particles: function() {
      return Array.from({ length: 14 }, function(_, index) { return index + 1 })
    }
  }
}
</script>

<style scoped>
.theme-ambient {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 1;
  overflow: hidden;
}

.theme-ambient__pattern {
  position: absolute;
  inset: 0;
  opacity: 0.55;
}

.theme-ambient--sakura .theme-ambient__pattern {
  background-image:
    linear-gradient(120deg, rgba(244, 114, 182, 0.08) 0, transparent 28%),
    linear-gradient(240deg, rgba(125, 211, 252, 0.08) 0, transparent 24%),
    repeating-linear-gradient(135deg, rgba(244, 114, 182, 0.04) 0 1px, transparent 1px 24px);
}

.theme-ambient--neon .theme-ambient__pattern {
  background-image:
    linear-gradient(rgba(34, 211, 238, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(217, 70, 239, 0.06) 1px, transparent 1px);
  background-size: 42px 42px;
}

.theme-ambient--starry .theme-ambient__pattern {
  background-image:
    radial-gradient(circle at 12% 18%, rgba(255,255,255,0.42) 0 1px, transparent 2px),
    radial-gradient(circle at 72% 32%, rgba(255,255,255,0.32) 0 1px, transparent 2px),
    radial-gradient(circle at 42% 76%, rgba(125,211,252,0.35) 0 1px, transparent 2px),
    linear-gradient(180deg, rgba(15,23,42,0.16), transparent 48%);
  background-size: 220px 220px, 260px 260px, 300px 300px, 100% 100%;
}

.theme-ambient__flow {
  position: absolute;
  height: 1px;
  width: 38vw;
  max-width: 520px;
  opacity: 0.4;
  transform: rotate(-18deg);
  animation: ambient-flow 11s linear infinite;
}

.theme-ambient__flow--one {
  top: 22%;
  left: -40vw;
  background: linear-gradient(90deg, transparent, var(--front-accent), transparent);
}

.theme-ambient__flow--two {
  top: 72%;
  left: -30vw;
  animation-delay: -4s;
  background: linear-gradient(90deg, transparent, var(--front-accent-2), transparent);
}

.theme-ambient__particle {
  position: absolute;
  left: calc((var(--i) * 7%) - 8%);
  top: -24px;
  width: 8px;
  height: 12px;
  border-radius: 8px 2px 8px 2px;
  background: color-mix(in srgb, var(--front-accent) 64%, white);
  opacity: 0.45;
  transform: rotate(calc(var(--i) * 23deg));
  animation: ambient-fall calc(10s + var(--i) * 0.7s) linear infinite;
  animation-delay: calc(var(--i) * -0.8s);
}

.theme-ambient--neon .theme-ambient__particle {
  width: 3px;
  height: 18px;
  border-radius: 3px;
  background: var(--front-accent-2);
  box-shadow: 0 0 12px var(--front-accent-2);
}

.theme-ambient--starry .theme-ambient__particle {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: rgba(255,255,255,0.8);
  box-shadow: 0 0 10px rgba(125,211,252,0.7);
}

@keyframes ambient-flow {
  from { transform: translateX(0) rotate(-18deg); }
  to { transform: translateX(150vw) rotate(-18deg); }
}

@keyframes ambient-fall {
  0% { transform: translate3d(0, -20px, 0) rotate(calc(var(--i) * 23deg)); opacity: 0; }
  12% { opacity: 0.45; }
  100% { transform: translate3d(30px, 110vh, 0) rotate(calc(var(--i) * 55deg)); opacity: 0; }
}

@media (max-width: 600px) {
  .theme-ambient__particle:nth-child(n+9) {
    display: none;
  }
  .theme-ambient__flow {
    width: 60vw;
  }
}

@media (prefers-reduced-motion: reduce) {
  .theme-ambient__flow,
  .theme-ambient__particle {
    animation: none;
  }
}
</style>
