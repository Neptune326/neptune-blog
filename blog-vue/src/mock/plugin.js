// Mock 插件 —— 实现 request.js 的插件接口
// 只在开发环境通过 main.js 注入，生产构建不引用此文件
import { mockHandler } from './handler.js'

var mockPlugin = {
  /**
   * 处理 mock 请求
   * @param {string} method
   * @param {string} url
   * @param {object} params
   * @param {object} data
   * @returns {object|null}
   */
  handle: function(method, url, params, data) {
    return mockHandler(method, url, params, data)
  },

  /**
   * 检测后端是否可用
   * @returns {Promise<boolean>} true=后端可用，false=需要 mock
   */
  detect: function() {
    return fetch('/api/articles?pageNum=1&pageSize=1', {
      signal: AbortSignal.timeout ? AbortSignal.timeout(3000) : undefined
    })
      .then(function(res) {
        return res.ok
      })
      .catch(function() {
        return false
      })
  }
}

export default mockPlugin
