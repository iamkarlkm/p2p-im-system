<template>
  <div class="device-management">
    <div class="page-header">
      <h2>设备管理</h2>
      <div class="header-actions">
        <button class="btn-icon" @click="refresh" title="刷新">
          🔄
        </button>
        <button class="btn-primary" @click="showLoginHistory = !showLoginHistory">
          {{ showLoginHistory ? '设备列表' : '登录历史' }}
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div v-if="stats" class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📱</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalDevices }}</span>
          <span class="stat-label">总设备数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.activeDevices }}</span>
          <span class="stat-label">活跃设备</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🔐</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.trustedDevices }}</span>
          <span class="stat-label">可信设备</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">{{ deviceService.getDeviceIcon(stats.mostUsedDeviceType) }}</div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.mostUsedDeviceType }}</span>
          <span class="stat-label">常用类型</span>
        </div>
      </div>
    </div>

    <!-- Alerts -->
    <div v-if="alerts.length > 0" class="alerts-section">
      <div v-for="alert in alerts" :key="alert.deviceId" class="alert-item alert-warning">
        <span class="alert-icon">⚠️</span>
        <div class="alert-content">
          <strong>{{ getAlertTitle(alert.type) }}</strong>
          <p>{{ alert.deviceName }} · {{ alert.ipAddress }} · {{ alert.location }}</p>
          <small>{{ formatTime(alert.timestamp) }}</small>
        </div>
        <button class="btn-small" @click="handleAcknowledge(alert.deviceId)">确认</button>
      </div>
    </div>

    <!-- Device List -->
    <div v-if="!showLoginHistory" class="device-list">
      <div v-if="store.isLoading" class="loading">加载中...</div>
      <div v-else-if="store.error" class="error-msg">{{ store.error }}</div>
      <div v-else>
        <div class="device-item" v-for="device in devices" :key="device.id"
          :class="{ current: device.isCurrent, inactive: !device.isActive }">
          <div class="device-icon">{{ deviceService.getDeviceIcon(device.deviceType) }}</div>
          <div class="device-info">
            <div class="device-name">
              {{ device.deviceName }}
              <span v-if="device.isCurrent" class="badge-current">当前</span>
              <span v-if="device.isTrusted" class="badge-trusted">可信</span>
              <span v-if="!device.isActive" class="badge-inactive">已离线</span>
            </div>
            <div class="device-detail">{{ device.deviceModel || device.deviceType }}</div>
            <div class="device-meta">
              {{ device.ipAddress || '未知IP' }} · {{ device.location || '未知位置' }}
            </div>
            <div class="device-meta">
              最后活跃: {{ deviceService.formatLastActive(device.lastActiveAt) }}
            </div>
          </div>
          <div class="device-actions">
            <button v-if="!device.isCurrent && device.isActive" class="btn-small" @click="handleSetCurrent(device.id)">
              设为当前
            </button>
            <button v-if="!device.isTrusted && device.isActive" class="btn-small btn-primary"
              @click="handleTrust(device.id)">
              信任
            </button>
            <button v-if="device.isTrusted && device.isActive" class="btn-small" @click="handleUntrust(device.id)">
              取消信任
            </button>
            <button v-if="device.isActive && !device.isCurrent" class="btn-small btn-danger"
              @click="handleDeactivate(device.id)">
              注销
            </button>
          </div>
        </div>
        <div v-if="devices.length === 0" class="empty-state">
          暂无设备记录
        </div>
      </div>
    </div>

    <!-- Login History -->
    <div v-else class="login-history">
      <div v-if="store.isLoading" class="loading">加载中...</div>
      <div v-else-if="loginHistory">
        <div class="history-item" v-for="entry in loginHistory.items" :key="entry.id">
          <div class="history-icon">{{ deviceService.getDeviceIcon(entry.deviceType) }}</div>
          <div class="history-info">
            <div class="history-title">
              {{ entry.action === 'LOGIN' ? '登录' : entry.action === 'LOGOUT' ? '退出' : '移除' }}
              <span :class="'status-' + entry.loginStatus.toLowerCase()">{{ entry.loginStatus }}</span>
            </div>
            <div class="history-meta">
              {{ entry.deviceName }} · {{ entry.ipAddress }}
            </div>
            <div class="history-meta">{{ entry.location || '未知位置' }}</div>
            <div class="history-time">{{ formatTime(entry.loginTime) }}</div>
          </div>
        </div>
        <div v-if="loginHistory.items.length === 0" class="empty-state">
          暂无登录记录
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useDeviceStore } from '../stores/device-store';
import { deviceService } from '../services/device-service';
import type { DeviceAlert, DeviceType } from '../types/device';

