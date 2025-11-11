import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import pinia from '@/store';
import { useAuthStore } from '@/store/auth';

const LoginView = () => import('@/views/auth/LoginView.vue');
const DashboardHome = () => import('@/views/dashboard/DashboardHome.vue');
const UserListView = () => import('@/views/user/UserListView.vue');
const VideoOverviewView = () => import('@/views/video/VideoOverviewView.vue');
const LogAnalyticsView = () => import('@/views/log/LogAnalyticsView.vue');
const RecommendDebugView = () => import('@/views/recommend/RecommendDebugView.vue');
const ProfileView = () => import('@/views/profile/ProfileView.vue');
const NoAccessView = () => import('@/views/common/NoAccessView.vue');
const NotFoundView = () => import('@/views/common/NotFoundView.vue');
const AppLayout = () => import('@/components/layout/AppLayout.vue');

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardHome,
        meta: { requiresAuth: true, title: '首页概览' }
      },
      {
        path: 'users',
        name: 'users',
        component: UserListView,
        meta: { requiresAuth: true, roles: ['ADMIN'], title: '用户管理' }
      },
      {
        path: 'videos',
        name: 'videos',
        component: VideoOverviewView,
        meta: { requiresAuth: true, roles: ['ADMIN', 'CREATOR'], title: '视频管理' }
      },
      {
        path: 'logs',
        name: 'logs',
        component: LogAnalyticsView,
        meta: { requiresAuth: true, roles: ['ADMIN'], title: '行为日志' }
      },
      {
        path: 'recommend',
        name: 'recommend',
        component: RecommendDebugView,
        meta: { requiresAuth: true, roles: ['ADMIN', 'CREATOR', 'USER'], title: '推荐调试' }
      },
      {
        path: 'profile',
        name: 'profile',
        component: ProfileView,
        meta: { requiresAuth: true, title: '个人中心' }
      },
      {
        path: 'no-access',
        name: 'no-access',
        component: NoAccessView,
        meta: { requiresAuth: true, title: '无权限' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: { public: true, title: '页面不存在' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore(pinia);
  const requiresAuth = to.meta.requiresAuth && !to.meta.public;

  if (requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  if (to.meta.roles && Array.isArray(to.meta.roles) && to.meta.roles.length) {
    if (!to.meta.roles.includes(authStore.role)) {
      next({ name: 'no-access' });
      return;
    }
  }

  if (to.name === 'login' && authStore.isLoggedIn) {
    next({ name: 'dashboard' });
    return;
  }

  next();
});

export default router;
