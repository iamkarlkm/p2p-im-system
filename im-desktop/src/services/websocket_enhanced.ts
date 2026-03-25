/**
 * Enhanced WebSocket Service (TypeScript)
 * 
 * 增强型 WebSocket 服务
 * 提供连接管理、心跳保活、自动重连等功能
 */

// ==================== 类型定义 ====================

export enum ConnectionState {
  DISCONNECTED = 'disconnected',
  CONNECTING = 'connecting',
  CONNECTED = 'connected',
  RECONNECTING = 'reconnecting',
  ERROR = 'error'
}

export interface WebSocketConfig {
  url: string;
  reconnectEnabled: boolean;
  reconnectInterval: number;
  maxReconnectAttempts: number;
  heartbeatInterval: number;
  heartbeatTimeout: number;
  messageQueueSize: number;
  compressionEnabled: boolean;
}

export interface WebSocketMessage {
  type: string;
  messageId?: string;
  senderId?: string;
  targetId?: string;
  timestamp: number;
  data: Record<string, any>;
}

export interface ConnectionStats {
  connectedAt?: number;
  totalSent: number;
  totalReceived: number;
  totalHeartbeats: number;
  reconnectAttempts: number;
  lastMessageAt?: number;
}

// ==================== 默认配置 ====================

const DEFAULT_CONFIG: WebSocketConfig = {
  url: 'ws://localhost:8080/ws',
  reconnectEnabled: true,
  reconnectInterval: 3000,
  maxReconnectAttempts: 5,
  heartbeatInterval: 30000,
  heartbeatTimeout: 60000,
  messageQueueSize: 100,
  compressionEnabled: true
};

// ==================== WebSocket 服务类 ====================

class EnhancedWebSocketService {
  private static instance: EnhancedWebSocketService;
  private ws: WebSocket | null = null;
  private config: WebSocketConfig;
  private state: ConnectionState = ConnectionState.DISCONNECTED;
  private reconnectAttempts: number = 0;
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private messageQueue: WebSocketMessage[] = [];
  private stats: ConnectionStats = {
    totalSent: 0,
    totalReceived: 0,
    totalHeartbeats: 0,
    reconnectAttempts: 0
  };

  // 事件处理器
  private onOpenHandlers: ((event: Event) => void)[] = [];
  private onCloseHandlers: ((event: CloseEvent) => void)[] = [];
  private onErrorHandlers: ((event: Event) => void)[] = [];
  private onMessageHandlers: ((message: WebSocketMessage) => void)[] = [];
  private onStateChangeHandlers: ((state: ConnectionState) => void)[] = [];

  private constructor(config: Partial<WebSocketConfig> = {}) {
    this.config = { ...DEFAULT_CONFIG, ...config };
  }

  static getInstance(config?: Partial<WebSocketConfig>): EnhancedWebSocketService {
    if (!EnhancedWebSocketService.instance) {
      EnhancedWebSocketService.instance = new EnhancedWebSocketService(config);
    }
    return EnhancedWebSocketService.instance;
  }

  // ==================== 连接管理 ====================

