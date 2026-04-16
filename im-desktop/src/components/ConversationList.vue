<template>
  <div class="conversation-list">
    <div
      v-for="conv in conversations"
      :key="conv.id"
      class="conversation-item"
      :class="{ active: conv.id === currentId }"
      @click="$emit('select', conv.id)"
    >
      <div class="avatar">
        <img v-if="conv.targetAvatar" :src="conv.targetAvatar" :alt="conv.targetName">
        <span v-else class="avatar-placeholder">{{ conv.targetName[0] }}</span>
      </div>
      <div class="content">
        <div class="header">
          <span class="name">{{ conv.targetName }}</span>
          <span class="time">{{ formatTime(conv.lastMessageTime) }}</span>
        </div>
        <div class="footer">
          <p class="preview">{{ conv.lastMessage }}</p>
          <span v-if="conv.unreadCount > 0" class="badge">
            {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Conversation } from '@/types/im';

defineProps<{
  conversations: Conversation[];
  currentId: string | null;
}>();

defineEmits<{
  (e: 'select', id: string): void;
}>();

function formatTime(timestamp: number): string {
  const now = Date.now();
  const diff = now - timestamp;
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
  
  return new Date(timestamp).toLocaleDateString();
}
</script>

<style scoped>
.conversation-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.conversation-item:hover,
.conversation-item.active {
  background: #f0f0f0;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.content {
  flex: 1;
  min-width: 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.name {
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview {
  margin: 0;
  font-size: 13px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.badge {
  background: #ff4d4f;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 8px;
  flex-shrink: 0;
}
</style>
