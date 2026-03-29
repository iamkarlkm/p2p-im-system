/**
 * 投票服务 (Poll Service)
 * 桌面端 TypeScript 实现
 * 
 * 功能：创建投票、投票、获取结果、WebSocket实时更新
 */

// ==================== 类型定义 ====================

export interface PollOption {
  optionId: string;
  optionText: string;
  voteCount: number;
  percentage: number;
  hasVoted: boolean;
  voterIds?: string[];
}

export interface PollResult {
  pollId: string;
  creatorId: string;
  groupId: string;
  conversationId: string;
  question: string;
  options: PollOption[];
  anonymous: boolean;
  multiSelect: boolean;
  deadline: string | null;
  status: 'ACTIVE' | 'CLOSED' | 'CANCELLED';
  totalVotes: number;
  totalParticipants: number;
  createdAt: string;
  updatedAt: string;
  allowOptionAdd: boolean;
  messageId: string;
  hasVoted: boolean;
  votedOptionIds: string[];
  remainingSeconds: number | null;
}

export interface CreatePollRequest {
  creatorId: string;
  groupId: string;
  conversationId?: string;
  question: string;
  optionTexts: string[];
  anonymous?: boolean;
  multiSelect?: boolean;
  deadline?: string;
  allowOptionAdd?: boolean;
  messageId?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

type PollEventCallback = (event: string, poll: PollResult) => void;

// ==================== 投票服务类 ====================

export class PollService {
  private baseUrl: string;
  private wsUrl: string;
  private userId: string;
  private eventListeners: Map<string, Set<PollEventCallback>> = new Map();
  private ws: WebSocket | null = null;
  private wsReconnectTimer: NodeJS.Timeout | null = null;
  private heartbeatTimer: NodeJS.Timeout | null = null;

  constructor(config: { baseUrl?: string; wsUrl?: string; userId: string }) {
    this.baseUrl = config.baseUrl || '/api';
    this.wsUrl = config.wsUrl || `ws://${window.location.host}/ws/poll`;
    this.userId = config.userId;
  }

  // ==================== HTTP API ====================

