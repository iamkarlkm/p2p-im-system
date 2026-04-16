/**
 * 聊天状态管理 Store - 功能#10 桌面端聊天界面
 * 使用 MobX 风格的状态管理
 * 时间: 2026-04-01 09:32
 */

import { makeAutoObservable, runInAction } from 'mobx';
import { chatService, Message, Conversation } from '../services/chatService';
import { socketService, SocketMessage } from '../services/socketService';

export interface ChatState {
  // 会话列表
  conversations: Conversation[];
  currentConversationId: string | null;
  
  // 消息
  messages: Map<string, Message[]>;
  
  // 加载状态
  loadingConversations: boolean;
  loadingMessages: boolean;
  sendingMessage: boolean;
  
  // 错误
  error: string | null;
  
  // 连接状态
  connectionStatus: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING' | 'ERROR';
  
  // 未读总数
  totalUnreadCount: number;
}

class ChatStore {
  // 状态
  conversations: Conversation[] = [];
  currentConversationId: string | null = null;
  messages: Map<string, Message[]> = new Map();
  loadingConversations: boolean = false;
  loadingMessages: boolean = false;
  sendingMessage: boolean = false;
  error: string | null = null;
  connectionStatus: 'CONNECTED' | 'DISCONNECTED' | 'CONNECTING' | 'ERROR' = 'DISCONNECTED';
  
  // 当前用户信息
  currentUserId: string = '';

  constructor() {
    makeAutoObservable(this);
    
    // 监听socket消息
    socketService.onMessage(this.handleSocketMessage);
    socketService.onStatusChange(this.handleConnectionStatusChange);
  }

  // Getters
  get currentConversation(): Conversation | null {
    if (!this.currentConversationId) return null;
    return this.conversations.find(c => c.conversationId === this.currentConversationId) || null;
  }

  get currentMessages(): Message[] {
    if (!this.currentConversationId) return [];
    return this.messages.get(this.currentConversationId) || [];
  }

  get totalUnreadCount(): number {
    return this.conversations.reduce((sum, c) => sum + (c.unreadCount || 0), 0);
  }

  // Actions
  
  /**
   * 初始化并连接
   */
  async initialize(userId: string, token: string): Promise<void> {
    this.currentUserId = userId;
    
    runInAction(() => {
      this.connectionStatus = 'CONNECTING';
    });

    try {
      // 连接WebSocket
      socketService.connect(userId, token);
      
      // 加载会话列表
      await this.loadConversations();
      
    } catch (error) {
      runInAction(() => {
        this.error = error instanceof Error ? error.message : '初始化失败';
        this.connectionStatus = 'ERROR';
      });
    }
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    socketService.disconnect();
    socketService.offMessage(this.handleSocketMessage);
    socketService.offStatusChange(this.handleConnectionStatusChange);
    
    runInAction(() => {
      this.connectionStatus = 'DISCONNECTED';
      this.conversations = [];
      this.messages.clear();
      this.currentConversationId = null;
    });
  }

  /**
   * 加载会话列表
   */
  async loadConversations(): Promise<void> {
    runInAction(() => {
      this.loadingConversations = true;
      this.error = null;
    });

    try {
      const conversations = await chatService.getConversations();
      runInAction(() => {
        this.conversations = conversations.sort((a, b) => 
          (b.lastMessageTime || 0) - (a.lastMessageTime || 0)
        );
        this.loadingConversations = false;
      });
    } catch (error) {
      runInAction(() => {
        this.error = error instanceof Error ? error.message : '加载会话失败';
        this.loadingConversations = false;
      });
    }
  }

  /**
   * 切换当前会话
   */
  async selectConversation(conversationId: string): Promise<void> {
    runInAction(() => {
      this.currentConversationId = conversationId;
    });

    // 清除未读数
    const conversation = this.conversations.find(c => c.conversationId === conversationId);
    if (conversation && conversation.unreadCount > 0) {
      runInAction(() => {
        conversation.unreadCount = 0;
      });
    }

    // 加载消息
    if (!this.messages.has(conversationId)) {
      await this.loadMessages(conversationId);
    }
  }

