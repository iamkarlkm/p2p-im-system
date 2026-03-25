import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { ScreenshotEvent, ScreenshotSettings, defaultScreenshotSettings } from '../types/screenshot-notification';
import ScreenshotNotificationService from '../services/screenshot-notification-service';

export const useScreenshotStore = defineStore('screenshot', () => {
  const service = ScreenshotNotificationService.getInstance();
  const settings = ref<ScreenshotSettings>({ ...defaultScreenshotSettings });
  const history = ref<ScreenshotEvent[]>([]);
  const latestEvent = ref<ScreenshotEvent | null>(null);
  const isLoading = ref(false);

  const isEnabled = computed(() => settings.value.enableScreenshotNotification);
  const canReceiveAlerts = computed(() => settings.value.receiveScreenshotAlerts);

  async function loadSettings() {
    isLoading.value = true;
    try {
      settings.value = await service.getSettings();
    } finally {
      isLoading.value = false;
    }
  }

  async function updateSettings(newSettings: Partial<ScreenshotSettings>) {
    isLoading.value = true;
    try {
      settings.value = await service.updateSettings(newSettings);
    } finally {
      isLoading.value = false;
    }
  }

  async function reportScreenshot(conversationId: number, conversationType: string) {
    if (!settings.value.notifyOnCapture) return null;
    const event = await service.reportScreenshot(conversationId, conversationType);
    if (event) latestEvent.value = event;
    return event;
  }

  async function loadHistory() {
    isLoading.value = true;
    try {
      history.value = await service.getHistory(100);
    } finally {
      isLoading.value = false;
    }
  }

  function clearLatestEvent() {
    latestEvent.value = null;
  }

  return {
    settings, history, latestEvent, isLoading, isEnabled, canReceiveAlerts,
    loadSettings, updateSettings, reportScreenshot, loadHistory, clearLatestEvent,
  };
});
