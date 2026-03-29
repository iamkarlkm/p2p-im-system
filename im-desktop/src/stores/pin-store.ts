import { defineStore } from 'pinia';
import { ref } from 'vue';
import { PinService } from '../services/pin-service';
import type { PinnedConversation } from '../types/conversation-pin';

export const usePinStore = defineStore('pin', () => {
  const pinnedConversations = ref<PinnedConversation[]>([]);
  const loading = ref(false);

  async function loadPinned() {
    loading.value = true;
    try {
      pinnedConversations.value = await PinService.getPinned();
    } finally {
      loading.value = false;
    }
  }

  async function pinConversation(conversationId: number, note?: string) {
    const result = await PinService.pin({ conversationId, pinNote: note });
    if (result.success && result.pinnedConversations) {
      pinnedConversations.value = result.pinnedConversations;
    }
    return result;
  }

  async function unpinConversation(conversationId: number) {
    const result = await PinService.unpin(conversationId);
    if (result.success && result.pinnedConversations) {
      pinnedConversations.value = result.pinnedConversations;
    }
    return result;
  }

  async function reorder(conversationIds: number[]) {
    const result = await PinService.reorder(conversationIds);
    if (result.success && result.pinnedConversations) {
      pinnedConversations.value = result.pinnedConversations;
    }
    return result;
  }

  function isPinned(conversationId: number): boolean {
    return pinnedConversations.value.some(c => c.conversationId === conversationId);
  }

  return { pinnedConversations, loading, loadPinned, pinConversation, unpinConversation, reorder, isPinned };
});