  /**
   * 创建投票
   */
  async createPoll(request: CreatePollRequest): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 获取投票详情
   */
  async getPoll(pollId: string): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}?userId=${this.userId}`);
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 投票（单选或多选）
   */
  async vote(pollId: string, optionIds: string[]): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}/vote`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: this.userId, optionIds }),
    });
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 取消投票
   */
  async cancelVote(pollId: string): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}/vote?userId=${this.userId}`, {
      method: 'DELETE',
    });
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 结束投票
   */
  async closePoll(pollId: string): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}/close`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: this.userId }),
    });
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 删除投票
   */
  async deletePoll(pollId: string): Promise<boolean> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}?userId=${this.userId}`, {
      method: 'DELETE',
    });
    const result: ApiResponse<boolean> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 添加投票选项
   */
  async addOption(pollId: string, optionText: string): Promise<PollResult> {
    const response = await fetch(`${this.baseUrl}/poll/${pollId}/options`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: this.userId, optionText }),
    });
    const result: ApiResponse<PollResult> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 获取群组所有投票
   */
  async getGroupPolls(groupId: string): Promise<PollResult[]> {
    const response = await fetch(`${this.baseUrl}/poll/group/${groupId}?userId=${this.userId}`);
    const result: ApiResponse<PollResult[]> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 获取群组进行中的投票
   */
  async getGroupActivePolls(groupId: string): Promise<PollResult[]> {
    const response = await fetch(`${this.baseUrl}/poll/group/${groupId}/active?userId=${this.userId}`);
    const result: ApiResponse<PollResult[]> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  /**
   * 获取用户参与的投票
   */
  async getUserVotedPolls(): Promise<PollResult[]> {
    const response = await fetch(`${this.baseUrl}/poll/user/${this.userId}/voted`);
    const result: ApiResponse<PollResult[]> = await response.json();
    if (!result.success) throw new Error(result.message);
    return result.data;
  }

  // ==================== WebSocket 实时更新 ====================

  /**
   * 连接 WebSocket
   */
  connect(groupId?: string): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) return;

    const url = groupId 
      ? `${this.wsUrl}?userId=${this.userId}&groupId=${groupId}`
      : `${this.wsUrl}?userId=${this.userId}`;

    try {
      this.ws = new WebSocket(url);

      this.ws.onopen = () => {
        console.log('[PollService] WebSocket connected');
        this.startHeartbeat();
        this.stopReconnect();
      };

      this.ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data);
          if (msg.type && msg.type.startsWith('poll.')) {
            this.emit(msg.type, msg.poll);
          }
        } catch (e) {
          console.error('[PollService] Failed to parse message:', e);
        }
      };

      this.ws.onclose = () => {
        console.log('[PollService] WebSocket disconnected');
        this.stopHeartbeat();
        this.scheduleReconnect();
      };

      this.ws.onerror = (error) => {
        console.error('[PollService] WebSocket error:', error);
      };
    } catch (e) {
      console.error('[PollService] Failed to connect WebSocket:', e);
      this.scheduleReconnect();
    }
  }

  /**
   * 断开 WebSocket
   */
  disconnect(): void {
    this.stopHeartbeat();
    this.stopReconnect();
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  /**
   * 订阅投票事件
   */
  on(event: string, callback: PollEventCallback): void {
    if (!this.eventListeners.has(event)) {
      this.eventListeners.set(event, new Set());
    }
    this.eventListeners.get(event)!.add(callback);
  }

  /**
   * 取消订阅
   */
  off(event: string, callback: PollEventCallback): void {
    const listeners = this.eventListeners.get(event);
    if (listeners) {
      listeners.delete(callback);
    }
  }

  private emit(event: string, poll: PollResult): void {
    const listeners = this.eventListeners.get(event);
    if (listeners) {
      listeners.forEach(cb => cb(event, poll));
    }
    // 触发通配符监听器
    const wildcardListeners = this.eventListeners.get('*');
    if (wildcardListeners) {
      wildcardListeners.forEach(cb => cb(event, poll));
    }
  }

  private startHeartbeat(): void {
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'ping' }));
      }
    }, 30000);
  }

  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  private scheduleReconnect(): void {
    if (this.wsReconnectTimer) return;
    this.wsReconnectTimer = setTimeout(() => {
      this.wsReconnectTimer = null;
      this.connect();
    }, 5000);
  }

  private stopReconnect(): void {
    if (this.wsReconnectTimer) {
      clearTimeout(this.wsReconnectTimer);
      this.wsReconnectTimer = null;
    }
  }

  // ==================== 快捷创建方法 ====================

  /**
   * 快捷创建投票（从聊天界面）
   */
  async createQuickPoll(
    groupId: string,
    question: string,
    options: string[],
    config?: {
      anonymous?: boolean;
      multiSelect?: boolean;
      deadlineMinutes?: number;
    }
  ): Promise<PollResult> {
    const request: CreatePollRequest = {
      creatorId: this.userId,
      groupId,
      question,
      optionTexts: options,
      anonymous: config?.anonymous ?? false,
      multiSelect: config?.multiSelect ?? false,
      deadline: config?.deadlineMinutes
        ? new Date(Date.now() + config.deadlineMinutes * 60000).toISOString()
        : undefined,
    };
    return this.createPoll(request);
  }
}

// ==================== 工具函数 ====================

/**
 * 格式化剩余时间
 */
export function formatRemainingTime(seconds: number | null): string {
  if (seconds === null || seconds <= 0) return '已结束';
  
  if (seconds < 60) return `${seconds}秒`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}小时${Math.floor((seconds % 3600) / 60)}分`;
  return `${Math.floor(seconds / 86400)}天${Math.floor((seconds % 86400) / 3600)}小时`;
}

/**
 * 格式化投票数
 */
export function formatVoteCount(count: number): string {
  if (count === 0) return '暂无投票';
  if (count === 1) return '1 票';
  return `${count} 票`;
}

/**
 * 获取状态标签
 */
export function getStatusLabel(status: string): string {
  switch (status) {
    case 'ACTIVE': return '进行中';
    case 'CLOSED': return '已结束';
    case 'CANCELLED': return '已取消';
    default: return status;
  }
}

/**
 * 获取状态颜色
 */
export function getStatusColor(status: string): string {
  switch (status) {
    case 'ACTIVE': return '#4CAF50';
    case 'CLOSED': return '#9E9E9E';
    case 'CANCELLED': return '#F44336';
    default: return '#9E9E9E';
  }
}

// ==================== 默认导出 ====================

export default PollService;
