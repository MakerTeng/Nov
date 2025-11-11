<template>
  <el-menu
    class="side-menu"
    :default-active="activePath"
    background-color="#001529"
    text-color="#fff"
    active-text-color="#ffd04b"
    router
  >
    <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
      <span>{{ item.name }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import type { MenuItem } from '@/store/auth';

interface Props {
  menuItems: MenuItem[];
}

const props = defineProps<Props>();
const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.side-menu {
  height: 100vh;
  border-right: none;
}
</style>
