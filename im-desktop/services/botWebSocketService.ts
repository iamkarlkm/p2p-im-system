/**
 * AI 聊天机器人 WebSocket 服务 - TypeScript 封装
 * 支持机器人消息接收、主动推送、Webhook 事件
 */

interface BotWebSocketMessage {
  type: 'connected' | 'message' | 'reply' | 'event' | 'broadcast' | 'stats' | 'ping' | 'pong' | 'error';
  botId?: string;
  userId?: string;
  conversationId?: string;
  message?: string;
  reply?: string;
  event?: string;
  data?: any;
}

export class BotWebSocketService {
  private ws: WebSocket | null = null;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private messageHandlers: Map<string, ((data: any) => void)[]> = new Map();
  private connected = false;
  private botId: string | null = null;

  constructor(private wsUrl = '/ws/bot') {}

  connect(botId: string): void {
    if (this.connected && this.ws && this.botId === botId) {
      return;
    }

    this.disconnect();
    this.botId = botId;

    const url = `${this.wsUrl}?botId=${botId}`;
    this.ws = new WebSocket(url);

    this.ws.onopen = () => {
      console.log(`Bot WebSocket 连接已建立: ${botId}`);
      this.connected = true;
      this.emit('connected', { botId });
    };

    this.ws.onmessage = (event) => {
      try {
        const message: BotWebSocketMessage = JSON.parse(event.data);
        this.handleMessage(message);
      } catch (error) {
        console.error('Bot WebSocket 消息解析失败:', error);
      }
    };

    this.ws.onclose = () => {
      console.log(`Bot WebSocket 连接已关闭: ${botId}`);
      this.connected = false;
      this.emit('disconnected', { botId });
      this.scheduleReconnect(botId);
    };

    this.ws.onerror = (error) => {
      console.error(`Bot WebSocket 错误:`, error);
      this.emit('error', { botId, error });
    };
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }

    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }

    this.connected = false;
    this.botId = null;
  }

  sendMessage(data: {
    type: 'message' | 'stats' | 'ping' | 'webhook';
    userId?: string;
    conversationId?: string;
    message?: string;
    interval?: string;
    event?: string;
  }): void {
    if (!this.ws || !this.connected) {
      throw new Error('WebSocket 未连接');
    }

    const message = { ...data, botId: this.botId };
    this.ws.send(JSON.stringify(message));
  }

  sendChatMessage(userId: string, conversationId: string, message: string): void {
    this.sendMessage({
      type: 'message',
      userId,
      conversationId,
      message,
    });
  }

  requestStats(interval = 'daily'): void {
    this.sendMessage({
      type: 'stats',
      interval,
    });
  }

  ping(): void {
    this.sendMessage({
      type: 'ping',
    });
  }

  triggerWebhook(event: string, data?: any): void {
    this.sendMessage({
      type: 'webhook',
      event,
      ...data,
    });
  }

  on(event: string, handler: (data: any) => void): void {
    if (!this.messageHandlers.has(event)) {
      this.messageHandlers.set(event, []);
    }
    this.messageHandlers.get(event)!.push(handler);
  }

  off(event: string, handler: (data: any) => void): void {
    const handlers = this.messageHandlers.get(event);
    if (handlers) {
      const index = handlers.indexOf(handler);
      if (index !== -1) {
        handlers.splice(index, 1);
      }
    }
  }

  isConnected(): boolean {
    return this.connected;
  }

  getBotId(): string | null {
    return this.botId;
  }

  private handleMessage(message: BotWebSocketMessage): void {
    switch (message.type) {
      case 'connected':
        this.emit('connected', message);
        break;
      case 'reply':
        this.emit('reply', message);
        break;
      case 'event':
        this.emit('event', message);
        if (message.event) {
          this.emit(`event:${message.event}`, message.data || {});
        }
        break;
      case 'broadcast':
        this.emit('broadcast', message);
        break;
      case 'stats':
        this.emit('stats', message.data || {});
        break;
      case 'pong':
        this.emit('pong', message);
        break;
      case 'error':
        this.emit('error', message);
        break;
      default:
        console.warn('未知的 WebSocket 消息类型:', message.type);
    }
  }

  private emit(event: string, data: any): void {
    const handlers = this.messageHandlers.get(event);
    if (handlers) {
      handlers.forEach((handler) => handler(data));
    }
  }

  private scheduleReconnect(botId: string): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }

    this.reconnectTimer = setTimeout(() => {
      console.log(`正在重新连接 Bot WebSocket: ${botId}`);
      this.connect(botId);
    }, 5000); // 5秒后重试
  }
}

// 单例实例
export const botWebSocket = new BotWebSocketService();

export default botWebSocket;
