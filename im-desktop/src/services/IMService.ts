import { EventEmitter } from 'events';
import { Message, ConnectionStatus } from '@/types/im';

export class IMService extends EventEmitter {
  private ws: WebSocket | null = null;
  private token: string | null = null;
  public userId: string | null = null;
  private heartbeatTimer: NodeJS.Timeout | null = null;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private reconnectAttempts = 0;
  private readonly maxReconnectAttempts = 10;
  private readonly heartbeatInterval = 30000;
  private readonly reconnectInterval = 5000;

  async connect(token: string, userId: string): Promise<void> {
    this.token = token;
    this.userId = userId;
    return this.doConnect();
  }

  private doConnect(): Promise<void> {
    return new Promise((resolve, reject) => {
      const wsUrl = `wss://api.im.example.com/ws/v1?token=${this.token}&userId=${this.userId}`;
      
      this.ws = new WebSocket(wsUrl);

      this.ws.onopen = () => {
        this.reconnectAttempts = 0;
        this.startHeartbeat();
        this.emit('statusChange', { status: 'connected' } as ConnectionStatus);
        resolve();
      };

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'heartbeat_ack') return;
          this.emit('message', data as Message);
        } catch (error) {
          console.error('Failed to parse message:', error);
        }
      };

      this.ws.onerror = (error) => {
        this.emit('statusChange', { status: 'error', message: 'Connection error' } as ConnectionStatus);
        reject(error);
      };

      this.ws.onclose = () => {
        this.emit('statusChange', { status: 'disconnected' } as ConnectionStatus);
        this.scheduleReconnect();
      };
    });
  }

  sendMessage(message: Message): void {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    } else {
      throw new Error('WebSocket is not connected');
    }
  }

  private startHeartbeat(): void {
    this.heartbeatTimer = setInterval(() => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'heartbeat' }));
      }
    }, this.heartbeatInterval);
  }

  private scheduleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnect attempts reached');
      return;
    }

    this.reconnectTimer = setTimeout(() => {
      this.reconnectAttempts++;
      this.emit('statusChange', { 
        status: 'connecting',
        message: `Reconnecting... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`
      } as ConnectionStatus);
      this.doConnect();
    }, this.reconnectInterval);
  }

  onMessage(callback: (message: Message) => void): void {
    this.on('message', callback);
  }

  onStatusChange(callback: (status: ConnectionStatus) => void): void {
    this.on('statusChange', callback);
  }

  disconnect(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }
}
