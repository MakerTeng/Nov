<template>
  <div class="no-access">
    <el-result icon="warning" title="暂无权限" sub-title="请联系管理员开通对应菜单或使用有权限的账号。">
      <template #extra>
        <el-button type="primary" @click="goHome">回到首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();
const goHome = () => router.push('/dashboard');
</script>

<style scoped>
.no-access {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
</style>
