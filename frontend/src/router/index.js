import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/user-management',
    name: 'UserManagement',
    component: () => import('@/views/SystemManagement/UserManagement.vue'),
    meta: { title: '用户管理', requiresAuth: true }
  },
  {
    path: '/menu-management',
    name: 'MenuManagement',
    component: () => import('@/views/SystemManagement/MenuManagement.vue'),
    meta: { title: '菜单管理', requiresAuth: true }
  },
  {
    path: '/role-management',
    name: 'RoleManagement',
    component: () => import('@/views/SystemManagement/RoleManagement.vue'),
    meta: { title: '角色管理', requiresAuth: true }
  },
  {
    path: '/permission-management',
    name: 'PermissionManagement',
    component: () => import('@/views/SystemManagement/PermissionManagement.vue'),
    meta: { title: '权限管理', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要认证的路由
    const token = localStorage.getItem('token')
    if (!token) {
      // 没有token，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }  // 保存目标路径，登录后可跳转回去
      })
    } else {
      // 有token，允许访问
      next()
    }
  } else if (to.path === '/login') {
    // 如果访问的是登录页面
    const token = localStorage.getItem('token')
    if (token) {
      // 如果已经有token，重定向到仪表板
      next('/dashboard')
    } else {
      // 没有token，允许访问登录页面
      next()
    }
  } else {
    // 不需要认证的路由，直接允许访问
    next()
  }
})

export default router