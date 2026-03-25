<template>
  <div class="report-page">
    <h2>举报与内容审核</h2>

    <div class="report-form" v-if="showForm">
      <h3>举报消息</h3>
      <div class="form-group">
        <label>举报原因</label>
        <select v-model="form.reportReason">
          <option v-for="reason in REPORT_REASONS" :key="reason" :value="reason">{{ reason }}</option>
        </select>
      </div>
      <div class="form-group">
        <label>详细描述</label>
        <textarea v-model="form.description" placeholder="请详细描述违规内容..." rows="4"></textarea>
      </div>
      <div class="form-actions">
        <button @click="submitReport" class="submit-btn">提交举报</button>
        <button @click="showForm = false" class="cancel-btn">取消</button>
      </div>
    </div>

    <div class="my-reports">
      <h3>我的举报记录</h3>
      <div v-if="store.isLoading" class="loading">加载中...</div>
      <div v-else-if="store.myReports.length === 0" class="empty">暂无举报记录</div>
      <div v-else class="reports-list">
        <div v-for="report in store.myReports" :key="report.reportId" class="report-item">
          <div class="report-header">
            <span class="category">{{ report.reportCategory || report.reportReason }}</span>
            <span :class="['status', report.status.toLowerCase()]">{{ getStatusText(report.status) }}</span>
          </div>
          <p class="description">{{ report.description }}</p>
          <small class="time">{{ formatTime(report.createdAt) }}</small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useReportStore } from '../stores/report-store';
import { REPORT_REASONS } from '../types/report-moderation';

const store = useReportStore();
const showForm = ref(false);
const form = ref({ reportReason: REPORT_REASONS[0], description: '' });

onMounted(() => store.loadMyReports());

async function submitReport() {
  await store.submitReport({
    reportedMessageId: 0,
    reportedUserId: 0,
    conversationId: 0,
    conversationType: 'private',
    reportReason: form.value.reportReason,
    reportCategory: form.value.reportReason,
    description: form.value.description,
    evidence: '',
  });
  showForm.value = false;
  form.value.description = '';
  await store.loadMyReports();
}

function getStatusText(status: string): string {
  const map: Record<string, string> = { PENDING: '待处理', REVIEWING: '审核中', RESOLVED: '已处理', DISMISSED: '已驳回', ESCALATED: '已上报' };
  return map[status] || status;
}

function formatTime(time: string): string {
  return new Date(time).toLocaleString('zh-CN');
}
</script>

<style scoped>
.report-page { padding: 16px; max-width: 600px; margin: 0 auto; }
.report-form, .my-reports { margin-bottom: 20px; }
.form-group { margin-bottom: 12px; }
.form-group label { display: block; margin-bottom: 4px; font-weight: bold; }
.form-group select, .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.form-actions { display: flex; gap: 10px; }
.submit-btn { background: #dc3545; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.cancel-btn { background: #6c757d; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.report-item { border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin-bottom: 10px; }
.report-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.category { font-weight: bold; }
.status { padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.status.pending { background: #fff3cd; color: #856404; }
.status.resolved { background: #d4edda; color: #155724; }
.status.dismissed { background: #f8d7da; color: #721c24; }
.description { margin: 4px 0; font-size: 14px; }
.time { color: #888; font-size: 12px; }
.loading, .empty { text-align: center; padding: 20px; color: #888; }
</style>
