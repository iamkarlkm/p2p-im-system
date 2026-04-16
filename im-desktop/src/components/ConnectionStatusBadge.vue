<template>
  <div class="connection-status" :class="statusClass">
    <span class="status-dot"></span>
    <span class="status-text">{{ statusText }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { ConnectionStatus } from '@/types/im';

const props = defineProps<{
  status: ConnectionStatus;
}>();

const statusClass = computed(() => `status-${props.status.status}`);

const statusText = computed(() => {
  switch (props.status.status) {
    case 'connected':
      return '已连接';
    case 'connecting':
      return props.status.message || '连接中...';
    case 'error':
      return '连接错误';
    default:
      return '未连接';
  }
});
</script>

<style scoped>
.connection-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
}

.status-connected .status-dot {
  background: #52c41a;
}

.status-connecting .status-dot {
  background: #faad14;
  animation: pulse 1s infinite;
}

.status-error .status-dot {
  background: #ff4d4f;
}

.status-connected {
  color: #52c41a;
}

.status-connecting {
  color: #faad14;
}

.status-error {
  color: #ff4d4f;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
