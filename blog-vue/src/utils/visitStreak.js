/**
 * 连续访问天数（localStorage），同日多次访问只计一次；跨自然日连续则 +1，否则重置为 1。
 * 首次调用在当日会写入 storage；再次调用同一会话同日返回已保存 streak，milestone 为 null。
 */
var STORAGE_STREAK = 'nb_visit_streak'
var STORAGE_LAST = 'nb_visit_last_ymd'

function toYmd(d) {
  var y = d.getFullYear()
  var m = d.getMonth() + 1
  var day = d.getDate()
  return y + '-' + (m < 10 ? '0' : '') + m + '-' + (day < 10 ? '0' : '') + day
}

function dayDiffYmd(a, b) {
  var da = new Date(a + 'T12:00:00')
  var db = new Date(b + 'T12:00:00')
  return Math.round((db - da) / 86400000)
}

/**
 * @returns {{ streak: number, milestone: number|null }} milestone 为 7 或 30 时表示达成里程碑（仅首次写入当日时可能返回）
 */
export function syncVisitStreak() {
  var today = toYmd(new Date())
  var last = localStorage.getItem(STORAGE_LAST) || ''
  var prevStreak = parseInt(localStorage.getItem(STORAGE_STREAK) || '0', 10) || 0
  if (last === today) {
    return { streak: prevStreak, milestone: null }
  }
  var streak = 1
  if (last) {
    var diff = dayDiffYmd(last, today)
    if (diff === 1) {
      streak = prevStreak + 1
    } else {
      streak = 1
    }
  }
  localStorage.setItem(STORAGE_STREAK, String(streak))
  localStorage.setItem(STORAGE_LAST, today)
  var milestone = null
  if (streak === 7 || streak === 30) {
    milestone = streak
  }
  return { streak: streak, milestone: milestone }
}
