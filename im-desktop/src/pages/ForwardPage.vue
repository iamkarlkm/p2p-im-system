<template>
  <div class="forward-page">
    <div class="forward-header">
      <h3>{{ isMerged ? 'Merge & Forward' : 'Forward Messages' }}</h3>
      <label class="merge-toggle">
        <input type="checkbox" v-model="isMerged" />
        Merge into one message
      </label>
    </div>

    <div v-if="isMerged && selectedCount > 1" class="merged-preview">
      <input v-model="mergedTitle" placeholder="Title (optional)" class="merged-title-input" />
      <div class="preview-list">
        <div v-for="msg in previewMessages" :key="msg.id" class="preview-item">
          <strong>{{ msg.sender }}</strong>: {{ msg.content }}
        </div>
      </div>
    </div>

    <div class="conversation-selector">
      <h4>Select destination:</h4>
      <div class="search-box">
        <input v-model="searchQuery" placeholder="Search conversations..." />
      </div>
      <div class="conversation-list">
        <div
          v-for="conv in filteredConversations"
          :key="conv.id"
          class="conversation-item"
          :class="{ selected: selectedTarget === conv.id }"
          @click="selectedTarget = conv.id"
        >
          <span class="conv-name">{{ conv.name }}</span>
          <span class="conv-type">{{ conv.type }}</span>
        </div>
      </div>
    </div>

    <div class="forward-actions">
      <button @click="$emit('cancel')" class="btn-cancel">Cancel</button>
      <button
        @click="handleForward"
        :disabled="!canForward || forwarding"
        class="btn-forward"
      >
        {{ forwarding ? 'Forwarding...' : 'Forward' }}
      </button>
    </div>

    <div v-if="result" class="result-message" :class="result.success ? 'success' : 'error'">
      {{ result.message }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useForwardStore } from '../stores/forward-store';
import { ForwardService } from '../services/forward-service';

const props = defineProps<{
  selectedMessageIds: number[];
  previewMessages: Array<{ id: number; sender: string; content: string }>;
  conversations: Array<{ id: number; name: string; type: string }>;
}>();

const emit = defineEmits(['cancel', 'forwarded']);
const forwardStore = useForwardStore();

const isMerged = ref(props.selectedMessageIds.length > 1);
const mergedTitle = ref('');
const searchQuery = ref('');
const selectedTarget = ref<number | null>(null);
const forwarding = ref(false);
const result = ref<{ success: boolean; message: string } | null>(null);

const selectedCount = computed(() => props.selectedMessageIds.length);
const filteredConversations = computed(() => {
  const q = searchQuery.value.toLowerCase();
  return props.conversations.filter(c => c.name.toLowerCase().includes(q));
});
const canForward = computed(() => selectedTarget.value !== null && props.selectedMessageIds.length > 0);

async function handleForward() {
  if (!canForward.value) return;
  forwarding.value = true;
  result.value = null;
  try {
    const response = await ForwardService.forwardMessage({
      messageIds: props.selectedMessageIds,
      targetConversationId: selectedTarget.value!,
      merged: isMerged.value,
      mergedTitle: isMerged.value ? mergedTitle.value : undefined,
    });
    result.value = response;
    if (response.success) {
      setTimeout(() => emit('forwarded', response), 1500);
    }
  } catch (e) {
    result.value = { success: false, message: 'Forward failed' };
  } finally {
    forwarding.value = false;
  }
}
</script>

<style scoped>
.forward-page { padding: 20px; max-width: 500px; }
.forward-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.merge-toggle { font-size: 13px; display: flex; align-items: center; gap: 6px; }
.merged-preview { background: #f5f5f5; border-radius: 8px; padding: 12px; margin-bottom: 16px; }
.merged-title-input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 8px; }
.preview-list { max-height: 150px; overflow-y: auto; }
.preview-item { padding: 4px 0; font-size: 12px; border-bottom: 1px solid #eee; }
.search-box input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 8px; }
.conversation-item { padding: 10px; border-radius: 6px; cursor: pointer; display: flex; justify-content: space-between; }
.conversation-item:hover, .conversation-item.selected { background: #e8f0fe; }
.forward-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; }
.btn-forward { background: #1a73e8; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-forward:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-cancel { background: #f1f3f4; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.result-message { margin-top: 12px; padding: 10px; border-radius: 4px; text-align: center; font-size: 13px; }
.result-message.success { background: #e6f4ea; color: #1e8e3e; }
.result-message.error { background: #fce8e6; color: #d93025; }
</style>
