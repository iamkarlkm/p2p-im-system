<template>
  <div class="message-list" ref="listRef">
    <div
      v-for="msg in messages"
      :key="msg.id"
      class="message-item"
      :class="{ self: msg.from === currentUserId }"
    >
      <MessageBubble :message="msg" :is-self="msg.from === currentUserId" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import type { Message } from '@/types/im';
import MessageBubble from './MessageBubble.vue';

const props = defineProps<{
  messages: Message[];
  currentUserId: string | null;
}>();

const listRef = ref<HTMLDivElement>();

watch(() => props.messages.length, () => {
  nextTick(() => {
    if (listRef.value) {
      listRef.value.scrollTop = listRef.value.scrollHeight;
    }
  });
});
</script>

<style scoped>
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  margin-bottom: 16px;
}

.message-item.self {
  text-align: right;
}
</style>
