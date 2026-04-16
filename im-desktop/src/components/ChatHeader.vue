<template>
  <div class="chat-header">
    <div class="user-info">
      <h3>{{ conversation.targetName }}</h3>
      <span class="status">{{ conversation.type === 'group' ? '群聊' : '在线' }}</span>
    </div>
    <div class="actions">
      <button class="icon-btn" title="语音通话">
        <i class="icon-phone"></i>
      </button>
      <button class="icon-btn" title="视频通话">
        <i class="icon-video"></i>
      </button>
      <button class="icon-btn" title="更多">
        <i class="icon-more"></i>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Conversation } from '@/types/im';

defineProps<{
  conversation: Conversation;
}>();
</script>

<style scoped>
.chat-header {
  height: 60px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e0e0e0;
  background: #fff;
}

.user-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.user-info .status {
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-btn:hover {
  background: #f0f0f0;
}
</style>
