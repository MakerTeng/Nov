<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
        <el-input v-model="keyword" placeholder="按用户名搜索" clearable class="search-input" />
      </div>
    </template>
    <el-table :data="filteredUsers" stripe :loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column label="角色" width="140">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.role)">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-alert
      style="margin-top: 12px"
      type="info"
      :closable="false"
      title="如需封禁/改角色，请在后端 user-service 增加对应接口（当前仓库未提供）。"
    />
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import userApi from '@/api/userApi';
import type { UserProfile } from '@/store/auth';

const loading = ref(false);
const users = ref<UserProfile[]>([]);
const keyword = ref('');

const filteredUsers = computed(() => {
  if (!keyword.value) return users.value;
  return users.value.filter((user) => user.username.toLowerCase().includes(keyword.value.toLowerCase()));
});

function roleTagType(role: string) {
  if (role === 'ADMIN') return 'danger';
  if (role === 'CREATOR') return 'warning';
  return 'info';
}

async function loadUsers() {
  try {
    loading.value = true;
    users.value = await userApi.listAll();
  } catch (error) {
    ElMessage.error('获取用户列表失败，确认当前账号为管理员');
  } finally {
    loading.value = false;
  }
}

onMounted(loadUsers);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input {
  width: 240px;
}
</style>
