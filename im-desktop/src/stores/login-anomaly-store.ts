import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { LoginAnomalyAlert, LoginAnomalySettings } from '../types/login-anomaly-alert';
import { loginAnomalyService } from '../services/login-anomaly-service';

export const useLoginAnomalyStore = defineStore('loginAnomaly', () => {
  const alerts = ref<LoginAnomalyAlert[]>([]);
  const pendingAlerts = ref<LoginAnomalyAlert[]>([]);
  const settings = ref<LoginAnomalySettings | null>(null);
  const loading = ref(false);

  const unconfirmedCount = computed(() =>
    pendingAlerts.value.filter(a => !a.isConfirmed).length
  );

  const highRiskAlerts = computed(() =>
    pendingAlerts.value.filter(a => (a.riskScore ?? 0) >= 60)
  );

  async function fetchAlerts() {
    loading.value = true;
    try {
      alerts.value = await loginAnomalyService.getAlerts();
      pendingAlerts.value = alerts.value.filter(a => !a.isDismissed);
    } finally {
      loading.value = false;
    }
  }

  async function fetchPendingAlerts() {
    pendingAlerts.value = await loginAnomalyService.getPendingAlerts();
  }

  async function confirmAlert(alertId: number) {
    const updated = await loginAnomalyService.confirmAlert(alertId);
    const idx = alerts.value.findIndex(a => a.id === alertId);
    if (idx >= 0) alerts.value[idx] = updated;
    await fetchPendingAlerts();
  }

  async function dismissAlert(alertId: number) {
    const updated = await loginAnomalyService.dismissAlert(alertId);
    const idx = alerts.value.findIndex(a => a.id === alertId);
    if (idx >= 0) alerts.value[idx] = updated;
    await fetchPendingAlerts();
  }

  async function fetchSettings() {
    settings.value = await loginAnomalyService.getSettings();
  }

  async function updateSettings(patch: Partial<LoginAnomalySettings>) {
    settings.value = await loginAnomalyService.updateSettings(patch);
  }

  return {
    alerts,
    pendingAlerts,
    settings,
    loading,
    unconfirmedCount,
    highRiskAlerts,
    fetchAlerts,
    fetchPendingAlerts,
    confirmAlert,
    dismissAlert,
    fetchSettings,
    updateSettings,
  };
});
