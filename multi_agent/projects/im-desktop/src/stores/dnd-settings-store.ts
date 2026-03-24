import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  DndSettings,
  DndStatus,
  DEFAULT_DND_SETTINGS,
} from '../types/dnd-settings';
import {
  fetchDndSettings,
  saveDndSettings,
  fetchDndStatus,
  deleteDndSettings,
} from '../services/dnd-settings-service';

export const useDndSettingsStore = defineStore('dndSettings', () => {
  const settings = ref<DndSettings>({ ...DEFAULT_DND_SETTINGS });
  const status = ref<DndStatus>({
    inDndPeriod: false,
    allowMention: true,
    allowStarred: true,
  });
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isEnabled = computed(() => settings.value.enabled);
  const isInDndPeriod = computed(() => status.value.inDndPeriod);
  const effectiveMuted = computed(() => {
    return isEnabled.value && isInDndPeriod.value;
  });

  async function loadSettings() {
    loading.value = true;
    error.value = null;
    try {
      const data = await fetchDndSettings();
      settings.value = data;
    } catch (e: any) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }
  }

  async function loadStatus() {
    try {
      const data = await fetchDndStatus();
      status.value = data;
    } catch (e: any) {
      error.value = e.message;
    }
  }

  async function updateSettings(updates: Partial<DndSettings>) {
    loading.value = true;
    error.value = null;
    try {
      const updated = await saveDndSettings(updates);
      settings.value = updated;
      await loadStatus();
    } catch (e: any) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }
  }

  async function removeSettings() {
    loading.value = true;
    error.value = null;
    try {
      await deleteDndSettings();
      settings.value = { ...DEFAULT_DND_SETTINGS };
      status.value = { inDndPeriod: false, allowMention: true, allowStarred: true };
    } catch (e: any) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }
  }

  return {
    settings,
    status,
    loading,
    error,
    isEnabled,
    isInDndPeriod,
    effectiveMuted,
    loadSettings,
    loadStatus,
    updateSettings,
    removeSettings,
  };
});
