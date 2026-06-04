import request from '@/utils/request'

export const getLogPage = (params) => {
  return request({
    url: '/log/page',
    method: 'get',
    params
  })
}