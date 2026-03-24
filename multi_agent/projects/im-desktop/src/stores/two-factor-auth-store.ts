import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
  TwoFactorSetupResponse,
  TwoFactorStatusResponse,
} from '../types/two-factor-auth';
import { twoFactorAuthService } from '../services/two-factor-auth-service';

export const useTwoFactorAuthStore = defineStore('twoFactorAuth', () => {
  const status = ref<TwoFactorStatusResponse | null>(null);
  const setupData = ref<TwoFactorSetupResponse | null>(null);
  const isLoading = ref(false);
  const error = ref<string | null>(null);
  const step = ref<'idle' | 'setup' | 'verify' | 'complete'>('idle');

  const isEnabled = computed(() => status.value?.enabled ?? false);
  const isVerified = computed(() => status.value?.verified ?? false);
  const backupCodesRemaining = computed(
    () => status.value?.backupCodesRemaining ?? 0
  );

  async function fetchStatus(userId: number) {
    isLoading.value = true;
    error.value = null;
    try {
      status.value = await twoFactorAuthService.getStatus(userId);
    } catch (e: any) {
      error.value = e.message;
    } finally {
      isLoading.value = false;
    }
  }

  async function startSetup(userId: number, accountName?: string) {
    isLoading.value = true;
    error.value = null;
    step.value = 'setup';
    try {
      setupData.value = await twoFactorAuthService.setup(userId, accountName);
    } catch (e: any) {
      error.value = e.message;
      step.value = 'idle';
    } finally {
      isLoading.value = false;
    }
  }

  async function verifyAndEnable(userId: number, code: string): Promise<boolean> {
    isLoading.value = true;
    error.value = null;
    step.value = 'verify';
    try {
      const enabled = await twoFactorAuthService.enable(userId, code);
      if (enabled) {
        await fetchStatus(userId);
        step.value = 'complete';
        return true;
      } else {
        error.value = 'Invalid verification code';
        return false;
      }
    } catch (e: any) {
      error.value = e.message;
      return false;
    } finally {
      isLoading.value = false;
    }
  }

  async function disable(userId: number, code: string): Promise<boolean> {
    isLoading.value = true;
    error.value = null;
    try {
      const disabled = await twoFactorAuthService.disable(userId, code);
      if (disabled) {
        status.value = null;
        step.value = 'idle';
        return true;
      }
      error.value = 'Invalid verification code';
      return false;
    } catch (e: any) {
      error.value = e.message;
      return false;
    } finally {
      isLoading.value = false;
    }
  }

  function reset() {
    status.value = null;
    setupData.value = null;
    isLoading.value = false;
    error.value = null;
    step.value = 'idle';
  }

  return {
    status,
    setupData,
    isLoading,
    error,
    step,
    isEnabled,
    isVerified,
    backupCodesRemaining,
    fetchStatus,
    startSetup,
    verifyAndEnable,
    disable,
    reset,
  };
});
