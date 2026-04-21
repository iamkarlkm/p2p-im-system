import { useEffect, useRef, useCallback } from 'react';
import { ImP2PClient, type ImMessage, type P2PClientOptions } from '../p2p/client.js';

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
 * WebSocket Hook - 基于 P2P-WS 协议
 */
export const useWebSocket = (options: WebSocketOptions): WebSocketHook => {
  const clientRef = useRef<ImP2PClient | null>(null);

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
    if (clientRef.current?.isConnected()) {
      return;
    }

    // TODO: keyfile 应通过 Tauri API 从本地安全加载
    // 开发阶段可先用硬编码的 5MB Uint8Array（生产环境必须替换）
    const keyfile = generateDevKeyfile();

    const p2pOptions: P2PClientOptions = {
      url,
      token,
      userId,
      keyfile,
      onConnect,
      onDisconnect,
      onMessage,
      onError,
      reconnect,
      reconnectInterval,
      maxReconnectAttempts,
    };

    clientRef.current = new ImP2PClient(p2pOptions);
    clientRef.current.connect().catch((e) => onError?.(e));
  }, [url, token, userId, onMessage, onConnect, onDisconnect, onError, reconnect, reconnectInterval, maxReconnectAttempts]);

  const disconnect = useCallback(() => {
    clientRef.current?.disconnect();
    clientRef.current = null;
  }, []);

  const send = useCallback((data: any) => {
    if (!clientRef.current?.isConnected()) {
      console.warn('P2P client is not connected');
      return;
    }
    // 兼容旧 JSON 接口，将 data 包装为 IM 消息
    let msg: ImMessage;
    try {
      msg = typeof data === 'string' ? JSON.parse(data) : data;
    } catch {
      msg = { type: 'chat', content: String(data), timestamp: Date.now() };
    }
    routeMessage(clientRef.current, msg);
  }, []);

  const sendJson = useCallback((data: any) => {
    send(data);
  }, [send]);

  const isConnected = useCallback(() => {
    return clientRef.current?.isConnected() ?? false;
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
 * WebSocket 服务类 - 用于非 React 组件
 */
export class WebSocketService {
  private client: ImP2PClient | null = null;
  private options: WebSocketOptions;

  constructor(options: WebSocketOptions) {
    this.options = options;
  }

  connect(): void {
    if (this.client?.isConnected()) return;
    const keyfile = generateDevKeyfile();
    this.client = new ImP2PClient({
      url: this.options.url,
      token: this.options.token,
      userId: this.options.userId,
      keyfile,
      onConnect: this.options.onConnect,
      onDisconnect: this.options.onDisconnect,
      onMessage: this.options.onMessage,
      onError: this.options.onError,
      reconnect: this.options.reconnect,
      reconnectInterval: this.options.reconnectInterval,
      maxReconnectAttempts: this.options.maxReconnectAttempts,
    });
    this.client.connect().catch((e) => this.options.onError?.(e));
  }

  disconnect(): void {
    this.client?.disconnect();
    this.client = null;
  }

  send(data: string): void {
    if (!this.client?.isConnected()) return;
    let msg: ImMessage;
    try {
      msg = JSON.parse(data);
    } catch {
      msg = { type: 'chat', content: data, timestamp: Date.now() };
    }
    routeMessage(this.client, msg);
  }

  sendJson(data: any): void {
    this.send(JSON.stringify(data));
  }

  isConnected(): boolean {
    return this.client?.isConnected() ?? false;
  }
}

function routeMessage(client: ImP2PClient, msg: ImMessage) {
  switch (msg.type) {
    case 'chat':
      client.sendChat(msg);
      break;
    case 'group_chat':
      client.sendGroupChat(msg);
      break;
    case 'read_receipt':
      client.sendReadReceipt(msg);
      break;
    case 'typing':
      client.sendTyping(msg);
      break;
    case 'presence':
      client.sendPresence(msg.payload?.status || 'online');
      break;
    default:
      // 其他类型默认走 chat command
      client.sendChat(msg);
  }
}

/**
 * 开发阶段临时 keyfile 生成器
 * 生产环境应通过 Tauri 安全通道从本地文件系统加载
 */
function generateDevKeyfile(): Uint8Array {
  // 5MB 随机 keyfile
  const size = 5 * 1024 * 1024;
  const arr = new Uint8Array(size);
  crypto.getRandomValues(arr);
  return arr;
}
