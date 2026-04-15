/**
 * 全局平滑滚动工具
 * 使用 JS 缓动动画实现，兼容 Vuetify v-app 等自定义滚动容器
 */

/**
 * 获取真实的滚动容器
 * Vuetify v-app 会将滚动容器设置在 .v-application 或其子元素上
 */
function getScrollContainer() {
  // 优先使用 document.scrollingElement（标准）
  if (document.scrollingElement && document.scrollingElement.scrollTop !== undefined) {
    return document.scrollingElement
  }
  return document.documentElement
}

/**
 * 缓动函数 —— easeInOutCubic，比 linear 更自然
 */
function easeInOutCubic(t) {
  return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2
}

/**
 * 平滑滚动到指定位置
 * @param {number} targetY - 目标滚动位置（px）
 * @param {number} duration - 动画时长（ms），默认 500
 */
export function smoothScrollTo(targetY, duration) {
  duration = duration || 500

  var container = getScrollContainer()
  var startY = container.scrollTop || window.scrollY || 0
  var distance = targetY - startY
  var startTime = null

  if (Math.abs(distance) < 1) return

  function step(currentTime) {
    if (!startTime) startTime = currentTime
    var elapsed = currentTime - startTime
    var progress = Math.min(elapsed / duration, 1)
    var ease = easeInOutCubic(progress)

    var newScrollY = startY + distance * ease

    // 同时设置两种方式，兼容不同环境
    container.scrollTop = newScrollY
    window.scrollTo(0, newScrollY)

    if (progress < 1) {
      requestAnimationFrame(step)
    }
  }

  requestAnimationFrame(step)
}

/**
 * 平滑滚动到指定元素
 * @param {string|Element} target - 元素 ID 或 DOM 元素
 * @param {number} offset - 顶部偏移量（px），用于避开固定导航栏，默认 80
 * @param {number} duration - 动画时长（ms），默认 500
 */
export function smoothScrollToElement(target, offset, duration) {
  offset = offset !== undefined ? offset : 80
  duration = duration || 500

  var el = typeof target === 'string' ? document.getElementById(target) : target
  if (!el) return

  var container = getScrollContainer()
  var containerScrollTop = container.scrollTop || window.scrollY || 0
  var elTop = el.getBoundingClientRect().top + containerScrollTop - offset

  smoothScrollTo(elTop, duration)
}

/**
 * 平滑回到顶部
 * @param {number} duration - 动画时长（ms），默认 600
 */
export function smoothScrollToTop(duration) {
  smoothScrollTo(0, duration || 600)
}
