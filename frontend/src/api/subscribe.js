import request from '@/utils/request'

export function subscribeAdd(data) {
  return request({
    url: '/subscribe/consume/add',
    method: 'post',
    data
  })
}

export function subscribeDelete(data) {
  return request({
    url: '/subscribe/consume/delete',
    method: 'post',
    data
  })
}

export function subscribeDetect() {
  return request({
    url: '/subscribe/consume/detect',
    method: 'get'
  })
}

export function getMsg(params) {
  return request({
    url: '/subscribe/consume/getMsg',
    method: 'get',
    params
  })
}

export function getLatestMsg() {
  return request({
    url: '/subscribe/consume/getLatestMsg',
    method: 'get'
  })
}