const store = useDeviceStore();
const showLoginHistory = ref(false);

const devices = computed(() => store.devices);
const stats = computed(() => store.stats);
const alerts = computed(() => store.alerts);
const loginHistory = computed(() => store.loginHistory);

onMounted(async () => {
  await Promise.all([
    store.fetchDevices(),
    store.fetchStats(),
    store.fetchAlerts(),
  ]);
});

async function refresh() {
  await Promise.all([
    store.fetchDevices(),
    store.fetchStats(),
    store.fetchAlerts(),
    showLoginHistory.value ? store.fetchLoginHistory() : Promise.resolve(),
  ]);
}

function getAlertTitle(type: string): string {
  const titles: Record<string, string> = {
    NEW_DEVICE: '新设备登录',
    SUSPICIOUS_LOGIN: '可疑登录',
    UNKNOWN_LOCATION: '未知位置登录',
    MULTIPLE_FAILURES: '多次登录失败',
  };
  return titles[type] || '设备告警';
}

function formatTime(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleString('zh-CN');
}

async function handleAcknowledge(deviceId: number) {
  await store.acknowledgeAlert(deviceId);
}

async function handleSetCurrent(deviceId: number) {
  await store.setCurrentDevice(deviceId);
}

async function handleTrust(deviceId: number) {
  await store.trustDevice(deviceId);
}

async function handleUntrust(deviceId: number) {
  await deviceService.untrustDevice(deviceId);
  await store.fetchDevices();
}

async function handleDeactivate(deviceId: number) {
  if (confirm('确定要注销该设备吗？')) {
    await store.deactivateDevice(deviceId);
  }
}
</script>

<style scoped>
.device-management {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

.alerts-section {
  margin-bottom: 16px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
}

.alert-warning {
  background: #fef3c7;
  border: 1px solid #f59e0b;
}

.alert-icon {
  font-size: 20px;
}

.alert-content {
  flex: 1;
}

.alert-content strong {
  display: block;
  margin-bottom: 4px;
}

.alert-content p {
  margin: 0;
  font-size: 13px;
}

.alert-content small {
  color: #64748b;
}

.device-list,
.login-history {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.device-item,
.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.device-item:last-child,
.history-item:last-child {
  border-bottom: none;
}

.device-item.current {
  background: #eff6ff;
}

.device-item.inactive {
  opacity: 0.6;
}

.device-icon,
.history-icon {
  font-size: 28px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border-radius: 8px;
}

.device-info,
.history-info {
  flex: 1;
}

.device-name {
  font-weight: 600;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.device-detail {
  font-size: 13px;
  color: #475569;
  margin-top: 2px;
}

.device-meta,
.history-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.history-title {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.history-time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.badge-current {
  background: #3b82f6;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 400;
}

.badge-trusted {
  background: #10b981;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 400;
}

.badge-inactive {
  background: #94a3b8;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 400;
}

.status-success {
  color: #10b981;
}

.status-failed {
  color: #ef4444;
}

.device-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-small {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 4px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.btn-small:hover {
  background: #e2e8f0;
}

.btn-danger {
  background: #fee2e2;
  color: #ef4444;
  border-color: #fecaca;
}

.btn-danger:hover {
  background: #fecaca;
}

.btn-icon {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  width: 36px;
  height: 36px;
  cursor: pointer;
  font-size: 16px;
}

.loading,
.empty-state {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}

.error-msg {
  color: #ef4444;
  text-align: center;
  padding: 20px;
}
</style>
