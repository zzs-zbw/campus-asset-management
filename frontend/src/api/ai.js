import request from '@/utils/request'

export const aiChat = (question) => {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { question }
  })
}
