import { makeAutoObservable, runInAction } from 'mobx';
import {
  ScheduledMessageRecall,
  RecallStatus,
  CreateScheduledRecallRequest,
  canCancelRecall,
  getRemainingSeconds,
  formatRemainingTime,
  getStatusDisplay,
  getStatusColor,
  RECOMMENDED_TIME_OPTIONS,
  formatTimeOption,
} from '../types/scheduled-message-recall';
import * as scheduledRecallApi from '../api/scheduled-message-recall-api';

/**
 * 消息定时撤回状态管理Store
 */
export class ScheduledMessageRecallStore {
  // ==================== State ====================
  recalls: ScheduledMessageRecall[] = [];
  pendingRecalls: ScheduledMessageRecall[] = [];
  selectedRecall: ScheduledMessageRecall | null = null;
  isLoading = false;
  isProcessing = false;
  errorMessage: string | null = null;
  pendingCount = 0;
  totalCount = 0;
  timeOptions = RECOMMENDED_TIME_OPTIONS;

  // 倒计时定时器
  private countdownInterval: NodeJS.Timeout | null = null;

  constructor() {
    makeAutoObservable(this);
    this.startCountdown();
  }

  // ==================== Computed ====================

  get hasPendingRecalls(): boolean {
    return this.pendingRecalls.length > 0;
  }

  get urgentRecalls(): ScheduledMessageRecall[] {
    return this.pendingRecalls.filter(
      (r) => {
        const remaining = getRemainingSeconds(r);
        return remaining > 0 && remaining <= 60;
      }
    );
  }

  get recallsByStatus(): Record<string, ScheduledMessageRecall[]> {
    const groups: Record<string, ScheduledMessageRecall[]> = {};
    for (const recall of this.recalls) {
      const status = recall.status;
      if (!groups[status]) groups[status] = [];
      groups[status].push(recall);
    }
    return groups;
  }

  // ==================== Actions ====================

  setLoading = (loading: boolean) => {
    this.isLoading = loading;
  };

  setProcessing = (processing: boolean) => {
    this.isProcessing = processing;
  };

  setError = (error: string | null) => {
    this.errorMessage = error;
  };

  clearError = () => {
    this.errorMessage = null;
  };

  selectRecall = (recall: ScheduledMessageRecall | null) => {
    this.selectedRecall = recall;
  };

  /**
   * 加载用户的所有定时撤回任务
   */
  loadUserRecalls = async (): Promise<boolean> => {
    this.setLoading(true);
    this.clearError();

    try {
      const result = await scheduledRecallApi.getUserRecalls();
      if (result.success && result.data) {
        runInAction(() => {
          this.recalls = result.data!;
          this.updatePendingRecalls();
        });
        return true;
      } else {
        this.setError(result.message || '加载失败');
        return false;
      }
    } catch (e) {
      this.setError(`加载定时撤回任务失败: ${e}`);
      return false;
    } finally {
      this.setLoading(false);
    }
  };

  /**
   * 加载待执行的任务
   */
  loadPendingRecalls = async (): Promise<boolean> => {
    try {
      const result = await scheduledRecallApi.getPendingRecalls();
      if (result.success && result.data) {
        runInAction(() => {
          this.pendingRecalls = result.data!;
        });
        return true;
      }
      return false;
    } catch (e) {
      this.setError(`加载待执行任务失败: ${e}`);
      return false;
    }
  };

