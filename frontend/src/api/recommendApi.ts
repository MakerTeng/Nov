import { get } from './http';
import type { VideoInfo } from './videoApi';
import { normalizeVideos } from './videoApi';

export const recommendApi = {
  listForCurrent(limit = 10) {
    return get<VideoInfo[]>('/api/recommend/list', { params: { limit } }).then(normalizeVideos);
  },
  listForUser(userId: number, limit = 10) {
    return get<VideoInfo[]>(`/api/recommend/admin/${userId}`, { params: { limit } }).then(normalizeVideos);
  },
  topN(limit = 10) {
    return get<VideoInfo[]>('/api/recommend/topN', { params: { limit } }).then(normalizeVideos);
  }
};

export default recommendApi;
