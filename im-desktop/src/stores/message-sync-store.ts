import { defineStore } from 'pinia';
import { ref } from 'vue';
import { SyncRequest, SyncResponse, SyncCheckpoint } from '../types/message-sync';
import { pullSync, fetchCheckpoints, getDeviceId } from '../services/message-sync-service';

export const useMessageSyncStore = defineStore('messageSync', () => {
  const lastSyncTime = ref<string | null>(null);
  const syncing = ref(false);
  const syncError = ref<string | null>(null);
  const checkpoints = ref<SyncCheckpoint[]>([]);

  const deviceId = getDeviceId();

  async function syncConversation(conversationId: number, lastMessageId?: number): Promise<SyncResponse | null> {
    syncing.value = true;
    syncError.value = null;
    try {
      const request: SyncRequest = {
        deviceId,
        conversationId,
        lastMessageId: lastMessageId || 0,
        limit: 50,
      };
      const response = await pullSync(request);
      lastSyncTime.value = new Date().toISOString();
      return response;
    } catch (e: any) {
      syncError.value = e.message;
      return null;
    } finally {
      syncing.value = false;
    }
  }

  async function loadCheckpoints() {
    try {
      checkpoints.value = await fetchCheckpoints(deviceId);
    } catch (e: any) {
      syncError.value = e.message;
    }
  }

  function getCheckpointForConversation(conversationId: number): SyncCheckpoint | undefined {
    return checkpoints.value.find(c => c.conversationId === conversationId);
  }

  return {
    lastSyncTime,
    syncing,
    syncError,
    checkpoints,
    deviceId,
    syncConversation,
    loadCheckpoints,
    getCheckpointForConversation,
  };
});