  /**
   * 加载统计数据
   */
  loadStats = async (): Promise<boolean> => {
    try {
      const result = await scheduledRecallApi.getRecallStats();
      if (result.success && result.data) {
        runInAction(() => {
          this.pendingCount = result.data!.pendingCount;
          this.totalCount = result.data!.totalCount;
        });
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  };

  /**
   * 创建定时撤回任务
   */
  createScheduledRecall = async (request: CreateScheduledRecallRequest): Promise<boolean> => {
    this.setProcessing(true);
    this.clearError();

    try {
      const result = await scheduledRecallApi.createScheduledRecall(request);
      if (result.success && result.data) {
        runInAction(() => {
          this.recalls.unshift(result.data!);
          if (result.data!.status === RecallStatus.PENDING) {
            this.pendingRecalls.push(result.data!);
          }
          this.pendingCount++;
          this.totalCount++;
        });
        return true;
      } else {
        this.setError(result.message || '创建失败');
        return false;
      }
    } catch (e) {
      this.setError(`创建定时撤回任务失败: ${e}`);
      return false;
    } finally {
      this.setProcessing(false);
    }
  };

  /**
   * 取消定时撤回任务
   */
  cancelRecall = async (id: number): Promise<boolean> => {
    this.setProcessing(true);
    this.clearError();

    try {
      const result = await scheduledRecallApi.cancelScheduledRecall(id);
      if (result.success && result.data) {
        runInAction(() => {
          const index = this.recalls.findIndex((r) => r.id === id);
          if (index !== -1) {
            this.recalls[index] = result.data!;
          }
          this.updatePendingRecalls();
          this.pendingCount = Math.max(0, this.pendingCount - 1);
        });
        return true;
      } else {
        this.setError(result.message || '取消失败');
        return false;
      }
    } catch (e) {
      this.setError(`取消定时撤回任务失败: ${e}`);
      return false;
    } finally {
      this.setProcessing(false);
    }
  };

  /**
   * 更新定时撤回时间
   */
  updateScheduledTime = async (id: number, newSeconds: number): Promise<boolean> => {
    this.setProcessing(true);
    this.clearError();

    try {
      const result = await scheduledRecallApi.updateScheduledTime(id, newSeconds);
      if (result.success && result.data) {
        runInAction(() => {
          const index = this.recalls.findIndex((r) => r.id === id);
          if (index !== -1) {
            this.recalls[index] = result.data!;
          }
          this.updatePendingRecalls();
        });
        return true;
      } else {
        this.setError(result.message || '更新时间失败');
        return false;
      }
    } catch (e) {
      this.setError(`更新定时撤回时间失败: ${e}`);
      return false;
    } finally {
      this.setProcessing(false);
    }
  };

  /**
   * 删除定时撤回任务
   */
  deleteRecall = async (id: number): Promise<boolean> => {
    this.setProcessing(true);
    this.clearError();

    try {
      const result = await scheduledRecallApi.deleteScheduledRecall(id);
      if (result.success) {
        runInAction(() => {
          this.recalls = this.recalls.filter((r) => r.id !== id);
          this.updatePendingRecalls();
          this.totalCount = Math.max(0, this.totalCount - 1);
        });
        return true;
      } else {
        this.setError(result.message || '删除失败');
        return false;
      }
    } catch (e) {
      this.setError(`删除定时撤回任务失败: ${e}`);
      return false;
    } finally {
      this.setProcessing(false);
    }
  };

  /**
   * 检查消息是否已设置定时撤回
   */
  checkMessageScheduled = async (messageId: number): Promise<boolean> => {
    try {
      const result = await scheduledRecallApi.checkMessageScheduled(messageId);
      return result.data?.isScheduled || false;
    } catch (e) {
      return false;
    }
  };

  /**
   * 获取任务详情
   */
  getRecallDetail = async (id: number): Promise<ScheduledMessageRecall | null> => {
    try {
      const result = await scheduledRecallApi.getRecallDetail(id);
      if (result.success && result.data) {
        runInAction(() => {
          this.selectedRecall = result.data!;
        });
        return result.data;
      }
    } catch (e) {
      this.setError(`获取任务详情失败: ${e}`);
    }
    return null;
  };

  /**
   * 刷新所有数据
   */
  refreshAll = async (): Promise<void> => {
    await Promise.all([
      this.loadUserRecalls(),
      this.loadPendingRecalls(),
      this.loadStats(),
    ]);
  };

  /**
   * 快速创建定时撤回
   */
  quickScheduleRecall = async (
    messageId: number,
    conversationId: number,
    conversationType: string,
    messageContent: string | undefined,
    scheduledSeconds: number = 60
  ): Promise<boolean> => {
    return this.createScheduledRecall({
      messageId,
      conversationId,
      conversationType: conversationType as any,
      messageContent,
      scheduledSeconds,
      notifyReceivers: true,
      isCancelable: true,
    });
  };

  /**
   * 取消所有待执行的任务
   */
  cancelAllPending = async (): Promise<number> => {
    let cancelledCount = 0;
    const toCancel = [...this.pendingRecalls];

    for (const recall of toCancel) {
      if (await this.cancelRecall(recall.id)) {
        cancelledCount++;
      }
    }

    return cancelledCount;
  };

  // ==================== Helper Methods ====================

  private updatePendingRecalls = () => {
    this.pendingRecalls = this.recalls.filter(
      (r) => r.status === RecallStatus.PENDING
    );
  };

  /**
   * 启动倒计时更新
   */
  private startCountdown = () => {
    this.countdownInterval = setInterval(() => {
      // 触发MobX重新计算剩余时间
      runInAction(() => {
        this.pendingRecalls = [...this.pendingRecalls];
      });
    }, 1000);
  };

  /**
   * 停止倒计时
   */
  stopCountdown = () => {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  };

  // ==================== Utility Methods ====================

  getStatusText = (status: RecallStatus): string => getStatusDisplay(status);
  getStatusColorCode = (status: RecallStatus): string => getStatusColor(status);
  canCancel = (recall: ScheduledMessageRecall): boolean => canCancelRecall(recall);
  getRemainingTime = (recall: ScheduledMessageRecall): number => getRemainingSeconds(recall);
  formatTime = (seconds: number): string => formatRemainingTime(seconds);
  formatTimeOption = (seconds: number): string => formatTimeOption(seconds);
}

// 单例实例
export const scheduledRecallStore = new ScheduledMessageRecallStore();
