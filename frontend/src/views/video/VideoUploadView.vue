<template>
  <div class="video-upload">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>上传视频</span>
          <small>支持 mp4、mov 等常用格式</small>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="可选" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="视频时长">
          <el-input-number v-model="form.durationSec" :min="1" :max="1800" placeholder="单位：秒" />
        </el-form-item>
        <el-form-item label="封面链接">
          <el-input v-model="form.coverUrl" placeholder="可选，使用已有图片地址" />
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <input type="file" accept="video/*" @change="handleFileChange" />
          <p v-if="fileName" class="file-hint">已选择：{{ fileName }}</p>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交上传</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="my-video-card" :loading="listLoading">
      <template #header>
        <div class="card-header">
          <span>我上传的视频</span>
          <el-button type="primary" text @click="loadMyVideos">刷新</el-button>
        </div>
      </template>
      <el-empty v-if="!myVideos.length && !listLoading" description="暂无上传记录" />
      <div v-else class="video-grid">
        <div v-for="video in myVideos" :key="video.id" class="video-item">
          <img :src="coverOf(video)" :alt="video.title" class="video-thumb" />
          <div class="video-info">
            <h4>{{ video.title }}</h4>
            <p class="meta">播放 {{ video.playCount }} · 点赞 {{ video.likeCount }}</p>
            <div class="actions">
              <el-button size="small" @click="preview(video)">预览</el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(video.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" width="720px" :title="previewVideo?.title || '视频预览'">
      <video v-if="previewVideo" class="preview-player" :src="previewVideo.fileUrl" controls />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElForm, ElMessage, ElMessageBox } from 'element-plus';
import { videoApi, type VideoInfo, type VideoUploadRequest } from '@/api/videoApi';

const form = reactive<Omit<VideoUploadRequest, 'file'>>({
  title: '',
  description: '',
  tags: '',
  durationSec: undefined,
  coverUrl: ''
});

const fileRef = ref<File | null>(null);
const loading = ref(false);
const formRef = ref<InstanceType<typeof ElForm>>();
const listLoading = ref(false);
const myVideos = ref<VideoInfo[]>([]);
const previewVisible = ref(false);
const previewVideo = ref<VideoInfo | null>(null);
const fallbackCover =
  'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180"><rect width="320" height="180" fill="%23283945"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-size="20" fill="white">Video</text></svg>';

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
};

const fileName = computed(() => fileRef.value?.name || '');

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement | null;
  const files = target?.files;
  if (files && files.length) {
    fileRef.value = files[0];
  }
}

async function handleSubmit() {
  if (!formRef.value) return;
  if (!fileRef.value) {
    ElMessage.warning('请选择视频文件');
    return;
  }
  try {
    loading.value = true;
    await videoApi.upload({ ...form, file: fileRef.value });
    ElMessage.success('上传成功');
    form.title = '';
    form.description = '';
    form.tags = '';
    form.durationSec = undefined;
    form.coverUrl = '';
    fileRef.value = null;
    await loadMyVideos();
  } finally {
    loading.value = false;
  }
}

function coverOf(video: VideoInfo) {
  return video.coverUrl || fallbackCover;
}

async function loadMyVideos() {
  try {
    listLoading.value = true;
    myVideos.value = await videoApi.listMine();
  } finally {
    listLoading.value = false;
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('删除后视频文件将被清理，确认继续？', '提示', {
      type: 'warning'
    });
    await videoApi.remove(id);
    ElMessage.success('已删除');
    await loadMyVideos();
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return;
    }
  }
}

function preview(video: VideoInfo) {
  previewVideo.value = video;
  previewVisible.value = true;
}

onMounted(() => {
  loadMyVideos();
});
</script>

<style scoped>
.video-upload {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-hint {
  color: #909399;
  margin-top: 6px;
}

.my-video-card {
  width: 100%;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.video-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.video-thumb {
  width: 100%;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  background: #1f2d3d;
}

.video-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.video-info h4 {
  margin: 0;
  font-size: 16px;
}

.meta {
  margin: 0;
  color: #909399;
}

.actions {
  display: flex;
  gap: 8px;
}

.preview-player {
  width: 100%;
  border-radius: 6px;
}
</style>
