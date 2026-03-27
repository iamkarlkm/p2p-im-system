import { EventEmitter } from 'events';

export interface UserStatus {
  userId: string;
  status: 'online' | 'offline' | 'away' | 'busy' | 'invisible';
  customStatus?: string;
  statusMessage?: string;
  lastSeen?: Date;
  isVisible: boolean;
}

export interface StatusUpdatePayload {
  userId: string;
  status: string;
  customStatus?: string;
  timestamp: number;
}

/**
 * 用户状态管理服务
 * 管理当前用户状态、订阅其他用户状态变化
 */
export class UserStatusService extends EventEmitter {
  private currentUserId: string | null = null;
  private currentStatus: UserStatus | null = null;
  private userStatuses: Map<string, UserStatus> = new Map();
  private subscriptions: Set<string> = new Set();
  private heartbeatInterval: NodeJS.Timeout | null = null;
  private apiBaseUrl: string = 'http://localhost:8080/api';

  // 心跳间隔（毫秒）
  private readonly HEARTBEAT_INTERVAL = 30000;
  // 空闲检测间隔
  private readonly IDLE_CHECK_INTERVAL = 60000;
  // 空闲超时时间（毫秒）
  private readonly IDLE_TIMEOUT = 300000; // 5分钟

  constructor(apiUrl?: string) {
    super();
    if (apiUrl) {
      this.apiBaseUrl = apiUrl;
    }
    this.setupActivityTracking();
  }

  /**
   * 初始化服务
   */
  public initialize(userId: string, token: string): void {
    this.currentUserId = userId;
    this.apiBaseUrl = `${this.apiBaseUrl}`;
    this.startHeartbeat();
    this.setUserOnline();
  }

