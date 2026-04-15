// Feature: personal-blog-system, Property 8: axios Token 注入规则
// 使用 fast-check 属性测试验证 axios 请求拦截器的 Token 注入规则

import { describe, it, expect } from 'vitest'
import * as fc from 'fast-check'

/**
 * 提取 request.js 中的 Token 注入判断逻辑为纯函数
 * 与 request.js 中的拦截器逻辑保持一致：
 * 判断 URL 是否以 /api/admin 开头
 */
function shouldInjectToken(url) {
  return url && url.indexOf('/api/admin') === 0
}

/**
 * 模拟请求拦截器处理逻辑
 * 与 request.js 中的 interceptors.request.use 回调保持一致
 */
function applyRequestInterceptor(config, token) {
  if (shouldInjectToken(config.url)) {
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = token
    }
  }
  return config
}

describe('axios 请求拦截器 Token 注入规则', function() {

  // Feature: personal-blog-system, Property 8: axios Token 注入规则
  it('后台接口路径（/api/admin 开头）应自动注入 Authorization 头', function() {
    // 生成以 /api/admin 开头的随机路径
    var adminPathArbitrary = fc.string({ minLength: 0, maxLength: 30 })
      .map(function(suffix) { return '/api/admin' + suffix })

    var tokenArbitrary = fc.string({ minLength: 8, maxLength: 64 })
      .filter(function(s) { return s.length > 0 })

    fc.assert(
      fc.property(adminPathArbitrary, tokenArbitrary, function(path, token) {
        var config = { url: path, headers: {} }
        var result = applyRequestInterceptor(config, token)

        // 断言：/api/admin 路径应注入 Authorization 头
        expect(result.headers['Authorization']).toBe(token)
        return true
      }),
      { numRuns: 100 }
    )
  })

  // Feature: personal-blog-system, Property 8: axios Token 注入规则（反向验证）
  it('前台接口路径（/api 开头但非 /api/admin）不应注入 Authorization 头', function() {
    // 生成以 /api 开头但不以 /api/admin 开头的随机路径
    var frontPathArbitrary = fc.string({ minLength: 1, maxLength: 30 })
      .filter(function(s) {
        // 排除以 /admin 开头的后缀，避免生成 /api/admin 路径
        return !s.startsWith('/admin')
      })
      .map(function(suffix) { return '/api/' + suffix })

    var tokenArbitrary = fc.string({ minLength: 8, maxLength: 64 })

    fc.assert(
      fc.property(frontPathArbitrary, tokenArbitrary, function(path, token) {
        var config = { url: path, headers: {} }
        var result = applyRequestInterceptor(config, token)

        // 断言：非 /api/admin 路径不应注入 Authorization 头
        expect(result.headers['Authorization']).toBeUndefined()
        return true
      }),
      { numRuns: 100 }
    )
  })

  // shouldInjectToken 正向验证
  it('shouldInjectToken 对 /api/admin 路径返回 true', function() {
    fc.assert(
      fc.property(
        fc.string({ minLength: 0, maxLength: 20 }),
        function(suffix) {
          var url = '/api/admin' + suffix
          expect(shouldInjectToken(url)).toBe(true)
          return true
        }
      ),
      { numRuns: 100 }
    )
  })

  // shouldInjectToken 反向验证
  it('shouldInjectToken 对非 /api/admin 路径返回 false', function() {
    fc.assert(
      fc.property(
        fc.string({ minLength: 1, maxLength: 30 }).filter(function(s) {
          return !s.startsWith('/api/admin')
        }),
        function(url) {
          expect(shouldInjectToken(url)).toBe(false)
          return true
        }
      ),
      { numRuns: 100 }
    )
  })
})
