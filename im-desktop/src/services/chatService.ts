import axios, { AxiosInstance, AxiosResponse } from 'axios';

/**
 * 聊天服务 - 功能#10 桌面端聊天界面
 */
export interface Message {
  messageId: string;
  senderId: string;
  senderName: string;
  content: string;
  messageType: string;
  timestamp: number;
  conversationId: string;
}

export interface Conversation {
  conversationId: string;
  conversationName: string;
  avatar?: string;
  lastMessage?: string;
  lastMessageTime: number;
  unreadCount: number;
  type: 'SINGLE' | 'GROUP';
}

class ChatService {
  private apiClient: AxiosInstance;
  private wsConnection: WebSocket | null = null;
  private messageCallbacks: ((message: Message) => void)[] = [];

  constructor() {
    this.apiClient = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
      timeout: 10000,
    });

    // 请求拦截器添加token
    this.apiClient.interceptors.request.use((config) => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });
  }

  /**
   * 连接WebSocket
   */
  connectWebSocket(userId: string): void {
    const wsUrl = `ws://localhost:8080/ws/im?userId=${userId}`;
    this.wsConnection = new WebSocket(wsUrl);

    this.wsConnection.onopen = () => {
      console.log('WebSocket connected');
    };

    this.wsConnection.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.type === 'MESSAGE') {
        this.notifyMessageListeners(data);
      }
    };

    this.wsConnection.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    this.wsConnection.onclose = () => {
      console.log('WebSocket disconnected');
    };
  }

  /**
   * 断开WebSocket
   */
  disconnectWebSocket(): void {
    this.wsConnection?.close();
    this.wsConnection = null;
  }

  /**
   * 获取会话列表
   */
  async getConversations(): Promise<Conversation[]> {
    const response: AxiosResponse<Conversation[]> = await this.apiClient.get('/conversations');
    return response.data;
  }

  /**
   * 获取历史消息
   */
  async getHistoryMessages(conversationId: string, beforeId?: string, limit: number = 20): Promise<Message[]> {
    const params: Record<string, string | number> = { limit };
    if (beforeId) {
      params.beforeId = beforeId;
    }
    const response: AxiosResponse<Message[]> = await this.apiClient.get(
      `/conversations/${conversationId}/messages`,
      { params }
    );
    return response.data;
  }

  /**
   * 发送消息
   */
  async sendMessage(conversationId: string, content: string, messageType: string = 'TEXT'): Promise<Message> {
    const response: AxiosResponse<Message> = await this.apiClient.post('/messages', {
      conversationId,
      content,
      messageType,
    });
    return response.data;
  }

  /**
   * 标记消息已读
   */
  async markAsRead(conversationId: string, messageIds: string[]): Promise<void> {
    await this.apiClient.post(`/conversations/${conversationId}/read`, { messageIds });
  }

  /**
   * 撤回消息
   */
  async recallMessage(messageId: string): Promise<void> {
    await this.apiClient.post(`/messages/${messageId}/recall`);
  }

  /**
   * 注册消息监听
   */
  onMessage(callback: (message: Message) => void): void {
    this.messageCallbacks.push(callback);
  }

  /**
   * 移除消息监听
   */
  offMessage(callback: (message: Message) => void): void {
    this.messageCallbacks = this.messageCallbacks.filter(cb => cb !== callback);
  }

  /**
   * 通知消息监听器
   */
  private notifyMessageListeners(message: Message): void {
    this.messageCallbacks.forEach(callback => callback(message));
  }
}

export const chatService = new ChatService();
export default chatService;