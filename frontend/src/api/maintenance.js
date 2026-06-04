import request from '@/utils/request'

export const getMaintenancePage = (params) => {
  return request({
    url: '/maintenance/page',
    method: 'get',
    params
  })
}

export const getMaintenanceByAssetId = (assetId) => {
  return request({
    url: `/maintenance/asset/${assetId}`,
    method: 'get'
  })
}

export const addMaintenance = (data) => {
  return request({
    url: '/maintenance',
    method: 'post',
    data
  })
}

export const updateMaintenance = (data) => {
  return request({
    url: '/maintenance',
    method: 'put',
    data
  })
}

export const deleteMaintenance = (id) => {
  return request({
    url: `/maintenance/${id}`,
    method: 'delete'
  })
}