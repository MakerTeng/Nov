<template>
<div class="dashboard-home">
    <component
      :is="currentComponent"
      v-bind="componentProps"
      :loading="loading"
      @refresh="handleRefresh"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useAuthStore } from '@/store/auth';
import AdminDashboard from './AdminDashboard.vue';
import CreatorDashboard from './CreatorDashboard.vue';
import UserDashboard from './UserDashboard.vue';
import userApi from '@/api/userApi';
import { videoApi } from '@/api/videoApi';
import { recommendApi } from '@/api/recommendApi';
import type { VideoInfo } from '@/api/videoApi';
import type { UserProfile } from '@/store/auth';
import { ElMessage } from 'element-plus';

const authStore = useAuthStore();
const loading = ref(false);
const adminUsers = ref<UserProfile[]>([]);
const adminVideos = ref<VideoInfo[]>([]);
const creatorVideos = ref<VideoInfo[]>([]);
const userRecommendations = ref<VideoInfo[]>([]);

const role = computed(() => authStore.role);

const currentComponent = computed(() => {
  if (role.value === 'ADMIN') return AdminDashboard;
  if (role.value === 'CREATOR') return CreatorDashboard;
  return UserDashboard;
});

const componentProps = computed(() => {
  if (role.value === 'ADMIN') {
    return {
      users: adminUsers.value,
      videos: adminVideos.value
    };
  }
  if (role.value === 'CREATOR') {
    return { videos: creatorVideos.value };
  }
  return { recommendations: userRecommendations.value };
});

async function loadAdminData() {
  adminUsers.value = await userApi.listAll();
  adminVideos.value = await videoApi.listAll({ page: 1, size: 200 });
}

async function loadCreatorData() {
  creatorVideos.value = await videoApi.listMine();
}

async function loadUserData() {
  userRecommendations.value = await recommendApi.listForCurrent(8);
}

async function loadData() {
  loading.value = true;
  try {
    if (role.value === 'ADMIN') {
      await loadAdminData();
    } else if (role.value === 'CREATOR') {
      await loadCreatorData();
    } else {
      await loadUserData();
    }
  } catch (error) {
    ElMessage.error('加载仪表盘数据失败，请检查后端接口');
    console.error(error);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
watch(role, loadData);

function handleRefresh() {
  loadData();
}
</script>

<style scoped>
.dashboard-home {
  min-height: calc(100vh - 120px);
}
</style>
