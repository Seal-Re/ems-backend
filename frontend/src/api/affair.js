import request from '@/utils/request'

export function addAffairDefault(data) {
  return request({
    url: '/subscribe/product/addDefault',
    method: 'post',
    data
  })
}

export function addAffairTransaction(data) {
  return request({
    url: '/subscribe/product/addTransaction',
    method: 'post',
    data
  })
}
