<template>
  <div class="batch-operation-page">
    <div class="batch-header">
      <h2>Batch Operations</h2>
      <div class="batch-actions">
        <button
          class="action-btn"
          :disabled="!hasSelection"
          @click="handleBatchAction('mark_read')"
        >
          ✓ Mark as Read
        </button>
        <button
          class="action-btn"
          :disabled="!hasSelection"
          @click="handleBatchAction('archive')"
        >
          📁 Archive
        </button>
        <button
          class="action-btn danger"
          :disabled="!hasSelection"
          @click="handleBatchAction('delete')"
        >
          🗑️ Delete
        </button>
        <button
          class="action-btn"
          :disabled="!hasSelection"
          @click="handleBatchAction('pin')"
        >
          📌 Pin
        </button>
        <button
          class="action-btn"
          :disabled="!hasSelection"
          @click="handleBatchAction('mute')"
        >
          🔇 Mute
        </button>
      </div>
    </div>

    <div class="selection-info" v-if="hasSelection">
      <span>{{ selectionCount }} conversations selected</span>
      <button class="clear-btn" @click="clearSelection">Clear Selection</button>
    </div>

    <div class="conversations-list">
      <div
        v-for="conversation in conversations"
        :key="conversation.id"
        class="conversation-item"
        :class="{ selected: isSelected(conversation.id) }"
        @click="toggleConversation(conversation.id)"
      >
        <input
          type="checkbox"
          :checked="isSelected(conversation.id)"
          @click.stop
          @change="toggleConversation(conversation.id)"
        />
        <div class="conversation-info">
          <div class="conversation-name">{{ conversation.name }}</div>
          <div class="conversation-preview">{{ conversation.lastMessage }}</div>
        </div>
        <div class="conversation-meta">
          <span v-if="conversation.unreadCount > 0" class="unread-badge">
            {{ conversation.unreadCount }}
          </span>
          <span class="conversation-time">{{ formatTime(conversation.lastMessageTime) }}</span>
        </div>
      </div>
    </div>

    <div class="operation-history" v-if="operationHistory.length > 0">
      <h3>Operation History</h3>
      <div
        v-for="operation in operationHistory"
        :key="operation.operationId"
        class="history-item"
      >
        <div class="history-type">{{ getOperationLabel(operation.operationType) }}</div>
        <div class="history-stats">
          {{ operation.successCount }}/{{ operation.totalCount }} succeeded
        </div>
        <div class="history-status" :class="operation.status">
          {{ operation.status }}
        </div>
      </div>
    </div>

    <div v-if="lastOperationResult" class="operation-result">
      <h3>Last Operation Result</h3>
      <div class="result-stats">
        <div>Total: {{ lastOperationResult.totalCount }}</div>
        <div>Success: {{ lastOperationResult.successCount }}</div>
        <div>Failed: {{ lastOperationResult.failureCount }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useBatchOperationStore } from '../stores/batch-operation-store';
import { BATCH_OPERATION_LABELS, BatchOperationType } from '../types/conversation-batch-operation';

const store = useBatchOperationStore();

const conversations = ref([
  { id: 1, name: 'John Doe', lastMessage: 'Hello!', lastMessageTime: Date.now(), unreadCount: 2 },
  { id: 2, name: 'Jane Smith', lastMessage: 'See you later', lastMessageTime: Date.now() - 3600000, unreadCount: 0 },
  { id: 3, name: 'Project Team', lastMessage: 'Meeting at 3pm', lastMessageTime: Date.now() - 7200000, unreadCount: 5 },
  { id: 4, name: 'Family Group', lastMessage: 'Happy birthday!', lastMessageTime: Date.now() - 86400000, unreadCount: 0 },
]);

const hasSelection = computed(() => store.hasSelection);
const selectionCount = computed(() => store.selectionCount);
const operationHistory = computed(() => store.operationHistory);
const lastOperationResult = computed(() => store.lastOperationResult);

function isSelected(conversationId: number): boolean {
  return store.selectedConversations.includes(conversationId);
}

function toggleConversation(conversationId: number) {
  store.toggleConversation(conversationId);
}

function clearSelection() {
  store.clearSelection();
}

async function handleBatchAction(operationType: BatchOperationType) {
  try {
    await store.executeBatchOperation(operationType);
    alert(`${BATCH_OPERATION_LABELS[operationType]} completed!`);
  } catch (error) {
    console.error('Batch operation failed:', error);
    alert('Operation failed. Please try again.');
  }
}

function getOperationLabel(operationType: string): string {
  return BATCH_OPERATION_LABELS[operationType as BatchOperationType] || operationType;
}

function formatTime(timestamp: number): string {
  const date = new Date(timestamp);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return 'Just now';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
  return date.toLocaleDateString();
}

onMounted(() => {
  store.loadOperationHistory();
});
</script>

<style scoped>
.batch-operation-page {
  padding: 20px;
}

.batch-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover:not(:disabled) {
  background: #f5f5f5;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-btn.danger {
  border-color: #dc3545;
  color: #dc3545;
}

.action-btn.danger:hover:not(:disabled) {
  background: #dc3545;
  color: white;
}

.selection-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #e3f2fd;
  border-radius: 4px;
  margin-bottom: 20px;
}

.clear-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  background: #2196f3;
  color: white;
  cursor: pointer;
}

.conversations-list {
  border: 1px solid #ddd;
  border-radius: 4px;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: #f5f5f5;
}

.conversation-item.selected {
  background: #e3f2fd;
}

.conversation-info {
  flex: 1;
  margin-left: 15px;
}

.conversation-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.conversation-preview {
  color: #666;
  font-size: 14px;
}

.conversation-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.unread-badge {
  background: #2196f3;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.conversation-time {
  color: #999;
  font-size: 12px;
}

.operation-history,
.operation-result {
  margin-top: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 4px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.history-item:last-child {
  border-bottom: none;
}

.history-status.completed {
  color: #4caf50;
}

.history-status.failed {
  color: #f44336;
}

.result-stats {
  display: flex;
  gap: 20px;
}
</style>
