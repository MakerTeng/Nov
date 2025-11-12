<template>
  <div class="register-page">
    <div class="register-card">
      <h2>创建新账号</h2>
      <p class="subtitle">填写基础信息完成注册</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="可选，便于找回密码" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleSubmit">
            注册
          </el-button>
          <el-button text @click="router.push({ name: 'login' })">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElForm, ElMessage } from 'element-plus';
import userApi, { type RegisterRequest } from '@/api/userApi';
import { useRouter } from 'vue-router';

const router = useRouter();
const formRef = ref<InstanceType<typeof ElForm>>();
const loading = ref(false);

const form = reactive<RegisterRequest>({
  username: '',
  password: '',
  email: ''
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

async function handleSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  try {
    loading.value = true;
    await userApi.register(form);
    ElMessage.success('注册成功，请登录');
    router.push({ name: 'login' });
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #1d2b64, #f8cdda);
}

.register-card {
  width: 420px;
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
