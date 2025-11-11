import type { MenuItem } from '@/store/auth';

export const ROLE_LABELS: Record<string, string> = {
  ADMIN: '超级管理员',
  CREATOR: '内容创作者',
  USER: '普通用户'
};

export const DEFAULT_ROLE_MENUS: Record<string, MenuItem[]> = {
  ADMIN: [
    { name: '首页概览', path: '/dashboard' },
    { name: '用户管理', path: '/users' },
    { name: '视频管理', path: '/videos' },
    { name: '行为日志', path: '/logs' },
    { name: '推荐调试', path: '/recommend' },
    { name: '个人中心', path: '/profile' }
  ],
  CREATOR: [
    { name: '首页概览', path: '/dashboard' },
    { name: '视频表现', path: '/videos' },
    { name: '推荐预览', path: '/recommend' },
    { name: '个人中心', path: '/profile' }
  ],
  USER: [
    { name: '首页概览', path: '/dashboard' },
    { name: '推荐视频', path: '/recommend' },
    { name: '个人中心', path: '/profile' }
  ]
};

export function resolveMenus(role: string, remoteMenus: MenuItem[]): MenuItem[] {
  if (remoteMenus && remoteMenus.length) {
    return remoteMenus;
  }
  return DEFAULT_ROLE_MENUS[role] || DEFAULT_ROLE_MENUS.USER;
}
