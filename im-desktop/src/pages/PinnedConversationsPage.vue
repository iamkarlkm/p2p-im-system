<template>
  <div class="pinned-page">
    <h3>Pinned Conversations</h3>
    <div v-if="pinStore.loading" class="loading">Loading...</div>
    <div v-else-if="pinStore.pinnedConversations.length === 0" class="empty">
      No pinned conversations yet.
    </div>
    <div v-else class="pinned-list">
      <div
        v-for="(conv, index) in pinStore.pinnedConversations"
        :key="conv.conversationId"
        class="pinned-item"
        draggable="true"
        @dragstart="onDragStart(index)"
        @dragover.prevent
        @drop="onDrop(index)"
      >
        <span class="drag-handle">☰</span>
        <span class="conv-name">{{ conv.conversationName }}</span>
        <button @click="handleUnpin(conv.conversationId)" class="btn-unpin">Unpin</button>
      </div>
    </div>
    <p class="hint">Drag to reorder pinned conversations.</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { usePinStore } from '../stores/pin-store';

const pinStore = usePinStore();
let dragIndex = -1;

onMounted(() => pinStore.loadPinned());

function onDragStart(index: number) { dragIndex = index; }

function onDrop(targetIndex: number) {
  if (dragIndex === -1 || dragIndex === targetIndex) return;
  const ids = pinStore.pinnedConversations.map(c => c.conversationId);
  const [removed] = ids.splice(dragIndex, 1);
  ids.splice(targetIndex, 0, removed);
  pinStore.reorder(ids);
  dragIndex = -1;
}

async function handleUnpin(conversationId: number) {
  await pinStore.unpinConversation(conversationId);
}
</script>

<style scoped>
.pinned-page { padding: 20px; }
.pinned-item { display: flex; align-items: center; gap: 10px; padding: 10px; border-bottom: 1px solid #eee; cursor: grab; }
.pinned-item:hover { background: #f5f5f5; }
.drag-handle { color: #999; cursor: grab; }
.conv-name { flex: 1; font-weight: 500; }
.btn-unpin { background: #f1f3f4; border: none; padding: 4px 10px; border-radius: 4px; cursor: pointer; font-size: 12px; }
.btn-unpin:hover { background: #e8eaed; }
.hint { font-size: 12px; color: #999; margin-top: 8px; }
.loading, .empty { padding: 20px; text-align: center; color: #999; }
</style>
