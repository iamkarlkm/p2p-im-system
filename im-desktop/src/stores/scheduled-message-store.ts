import { makeAutoObservable, runInAction } from 'mobx';
import { ScheduledMessage, ScheduledMessageStatus } from '../types/scheduled-message';
import { api } from '../utils/api';

/**
 * 定时消息状态管理
 */
class ScheduledMessageStore {
  messages: ScheduledMessage[] = [];
  loading: boolean = false;
  error: string | null = null;
  currentMessage: ScheduledMessage | null = null;
  totalCount: number = 0;
  pendingCount: number = 0;
  page: number = 0;
  pageSize: number = 20;
  filterStatus: ScheduledMessageStatus | null = null;

  constructor() {
    makeAutoObservable(this);
  }

  /**
   * 获取定时消息列表
   */
  async fetchMessages(reset: boolean = false) {
    if (reset) {
      this.page = 0;
      this.messages = [];
    }

    this.loading = true;
    this.error = null;

    try {
      const params: Record<string, any> = {
        page: this.page,
        size: this.pageSize,
      };

      if (this.filterStatus) {
        params.status = this.filterStatus;
      }

      const response = await api.get('/scheduled-messages', { params });

      runInAction(() => {
        if (reset) {
          this.messages = response.data.data;
        } else {
          this.messages = [...this.messages, ...response.data.data];
        }
        this.totalCount = response.data.totalElements;
        this.loading = false;
      });
    } catch (err: any) {
      runInAction(() => {
        this.error = err.response?.data?.message || '获取定时消息失败';
        this.loading = false;
      });
    }
  }

  /**
   * 加载更多
   */
  loadMore() {
    if (this.messages.length < this.totalCount && !this.loading) {
      this.page += 1;
      this.fetchMessages();
    }
  }

  /**
   * 创建定时消息
   */
  async createMessage(message: Omit<ScheduledMessage, 'id' | 'status' | 'createdAt'>): Promise<boolean> {
    this.loading = true;
    this.error = null;

    try {
      const response = await api.post('/scheduled-messages', message);

      runInAction(() => {
        this.messages.unshift(response.data.data);
        this.totalCount += 1;
        this.loading = false;
      });

      return true;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.response?.data?.message || '创建定时消息失败';
        this.loading = false;
      });
      return false;
    }
  }

  /**
   * 更新定时消息
   */
  async updateMessage(id: number, message: Partial<ScheduledMessage>): Promise<boolean> {
    this.loading = true;
    this.error = null;

    try {
      const response = await api.put(`/scheduled-messages/${id}`, message);

      runInAction(() => {
        const index = this.messages.findIndex(m => m.id === id);
        if (index !== -1) {
          this.messages[index] = response.data.data;
        }
        this.loading = false;
      });

      return true;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.response?.data?.message || '更新定时消息失败';
        this.loading = false;
      });
      return false;
    }
  }

  /**
   * 取消定时消息
   */
  async cancelMessage(id: number): Promise<boolean> {
    try {
      const response = await api.post(`/scheduled-messages/${id}/cancel`);

      runInAction(() => {
        const index = this.messages.findIndex(m => m.id === id);
        if (index !== -1) {
          this.messages[index] = response.data.data;
        }
      });

      return true;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.response?.data?.message || '取消定时消息失败';
      });
      return false;
    }
  }

  /**
   * 删除定时消息
   */
  async deleteMessage(id: number): Promise<boolean> {
    try {
      await api.delete(`/scheduled-messages/${id}`);

      runInAction(() => {
        this.messages = this.messages.filter(m => m.id !== id);
        this.totalCount -= 1;
      });

      return true;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.response?.data?.message || '删除定时消息失败';
      });
      return false;
    }
  }

  /**
   * 获取统计信息
   */
  async fetchStats() {
    try {
      const response = await api.get('/scheduled-messages/stats');
      runInAction(() => {
        this.pendingCount = response.data.data.pendingCount;
      });
    } catch (err) {
      console.error('获取统计信息失败', err);
    }
  }

  /**
   * 设置状态筛选
   */
  setFilterStatus(status: ScheduledMessageStatus | null) {
    this.filterStatus = status;
    this.fetchMessages(true);
  }

  /**
   * 清除错误
   */
  clearError() {
    this.error = null;
  }
}

export const scheduledMessageStore = new ScheduledMessageStore();
