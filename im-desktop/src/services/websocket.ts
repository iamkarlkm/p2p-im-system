import { useEffect, useRef, useCallback } from 'react';

interface WebSocketOptions {
  url: string;
  token?: string;
  userId?: string;
  onMessage?: (data: any) => void;
  onConnect?: () => void;
  onDisconnect?: (reason: string) => void;
  onError?: (error: any) => void;
  reconnect?: boolean;
  reconnectInterval?: number;
  maxReconnectAttempts?: number;
}

interface WebSocketHook {
  connect: () => void;
  disconnect: () => void;
  send: (data: any) => void;
  sendJson: (data: any) => void;
  isConnected: () => boolean;
}

/**
 * WebSocket Hook - 管理WebSocket连接
 */
export const useWebSocket = (options: WebSocketOptions): WebSocketHook => {
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectAttemptsRef = useRef(0);
  const reconnectTimerRef = useRef<NodeJS.Timeout | null>(null);

  const {
    url,
    token,
    userId,
    onMessage,
    onConnect,
    onDisconnect,
    onError,
    reconnect = true,
    reconnectInterval = 5000,
    maxReconnectAttempts = 10,
  } = options;

  const connect = useCallback(() => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      return;
    }

    try {
      // 构建连接URL
      const urlObj = new URL(url);
      if (token) urlObj.searchParams.set('token', token);
      if (userId) urlObj.searchParams.set('userId', userId);

      wsRef.current = new WebSocket(urlObj.toString());

      wsRef.current.onopen = () => {
        console.log('WebSocket connected');
        reconnectAttemptsRef.current = 0;
        onConnect?.();

        // 发送连接确认消息
        wsRef.current?.send(
          JSON.stringify({
            type: 'CONNECT',
            userId,
            timestamp: Date.now(),
          })
        );
      };

      wsRef.current.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          onMessage?.(data);
        } catch (e) {
          console.error('Failed to parse message:', e);
          onMessage?.(event.data);
        }
      };

      wsRef.current.onclose = (event) => {
        console.log('WebSocket disconnected:', event.code, event.reason);
        onDisconnect?.(event.reason || 'Connection closed');

        if (reconnect && reconnectAttemptsRef.current < maxReconnectAttempts) {
          reconnectAttemptsRef.current++;
          console.log(
            `Reconnecting... (${reconnectAttemptsRef.current}/${maxReconnectAttempts})`
          );
          reconnectTimerRef.current = setTimeout(() => {
            connect();
          }, reconnectInterval * reconnectAttemptsRef.current);
        }
      };

      wsRef.current.onerror = (error) => {
        console.error('WebSocket error:', error);
        onError?.(error);
      };
    } catch (error) {
      console.error('Failed to connect WebSocket:', error);
      onError?.(error);
    }
  }, [url, token, userId, onMessage, onConnect, onDisconnect, onError, reconnect, reconnectInterval, maxReconnectAttempts]);

  const disconnect = useCallback(() => {
    if (reconnectTimerRef.current) {
      clearTimeout(reconnectTimerRef.current);
      reconnectTimerRef.current = null;
    }

    if (wsRef.current) {
      // 发送断开连接消息
      try {
        wsRef.current.send(
          JSON.stringify({
            type: 'DISCONNECT',
            timestamp: Date.now(),
          })
        );
      } catch (_) {
        // Ignore errors when sending disconnect message
      }

      wsRef.current.close();
      wsRef.current = null;
    }

    reconnectAttemptsRef.current = maxReconnectAttempts; // Prevent auto reconnect
  }, [maxReconnectAttempts]);

  const send = useCallback((data: any) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(data);
    } else {
      console.warn('WebSocket is not connected');
    }
  }, []);

  const sendJson = useCallback((data: any) => {
    send(JSON.stringify(data));
  }, [send]);

  const isConnected = useCallback(() => {
    return wsRef.current?.readyState === WebSocket.OPEN;
  }, []);

  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return {
    connect,
    disconnect,
    send,
    sendJson,
    isConnected,
  };
};

/**
 * WebSocket服务类 - 用于非React组件
 */
export class WebSocketService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private options: WebSocketOptions;

  constructor(options: WebSocketOptions) {
    this.options = options;
  }

  connect(): void {
    if (this.ws?.readyState === WebSocket.OPEN) return;

    const { url, token, userId, onConnect, onMessage, onDisconnect, onError } =
      this.options;

    try {
      const urlObj = new URL(url);
      if (token) urlObj.searchParams.set('token', token);
      if (userId) urlObj.searchParams.set('userId', userId);

      this.ws = new WebSocket(urlObj.toString());

      this.ws.onopen = () => {
        this.reconnectAttempts = 0;
        onConnect?.();
        this.sendJson({ type: 'CONNECT', userId, timestamp: Date.now() });
      };

      this.ws.onmessage = (event) => {
        try {
          onMessage?.(JSON.parse(event.data));
        } catch {
          onMessage?.(event.data);
        }
      };

      this.ws.onclose = (event) => {
        onDisconnect?.(event.reason);
        this.scheduleReconnect();
      };

      this.ws.onerror = (error) => onError?.(error);
    } catch (error) {
      onError?.(error);
    }
  }

  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.ws?.close();
    this.ws = null;
    this.reconnectAttempts = this.options.maxReconnectAttempts || 10;
  }

  send(data: string): void {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(data);
    }
  }

  sendJson(data: any): void {
    this.send(JSON.stringify(data));
  }

  isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN;
  }

  private scheduleReconnect(): void {
    const { reconnect = true, reconnectInterval = 5000, maxReconnectAttempts = 10 } =
      this.options;

    if (!reconnect || this.reconnectAttempts >= maxReconnectAttempts) return;

    this.reconnectAttempts++;
    this.reconnectTimer = setTimeout(() => {
      this.connect();
    }, reconnectInterval * this.reconnectAttempts);
  }
}

export default WebSocketService;
