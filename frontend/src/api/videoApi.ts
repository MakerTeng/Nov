import { API_BASE_URL, del, get, post } from './http';

export interface VideoInfo {
  id: number;
  title: string;
  description: string;
  fileUrl: string;
  coverUrl: string;
  tags?: string;
  durationSec?: number;
  commentCount?: number;
  uploaderId: number;
  uploaderName: string;
  likeCount: number;
  playCount: number;
  status: number;
  createTime: string;
}

export interface VideoUploadRequest {
  title: string;
  description?: string;
  tags?: string;
  durationSec?: number;
  coverUrl?: string;
  file: File;
}

export interface VideoListQuery {
  page?: number;
  size?: number;
}

export function toAbsoluteUrl(url?: string | null) {
  if (!url) {
    return '';
  }
  if (/^https?:\/\//i.test(url)) {
    return url;
  }
  const normalized = url.startsWith('/') ? url : `/${url}`;
  return `${API_BASE_URL}${normalized}`;
}

export function normalizeVideo(video: VideoInfo): VideoInfo {
  return {
    ...video,
    fileUrl: toAbsoluteUrl(video.fileUrl),
    coverUrl: video.coverUrl ? toAbsoluteUrl(video.coverUrl) : video.coverUrl
  };
}

export function normalizeVideos(data?: VideoInfo[]): VideoInfo[] {
  return (data ?? []).map(normalizeVideo);
}

export const videoApi = {
  async listAll(params?: VideoListQuery) {
    const data = await get<VideoInfo[]>('/api/video/list', { params });
    return normalizeVideos(data);
  },
  async listMine() {
    const data = await get<VideoInfo[]>('/api/video/mylist');
    return normalizeVideos(data);
  },
  async detail(id: number, increasePlay = false) {
    const data = await get<VideoInfo>(`/api/video/${id}`, { params: { increasePlay } });
    return normalizeVideo(data);
  },
  upload(payload: VideoUploadRequest) {
    const formData = new FormData();
    formData.append('title', payload.title);
    if (payload.description) formData.append('description', payload.description);
    if (payload.tags) formData.append('tags', payload.tags);
    if (payload.durationSec != null) formData.append('durationSec', String(payload.durationSec));
    if (payload.coverUrl) formData.append('coverUrl', payload.coverUrl);
    formData.append('file', payload.file);
    return post<number>('/api/video/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
  like(id: number) {
    return post<void>(`/api/video/${id}/like`);
  },
  async batch(ids: number[]) {
    const data = await post<VideoInfo[]>('/api/video/batch', { ids });
    return normalizeVideos(data);
  },
  remove(id: number) {
    return del<void>(`/api/video/${id}`);
  }
};

export default videoApi;
