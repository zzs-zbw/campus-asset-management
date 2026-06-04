import request from '@/utils/request'

export const getAssetPage = (params) => {
  return request({
    url: '/asset/page',
    method: 'get',
    params
  })
}

export const getAssetById = (id) => {
  return request({
    url: `/asset/${id}`,
    method: 'get'
  })
}

export const addAsset = (data) => {
  return request({
    url: '/asset',
    method: 'post',
    data
  })
}

export const updateAsset = (data) => {
  return request({
    url: '/asset',
    method: 'put',
    data
  })
}

export const deleteAsset = (id) => {
  return request({
    url: `/asset/${id}`,
    method: 'delete'
  })
}

export const batchDeleteAssets = (ids) => {
  return request({
    url: '/asset/batch',
    method: 'delete',
    data: ids
  })
}

export const getStatistics = () => {
  return request({
    url: '/asset/statistics',
    method: 'get'
  })
}

export const getCategoryStatistics = () => {
  return request({
    url: '/asset/category-statistics',
    method: 'get'
  })
}

export const getMaintenanceStatistics = () => {
  return request({
    url: '/asset/maintenance-statistics',
    method: 'get'
  })
}

export const generateAssetCode = () => {
  return request({
    url: '/asset/generate-code',
    method: 'get'
  })
}

export const importAssets = (file, mode = 'append') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('mode', mode)
  return request({
    url: '/excel/import/assets',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const exportAssets = () => {
  return request({
    url: '/excel/export/assets',
    method: 'get',
    responseType: 'blob'
  })
}

export const getTemplate = () => {
  return request({
    url: '/excel/export/template',
    method: 'get',
    responseType: 'blob'
  })
}

export function getMaintenancePage(params) {
  return request({
    url: '/maintenance/page',
    method: 'get',
    params
  })
}

export function getMaintenanceByAssetId(assetId) {
  return request({
    url: `/maintenance/asset/${assetId}`,
    method: 'get'
  })
}

export function addMaintenance(data) {
  return request({
    url: '/maintenance',
    method: 'post',
    data
  })
}

export function updateMaintenance(id, data) {
  return request({
    url: `/maintenance/${id}`,
    method: 'put',
    data
  })
}

export function deleteMaintenance(id) {
  return request({
    url: `/maintenance/${id}`,
    method: 'delete'
  })
}
