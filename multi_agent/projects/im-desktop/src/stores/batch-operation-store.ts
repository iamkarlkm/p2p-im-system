import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  BatchOperationRequest,
  BatchOperationResponse,
  BatchOperationHistory,
  BatchOperationType,
} from '../types/conversation-batch-operation';
import { batchOperationService } from '../services/batch-operation-service';

export const useBatchOperationStore = defineStore('batchOperation', () => {
  const selectedConversations = ref<number[]>([]);
  const isLoading = ref(false);
  const operationHistory = ref<BatchOperationHistory[]>([]);
  const lastOperationResult = ref<BatchOperationResponse | null>(null);

  const hasSelection = computed(() => selectedConversations.value.length > 0);
  const selectionCount = computed(() => selectedConversations.value.length);

  function toggleConversation(conversationId: number) {
    const index = selectedConversations.value.indexOf(conversationId);
    if (index === -1) {
      selectedConversations.value.push(conversationId);
    } else {
      selectedConversations.value.splice(index, 1);
    }
  }

  function selectAll(conversationIds: number[]) {
    selectedConversations.value = [...conversationIds];
  }

  function clearSelection() {
    selectedConversations.value = [];
  }

  function selectRange(startId: number, endId: number, conversationIds: number[]) {
    const startIndex = conversationIds.indexOf(startId);
    const endIndex = conversationIds.indexOf(endId);
    if (startIndex === -1 || endIndex === -1) return;
    
    const [min, max] = startIndex < endIndex 
      ? [startIndex, endIndex] 
      : [endIndex, startIndex];
    
    selectedConversations.value = conversationIds.slice(min, max + 1);
  }

  async function executeBatchOperation(operationType: BatchOperationType) {
    if (!hasSelection.value) {
      throw new Error('No conversations selected');
    }

    isLoading.value = true;
    try {
      const request: BatchOperationRequest = {
        conversationIds: selectedConversations.value,
        operationType,
      };

      const response = await batchOperationService.executeBatchOperation(request);
      lastOperationResult.value = response;
      clearSelection();
      
      return response;
    } finally {
      isLoading.value = false;
    }
  }

  async function loadOperationHistory() {
    isLoading.value = true;
    try {
      operationHistory.value = await batchOperationService.getBatchOperationHistory();
    } finally {
      isLoading.value = false;
    }
  }

  async function batchMarkAsRead() {
    return executeBatchOperation('mark_read');
  }

  async function batchArchive() {
    return executeBatchOperation('archive');
  }

  async function batchDelete() {
    return executeBatchOperation('delete');
  }

  async function batchPin() {
    return executeBatchOperation('pin');
  }

  async function batchUnpin() {
    return executeBatchOperation('unpin');
  }

  async function batchMute() {
    return executeBatchOperation('mute');
  }

  async function batchUnmute() {
    return executeBatchOperation('unmute');
  }

  return {
    selectedConversations,
    isLoading,
    operationHistory,
    lastOperationResult,
    hasSelection,
    selectionCount,
    toggleConversation,
    selectAll,
    clearSelection,
    selectRange,
    executeBatchOperation,
    loadOperationHistory,
    batchMarkAsRead,
    batchArchive,
    batchDelete,
    batchPin,
    batchUnpin,
    batchMute,
    batchUnmute,
  };
});
