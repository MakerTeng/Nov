<template>
  <div class="login-page">
    <div class="login-card">
      <h2>抖音风格运营平台</h2>
      <p class="subtitle">统一身份登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleSubmit">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElForm, ElMessage } from 'element-plus';
import userApi, { type LoginRequest } from '@/api/userApi';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const form = reactive<LoginRequest>({
  username: '',
  password: ''
});

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const loading = ref(false);
const formRef = ref<InstanceType<typeof ElForm>>();

const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  try {
    loading.value = true;
    const data = await userApi.login(form);
    authStore.setAuth({ token: data.token, profile: data.profile, menus: data.menus });
    ElMessage.success('登录成功');
    const redirect = (route.query.redirect as string) || '/dashboard';
    router.replace(redirect);
  } catch (error) {
    console.warn('登录失败', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #141e30, #243b55);
}

.login-card {
  width: 360px;
  background: #fff;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.25);
}

.subtitle {
  color: #909399;
  margin-top: -8px;
  margin-bottom: 24px;
}

.submit-btn {
  width: 100%;
}
</style>
