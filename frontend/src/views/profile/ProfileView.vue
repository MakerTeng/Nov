<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div class="card-actions">
            <el-button text @click="handleUpload">上传视频</el-button>
            <el-button type="primary" text @click="loadProfile">刷新</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ profile?.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ profile?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ resolveRoleLabel(profile?.role) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="profile?.status === 1 ? 'success' : 'danger'">
            {{ profile?.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ profile?.id }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <template #header>
        <span>历史观看记录</span>
      </template>
      <el-alert
        v-if="historyError"
        type="warning"
        :closable="false"
        title="获取历史行为失败，请稍后重试或检查 log-service。"
      />
      <el-table v-else :data="historyLogs" size="small" stripe>
        <el-table-column prop="videoId" label="视频ID" width="120" />
        <el-table-column prop="action" label="行为" width="120" />
        <el-table-column prop="detail" label="详情" />
        <el-table-column prop="createTime" label="时间" width="200" />
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <span>我的推荐</span>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="item in recommendations" :key="item.id">
          <el-card shadow="hover">
            <h4>{{ item.title }}</h4>
            <p class="desc">作者：{{ item.uploaderName }}</p>
            <p class="desc">播放量：{{ item.playCount }}</p>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!recommendations.length" description="暂无推荐" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import userApi from '@/api/userApi';
import { recommendApi } from '@/api/recommendApi';
import logApi, { type UserBehaviorLog } from '@/api/logApi';
import type { UserProfile } from '@/store/auth';
import type { VideoInfo } from '@/api/videoApi';
import { useAuthStore } from '@/store/auth';
import { ROLE_LABELS } from '@/utils/roles';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();
const profile = ref<UserProfile | null>(authStore.profile);
const historyLogs = ref<UserBehaviorLog[]>([]);
const recommendations = ref<VideoInfo[]>([]);
const historyError = ref(false);

function resolveRoleLabel(role?: string | null) {
  if (!role) {
    return '--';
  }
  return ROLE_LABELS[role] || role;
}

async function loadProfile() {
  try {
    const data = await userApi.profile();
    profile.value = data;
    authStore.updateProfile(data);
  } catch (error) {
    ElMessage.error('获取个人信息失败');
  }
}

async function loadHistory() {
  if (!profile.value?.id) return;
  try {
    historyLogs.value = await logApi.listByUser(profile.value.id);
    historyError.value = false;
  } catch (error) {
    historyError.value = true;
    console.warn('获取历史行为失败', error);
  }
}

async function loadRecommendations() {
  try {
    recommendations.value = await recommendApi.listForCurrent(6);
  } catch (error) {
    ElMessage.error('获取推荐失败');
  }
}

function handleUpload() {
  router.push({ name: 'video-upload' });
}

onMounted(() => {
  loadProfile();
  loadHistory();
  loadRecommendations();
});
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.desc {
  color: #909399;
  margin: 0;
}
</style>
