// 文件上传 API
import request from './request.js'

/**
 * 上传图片
 * @param {File} file - 图片文件对象
 * @returns {Promise<string>} 图片访问 URL
 */
export function uploadImage(file) {
  var formData = new FormData()
  formData.append('file', file)
  return request({
    method: 'post',
    url: '/api/admin/upload/image',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 上传通用文件
 * @param {File} file - 文件对象
 * @returns {Promise<Object>} 文件元数据
 */
export function uploadFile(file) {
  var formData = new FormData()
  formData.append('file', file)
  return request({
    method: 'post',
    url: '/api/admin/upload/file',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
