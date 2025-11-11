import { get, post } from './http';
import type { MenuItem, UserProfile } from '@/store/auth';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  profile: UserProfile;
  menus: MenuItem[];
}

export interface RegisterRequest {
  username: string;
  password: string;
  role?: string;
}

export const userApi = {
  login(payload: LoginRequest) {
    return post<LoginResponse>('/api/user/login', payload);
  },
  register(payload: RegisterRequest) {
    return post<number>('/api/user/register', payload);
  },
  profile() {
    return get<UserProfile>('/api/user/profile');
  },
  menus() {
    return get<MenuItem[]>('/api/user/menu');
  },
  listAll() {
    return get<UserProfile[]>('/api/user/list');
  }
};

export default userApi;
