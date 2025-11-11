<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>推荐结果调试</span>
        <el-tag type="info">所有请求均通过 /api/recommend/** 网关</el-tag>
      </div>
    </template>
    <el-form :model="form" inline class="query-form">
      <el-form-item label="用户ID">
        <el-input v-model="form.userId" placeholder="管理员可指定用户" :disabled="!isAdmin" />
      </el-form-item>
      <el-form-item label="数量">
        <el-input-number v-model="form.limit" :min="1" :max="20" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">获取推荐</el-button>
      </el-form-item>
    </el-form>
    <el-alert
      :title="alertText"
      type="info"
      :closable="false"
      style="margin-bottom: 12px"
    />
    <el-table :data="recommendations" :loading="loading" stripe>
      <el-table-column prop="id" label="视频ID" width="100" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="uploaderName" label="作者" width="140" />
      <el-table-column prop="playCount" label="播放量" width="120" />
      <el-table-column prop="likeCount" label="点赞" width="120" />
      <el-table-column label="行为日志" width="160">
        <template #default="{ row }">
          <el-button
            size="small"
            :disabled="!isAdmin"
            @click="() => openLogDialog(row.id)"
          >
            查看日志
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" width="60%" title="推荐视频行为日志">
    <el-table :data="dialogLogs" stripe size="small" :loading="dialogLoading">
      <el-table-column prop="userId" label="用户ID" width="120" />
      <el-table-column prop="action" label="行为" width="120" />
      <el-table-column prop="detail" label="详情" />
      <el-table-column prop="createTime" label="时间" width="200" />
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import recommendApi from '@/api/recommendApi';
import logApi, { type UserBehaviorLog } from '@/api/logApi';
import type { VideoInfo } from '@/api/videoApi';
import { useAuthStore } from '@/store/auth';

const authStore = useAuthStore();
const isAdmin = computed(() => authStore.role === 'ADMIN');
const recommendations = ref<VideoInfo[]>([]);
const loading = ref(false);
const form = reactive({ userId: '', limit: 10 });
const alertText = computed(() =>
  isAdmin.value
    ? '管理员可通过 /api/recommend/admin/{userId} 指定任意用户调试推荐策略'
    : '当前角色仅能查看自己的推荐结果，如需调试他人请联系管理员开放接口'
);

const dialogVisible = ref(false);
const dialogLoading = ref(false);
const dialogLogs = ref<UserBehaviorLog[]>([]);

async function handleQuery() {
  try {
    loading.value = true;
    if (isAdmin.value && form.userId) {
      recommendations.value = await recommendApi.listForUser(Number(form.userId), form.limit);
    } else {
      recommendations.value = await recommendApi.listForCurrent(form.limit);
      if (isAdmin.value && !form.userId) {
        ElMessage.info('未填写用户ID，默认使用当前管理员身份进行推荐');
      }
    }
  } catch (error) {
    ElMessage.error('获取推荐数据失败，请确认登录态');
  } finally {
    loading.value = false;
  }
}

async function openLogDialog(videoId: number) {
  if (!isAdmin.value) {
    ElMessage.warning('仅管理员可查看日志');
    return;
  }
  dialogVisible.value = true;
  try {
    dialogLoading.value = true;
    dialogLogs.value = await logApi.listByVideo(videoId);
  } catch (error) {
    ElMessage.error('拉取日志失败');
  } finally {
    dialogLoading.value = false;
  }
}

handleQuery();
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 12px;
}
</style>
