import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'DataAnalysis', roles: [1, 2, 3, 4, 5, 6, 7] }
      },
      {
        path: 'assets',
        name: 'Assets',
        component: () => import('@/views/Assets.vue'),
        meta: { title: '资产管理', icon: 'Box', roles: [1, 2, 3, 4, 5, 6, 7] }
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('@/views/Maintenance.vue'),
        meta: { title: '维修管理', icon: 'Tools', roles: [1, 2, 3, 5, 7] }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/Categories.vue'),
        meta: { title: '分类管理', icon: 'FolderOpened', roles: [1, 2] }
      },
      {
        path: 'finance',
        name: 'Finance',
        component: () => import('@/views/Finance.vue'),
        meta: { title: '财务管理', icon: 'Money', roles: [1, 2, 6] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { title: '用户管理', icon: 'User', roles: [1] }
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('@/views/Logs.vue'),
        meta: { title: '操作日志', icon: 'Document', roles: [1, 2] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else if (to.meta.roles && userStore.isLoggedIn) {
    const userRole = userStore.role
    if (to.meta.roles.includes(userRole)) {
      next()
    } else {
      next('/dashboard')
    }
  } else {
    next()
  }
})

export default router
