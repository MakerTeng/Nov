<template>
  <el-container class="app-layout">
    <el-aside width="220px" class="app-layout__sider">
      <div class="app-layout__logo">抖音风格运营控制台</div>
      <SideMenu :menu-items="menus" />
    </el-aside>
    <el-container>
      <el-header class="app-layout__header">
        <div class="header-left">{{ currentTitle }}</div>
        <div class="header-right">
          <el-tag type="info" class="role-tag">{{ roleLabel }}</el-tag>
          <span class="username">{{ authStore.profile?.username }}</span>
          <el-button text type="primary" @click="router.push('/profile')">个人中心</el-button>
          <el-button text type="danger" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="app-layout__content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import SideMenu from './SideMenu.vue';
import { useAuthStore } from '@/store/auth';
import { ROLE_LABELS, resolveMenus } from '@/utils/roles';
import userApi from '@/api/userApi';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const menus = ref(resolveMenus(authStore.role, authStore.menus));

const currentTitle = computed(() => route.meta.title ?? '运营后台');
const roleLabel = computed(() => ROLE_LABELS[authStore.role] || '访客');

async function fetchMenus() {
  try {
    const remoteMenus = await userApi.menus();
    authStore.setMenus(remoteMenus);
    menus.value = resolveMenus(authStore.role, remoteMenus);
  } catch (error) {
    console.warn('拉取菜单失败，使用默认配置', error);
    menus.value = resolveMenus(authStore.role, authStore.menus);
  }
}

function handleLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      authStore.logout();
      router.replace({ name: 'login' });
    })
    .catch(() => undefined);
}

onMounted(() => {
  if (!authStore.menus.length) {
    fetchMenus();
  }
});

watch(
  () => authStore.role,
  (role) => {
    menus.value = resolveMenus(role, authStore.menus);
  }
);
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
}

.app-layout__sider {
  background: #001529;
  color: #fff;
}

.app-layout__logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.app-layout__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.12);
  margin-bottom: 8px;
}

.header-left {
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-tag {
  text-transform: uppercase;
}

.username {
  font-weight: 500;
}

.app-layout__content {
  background: #f5f6fa;
  padding: 16px;
}
</style>
