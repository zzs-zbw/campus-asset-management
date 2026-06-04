import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'

const ROLE_MAP = {
  1: '超级管理员',
  2: '校级资产负责人',
  3: '部门资产管理员',
  4: '仓库/采购管理员',
  5: '维修运维员',
  6: '财务审核员',
  7: '普通使用人'
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.user?.role || 7,
    roleName: (state) => ROLE_MAP[state.user?.role] || '未知',
    userName: (state) => state.user?.realName || '',
    department: (state) => state.user?.department || '',

    isAdmin: (state) => state.user?.role === 1,
    isSchoolAdmin: (state) => [1, 2].includes(state.user?.role),
    isDeptAdmin: (state) => [1, 2, 3].includes(state.user?.role),
    isWarehouseAdmin: (state) => [1, 2, 3, 4].includes(state.user?.role),
    isMaintenanceStaff: (state) => [1, 2, 3, 5].includes(state.user?.role),
    isFinanceStaff: (state) => [1, 2, 6].includes(state.user?.role),

    canAddAsset: (state) => [1, 2, 3, 4].includes(state.user?.role),
    canEditAsset: (state) => [1, 2, 3, 4].includes(state.user?.role),
    canDeleteAsset: (state) => [1, 2].includes(state.user?.role),
    canImportAsset: (state) => [1, 2, 3, 4].includes(state.user?.role),
    canExportAsset: (state) => state.user?.role != null,

    canAddMaintenance: (state) => [1, 2, 3, 5, 7].includes(state.user?.role),
    canEditMaintenance: (state) => [1, 2, 3, 5].includes(state.user?.role),
    canDeleteMaintenance: (state) => [1, 2].includes(state.user?.role),

    canManageCategory: (state) => [1, 2].includes(state.user?.role),
    canManageUser: (state) => [1].includes(state.user?.role),
    canViewLog: (state) => [1, 2].includes(state.user?.role),

    canViewFinance: (state) => [1, 2, 6].includes(state.user?.role),

    menuRoutes(state) {
      const role = state.user?.role || 7
      const allRoutes = [
        { path: '/dashboard', meta: { title: '首页', icon: 'DataAnalysis' }, roles: [1, 2, 3, 4, 5, 6, 7] },
        { path: '/assets', meta: { title: '资产管理', icon: 'Box' }, roles: [1, 2, 3, 4, 5, 6, 7] },
        { path: '/maintenance', meta: { title: '维修管理', icon: 'Tools' }, roles: [1, 2, 3, 5, 7] },
        { path: '/categories', meta: { title: '分类管理', icon: 'FolderOpened' }, roles: [1, 2] },
        { path: '/finance', meta: { title: '财务管理', icon: 'Money' }, roles: [1, 2, 6] },
        { path: '/users', meta: { title: '用户管理', icon: 'User' }, roles: [1] },
        { path: '/logs', meta: { title: '操作日志', icon: 'Document' }, roles: [1, 2] }
      ]
      return allRoutes.filter(r => r.roles.includes(role))
    }
  },

  actions: {
    async loginAction(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.user = res.data.user
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      return res
    },

    async logoutAction() {
      await logout()
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
