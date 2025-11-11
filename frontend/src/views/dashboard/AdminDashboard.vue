<template>
  <div class="admin-dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ card.value }}</div>
          <p class="desc">{{ card.desc }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section">
      <template #header>
        <div class="card-header">
          <span>视频表现榜</span>
          <small>数据来源：/api/video/list</small>
        </div>
      </template>
      <el-table :data="topVideos" stripe style="width: 100%" size="small">
        <el-table-column prop="title" label="视频标题" min-width="200" />
        <el-table-column prop="uploaderName" label="作者" width="120" />
        <el-table-column prop="playCount" label="播放量" width="120" />
        <el-table-column prop="likeCount" label="点赞数" width="120" />
        <el-table-column prop="statusText" label="状态" width="120" />
      </el-table>
    </el-card>

    <el-alert type="info" :closable="false" class="todo-alert">
      后端目前尚未提供活跃用户、服务健康度等指标的接口，可在后续迭代中由 log-service / gateway 暴露统计 API 后再补齐。
    </el-alert>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { VideoInfo } from '@/api/videoApi';
import type { UserProfile } from '@/store/auth';

interface Props {
  users: UserProfile[];
  videos: VideoInfo[];
  loading?: boolean;
}

const props = defineProps<Props>();

const statCards = computed(() => [
  {
    label: '注册用户数',
    value: props.users.length,
    desc: '数据来源 /api/user/list'
  },
  {
    label: '视频总数',
    value: props.videos.length,
    desc: '数据来源 /api/video/list'
  },
  {
    label: '管理员数量',
    value: props.users.filter((u) => u.role === 'ADMIN').length,
    desc: '基于用户角色统计'
  },
  {
    label: '创作者数量',
    value: props.users.filter((u) => u.role === 'CREATOR').length,
    desc: '基于用户角色统计'
  }
]);

const topVideos = computed(() =>
  props.videos
    .map((video) => ({
      ...video,
      statusText: video.status === 1 ? '已发布' : '未上线'
    }))
    .sort((a, b) => (b.playCount || 0) - (a.playCount || 0))
    .slice(0, 8)
);
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-card {
  min-height: 140px;
}

.stat-card .label {
  color: #909399;
}

.stat-card .value {
  font-size: 28px;
  font-weight: 600;
  margin-top: 8px;
}

.section {
  background: #fff;
}

.card-header {
  display: flex;
  flex-direction: column;
  font-weight: 600;
}

.card-header small {
  font-weight: normal;
  color: #909399;
}

.todo-alert {
  margin-top: 8px;
}
</style>
