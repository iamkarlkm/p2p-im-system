import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { ForwardService } from '../services/forward-service';
import type { ForwardRequest, ForwardResponse, MessageForward } from '../types/message-forward';

export const useForwardStore = defineStore('forward', () => {
  const selectedMessages = ref<number[]>([]);
  const forwardHistory = ref<Map<number, MessageForward[]>>(new Map());
  const isForwarding = ref(false);
  const lastResult = ref<ForwardResponse | null>(null);

  const hasSelection = computed(() => selectedMessages.value.length > 0);
  const selectionCount = computed(() => selectedMessages.value.length);

  function selectMessage(msgId: number) {
    if (!selectedMessages.value.includes(msgId)) {
      selectedMessages.value.push(msgId);
    }
  }

  function deselectMessage(msgId: number) {
    selectedMessages.value = selectedMessages.value.filter(id => id !== msgId);
  }

  function toggleSelection(msgId: number) {
    if (selectedMessages.value.includes(msgId)) {
      deselectMessage(msgId);
    } else {
      selectMessage(msgId);
    }
  }

  function clearSelection() {
    selectedMessages.value = [];
  }

  async function forwardToConversation(request: ForwardRequest): Promise<ForwardResponse> {
    isForwarding.value = true;
    try {
      const result = await ForwardService.forwardMessage(request);
      lastResult.value = result;
      if (result.success) {
        clearSelection();
      }
      return result;
    } finally {
      isForwarding.value = false;
    }
  }

  async function loadForwardHistory(messageId: number) {
    const history = await ForwardService.getForwardHistory(messageId);
    forwardHistory.value.set(messageId, history);
  }

  function getForwardHistory(messageId: number): MessageForward[] {
    return forwardHistory.value.get(messageId) || [];
  }

  return {
    selectedMessages,
    isForwarding,
    lastResult,
    hasSelection,
    selectionCount,
    selectMessage,
    deselectMessage,
    toggleSelection,
    clearSelection,
    forwardToConversation,
    loadForwardHistory,
    getForwardHistory,
  };
});
