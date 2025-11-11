import { defineStore } from 'pinia';

export interface UserProfile {
  id: number;
  username: string;
  role: 'ADMIN' | 'CREATOR' | 'USER' | string;
  status: number;
}

export interface MenuItem {
  name: string;
  path: string;
}

interface AuthState {
  token: string | null;
  profile: UserProfile | null;
  menus: MenuItem[];
}

const STORAGE_KEY = 'company-frontend-auth';

function readStorage(): AuthState {
  try {
    const cached = localStorage.getItem(STORAGE_KEY);
    if (!cached) {
      return { token: null, profile: null, menus: [] };
    }
    return JSON.parse(cached) as AuthState;
  } catch (error) {
    console.warn('读取本地登录态失败', error);
    return { token: null, profile: null, menus: [] };
  }
}

function persist(state: AuthState) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: null,
    profile: null,
    menus: [],
    ...readStorage()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    role: (state) => state.profile?.role ?? ''
  },
  actions: {
    setAuth(payload: AuthState) {
      this.token = payload.token;
      this.profile = payload.profile;
      this.menus = payload.menus;
      persist({ token: this.token, profile: this.profile, menus: this.menus });
    },
    setMenus(menus: MenuItem[]) {
      this.menus = menus;
      persist({ token: this.token, profile: this.profile, menus: this.menus });
    },
    updateProfile(profile: UserProfile) {
      this.profile = profile;
      persist({ token: this.token, profile: this.profile, menus: this.menus });
    },
    logout() {
      this.token = null;
      this.profile = null;
      this.menus = [];
      localStorage.removeItem(STORAGE_KEY);
    }
  }
});
