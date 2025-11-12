<template>
  <div class="video-player-page" v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card v-if="currentVideo">
          <template #header>
            <div class="card-header">
              <div>
                <h3>{{ currentVideo.title }}</h3>
                <p class="meta">作者：{{ currentVideo.uploaderName }} · 播放 {{ currentVideo.playCount }}</p>
              </div>
              <div class="actions">
                <el-button type="primary" @click="handleLike">点赞</el-button>
                <el-button @click="handleComment">已留言</el-button>
              </div>
            </div>
          </template>
          <video
            ref="playerRef"
            class="video-player"
            :src="currentVideo.fileUrl"
            controls
            @timeupdate="handleTimeUpdate"
            @ended="handleEnded"
          />
          <el-alert
            v-if="actionMessage"
            :title="actionMessage"
            type="success"
            :closable="false"
            class="mt-12"
          />
        </el-card>
        <el-empty v-else description="暂无可播放视频" />
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>视频列表</span>
          </template>
          <div class="recommend-grid">
            <div
              v-for="item in videos"
              :key="item.id"
              class="recommend-card"
              :class="{ active: currentVideo && currentVideo.id === item.id }"
              @click="switchVideo(item)"
            >
              <img :src="coverOf(item)" :alt="item.title" />
              <div class="info">
                <p class="title">{{ item.title }}</p>
                <p class="desc">作者：{{ item.uploaderName }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { recommendApi } from '@/api/recommendApi';
import { videoApi, type VideoInfo } from '@/api/videoApi';
import { useRoute, useRouter } from 'vue-router';
import logApi from '@/api/logApi';

const route = useRoute();
const router = useRouter();
const videos = ref<VideoInfo[]>([]);
const currentVideo = ref<VideoInfo | null>(null);
const playerRef = ref<HTMLVideoElement>();
const loading = ref(false);
const watchSeconds = ref(0);
const actionMessage = ref('');
const fallbackCover =
  'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="240" height="135"><rect width="240" height="135" fill="%23283945"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-size="18" fill="white">Video</text></svg>';

const LIST_LIMIT = 8;

async function refreshSidebar(targetId?: number, keepCurrent = false) {
  loading.value = true;
  try {
    let list = await recommendApi.listForCurrent(LIST_LIMIT);
    if (!list.length) {
      list = await videoApi.listAll({ page: 1, size: LIST_LIMIT });
    }
    list = list.slice(0, LIST_LIMIT);
    if (targetId) {
      const existing = list.find((item) => item.id === targetId);
      if (!existing) {
        try {
          const detail = await videoApi.detail(targetId, false);
          list = [detail, ...list.filter((item) => item.id !== detail.id)];
        } catch {
          /* ignore */
        }
      } else {
        list = [existing, ...list.filter((item) => item.id !== existing.id)];
      }
    }
    videos.value = list.slice(0, LIST_LIMIT);
    if (!keepCurrent) {
      currentVideo.value = videos.value[0] ?? null;
    }
  } finally {
    loading.value = false;
  }
}

function handleTimeUpdate() {
  const player = playerRef.value;
  if (!player) return;
  watchSeconds.value = Math.floor(player.currentTime);
}

async function reportAction(flags: { liked?: boolean; commented?: boolean }) {
  if (!currentVideo.value) return;
  try {
    await logApi.reportVideoAction({
      videoId: currentVideo.value.id,
      watchTime: watchSeconds.value,
      liked: flags.liked,
      commented: flags.commented
    });
    actionMessage.value = '行为已上报，感谢反馈';
    setTimeout(() => (actionMessage.value = ''), 2500);
  } catch {
    /* 已在 http 拦截器提示 */
  }
}

function handleEnded() {
  reportAction({ liked: false, commented: false });
}

function handleLike() {
  reportAction({ liked: true });
}

function handleComment() {
  reportAction({ commented: true });
}

function switchVideo(video: VideoInfo) {
  if (!video.id) return;
  if (currentVideo.value && watchSeconds.value > 0 && currentVideo.value.id !== video.id) {
    reportAction({ liked: false, commented: false });
  }
  currentVideo.value = video;
  watchSeconds.value = 0;
  actionMessage.value = '';
  playerRef.value?.load();
  router.replace({ name: 'video-play', query: { id: video.id } });
  refreshSidebar(video.id, true);
}

function coverOf(video: VideoInfo) {
  return video.coverUrl || fallbackCover;
}

onMounted(() => {
  const target = Number(route.query.id);
  refreshSidebar(Number.isNaN(target) ? undefined : target);
});
onBeforeUnmount(() => {
  if (currentVideo.value && watchSeconds.value > 0) {
    reportAction({ liked: false, commented: false });
  }
});
</script>

<style scoped>
.video-player-page {
  min-height: calc(100vh - 120px);
}

.video-player {
  width: 100%;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta {
  margin: 0;
  color: #909399;
}

.actions {
  display: flex;
  gap: 8px;
}

.recommend-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 520px;
  overflow-y: auto;
}

.recommend-card {
  display: flex;
  gap: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;
}

.recommend-card.active {
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff inset;
}

.recommend-card img {
  width: 120px;
  height: 68px;
  object-fit: cover;
  background: #1f2d3d;
}

.recommend-card .info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.recommend-card .title {
  margin: 0;
  font-weight: 600;
}

.recommend-card .desc {
  margin: 4px 0 0;
  color: #909399;
  font-size: 12px;
}

.mt-12 {
  margin-top: 12px;
}
</style>
