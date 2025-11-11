import { get } from './http';
import type { VideoInfo } from './videoApi';

export const recommendApi = {
  listForCurrent(limit = 10) {
    return get<VideoInfo[]>('/api/recommend/list', { params: { limit } });
  },
  listForUser(userId: number, limit = 10) {
    return get<VideoInfo[]>(`/api/recommend/admin/${userId}`, { params: { limit } });
  }
};

export default recommendApi;
