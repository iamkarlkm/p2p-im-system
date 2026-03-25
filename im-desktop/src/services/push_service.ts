/**
 * 推送通知服务 (Push Notification Service)
 * 
 * 统一管理 iOS (APNs)、Android (FCM) 和 Web 推送
 * 
 * @version 1.0.0
 * @date 2026-03-19
 */

type PushPlatform = 'ios' | 'android' | 'web';
type PushType = 'notification' | 'data' | 'silent';
type PushPriority = 'high' | 'normal' | 'low';

interface PushMessage {
  title?: string;
  body?: string;
  subtitle?: string;
  icon?: string;
  sound?: string;
  badge?: string;
  color?: string;
  tag?: string;
  channelId?: string;
  channelName?: string;
  category?: string;
  data?: Record<string, string>;
  priority?: PushPriority;
  type?: PushType;
  messageId?: string;
  conversationId?: string;
  conversationType?: string;
  senderId?: number;
  senderName?: string;
  senderAvatar?: string;
  mergeKey?: string;
}

interface DeviceInfo {
  platform: PushPlatform;
  version: string;
  model: string;
  name: string;
}

interface PushResult {
  success: boolean;
  message: string;
  messageId?: string;
  timestamp: number;
}

interface PushStats {
  total: number;
  success: number;
  failure: number;
  lastPushTime?: number;
}

interface PushSubscription {
  endpoint: string;
  keys?: {
    p256dh: string;
    auth: string;
  };
  expirationTime?: number;
}

interface NotificationPermission {
  granted: boolean;
  status: 'granted' | 'denied' | 'default' | 'prompt';
}

interface NotificationPayload {
  title: string;
  body: string;
  icon?: string;
  badge?: string;
  tag?: string;
  data?: Record<string, any>;
  actions?: NotificationAction[];
  requireInteraction?: boolean;
  silent?: boolean;
  vibrate?: number[];
  timestamp?: number;
}

interface NotificationAction {
  action: string;
  title: string;
  icon?: string;
  destructive?: boolean;
}

interface RemoteMessage {
  messageId: string;
  data: Record<string, string>;
  notification?: {
    title?: string;
    body?: string;
    image?: string;
  };
  from: string;
  sentTime: number;
  ttl?: number;
  collapseKey?: string;
  messageType?: string;
  priority?: string;
}

class PushService {
  private static instance: PushService;
  private platform: PushPlatform | null = null;
  private vapidKey: string = '';
  private swRegistration: ServiceWorkerRegistration | null = null;
  private messaging: any = null;
  private permission: NotificationPermission = { granted: false, status: 'default' };
  private deviceToken: string | null = null;
  private deviceInfo: DeviceInfo | null = null;
  private tokenRefreshCallback: ((token: string) => void) | null = null;
  private foregroundHandler: ((message: RemoteMessage) => void) | null = null;
  private backgroundHandler: ((message: RemoteMessage) => void) | null = null;
  private stats: PushStats = { total: 0, success: 0, failure: 0 };

  private constructor() {
    this.detectPlatform();
  }

  static getInstance(): PushService {
    if (!PushService.instance) {
      PushService.instance = new PushService();
    }
    return PushService.instance;
  }

  // ==================== 初始化 ====================

  /**
   * 初始化推送服务
   */
  async initialize(config: {
    vapidKey?: string;
    apiServer?: string;
    userId: number;
    tokenRefreshCallback?: (token: string) => void;
    foregroundHandler?: (message: RemoteMessage) => void;
    backgroundHandler?: (message: RemoteMessage) => void;
  }): Promise<boolean> {
    try {
      this.vapidKey = config.vapidKey || '';
      this.tokenRefreshCallback = config.tokenRefreshCallback || null;
      this.foregroundHandler = config.foregroundHandler || null;
      this.backgroundHandler = config.backgroundHandler || null;

      this.platform = this.getPlatform();
      this.deviceInfo = await this.getDeviceInfo();

      if (this.platform === 'web') {
        return await this.initWebPush(config);
      } else if (this.platform === 'ios' || this.platform === 'android') {
        return await this.initNativePush(config);
      }

      return false;
    } catch (error) {
      console.error('[Push] Initialize error:', error);
      return false;
    }
  }

