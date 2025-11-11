<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>行为日志查询（仅管理员可调用 /api/log/user /api/log/video）</span>
      </div>
    </template>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="按用户" name="user">
        <el-form inline :model="userForm" class="query-form">
          <el-form-item label="用户ID">
            <el-input v-model="userForm.userId" placeholder="请输入用户ID" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUserQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="按视频" name="video">
        <el-form inline :model="videoForm" class="query-form">
          <el-form-item label="视频ID">
            <el-input v-model="videoForm.videoId" placeholder="请输入视频ID" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleVideoQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <el-table :data="logs" stripe :loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="120" />
      <el-table-column prop="videoId" label="视频ID" width="120" />
      <el-table-column prop="action" label="行为" width="120" />
      <el-table-column prop="detail" label="详情" />
      <el-table-column prop="createTime" label="时间" width="200" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import logApi, { type UserBehaviorLog } from '@/api/logApi';

const loading = ref(false);
const logs = ref<UserBehaviorLog[]>([]);
const activeTab = ref('user');
const userForm = ref({ userId: '' });
const videoForm = ref({ videoId: '' });

async function handleUserQuery() {
  if (!userForm.value.userId) {
    ElMessage.warning('请输入用户ID');
    return;
  }
  await fetchLogs('user');
}

async function handleVideoQuery() {
  if (!videoForm.value.videoId) {
    ElMessage.warning('请输入视频ID');
    return;
  }
  await fetchLogs('video');
}

async function fetchLogs(type: 'user' | 'video') {
  try {
    loading.value = true;
    logs.value =
      type === 'user'
        ? await logApi.listByUser(Number(userForm.value.userId))
        : await logApi.listByVideo(Number(videoForm.value.videoId));
  } catch (error) {
    ElMessage.error('查询日志失败，请确认当前账号具备管理员权限');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.card-header {
  font-weight: 600;
}

.query-form {
  margin-bottom: 16px;
}
</style>
