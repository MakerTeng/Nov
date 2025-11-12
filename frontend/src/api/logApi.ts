import { get, post } from './http';

export interface LogCollectRequest {
  videoId: number;
  action: string;
  detail?: string;
}

export interface UserBehaviorLog {
  id: number;
  userId: number;
  videoId: number;
  action: string;
  detail: string;
  createTime: string;
}

export interface VideoActionRequest {
  videoId: number;
  watchTime?: number;
  liked?: boolean;
  commented?: boolean;
}

export const logApi = {
  collect(payload: LogCollectRequest) {
    return post<number>('/api/log/collect', payload);
  },
  listByUser(userId: number) {
    return get<UserBehaviorLog[]>(`/api/log/user/${userId}`);
  },
  listByVideo(videoId: number) {
    return get<UserBehaviorLog[]>(`/api/log/video/${videoId}`);
  },
  reportVideoAction(payload: VideoActionRequest) {
    return post<number>('/api/log/video/action', payload);
  }
};

export default logApi;
