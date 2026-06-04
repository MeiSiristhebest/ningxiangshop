import request from '@/utils/request'

export function list () {
  return request({
    url: '/ningxiang_delivery/m/delivery_company/list',
    method: 'get'
  })
}