  connect(url?: string): Promise<void> {
    if (this.state === ConnectionState.CONNECTED || this.state === ConnectionState.CONNECTING) {
      console.warn('[WS] Already connected or connecting');
      return Promise.resolve();
    }

    const wsUrl = url || this.config.url;
    this.setState(ConnectionState.CONNECTING);

    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(wsUrl);

        this.ws.onopen = (event) => {
          console.info('[WS] Connected to:', wsUrl);
          this.setState(ConnectionState.CONNECTED);
          this.reconnectAttempts = 0;
          this.stats.connectedAt = Date.now();
          this.startHeartbeat();
          this.flushMessageQueue();
          this.notifyOpen(event);
          resolve();
        };

        this.ws.onclose = (event) => {
          console.info('[WS] Disconnected, code:', event.code, 'reason:', event.reason);
          this.stopHeartbeat();
          this.setState(ConnectionState.DISCONNECTED);
          this.notifyClose(event);
          this.handleReconnect();
        };

        this.ws.onerror = (event) => {
          console.error('[WS] Error:', event);
          this.setState(ConnectionState.ERROR);
          this.notifyError(event);
        };

        this.ws.onmessage = (event) => {
          this.handleMessage(event);
        };
      } catch (error) {
        this.setState(ConnectionState.ERROR);
        reject(error);
      }
    });
  }

  disconnect(code: number = 1000, reason: string = 'Normal closure'): void {
    this.stopHeartbeat();
    this.stopReconnect();

    if (this.ws) {
      this.ws.close(code, reason);
      this.ws = null;
    }

    this.setState(ConnectionState.DISCONNECTED);
  }

  private handleReconnect(): void {
    if (!this.config.reconnectEnabled) {
      return;
    }

    if (this.reconnectAttempts >= this.config.maxReconnectAttempts) {
      console.error('[WS] Max reconnect attempts reached');
      return;
    }

    this.reconnectAttempts++;
    this.stats.reconnectAttempts = this.reconnectAttempts;
    this.setState(ConnectionState.RECONNECTING);

    console.info(`[WS] Reconnecting in ${this.config.reconnectInterval}ms (attempt ${this.reconnectAttempts})`);

    this.reconnectTimer = setTimeout(() => {
      this.connect().catch((error) => {
        console.error('[WS] Reconnect failed:', error);
      });
    }, this.config.reconnectInterval);
  }

  // ==================== 心跳机制 ====================

  private startHeartbeat(): void {
    this.stopHeartbeat();

    this.heartbeatTimer = setInterval(() => {
      this.sendHeartbeat();
    }, this.config.heartbeatInterval);
  }

  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  private sendHeartbeat(): void {
    this.stats.totalHeartbeats++;
    this.send({
      type: 'heartbeat',
      timestamp: Date.now(),
      data: {}
    });
  }

  // ==================== 消息发送 ====================

  send(message: WebSocketMessage): boolean {
    const messageWithId = {
      ...message,
      messageId: message.messageId || this.generateMessageId(),
      timestamp: message.timestamp || Date.now()
    };

    if (this.state !== ConnectionState.CONNECTED) {
      this.queueMessage(messageWithId);
      return false;
    }

    try {
      this.ws!.send(JSON.stringify(messageWithId));
      this.stats.totalSent++;
      return true;
    } catch (error) {
      console.error('[WS] Send error:', error);
      this.queueMessage(messageWithId);
      return false;
    }
  }

  private queueMessage(message: WebSocketMessage): void {
    if (this.messageQueue.length >= this.config.messageQueueSize) {
      this.messageQueue.shift(); // 移除最旧的消息
    }
    this.messageQueue.push(message);
  }

  private flushMessageQueue(): void {
    while (this.messageQueue.length > 0) {
      const message = this.messageQueue.shift()!;
      this.send(message);
    }
  }

  // ==================== 消息接收 ====================

  private handleMessage(event: MessageEvent): void {
    try {
      const message: WebSocketMessage = JSON.parse(event.data);
      this.stats.totalReceived++;
      this.stats.lastMessageAt = Date.now();

      // 处理心跳相关消息
      if (message.type === 'heartbeat_ack') {
        console.debug('[WS] Heartbeat ack received');
        return;
      }

      // 通知所有处理器
      this.notifyMessage(message);
    } catch (error) {
      console.error('[WS] Parse error:', error);
    }
  }

  // ==================== 事件订阅 ====================

  onOpen(handler: (event: Event) => void): () => void {
    this.onOpenHandlers.push(handler);
    return () => {
      this.onOpenHandlers = this.onOpenHandlers.filter(h => h !== handler);
    };
  }

  onClose(handler: (event: CloseEvent) => void): () => void {
    this.onCloseHandlers.push(handler);
    return () => {
      this.onCloseHandlers = this.onCloseHandlers.filter(h => h !== handler);
    };
  }

  onError(handler: (event: Event) => void): () => void {
    this.onErrorHandlers.push(handler);
    return () => {
      this.onErrorHandlers = this.onErrorHandlers.filter(h => h !== handler);
    };
  }

  onMessage(handler: (message: WebSocketMessage) => void): () => void {
    this.onMessageHandlers.push(handler);
    return () => {
      this.onMessageHandlers = this.onMessageHandlers.filter(h => h !== handler);
    };
  }

  onStateChange(handler: (state: ConnectionState) => void): () => void {
    this.onStateChangeHandlers.push(handler);
    return () => {
      this.onStateChangeHandlers = this.onStateChangeHandlers.filter(h => h !== handler);
    };
  }

  private notifyOpen(event: Event): void {
    this.onOpenHandlers.forEach(h => h(event));
  }

  private notifyClose(event: CloseEvent): void {
    this.onCloseHandlers.forEach(h => h(event));
  }

  private notifyError(event: Event): void {
    this.onErrorHandlers.forEach(h => h(event));
  }

  private notifyMessage(message: WebSocketMessage): void {
    this.onMessageHandlers.forEach(h => h(message));
  }

  // ==================== 状态管理 ====================

  private setState(state: ConnectionState): void {
    if (this.state !== state) {
      this.state = state;
      this.onStateChangeHandlers.forEach(h => h(state));
    }
  }

  getState(): ConnectionState {
    return this.state;
  }

  isConnected(): boolean {
    return this.state === ConnectionState.CONNECTED;
  }

  // ==================== 统计 ====================

  getStats(): ConnectionStats {
    return { ...this.stats };
  }

  resetStats(): void {
    this.stats = {
      totalSent: 0,
      totalReceived: 0,
      totalHeartbeats: 0,
      reconnectAttempts: this.stats.reconnectAttempts,
      connectedAt: this.stats.connectedAt
    };
  }

  // ==================== 工具方法 ====================

  private generateMessageId(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  private stopReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
  }

  // ==================== 快捷发送方法 ====================

  sendChat(toUserId: string, content: string, extra: Record<string, any> = {}): boolean {
    return this.send({
      type: 'chat',
      targetId: toUserId,
      timestamp: Date.now(),
      data: { content, ...extra }
    });
  }

  sendPresence(status: 'online' | 'offline' | 'away' | 'busy'): boolean {
    return this.send({
      type: 'presence',
      timestamp: Date.now(),
      data: { status }
    });
  }

  sendTyping(toUserId: string, isTyping: boolean): boolean {
    return this.send({
      type: 'typing',
      targetId: toUserId,
      timestamp: Date.now(),
      data: { isTyping }
    });
  }

  sendRead(conversationId: string, messageId: string): boolean {
    return this.send({
      type: 'read',
      targetId: conversationId,
      timestamp: Date.now(),
      data: { messageId }
    });
  }
}

// ==================== 导出单例 ====================

export const enhancedWebSocketService = EnhancedWebSocketService.getInstance();

// ==================== 默认导出 ====================

export default enhancedWebSocketService;
