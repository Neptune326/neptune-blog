import request from './request.js'

export function toggleFavorite(id) {
  return request({
    method: 'post',
    url: '/api/articles/' + id + '/favorite'
  })
}

export function getFavoriteStatus(id) {
  return request({
    method: 'get',
    url: '/api/articles/' + id + '/favorite'
  })
}

export function getMyFavorites() {
  return request({
    method: 'get',
    url: '/api/articles/favorites'
  })
}
