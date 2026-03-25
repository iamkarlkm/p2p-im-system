import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  Device,
  DeviceStats,
  LoginHistoryPage,
  DeviceAlert,
} from '../types/device';
import { deviceService } from '../services/device-service';

export const useDeviceStore = defineStore('device', () => {
  const devices = ref<Device[]>([]);
  const activeDevices = ref<Device[]>([]);
  const currentDevice = ref<Device | null>(null);
  const stats = ref<DeviceStats | null>(null);
  const loginHistory = ref<LoginHistoryPage | null>(null);
  const alerts = ref<DeviceAlert[]>([]);
  const isLoading = ref(false);
  const error = ref<string | null>(null);
  const selectedDeviceId = ref<number | null>(null);

  const currentDeviceInfo = computed(() => {
    return devices.value.find((d) => d.isCurrent) || null;
  });

  const trustedDevices = computed(() => {
    return devices.value.filter((d) => d.isTrusted);
  });

  const untrustedDevices = computed(() => {
    return devices.value.filter((d) => !d.isTrusted && d.isActive);
  });

  const activeAlertCount = computed(() => {
    return alerts.value.filter((a) => !a.acknowledged).length;
  });

  async function fetchDevices() {
    isLoading.value = true;
    error.value = null;
    try {
      devices.value = await deviceService.getUserDevices();
    } catch (e: any) {
      error.value = e.message || '获取设备列表失败';
    } finally {
      isLoading.value = false;
    }
  }

  async function fetchActiveDevices() {
    try {
      activeDevices.value = await deviceService.getActiveDevices();
    } catch (e: any) {
      error.value = e.message || '获取活跃设备失败';
    }
  }

  async function fetchStats() {
    try {
      stats.value = await deviceService.getDeviceStats();
    } catch (e: any) {
      error.value = e.message || '获取设备统计失败';
    }
  }

  async function fetchLoginHistory(page?: number, size?: number) {
    isLoading.value = true;
    try {
      loginHistory.value = await deviceService.getLoginHistory(page, size);
    } catch (e: any) {
      error.value = e.message || '获取登录历史失败';
    } finally {
      isLoading.value = false;
    }
  }

  async function fetchAlerts() {
    try {
      alerts.value = await deviceService.getDeviceAlerts();
    } catch (e: any) {
      error.value = e.message || '获取设备告警失败';
    }
  }

  async function deactivateDevice(deviceId: number) {
    try {
      await deviceService.deactivateDevice(deviceId);
      await fetchDevices();
      await fetchStats();
    } catch (e: any) {
      error.value = e.message || '注销设备失败';
      throw e;
    }
  }

  async function removeDevice(deviceId: number) {
    try {
      await deviceService.removeDevice(deviceId);
      await fetchDevices();
      await fetchStats();
    } catch (e: any) {
      error.value = e.message || '移除设备失败';
      throw e;
    }
  }

  async function trustDevice(deviceId: number) {
    try {
      await deviceService.trustDevice(deviceId);
      await fetchDevices();
      await fetchAlerts();
    } catch (e: any) {
      error.value = e.message || '信任设备失败';
      throw e;
    }
  }

  async function setCurrentDevice(deviceId: number) {
    try {
      await deviceService.setCurrentDevice(deviceId);
      await fetchDevices();
    } catch (e: any) {
      error.value = e.message || '设置当前设备失败';
      throw e;
    }
  }

  async function acknowledgeAlert(deviceId: number) {
    try {
      await deviceService.acknowledgeAlert(deviceId);
      alerts.value = alerts.value.filter((a) => a.deviceId !== deviceId);
    } catch (e: any) {
      error.value = e.message || '确认告警失败';
      throw e;
    }
  }

  function selectDevice(deviceId: number | null) {
    selectedDeviceId.value = deviceId;
  }

  function clearError() {
    error.value = null;
  }

  return {
    devices,
    activeDevices,
    currentDevice,
    stats,
    loginHistory,
    alerts,
    isLoading,
    error,
    selectedDeviceId,
    currentDeviceInfo,
    trustedDevices,
    untrustedDevices,
    activeAlertCount,
    fetchDevices,
    fetchActiveDevices,
    fetchStats,
    fetchLoginHistory,
    fetchAlerts,
    deactivateDevice,
    removeDevice,
    trustDevice,
    setCurrentDevice,
    acknowledgeAlert,
    selectDevice,
    clearError,
  };
});
