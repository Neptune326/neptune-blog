import request from './request.js'

export function generateAiWriting(data) {
  return request({
    method: 'post',
    url: '/api/admin/ai/writing',
    data: data
  })
}
