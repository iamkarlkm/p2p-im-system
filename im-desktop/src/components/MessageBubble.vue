<template>
  <div class="message-bubble" :class="{ self: isSelf }">
    <div class="content" :class="message.contentType">
      <template v-if="message.contentType === 'text'">
        {{ message.content }}
      </template>
      <template v-else-if="message.contentType === 'image'">
        <img :src="message.content" alt="图片" />
      </template>
      <template v-else-if="message.contentType === 'voice'">
        <span class="voice-message">[语音]</span>
      </template>
      <template v-else-if="message.contentType === 'video'">
        <span class="video-message">[视频]</span>
      </template>
      <template v-else-if="message.contentType === 'file'">
        <span class="file-message">[文件]</span>
      </template>
    </div>
    <span class="time">{{ formatTime(message.timestamp) }}</span>
  </div>
</template>

<script setup lang="ts">
import type { Message } from '@/types/im';

const props = defineProps<{
  message: Message;
  isSelf: boolean;
}>();

function formatTime(timestamp: number): string {
  return new Date(timestamp).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  });
}
</script>

<style scoped>
.message-bubble {
  display: inline-flex;
  flex-direction: column;
  max-width: 60%;
}

.message-bubble.self {
  align-items: flex-end;
}

.content {
  padding: 10px 14px;
  border-radius: 4px;
  background: #fff;
  word-break: break-word;
}

.message-bubble.self .content {
  background: #95ec69;
}

.content.image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 4px;
}

.time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}
</style>
