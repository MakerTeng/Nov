<template>
  <div class="user-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="title">
            <span>为你推荐</span>
            <small>接口：/api/recommend/list</small>
          </div>
          <el-button type="primary" text :loading="props.loading" @click="emit('refresh')">换一换</el-button>
        </div>
      </template>
      <el-empty description="暂无推荐数据" v-if="!recommendations.length" />
      <el-row v-else :gutter="16">
        <el-col :span="8" v-for="video in recommendations" :key="video.id">
          <el-card shadow="hover" class="video-card" @click="gotoPlayer(video.id)">
            <h4>{{ video.title }}</h4>
            <p class="desc">作者：{{ video.uploaderName }}</p>
            <p class="desc">播放量：{{ video.playCount }}</p>
            <p class="desc">点赞数：{{ video.likeCount }}</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import type { VideoInfo } from '@/api/videoApi';
import { useRouter } from 'vue-router';

interface Props {
  recommendations: VideoInfo[];
  loading?: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'refresh'): void }>();
const router = useRouter();

function gotoPlayer(id?: number) {
  if (!id) return;
  router.push({ name: 'video-play', query: { id } });
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  display: flex;
  flex-direction: column;
}

.desc {
  color: #909399;
  margin: 4px 0;
}

.video-card {
  cursor: pointer;
}
</style>
