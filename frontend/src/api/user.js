import request from '@/utils/request'

export const getUserPage = (params) => {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

export const getUserById = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export const addUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export const updateUserStatus = (id, status) => {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}