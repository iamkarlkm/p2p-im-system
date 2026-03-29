<template>
  <div class="screenshot-notification-page">
    <h2>截屏通知</h2>

    <div v-if="latestEvent" class="screenshot-alert">
      <div class="alert-icon">📸</div>
      <div class="alert-content">
        <p>{{ latestEvent.capturedByUsername }} 在 {{ formatTime(latestEvent.screenshotTime) }} 截屏了</p>
        <small>{{ latestEvent.conversationType === 'private' ? '私聊' : '群聊' }}</small>
      </div>
      <button @click="clearEvent" class="dismiss-btn">关闭</button>
    </div>

    <div class="settings-section">
      <h3>通知设置</h3>
      <div class="settings-list">
        <label>
          <input type="checkbox" v-model="localSettings.enableScreenshotNotification" @change="saveSettings" />
          启用截屏通知
        </label>
        <label>
          <input type="checkbox" v-model="localSettings.notifyOnCapture" @change="saveSettings" />
          截屏时通知对方
        </label>
        <label>
          <input type="checkbox" v-model="localSettings.receiveScreenshotAlerts" @change="saveSettings" />
          接收截屏提醒
        </label>
        <label>
          <input type="checkbox" v-model="localSettings.alertForContacts" @change="saveSettings" />
          联系人截屏提醒
        </label>
        <label>
          <input type="checkbox" v-model="localSettings.alertForGroups" @change="saveSettings" />
          群聊截屏提醒
        </label>
      </div>
    </div>

    <div class="history-section">
      <h3>截屏历史</h3>
      <div v-if="isLoading" class="loading">加载中...</div>
      <div v-else-if="history.length === 0" class="empty">暂无截屏记录</div>
      <div v-else class="history-list">
        <div v-for="event in history" :key="event.eventId" class="history-item">
          <span class="icon">📸</span>
          <div class="info">
            <p>{{ event.capturedByUsername }}</p>
            <small>{{ formatTime(event.screenshotTime) }}</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useScreenshotStore } from '../stores/screenshot-store';
import { ScreenshotSettings } from '../types/screenshot-notification';

const store = useScreenshotStore();
const localSettings = ref<ScreenshotSettings>({ ...store.settings });

onMounted(async () => {
  await store.loadSettings();
  localSettings.value = { ...store.settings };
  await store.loadHistory();
});

async function saveSettings() {
  await store.updateSettings(localSettings.value);
}

function clearEvent() {
  store.clearLatestEvent();
}

function formatTime(time: string): string {
  return new Date(time).toLocaleString('zh-CN');
}
</script>

<style scoped>
.screenshot-notification-page {
  padding: 16px;
  max-width: 600px;
  margin: 0 auto;
}
.screenshot-alert {
  display: flex;
  align-items: center;
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
}
.alert-icon { font-size: 24px; margin-right: 12px; }
.alert-content { flex: 1; }
.dismiss-btn { padding: 4px 12px; background: #ffc107; border: none; border-radius: 4px; cursor: pointer; }
.settings-section, .history-section { margin-bottom: 20px; }
.settings-list label { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; cursor: pointer; }
.history-item { display: flex; align-items: center; gap: 10px; padding: 8px; border-bottom: 1px solid #eee; }
.history-item .icon { font-size: 20px; }
.history-item small { color: #888; }
.loading, .empty { text-align: center; padding: 20px; color: #888; }
</style>
