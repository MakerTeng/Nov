<template>
  <div class="user-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>为你推荐</span>
          <small>接口：/api/recommend/list</small>
        </div>
      </template>
      <el-empty description="暂无推荐数据" v-if="!recommendations.length" />
      <el-row v-else :gutter="16">
        <el-col :span="8" v-for="video in recommendations" :key="video.id">
          <el-card shadow="hover">
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

interface Props {
  recommendations: VideoInfo[];
}

defineProps<Props>();
</script>

<style scoped>
.card-header {
  display: flex;
  flex-direction: column;
}

.desc {
  color: #909399;
  margin: 4px 0;
}
</style>
