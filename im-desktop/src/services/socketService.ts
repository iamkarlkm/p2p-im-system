/**
 * Socket服务 - 功能#10 桌面端聊天界面
 * WebSocket连接管理
 * 时间: 2026-04-01 09:28
 */

export interface SocketMessage {
  type: string;
  data?: any;
  timestamp?: number;
}

export type ConnectionStatus = 'CONNECTING' | 'CONNECTED' | 'DISCONNECTED' | 'ERROR';

class SocketService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private heartbeatInterval: NodeJS.Timeout | null = null;
  private messageListeners: ((message: SocketMessage) => void)[] = [];
  private statusListeners: ((status: ConnectionStatus) => void)[] = [];
  private userId: string | null = null;
  private token: string | null = null;
  private wsUrl: string = '';

  private currentStatus: ConnectionStatus = 'DISCONNECTED';

  /**
   * 初始化WebSocket连接
   */
  connect(userId: string, token: string, wsUrl: string = 'ws://localhost:8080/ws/im'): void {
    this.userId = userId;
    this.token = token;
    this.wsUrl = `${wsUrl}?userId=${userId}`;
    
    this.doConnect();
  }

  /**
   * 执行连接
   */
  private doConnect(): void {
    if (this.ws?.readyState === WebSocket.OPEN) {
      return;
    }

    this.updateStatus('CONNECTING');

    try {
      this.ws = new WebSocket(this.wsUrl);

      this.ws.onopen = () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        this.updateStatus('CONNECTED');
        this.startHeartbeat();
        
        // 发送认证消息
        this.send({
          type: 'AUTH',
          data: { token: this.token },
        });
      };

      this.ws.onmessage = (event) => {
        try {
          const message: SocketMessage = JSON.parse(event.data);
          this.handleMessage(message);
        } catch (e) {
          console.error('Parse message error:', e);
        }
      };

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.updateStatus('ERROR');
      };

      this.ws.onclose = () => {
        console.log('WebSocket closed');
        this.stopHeartbeat();
        this.updateStatus('DISCONNECTED');
        this.attemptReconnect();
      };
    } catch (e) {
      console.error('WebSocket connection error:', e);
      this.updateStatus('ERROR');
      this.attemptReconnect();
    }
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    this.stopReconnect();
    this.stopHeartbeat();
    
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    
    this.updateStatus('DISCONNECTED');
  }

  /**
   * 发送消息
   */
  send(message: SocketMessage): boolean {
    if (this.ws?.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket is not connected');
      return false;
    }

    try {
      this.ws.send(JSON.stringify({
        ...message,
        timestamp: Date.now(),
      }));
      return true;
    } catch (e) {
      console.error('Send message error:', e);
      return false;
    }
  }

  /**
   * 发送聊天消息
   */
  sendChatMessage(conversationId: string, content: string, messageType: string = 'TEXT'): boolean {
    return this.send({
      type: 'MESSAGE',
      data: {
        conversationId,
        content,
        messageType,
      },
    });
  }

  /**
   * 添加消息监听
   */
  onMessage(listener: (message: SocketMessage) => void): void {
    this.messageListeners.push(listener);
  }

  /**
   * 移除消息监听
   */
  offMessage(listener: (message: SocketMessage) => void): void {
    this.messageListeners = this.messageListeners.filter(l => l !== listener);
  }

  /**
   * 添加状态监听
   */
  onStatusChange(listener: (status: ConnectionStatus) => void): void {
    this.statusListeners.push(listener);
    // 立即通知当前状态
    listener(this.currentStatus);
  }

  /**
   * 移除状态监听
   */
  offStatusChange(listener: (status: ConnectionStatus) => void): void {
    this.statusListeners = this.statusListeners.filter(l => l !== listener);
  }

  /**
   * 获取当前连接状态
   */
  getStatus(): ConnectionStatus {
    return this.currentStatus;
  }

  /**
   * 是否已连接
   */
  isConnected(): boolean {
    return this.currentStatus === 'CONNECTED';
  }

  /**
   * 处理收到的消息
   */
  private handleMessage(message: SocketMessage): void {
    // 处理心跳响应
    if (message.type === 'PONG') {
      return;
    }

    // 通知所有监听器
    this.messageListeners.forEach(listener => {
      try {
        listener(message);
      } catch (e) {
        console.error('Message listener error:', e);
      }
    });
  }

  /**
   * 更新状态
   */
  private updateStatus(status: ConnectionStatus): void {
    this.currentStatus = status;
    this.statusListeners.forEach(listener => {
      try {
        listener(status);
      } catch (e) {
        console.error('Status listener error:', e);
      }
    });
  }

  /**
   * 启动心跳
   */
  private startHeartbeat(): void {
    this.heartbeatInterval = setInterval(() => {
      this.send({ type: 'PING' });
    }, 30000); // 30秒心跳
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
   * 尝试重连
   */
  private attemptReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('Max reconnect attempts reached');
      return;
    }

    this.reconnectAttempts++;
    console.log(`Reconnecting... Attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);

    this.reconnectTimer = setTimeout(() => {
      this.doConnect();
    }, this.reconnectDelay);
  }

  /**
   * 停止重连
   */
  private stopReconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
  }
}

export const socketService = new SocketService();
export default socketService;
