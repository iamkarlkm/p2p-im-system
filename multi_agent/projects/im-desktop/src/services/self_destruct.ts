/**
 * 阅后即焚服务 - 桌面端
 * 消息自动销毁功能
 */

import { apiClient } from './api_client';
import { wsClient } from './websocket';
import { NotificationService } from './notification';

// 销毁计时器类型
export type TimerType = 
  | 'SECONDS_5' 
  | 'SECONDS_30' 
  | 'MINUTE_1' 
  | 'MINUTES_5' 
  | 'HOUR_1' 
  | 'HOURS_24' 
  | 'CUSTOM';

// 销毁状态
export type DestroyStatus = 'PENDING' | 'COUNTING' | 'DESTROYED' | 'EXPIRED';

// 计时器配置
export interface TimerConfig {
  type: TimerType;
  label: string;
  seconds: number;
  icon: string;
}

export const TIMER_CONFIGS: TimerConfig[] = [
  { type: 'SECONDS_5', label: '5秒', seconds: 5, icon: '⏱️' },
  { type: 'SECONDS_30', label: '30秒', seconds: 30, icon: '⏱️' },
  { type: 'MINUTE_1', label: '1分钟', seconds: 60, icon: '🔥' },
  { type: 'MINUTES_5', label: '5分钟', seconds: 300, icon: '🔥' },
  { type: 'HOUR_1', label: '1小时', seconds: 3600, icon: '💣' },
  { type: 'HOURS_24', label: '24小时', seconds: 86400, icon: '💣' },
  { type: 'CUSTOM', label: '自定义', seconds: -1, icon: '⚙️' },
];

// 阅后即焚消息数据
export interface SelfDestructMessage {
  id: number;
  messageId: number;
  senderId: number;
  receiverId: number;
  timerType: TimerType;
  durationSeconds: number;
  status: DestroyStatus;
  createdAt: string;
  readAt?: string;
  destroyedAt?: string;
  destroyReason?: string;
}

// 销毁记录
export interface DestroyRecord {
  messageId: number;
  reason: string;
  operatorId: number | null;
  destroyTime: string;
  note?: string;
}

// 消息销毁状态响应
export interface DestroyStatusResponse {
  status: DestroyStatus | null;
  remainingSeconds: number;
}

class SelfDestructService {
  private activeCountdowns: Map<number, {
    timer: NodeJS.Timeout;
    remaining: number;
    callback: (remaining: number) => void;
  }> = new Map();

  private destroyedMessages: Set<number> = new Set();

  constructor() {
    this.initWebSocket();
  }

  /**
   * 初始化WebSocket监听
   */
  private initWebSocket() {
    wsClient.on('self_destruct:status_change', (data: any) => {
      this.handleStatusChange(data.messageId, data.status);
    });

    wsClient.on('self_destruct:destroyed', (data: any) => {
      this.handleDestroyed(data.messageId, data.reason);
    });
  }

  /**
   * 设置消息阅后即焚
   */
  async setupSelfDestruct(
    messageId: number,
    senderId: number,
    receiverId: number,
    timerType: TimerType,
    customSeconds?: number
  ): Promise<SelfDestructMessage> {
    const response = await apiClient.post<any>('/self-destruct/setup', {
      messageId,
      senderId,
      receiverId,
      timerType,
      customSeconds,
    });

    return response.data;
  }

  /**
   * 标记消息已读（开始倒计时）
   */
  async markAsRead(messageId: number, readerId: number): Promise<void> {
    await apiClient.post('/self-destruct/read', {
      messageId,
      readerId,
    });
  }

  /**
   * 获取消息销毁状态
   */
  async getStatus(messageId: number): Promise<DestroyStatusResponse> {
    const response = await apiClient.get<any>('/self-destruct/status', {
      params: { messageId },
    });

    return {
      status: response.data.status,
      remainingSeconds: response.data.remainingSeconds,
    };
  }

  /**
   * 手动销毁消息
   */
  async destroyMessage(messageId: number, operatorId: number): Promise<void> {
    await apiClient.post('/self-destruct/destroy', null, {
      params: { messageId, operatorId },
    });

    this.clearCountdown(messageId);
  }

  /**
   * 批量销毁消息
   */
  async batchDestroy(messageIds: number[], operatorId: number): Promise<void> {
    await apiClient.post('/self-destruct/batch-destroy', {
      messageIds,
      operatorId,
    });

    messageIds.forEach(id => this.clearCountdown(id));
  }

  /**
   * 获取销毁历史
   */
  async getDestroyHistory(
    userId: number,
    page: number = 0,
    size: number = 20
  ): Promise<DestroyRecord[]> {
    const response = await apiClient.get<any>('/self-destruct/history', {
      params: { userId, page, size },
    });

    return response.data;
  }

  /**
   * 开始本地倒计时（显示在UI上）
   */
  startLocalCountdown(
    messageId: number,
    durationSeconds: number,
    onTick: (remaining: number) => void,
    onComplete: () => void
  ): void {
    // 如果已有倒计时，先清除
    this.clearCountdown(messageId);

    let remaining = durationSeconds;

    const timer = setInterval(() => {
      remaining--;
      onTick(remaining);

      if (remaining <= 0) {
        this.clearCountdown(messageId);
        this.markAsDestroyed(messageId);
        onComplete();
      }
    }, 1000);

    this.activeCountdowns.set(messageId, {
      timer,
      remaining,
      callback: onTick,
    });
  }

