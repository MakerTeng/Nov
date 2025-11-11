import axios, {
  AxiosError,
  AxiosHeaders,
  AxiosRequestConfig,
  type AxiosResponse
} from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import pinia from '@/store';
import { useAuthStore } from '@/store/auth';

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
});

function readTokenFromStorage(): string | null {
  try {
    const cached = localStorage.getItem('company-frontend-auth');
    if (!cached) {
      return null;
    }
    const parsed = JSON.parse(cached) as { token?: string };
    return parsed.token ?? null;
  } catch {
    return null;
  }
}

http.interceptors.request.use((config) => {
  const authStore = useAuthStore(pinia);
  const token = authStore.token || readTokenFromStorage();
  const headers = new AxiosHeaders(config.headers ?? {});
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  headers.set('Accept', 'application/json');
  headers.set('Content-Type', 'application/json');
  config.headers = headers;
  return config;
});

http.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<unknown>>) => {
    const payload = response.data;
    if (payload.code !== 0) {
      ElMessage.error(payload.message || '请求失败');
      return Promise.reject(payload);
    }
    return response;
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const status = error.response?.status;
    if (status === 401 || status === 403) {
      const authStore = useAuthStore(pinia);
      authStore.logout();
      ElMessage.warning('登录已过期，请重新登录');
      router.replace({ name: 'login' });
    } else {
      const message = error.response?.data?.message || error.message || '请求异常';
      ElMessage.error(message);
    }
    return Promise.reject(error);
  }
);

export async function get<T>(url: string, config?: AxiosRequestConfig) {
  const response = await http.get<ApiResponse<T>>(url, config);
  return (response.data.data ?? null) as T;
}

export async function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig) {
  const response = await http.post<ApiResponse<T>>(url, data, config);
  return (response.data.data ?? null) as T;
}

export default http;