  /**
   * 加载历史消息
   */
  async loadMessages(conversationId: string, beforeId?: string): Promise<void> {
    runInAction(() => {
      this.loadingMessages = true;
    });

    try {
      const messages = await chatService.getHistoryMessages(conversationId, beforeId, 20);
      
      runInAction(() => {
        const existingMessages = this.messages.get(conversationId) || [];
        
        if (beforeId) {
          // 加载更多，追加到前面
          this.messages.set(conversationId, [...messages, ...existingMessages]);
        } else {
          // 首次加载
          this.messages.set(conversationId, messages);
        }
        
        this.loadingMessages = false;
      });
    } catch (error) {
      runInAction(() => {
        this.error = error instanceof Error ? error.message : '加载消息失败';
        this.loadingMessages = false;
      });
    }
  }

  /**
   * 发送消息
   */
  async sendMessage(content: string, messageType: string = 'TEXT'): Promise<void> {
    if (!this.currentConversationId) return;

    runInAction(() => {
      this.sendingMessage = true;
    });

    try {
      // 先通过WebSocket发送
      const sent = socketService.sendChatMessage(this.currentConversationId, content, messageType);
      
      if (!sent) {
        // WebSocket发送失败，使用HTTP API
        await chatService.sendMessage(this.currentConversationId, content, messageType);
      }

      // 乐观更新：添加到本地消息列表
      const optimisticMessage: Message = {
        messageId: `temp_${Date.now()}`,
        senderId: this.currentUserId,
        senderName: '我',
        content,
        messageType,
        timestamp: Date.now(),
        conversationId: this.currentConversationId,
      };

      runInAction(() => {
        const existingMessages = this.messages.get(this.currentConversationId!) || [];
        this.messages.set(this.currentConversationId!, [...existingMessages, optimisticMessage]);
        
        // 更新会话最后消息
        const conversation = this.conversations.find(c => c.conversationId === this.currentConversationId);
        if (conversation) {
          conversation.lastMessage = content;
          conversation.lastMessageTime = Date.now();
          // 移到顶部
          this.conversations = [conversation, ...this.conversations.filter(c => c.conversationId !== conversation.conversationId)];
        }
        
        this.sendingMessage = false;
      });
    } catch (error) {
      runInAction(() => {
        this.error = error instanceof Error ? error.message : '发送失败';
        this.sendingMessage = false;
      });
    }
  }

  /**
   * 创建新会话
   */
  createConversation(conversation: Conversation): void {
    runInAction(() => {
      const exists = this.conversations.some(c => c.conversationId === conversation.conversationId);
      if (!exists) {
        this.conversations.unshift(conversation);
      }
      this.currentConversationId = conversation.conversationId;
    });
  }

  /**
   * 处理Socket消息
   */
  private handleSocketMessage = (socketMessage: SocketMessage): void => {
    if (socketMessage.type === 'MESSAGE') {
      const message: Message = socketMessage.data;
      
      runInAction(() => {
        // 添加到对应会话的消息列表
        const existingMessages = this.messages.get(message.conversationId) || [];
        
        // 检查是否已存在
        const exists = existingMessages.some(m => m.messageId === message.messageId);
        if (!exists) {
          this.messages.set(message.conversationId, [...existingMessages, message]);
        }

        // 更新会话列表
        const conversation = this.conversations.find(c => c.conversationId === message.conversationId);
        if (conversation) {
          conversation.lastMessage = message.content;
          conversation.lastMessageTime = message.timestamp;
          
          // 如果不是当前会话，增加未读数
          if (this.currentConversationId !== message.conversationId) {
            conversation.unreadCount = (conversation.unreadCount || 0) + 1;
          }
          
          // 重新排序
          this.conversations = [...this.conversations].sort((a, b) => 
            (b.lastMessageTime || 0) - (a.lastMessageTime || 0)
          );
        } else {
          // 新会话，需要刷新列表
          this.loadConversations();
        }
      });
    }
  };

  /**
   * 处理连接状态变化
   */
  private handleConnectionStatusChange = (status: any): void => {
    runInAction(() => {
      this.connectionStatus = status;
    });
  };

  /**
   * 清除错误
   */
  clearError(): void {
    this.error = null;
  }
}

// 导出单例
export const chatStore = new ChatStore();
export default chatStore;