  /**
   * 清除倒计时
   */
  private clearCountdown(messageId: number): void {
    const entry = this.activeCountdowns.get(messageId);
    if (entry) {
      clearInterval(entry.timer);
      this.activeCountdowns.delete(messageId);
    }
  }

  /**
   * 标记消息为已销毁
   */
  private markAsDestroyed(messageId: number): void {
    this.destroyedMessages.add(messageId);
    this.clearCountdown(messageId);
  }

  /**
   * 检查消息是否已销毁
   */
  isDestroyed(messageId: number): boolean {
    return this.destroyedMessages.has(messageId);
  }

  /**
   * 获取剩余倒计时
   */
  getRemainingSeconds(messageId: number): number {
    const entry = this.activeCountdowns.get(messageId);
    return entry ? entry.remaining : -1;
  }

  /**
   * 处理状态变化
   */
  private handleStatusChange(messageId: number, status: DestroyStatus): void {
    if (status === 'COUNTING') {
      // 消息开始倒计时，可能需要通知UI
      NotificationService.show({
        title: '🔓 消息已读',
        body: '阅后即焚消息已开始倒计时',
      });
    }
  }

  /**
   * 处理销毁事件
   */
  private handleDestroyed(messageId: number, reason: string): void {
    this.markAsDestroyed(messageId);
    
    NotificationService.show({
      title: '💥 消息已销毁',
      body: `阅后即焚消息已被销毁，原因: ${reason}`,
    });
  }

  /**
   * 格式化剩余时间
   */
  formatRemainingTime(seconds: number): string {
    if (seconds < 0) return '';
    if (seconds < 60) return `${seconds}秒`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}分${seconds % 60}秒`;
    return `${Math.floor(seconds / 3600)}小时${Math.floor((seconds % 3600) / 60)}分`;
  }

  /**
   * 获取计时器配置
   */
  getTimerConfig(type: TimerType): TimerConfig | undefined {
    return TIMER_CONFIGS.find(c => c.type === type);
  }

  /**
   * 选择计时器的UI渲染
   */
  renderTimerSelector(
    container: HTMLElement,
    onSelect: (config: TimerConfig, customSeconds?: number) => void
  ): void {
    container.innerHTML = `
      <div class="self-destruct-timer-selector">
        <div class="timer-title">阅后即焚</div>
        <div class="timer-options">
          ${TIMER_CONFIGS.map(c => `
            <button class="timer-option" data-type="${c.type}" data-seconds="${c.seconds}">
              <span class="timer-icon">${c.icon}</span>
              <span class="timer-label">${c.label}</span>
            </button>
          `).join('')}
        </div>
        <div class="custom-timer" style="display:none;">
          <input type="number" class="custom-seconds" placeholder="秒数" min="1" max="86400">
          <button class="custom-confirm">确定</button>
        </div>
      </div>
    `;

    const options = container.querySelectorAll('.timer-option');
    options.forEach(btn => {
      btn.addEventListener('click', () => {
        const type = btn.getAttribute('data-type') as TimerType;
        const seconds = parseInt(btn.getAttribute('data-seconds') || '0');
        const config = this.getTimerConfig(type);

        if (config && config.type !== 'CUSTOM') {
          onSelect(config);
        } else {
          // 显示自定义输入
          const customDiv = container.querySelector('.custom-timer') as HTMLElement;
          customDiv.style.display = 'flex';

          const confirmBtn = customDiv.querySelector('.custom-confirm');
          confirmBtn?.addEventListener('click', () => {
            const input = customDiv.querySelector('.custom-seconds') as HTMLInputElement;
            const secs = parseInt(input.value) || 30;
            onSelect({ type: 'CUSTOM', label: '自定义', seconds: secs, icon: '⚙️' }, secs);
          });
        }
      });
    });
  }

  /**
   * 渲染销毁状态UI
   */
  renderDestroyStatus(
    container: HTMLElement,
    messageId: number,
    status: DestroyStatus,
    remainingSeconds: number
  ): void {
    if (status === 'DESTROYED') {
      container.innerHTML = `
        <div class="destroyed-overlay">
          <div class="destroyed-icon">💥</div>
          <div class="destroyed-text">此消息已被销毁</div>
        </div>
      `;
      container.classList.add('destroyed');
    } else if (status === 'COUNTING') {
      container.innerHTML = `
        <div class="countdown-overlay">
          <div class="countdown-icon">🔥</div>
          <div class="countdown-text">${this.formatRemainingTime(remainingSeconds)}后销毁</div>
          <div class="countdown-bar">
            <div class="countdown-progress" style="width: ${(remainingSeconds / 30) * 100}%"></div>
          </div>
        </div>
      `;
    } else if (status === 'PENDING') {
      container.innerHTML = `
        <div class="pending-overlay">
          <div class="pending-icon">🔒</div>
          <div class="pending-text">阅后即焚消息</div>
        </div>
      `;
    }
  }
}

export const selfDestructService = new SelfDestructService();
