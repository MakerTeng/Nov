<template>
  <div class="creator-dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <p class="label">上传视频数</p>
          <p class="value">{{ videos.length }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <p class="label">总播放量</p>
          <p class="value">{{ totalPlay }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <p class="label">总点赞数</p>
          <p class="value">{{ totalLike }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <p class="label">近期完播率</p>
          <p class="value">后端待补全</p>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>播放趋势（基于 /api/video/mylist 数据估算）</span>
      </template>
      <div ref="chartRef" class="chart" />
    </el-card>

    <el-table :data="videos" stripe style="width: 100%" size="small">
      <el-table-column prop="title" label="视频标题" min-width="200" />
      <el-table-column prop="playCount" label="播放量" width="120" />
      <el-table-column prop="likeCount" label="点赞数" width="120" />
      <el-table-column prop="createTime" label="上传时间" width="180" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onBeforeUnmount, ref, watch } from 'vue';
import * as echarts from 'echarts/core';
import { BarChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, TitleComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import type { VideoInfo } from '@/api/videoApi';

echarts.use([BarChart, GridComponent, TooltipComponent, TitleComponent, CanvasRenderer]);

interface Props {
  videos: VideoInfo[];
}

const props = defineProps<Props>();
const chartRef = ref<HTMLDivElement>();
let chartInstance: echarts.ECharts | null = null;

const totalPlay = computed(() => props.videos.reduce((sum, v) => sum + (v.playCount || 0), 0));
const totalLike = computed(() => props.videos.reduce((sum, v) => sum + (v.likeCount || 0), 0));

function initChart() {
  if (!chartRef.value) return;
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
  }
  const sorted = [...props.videos].sort((a, b) => (b.playCount || 0) - (a.playCount || 0)).slice(0, 10);
  chartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 40, bottom: 60 },
    xAxis: {
      type: 'category',
      data: sorted.map((item) => item.title || `视频${item.id}`),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: { type: 'value', name: '播放量' },
    series: [
      {
        type: 'bar',
        data: sorted.map((item) => item.playCount || 0),
        itemStyle: { color: '#409eff' }
      }
    ]
  });
}

function disposeChart() {
  chartInstance?.dispose();
  chartInstance = null;
}

watch(
  () => props.videos,
  () => nextTick(() => initChart()),
  { deep: true }
);

onMounted(() => nextTick(() => initChart()));

onBeforeUnmount(disposeChart);
</script>

<style scoped>
.creator-dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.label {
  color: #909399;
  margin: 0;
}

.value {
  font-size: 24px;
  font-weight: 600;
  margin: 8px 0 0;
}

.chart {
  width: 100%;
  height: 320px;
}
</style>
