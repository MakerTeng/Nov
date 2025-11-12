<template>
  <div class="video-overview">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <el-button type="primary" text @click="loadVideos">刷新</el-button>
        </div>
      </template>
      <el-table :data="videos" stripe :loading="loading" @row-click="handleRowClick">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="uploaderName" label="作者" width="140" />
        <el-table-column prop="playCount" label="播放量" width="120" />
        <el-table-column prop="likeCount" label="点赞" width="120" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上线' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click.stop="preview(row)">播放</el-button>
            <el-button
              v-if="canDelete(row)"
              size="small"
              type="danger"
              plain
              @click.stop="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="drawerVisible" :title="drawerTitle" size="50%">
      <template v-if="selectedVideo">
        <el-descriptions title="视频信息" :column="2" border>
          <el-descriptions-item label="标题">{{ selectedVideo.title }}</el-descriptions-item>
          <el-descriptions-item label="作者">{{ selectedVideo.uploaderName }}</el-descriptions-item>
          <el-descriptions-item label="播放量">{{ selectedVideo.playCount }}</el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ selectedVideo.likeCount }}</el-descriptions-item>
        </el-descriptions>

        <el-alert
          v-if="!isAdmin"
          title="行为日志接口仅管理员可用，创作者可联系后台同学开放数据"
          type="warning"
          :closable="false"
          class="mt-12"
        />

        <div v-else>
          <el-card class="mt-12" :loading="logLoading">
            <template #header>
              <span>行为指标（基于 /api/log/video/{id} 结果即时统计）</span>
            </template>
            <el-row :gutter="16">
              <el-col :span="8" v-for="metric in metrics" :key="metric.label">
                <div class="metric">
                  <p class="metric-label">{{ metric.label }}</p>
                  <p class="metric-value">{{ metric.value }}</p>
                </div>
              </el-col>
            </el-row>
            <el-table :data="videoLogs" size="small" stripe style="margin-top: 12px">
              <el-table-column prop="action" label="行为" width="120" />
              <el-table-column prop="detail" label="详情" />
              <el-table-column prop="userId" label="用户ID" width="120" />
              <el-table-column prop="createTime" label="时间" width="200" />
            </el-table>
          </el-card>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { videoApi, type VideoInfo } from '@/api/videoApi';
import logApi, { type UserBehaviorLog } from '@/api/logApi';
import { useAuthStore } from '@/store/auth';

const authStore = useAuthStore();
const loading = ref(false);
const videos = ref<VideoInfo[]>([]);
const selectedVideo = ref<VideoInfo | null>(null);
const drawerVisible = ref(false);
const logLoading = ref(false);
const videoLogs = ref<UserBehaviorLog[]>([]);

const isAdmin = computed(() => authStore.role === 'ADMIN');
const userId = computed(() => authStore.profile?.id ?? 0);
const title = computed(() => (isAdmin.value ? '全量视频列表' : '我上传的视频'));
const drawerTitle = computed(() => (selectedVideo.value ? `视频详情 - ${selectedVideo.value.title}` : ''));

const metrics = computed(() => {
  const logs = videoLogs.value;
  const playCount = logs.filter((log) => log.action === 'PLAY').length;
  const likeCount = logs.filter((log) => log.action === 'LIKE').length;
  const commentCount = logs.filter((log) => log.action === 'COMMENT').length;
  return [
    { label: '播放行为', value: playCount },
    { label: '点赞行为', value: likeCount },
    { label: '评论行为', value: commentCount }
  ];
});

async function loadVideos() {
  try {
    loading.value = true;
    videos.value = isAdmin.value ? await videoApi.listAll({ page: 1, size: 200 }) : await videoApi.listMine();
  } catch (error) {
    ElMessage.error('获取视频数据失败，请确认权限');
  } finally {
    loading.value = false;
  }
}

async function loadLogs(videoId: number) {
  if (!isAdmin.value) return;
  try {
    logLoading.value = true;
    videoLogs.value = await logApi.listByVideo(videoId);
  } catch (error) {
    ElMessage.error('请求日志数据失败');
  } finally {
    logLoading.value = false;
  }
}

function handleRowClick(row: VideoInfo) {
  selectedVideo.value = row;
  drawerVisible.value = true;
  if (row.id && isAdmin.value) {
    loadLogs(row.id);
  }
}

function preview(row: VideoInfo) {
  selectedVideo.value = row;
  drawerVisible.value = true;
  if (row.id && isAdmin.value) {
    loadLogs(row.id);
  }
}

function canDelete(row: VideoInfo) {
  return isAdmin.value || row.uploaderId === userId.value;
}

async function handleDelete(row: VideoInfo) {
  if (!row.id) return;
  if (!canDelete(row)) {
    ElMessage.warning('只能删除自己上传的视频');
    return;
  }
  try {
    await ElMessageBox.confirm(`确认删除「${row.title}」？该操作不可恢复。`, '提示', { type: 'warning' });
    await videoApi.remove(row.id);
    ElMessage.success('删除成功');
    await loadVideos();
  } catch (error) {
    if (error === 'cancel' || error === 'close') return;
  }
}

onMounted(loadVideos);
</script>

<style scoped>
.video-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metric {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 12px;
}

.metric-label {
  margin: 0;
  color: #909399;
}

.metric-value {
  margin: 4px 0 0;
  font-size: 22px;
  font-weight: 600;
}

.mt-12 {
  margin-top: 12px;
}
</style>