  private async initWebPush(config: any): Promise<boolean> {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
      console.warn('[Push] Web Push not supported');
      return false;
    }

    try {
      // 请求通知权限
      const perm = await this.requestNotificationPermission();
      if (!perm.granted) {
        console.warn('[Push] Notification permission denied');
        return false;
      }

      // 注册 Service Worker
      this.swRegistration = await navigator.serviceWorker.register('/sw.js');
      await navigator.serviceWorker.ready;

      // 监听推送消息
      navigator.serviceWorker.addEventListener('message', (event) => {
        this.handleServiceWorkerMessage(event);
      });

      // 监听前台推送
      this.swRegistration.active?.postMessage({
        type: 'INIT_PUSH',
        config: { vapidKey: this.vapidKey }
      });

      return true;
    } catch (error) {
      console.error('[Push] Web init error:', error);
      return false;
    }
  }

  private async initNativePush(config: any): Promise<boolean> {
    // 原生平台需要调用原生模块
    // 这里使用模拟实现
    console.log('[Push] Native push init for platform:', this.platform);
    return true;
  }

  private detectPlatform(): void {
    const ua = navigator.userAgent.toLowerCase();
    if (/iphone|ipad|ipod/.test(ua)) {
      this.platform = 'ios';
    } else if (/android/.test(ua)) {
      this.platform = 'android';
    } else if (this.isSupported()) {
      this.platform = 'web';
    }
  }

  private getPlatform(): PushPlatform {
    if (this.platform) return this.platform;
    const ua = navigator.userAgent.toLowerCase();
    if (/iphone|ipad|ipod/.test(ua)) return 'ios';
    if (/android/.test(ua)) return 'android';
    return 'web';
  }

  // ==================== 权限管理 ====================

  /**
   * 请求通知权限
   */
  async requestNotificationPermission(): Promise<NotificationPermission> {
    if (!('Notification' in window)) {
      this.permission = { granted: false, status: 'denied' };
      return this.permission;
    }

    if (Notification.permission === 'granted') {
      this.permission = { granted: true, status: 'granted' };
      return this.permission;
    }

    if (Notification.permission === 'denied') {
      this.permission = { granted: false, status: 'denied' };
      return this.permission;
    }

    try {
      const permission = await Notification.requestPermission();
      this.permission = {
        granted: permission === 'granted',
        status: permission as any
      };
      return this.permission;
    } catch (error) {
      console.error('[Push] Permission request error:', error);
      this.permission = { granted: false, status: 'default' };
      return this.permission;
    }
  }

  /**
   * 获取当前通知权限状态
   */
  getPermission(): NotificationPermission {
    if ('Notification' in window) {
      this.permission = {
        granted: Notification.permission === 'granted',
        status: Notification.permission as any
      };
    }
    return this.permission;
  }

  // ==================== 订阅管理 ====================

  /**
   * 订阅 Web Push
   */
  async subscribe(): Promise<PushSubscription | null> {
    if (this.platform !== 'web' || !this.swRegistration) {
      return null;
    }

    try {
      const subscription = await this.swRegistration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: this.urlBase64ToUint8Array(this.vapidKey)
      });

      const sub = subscription as PushSubscription;
      console.log('[Push] Subscribed:', sub.endpoint);

      // 发送订阅信息到服务器
      await this.sendSubscriptionToServer(sub);

      return sub;
    } catch (error) {
      console.error('[Push] Subscribe error:', error);
      return null;
    }
  }

  /**
   * 取消订阅
   */
  async unsubscribe(): Promise<boolean> {
    if (this.platform !== 'web' || !this.swRegistration) {
      return false;
    }

    try {
      const subscription = await this.swRegistration.pushManager.getSubscription();
      if (subscription) {
        await subscription.unsubscribe();
        await this.removeSubscriptionFromServer((subscription as any).endpoint);
      }
      return true;
    } catch (error) {
      console.error('[Push] Unsubscribe error:', error);
      return false;
    }
  }

  /**
   * 获取当前订阅
   */
  async getSubscription(): Promise<PushSubscription | null> {
    if (this.platform !== 'web' || !this.swRegistration) {
      return null;
    }

    try {
      return await this.swRegistration.pushManager.getSubscription() as PushSubscription | null;
    } catch (error) {
      console.error('[Push] Get subscription error:', error);
      return null;
    }
  }

  // ==================== 设备Token ====================

  /**
   * 获取设备Token
   */
  async getToken(): Promise<string | null> {
    if (this.platform === 'web') {
      const sub = await this.getSubscription();
      return sub?.endpoint || null;
    }

    // 原生平台
    if (this.platform === 'ios' || this.platform === 'android') {
      return this.deviceToken;
    }

    return null;
  }

  /**
   * 注册设备Token到服务器
   */
  async registerToken(token: string, extra?: Record<string, any>): Promise<boolean> {
    try {
      const response = await fetch('/api/push/device/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: this.getCurrentUserId(),
          token,
          platform: this.platform,
          deviceType: this.deviceInfo?.model,
          deviceName: this.deviceInfo?.name,
          osVersion: this.deviceInfo?.version,
          appVersion: '1.0.0',
          ...extra
        })
      });

      if (response.ok) {
        this.deviceToken = token;
        return true;
      }
      return false;
    } catch (error) {
      console.error('[Push] Register token error:', error);
      return false;
    }
  }

  /**
   * 更新Token
   */
  async updateToken(oldToken: string, newToken: string): Promise<boolean> {
    try {
      const response = await fetch('/api/push/device/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ oldToken, newToken })
      });
      return response.ok;
    } catch (error) {
      console.error('[Push] Update token error:', error);
      return false;
    }
  }

  /**
   * 删除Token
   */
  async deleteToken(token: string): Promise<boolean> {
    try {
      const response = await fetch(`/api/push/device/${encodeURIComponent(token)}`, {
        method: 'DELETE'
      });
      return response.ok;
    } catch (error) {
      console.error('[Push] Delete token error:', error);
      return false;
    }
  }

  // ==================== 通知显示 ====================

  /**
   * 显示本地通知
   */
  async showNotification(options: NotificationPayload): Promise<Notification | null> {
    if (!this.permission.granted && this.platform === 'web') {
      console.warn('[Push] Notification permission not granted');
      return null;
    }

    try {
      let notification: Notification;

      if (this.platform === 'web' && this.swRegistration) {
        notification = await this.swRegistration.showNotification(options.title, {
          body: options.body,
          icon: options.icon || '/icons/notification-icon.png',
          badge: options.badge || '/icons/badge-icon.png',
          tag: options.tag,
          data: options.data,
          requireInteraction: options.requireInteraction,
          vibrate: options.vibrate,
          timestamp: options.timestamp,
          actions: options.actions
        });
      } else {
        notification = new Notification(options.title, {
          body: options.body,
          icon: options.icon,
          tag: options.tag,
          data: options.data
        });
      }

      notification.onclick = () => {
        window.focus();
        notification.close();
        if (options.data?.conversationId) {
          window.location.hash = `#/chat/${options.data.conversationId}`;
        }
      };

      return notification;
    } catch (error) {
      console.error('[Push] Show notification error:', error);
      return null;
    }
  }

  /**
   * 处理推送消息
   */
  private handlePushMessage(message: RemoteMessage): void {
    console.log('[Push] Handle message:', message);

    // 如果应用在前台，根据配置决定如何处理
    if (document.hasFocus() && this.foregroundHandler) {
      this.foregroundHandler(message);
      return;
    }

    // 应用在后台或未聚焦，显示通知
    if (message.notification) {
      this.showNotification({
        title: message.notification.title || '',
        body: message.notification.body || '',
        icon: message.notification.image,
        tag: message.collapseKey,
        data: message.data,
        timestamp: message.sentTime,
        requireInteraction: message.priority === 'high'
      });
    }

    this.stats.success++;
  }

  private handleServiceWorkerMessage(event: MessageEvent): void {
    const { type, data } = event.data;

    switch (type) {
      case 'PUSH_RECEIVED':
        this.handlePushMessage(data);
        break;
      case 'TOKEN_REFRESH':
        if (this.tokenRefreshCallback) {
          this.tokenRefreshCallback(data.token);
        }
        break;
      case 'NOTIFICATION_CLICKED':
        if (data.conversationId) {
          window.location.hash = `#/chat/${data.conversationId}`;
        }
        break;
    }
  }

  // ==================== 推送发送 ====================

  /**
   * 发送测试推送
   */
  async sendTestPush(userId: number): Promise<PushResult> {
    try {
      const response = await fetch(`/api/push/test/${userId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      });

      const result = await response.json();
      return {
        success: result.success,
        message: result.message,
        timestamp: Date.now()
      };
    } catch (error) {
      return {
        success: false,
        message: String(error),
        timestamp: Date.now()
      };
    }
  }

  /**
   * 发送推送
   */
  async sendPush(userId: number, message: PushMessage): Promise<PushResult> {
    this.stats.total++;

    try {
      const response = await fetch('/api/push/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          ...message
        })
      });

      const result = await response.json();
      return {
        success: result.success,
        message: result.data?.message || result.message,
        timestamp: Date.now()
      };
    } catch (error) {
      this.stats.failure++;
      return {
        success: false,
        message: String(error),
        timestamp: Date.now()
      };
    }
  }

  // ==================== 工具方法 ====================

  private urlBase64ToUint8Array(base64String: string): Uint8Array {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }

  private async sendSubscriptionToServer(subscription: PushSubscription): Promise<void> {
    await this.registerToken(subscription.endpoint);
  }

  private async removeSubscriptionFromServer(endpoint: string): Promise<void> {
    await this.deleteToken(endpoint);
  }

  private getCurrentUserId(): number {
    // 从 localStorage 或全局变量获取当前用户ID
    return parseInt(localStorage.getItem('userId') || '0', 10);
  }

  private async getDeviceInfo(): Promise<DeviceInfo> {
    const ua = navigator.userAgent;
    return {
      platform: this.getPlatform(),
      version: this.getOSVersion(ua),
      model: this.getDeviceModel(ua),
      name: 'Web Browser'
    };
  }

  private getOSVersion(ua: string): string {
    if (/Windows/.test(ua)) return 'Windows';
    if (/Mac/.test(ua)) return 'macOS';
    if (/iPhone|iPad/.test(ua)) {
      const match = ua.match(/OS ([\d_]+)/);
      return match ? `iOS ${match[1].replace(/_/g, '.')}` : 'iOS';
    }
    if (/Android/.test(ua)) {
      const match = ua.match(/Android ([\d.]+)/);
      return match ? `Android ${match[1]}` : 'Android';
    }
    return 'Unknown';
  }

  private getDeviceModel(ua: string): string {
    if (/iPhone/.test(ua)) {
      const match = ua.match(/iPhone.*?OS ([\d_]+)/);
      return match ? `iPhone` : 'iPhone';
    }
    if (/iPad/.test(ua)) return 'iPad';
    if (/Android/.test(ua)) return 'Android Device';
    return 'Desktop';
  }

  private isSupported(): boolean {
    return 'Notification' in window && 'serviceWorker' in navigator && 'PushManager' in window;
  }

  // ==================== 统计 ====================

  getStats(): PushStats {
    return { ...this.stats };
  }

  resetStats(): void {
    this.stats = { total: 0, success: 0, failure: 0 };
  }

  // ==================== 便捷方法 ====================

  /**
   * 构建聊天消息推送配置
   */
  buildChatNotification(senderName: string, content: string, conversationId: string, 
                       avatar?: string): NotificationPayload {
    return {
      title: senderName,
      body: this.truncateContent(content, 100),
      icon: avatar || '/icons/chat-icon.png',
      tag: `chat:${conversationId}`,
      requireInteraction: false,
      data: {
        type: 'chat_message',
        conversationId,
        content
      }
    };
  }

  /**
   * 构建系统通知
   */
  buildSystemNotification(title: string, content: string): NotificationPayload {
    return {
      title,
      body: content,
      icon: '/icons/system-icon.png',
      tag: 'system-notification',
      requireInteraction: true,
      data: { type: 'system_notification' }
    };
  }

  private truncateContent(content: string, maxLength: number): string {
    if (!content) return '';
    if (content.length <= maxLength) return content;
    return content.substring(0, maxLength) + '...';
  }
}

// ==================== 导出 ====================

export const pushService = PushService.getInstance();
export type { 
  PushMessage, 
  PushResult, 
  PushStats, 
  DeviceInfo, 
  PushSubscription,
  NotificationPermission,
  NotificationPayload,
  NotificationAction,
  RemoteMessage
};
