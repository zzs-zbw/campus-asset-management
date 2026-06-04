import request from '@/utils/request'

export const getCategoryList = () => {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

export const getActiveCategories = () => {
  return request({
    url: '/category/active',
    method: 'get'
  })
}

export const addCategory = (data) => {
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

export const updateCategory = (data) => {
  return request({
    url: '/category',
    method: 'put',
    data
  })
}

export const deleteCategory = (id) => {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}