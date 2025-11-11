import { get, post } from './http';

export interface VideoInfo {
  id: number;
  title: string;
  description: string;
  fileUrl: string;
  coverUrl: string;
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
  fileUrl: string;
  coverUrl?: string;
}

export const videoApi = {
  listAll() {
    return get<VideoInfo[]>('/api/video/list');
  },
  listMine() {
    return get<VideoInfo[]>('/api/video/mylist');
  },
  detail(id: number, increasePlay = false) {
    return get<VideoInfo>(`/api/video/${id}`, { params: { increasePlay } });
  },
  upload(payload: VideoUploadRequest) {
    return post<number>('/api/video/upload', payload);
  },
  like(id: number) {
    return post<void>(`/api/video/${id}/like`);
  },
  batch(ids: number[]) {
    return post<VideoInfo[]>('/api/video/batch', { ids });
  }
};

export default videoApi;