  /**
   * 设置当前用户状态
   */
  public async setStatus(status: UserStatus['status'], customStatus?: string, statusMessage?: string): Promise<boolean> {
    if (!this.currentUserId) {
      console.error('UserStatusService: 未初始化');
      return false;
    }

    try {
      const response = await fetch(`${this.apiBaseUrl}/status/custom`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userId: this.currentUserId,
          customStatus: status,
          statusMessage: statusMessage || '',
        }),
      });

      if (response.ok) {
        if (this.currentStatus) {
          this.currentStatus.status = status;
          this.currentStatus.customStatus = customStatus;
          this.currentStatus.statusMessage = statusMessage;
        }
        this.emit('statusChanged', this.currentStatus);
        return true;
      }
      return false;
    } catch (error) {
      console.error('设置状态失败:', error);
      return false;
    }
  }

  /**
   * 获取当前用户状态
   */
  public getCurrentStatus(): UserStatus | null {
    return this.currentStatus;
  }

  /**
   * 获取指定用户状态
   */
  public async getUserStatus(userId: string): Promise<UserStatus | null> {
    // 先检查缓存
    if (this.userStatuses.has(userId)) {
      return this.userStatuses.get(userId)!;
    }

    if (!this.currentUserId) {
      return null;
    }

    try {
      const response = await fetch(`${this.apiBaseUrl}/status/${userId}?requesterId=${this.currentUserId}`);
      if (response.ok) {
        const status = await response.json();
        this.userStatuses.set(userId, status);
        return status;
      }
      return null;
    } catch (error) {
      console.error('获取用户状态失败:', error);
      return null;
    }
  }

  /**
   * 批量获取用户状态
   */
  public async getUserStatuses(userIds: string[]): Promise<UserStatus[]> {
    if (!this.currentUserId || userIds.length === 0) {
      return [];
    }

    try {
      const response = await fetch(`${this.apiBaseUrl}/status/batch?requesterId=${this.currentUserId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userIds),
      });

      if (response.ok) {
        const statuses: UserStatus[] = await response.json();
        statuses.forEach(status => {
          this.userStatuses.set(status.userId, status);
        });
        return statuses;
      }
      return [];
    } catch (error) {
      console.error('批量获取用户状态失败:', error);
      return [];
    }
  }

  /**
   * 订阅用户状态
   */
  public async subscribeUserStatus(userId: string): Promise<boolean> {
    if (!this.currentUserId) {
      return false;
    }

    try {
      const response = await fetch(`${this.apiBaseUrl}/status/subscribe`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          subscriberId: this.currentUserId,
          targetUserId: userId,
        }),
      });

      if (response.ok) {
        this.subscriptions.add(userId);
        this.emit('subscribed', userId);
        return true;
      }
      return false;
    } catch (error) {
      console.error('订阅用户状态失败:', error);
      return false;
    }
  }

  /**
   * 取消订阅用户状态
   */
  public async unsubscribeUserStatus(userId: string): Promise<boolean> {
    if (!this.currentUserId) {
      return false;
    }

    try {
      const response = await fetch(`${this.apiBaseUrl}/status/unsubscribe`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          subscriberId: this.currentUserId,
          targetUserId: userId,
        }),
      });

      if (response.ok) {
        this.subscriptions.delete(userId);
        this.emit('unsubscribed', userId);
        return true;
      }
      return false;
    } catch (error) {
      console.error('取消订阅失败:', error);
      return false;
    }
  }

  /**
   * 处理状态更新消息
   */
  public handleStatusUpdate(payload: StatusUpdatePayload): void {
    const { userId, status, customStatus, timestamp } = payload;
    
    let userStatus = this.userStatuses.get(userId);
    if (!userStatus) {
      userStatus = {
        userId,
        status: status as UserStatus['status'],
        customStatus,
        isVisible: true,
      };
      this.userStatuses.set(userId, userStatus);
    } else {
      userStatus.status = status as UserStatus['status'];
      userStatus.customStatus = customStatus;
      userStatus.lastSeen = new Date(timestamp);
    }

    this.emit('userStatusChanged', userStatus);
  }

  /**
   * 获取在线用户列表
   */
  public async getOnlineUsers(): Promise<UserStatus[]> {
    try {
      const response = await fetch(`${this.apiBaseUrl}/status/online/list`);
      if (response.ok) {
        const data = await response.json();
        if (data.success) {
          return data.users || [];
        }
      }
      return [];
    } catch (error) {
      console.error('获取在线用户列表失败:', error);
      return [];
    }
  }

  /**
   * 检查用户是否在线
   */
  public async isUserOnline(userId: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiBaseUrl}/status/online/check/${userId}`);
      if (response.ok) {
        const data = await response.json();
        return data.isOnline || false;
      }
      return false;
    } catch (error) {
      console.error('检查用户在线状态失败:', error);
      return false;
    }
  }

  /**
   * 设置用户上线
   */
  private async setUserOnline(): Promise<void> {
    if (!this.currentUserId) return;

    try {
      await fetch(`${this.apiBaseUrl}/status/online`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userId: this.currentUserId,
          deviceType: 'desktop',
        }),
      });
      
      this.emit('online');
    } catch (error) {
      console.error('设置用户上线失败:', error);
    }
  }

  /**
   * 设置用户离线
   */
  public async setUserOffline(): Promise<void> {
    if (!this.currentUserId) return;

    try {
      await fetch(`${this.apiBaseUrl}/status/offline`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userId: this.currentUserId,
        }),
      });
      
      this.stopHeartbeat();
      this.emit('offline');
    } catch (error) {
      console.error('设置用户离线失败:', error);
    }
  }

  /**
   * 更新用户活动
   */
  private async updateActivity(): Promise<void> {
    if (!this.currentUserId) return;

    try {
      await fetch(`${this.apiBaseUrl}/status/activity`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userId: this.currentUserId,
        }),
      });
    } catch (error) {
      console.error('更新活动状态失败:', error);
    }
  }

  /**
   * 启动心跳
   */
  private startHeartbeat(): void {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval);
    }

    this.heartbeatInterval = setInterval(() => {
      this.updateActivity();
    }, this.HEARTBEAT_INTERVAL);
  }

  /**
   * 停止心跳
   */
  private stopHeartbeat(): void {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval);
      this.heartbeatInterval = null;
    }
  }

  /**
   * 设置活动追踪
   */
  private setupActivityTracking(): void {
    // 监听用户活动以检测空闲状态
    let lastActivity = Date.now();

    const updateActivity = () => {
      lastActivity = Date.now();
      if (this.currentStatus?.status === 'away') {
        this.setStatus('online');
      }
    };

    // 监听各种用户活动事件
    if (typeof window !== 'undefined') {
      window.addEventListener('mousemove', updateActivity);
      window.addEventListener('keydown', updateActivity);
      window.addEventListener('click', updateActivity);
      window.addEventListener('scroll', updateActivity);

      // 定期检查空闲状态
      setInterval(() => {
        const idleTime = Date.now() - lastActivity;
        if (idleTime > this.IDLE_TIMEOUT && this.currentStatus?.status === 'online') {
          this.setStatus('away');
        }
      }, this.IDLE_CHECK_INTERVAL);
    }
  }

  /**
   * 获取订阅列表
   */
  public getSubscriptions(): string[] {
    return Array.from(this.subscriptions);
  }

  /**
   * 清理资源
   */
  public dispose(): void {
    this.setUserOffline();
    this.stopHeartbeat();
    this.userStatuses.clear();
    this.subscriptions.clear();
    this.removeAllListeners();
  }
}

// 导出单例实例
export const userStatusService = new UserStatusService();
export default UserStatusService;
