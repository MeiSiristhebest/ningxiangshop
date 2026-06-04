import request from '@/utils/request'

export function login (data) {
  return request({
    url: '/ningxiang_auth/ua/login',
    method: 'post',
    data
  })
}

export function logout () {
  return request({
    url: '/ningxiang_auth/login_out',
    method: 'post'
  })
}
