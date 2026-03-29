// stores/do-not-disturb-store.ts
import { makeAutoObservable, runInAction } from 'mobx';
import {
  DoNotDisturbPeriod,
  CreateDoNotDisturbPeriodRequest,
  UpdateDoNotDisturbPeriodRequest,
  DoNotDisturbStatus,
  isCurrentlyActive,
} from '../types/do-not-disturb';
import api from '../services/api';

class DoNotDisturbStore {
  periods: DoNotDisturbPeriod[] = [];
  isLoading = false;
  error: string | null = null;
  status: DoNotDisturbStatus | null = null;

  constructor() {
    makeAutoObservable(this);
  }

  get enabledPeriods(): DoNotDisturbPeriod[] {
    return this.periods.filter(p => p.isEnabled);
  }

  get activePeriods(): DoNotDisturbPeriod[] {
    return this.enabledPeriods.filter(isCurrentlyActive);
  }

  get isInDoNotDisturbMode(): boolean {
    return this.activePeriods.length > 0;
  }

  get shouldAllowCalls(): boolean {
    if (!this.isInDoNotDisturbMode) return true;
    return this.activePeriods.every(p => p.allowCalls);
  }

  get shouldAllowMentions(): boolean {
    if (!this.isInDoNotDisturbMode) return true;
    return this.activePeriods.some(p => p.allowMentions);
  }

  async fetchPeriods() {
    this.isLoading = true;
    this.error = null;
    
    try {
      const response = await api.get('/do-not-disturb/periods');
      runInAction(() => {
        this.periods = response.data.data || [];
      });
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '加载免打扰时段失败';
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async fetchStatus() {
    try {
      const response = await api.get('/do-not-disturb/status');
      runInAction(() => {
        this.status = response.data.data;
      });
    } catch (err: any) {
      console.error('获取免打扰状态失败:', err);
    }
  }

  async createPeriod(data: CreateDoNotDisturbPeriodRequest) {
    this.isLoading = true;
    this.error = null;
    
    try {
      const response = await api.post('/do-not-disturb/periods', data);
      runInAction(() => {
        this.periods.unshift(response.data.data);
      });
      return response.data.data;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '创建免打扰时段失败';
      });
      throw err;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async updatePeriod(periodId: string, data: UpdateDoNotDisturbPeriodRequest) {
    this.isLoading = true;
    this.error = null;
    
    try {
      const response = await api.put(`/do-not-disturb/periods/${periodId}`, data);
      runInAction(() => {
        const index = this.periods.findIndex(p => p.id === periodId);
        if (index !== -1) {
          this.periods[index] = response.data.data;
        }
      });
      return response.data.data;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '更新免打扰时段失败';
      });
      throw err;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async deletePeriod(periodId: string) {
    this.isLoading = true;
    this.error = null;
    
    try {
      await api.delete(`/do-not-disturb/periods/${periodId}`);
      runInAction(() => {
        this.periods = this.periods.filter(p => p.id !== periodId);
      });
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '删除免打扰时段失败';
      });
      throw err;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async togglePeriod(periodId: string, isEnabled: boolean) {
    this.isLoading = true;
    this.error = null;
    
    try {
      const response = await api.patch(`/do-not-disturb/periods/${periodId}/toggle?enabled=${isEnabled}`);
      runInAction(() => {
        const index = this.periods.findIndex(p => p.id === periodId);
        if (index !== -1) {
          this.periods[index] = response.data.data;
        }
      });
      return response.data.data;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '切换免打扰时段状态失败';
      });
      throw err;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  clearError() {
    this.error = null;
  }
}

export const doNotDisturbStore = new DoNotDisturbStore();
