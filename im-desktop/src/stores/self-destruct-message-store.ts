/**
 * 阅后即焚消息状态管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */

import { makeAutoObservable, runInAction } from 'mobx';
import {
  SelfDestructMessage,
  SelfDestructConfig,
  CreateSelfDestructMessageRequest,
  ReadSelfDestructMessageResponse,
  ScreenshotDetectResponse,
} from '../types/self-destruct-message';
import { selfDestructMessageApi } from '../api/self-destruct-message-api';

export class SelfDestructMessageStore {
  messages: SelfDestructMessage[] = [];
  sentMessages: SelfDestructMessage[] = [];
  receivedMessages: SelfDestructMessage[] = [];
  screenshotDetectedMessages: SelfDestructMessage[] = [];
  currentMessage: SelfDestructMessage | null = null;
  config: SelfDestructConfig | null = null;
  unreadCount: number = 0;
  isLoading: boolean = false;
  errorMessage: string | null = null;
  isReading: boolean = false;
  countdownSeconds: number | null = null;
  private countdownInterval: NodeJS.Timeout | null = null;

  constructor() {
    makeAutoObservable(this);
  }

  get activeMessages(): SelfDestructMessage[] {
    return this.messages.filter(m => !m.isDestroyed);
  }

  get destroyedMessages(): SelfDestructMessage[] {
    return this.messages.filter(m => m.isDestroyed);
  }

  get hasUnread(): boolean {
    return this.unreadCount > 0;
  }

  get hasScreenshotDetected(): boolean {
    return this.screenshotDetectedMessages.length > 0;
  }

  async fetchConfig(): Promise<void> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const config = await selfDestructMessageApi.getConfig();
      runInAction(() => {
        this.config = config;
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取配置失败: ${error}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async fetchMessages(conversationId: string, page: number = 0, size: number = 20): Promise<void> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const messages = await selfDestructMessageApi.getMessagesByConversation(conversationId, page, size);
      runInAction(() => {
        if (page === 0) {
          this.messages = messages;
        } else {
          this.messages.push(...messages);
        }
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取消息失败: ${error}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async fetchSentMessages(): Promise<void> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const messages = await selfDestructMessageApi.getSentMessages();
      runInAction(() => {
        this.sentMessages = messages;
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取发送消息失败: ${error}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async fetchReceivedMessages(): Promise<void> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const messages = await selfDestructMessageApi.getReceivedMessages();
      runInAction(() => {
        this.receivedMessages = messages;
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取接收消息失败: ${error}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async fetchUnreadCount(): Promise<void> {
    try {
      const { count } = await selfDestructMessageApi.getUnreadCount();
      runInAction(() => {
        this.unreadCount = count;
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取未读数量失败: ${error}`;
      });
    }
  }

  async fetchUnreadCountByConversation(conversationId: string): Promise<number> {
    try {
      const { count } = await selfDestructMessageApi.getUnreadCountByConversation(conversationId);
      return count;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取未读数量失败: ${error}`;
      });
      return 0;
    }
  }

  async createMessage(request: CreateSelfDestructMessageRequest): Promise<SelfDestructMessage | null> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const message = await selfDestructMessageApi.createMessage(request);
      runInAction(() => {
        this.messages.unshift(message);
        this.sentMessages.unshift(message);
      });
      return message;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `创建消息失败: ${error}`;
      });
      return null;
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  async readMessage(messageId: string): Promise<ReadSelfDestructMessageResponse | null> {
    runInAction(() => {
      this.isReading = true;
      this.errorMessage = null;
    });

    try {
      const response = await selfDestructMessageApi.readMessage(messageId);
      
      runInAction(() => {
        const index = this.messages.findIndex(m => m.id === messageId);
        if (index !== -1) {
          this.messages[index] = {
            ...this.messages[index],
            isRead: true,
            remainingSeconds: response.remainingSeconds,
          };
        }

        this.countdownSeconds = response.remainingSeconds;
        this.startCountdown(messageId);
      });

      return response;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `阅读消息失败: ${error}`;
      });
      return null;
    } finally {
      runInAction(() => {
        this.isReading = false;
      });
    }
  }

  private startCountdown(messageId: string): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }

    this.countdownInterval = setInterval(() => {
      runInAction(() => {
        if (this.countdownSeconds === null || this.countdownSeconds <= 0) {
          this.destroyMessageInternal(messageId);
          if (this.countdownInterval) {
            clearInterval(this.countdownInterval);
            this.countdownInterval = null;
          }
        } else {
          this.countdownSeconds--;
          const index = this.messages.findIndex(m => m.id === messageId);
          if (index !== -1) {
            this.messages[index].remainingSeconds = this.countdownSeconds;
          }
        }
      });
    }, 1000);
  }

  private destroyMessageInternal(messageId: string): void {
    const index = this.messages.findIndex(m => m.id === messageId);
    if (index !== -1) {
      this.messages[index] = {
        ...this.messages[index],
        isDestroyed: true,
        messageContent: undefined,
        remainingSeconds: 0,
      };
    }

    selfDestructMessageApi.destroyMessage(messageId).catch(() => {
      // 忽略错误
    });
  }

  async destroyMessage(messageId: string): Promise<boolean> {
    try {
      await selfDestructMessageApi.destroyMessage(messageId);
      
      runInAction(() => {
        const index = this.messages.findIndex(m => m.id === messageId);
        if (index !== -1) {
          this.messages[index] = {
            ...this.messages[index],
            isDestroyed: true,
            messageContent: undefined,
            remainingSeconds: 0,
          };
        }

        if (this.countdownInterval) {
          clearInterval(this.countdownInterval);
          this.countdownInterval = null;
        }
      });

      return true;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `销毁消息失败: ${error}`;
      });
      return false;
    }
  }

  async deleteMessage(messageId: string): Promise<boolean> {
    try {
      await selfDestructMessageApi.deleteMessage(messageId);
      
      runInAction(() => {
        this.messages = this.messages.filter(m => m.id !== messageId);
        this.sentMessages = this.sentMessages.filter(m => m.id !== messageId);
      });

      return true;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `删除消息失败: ${error}`;
      });
      return false;
    }
  }

  async detectScreenshot(messageId: string): Promise<ScreenshotDetectResponse | null> {
    try {
      const response = await selfDestructMessageApi.detectScreenshot(messageId);
      
      runInAction(() => {
        const index = this.messages.findIndex(m => m.id === messageId);
        if (index !== -1) {
          this.messages[index] = {
            ...this.messages[index],
            screenshotDetected: true,
            screenshotCount: response.totalCount,
          };
        }
      });

      return response;
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `截图检测失败: ${error}`;
      });
      return null;
    }
  }

  async fetchScreenshotDetectedMessages(): Promise<void> {
    runInAction(() => {
      this.isLoading = true;
      this.errorMessage = null;
    });

    try {
      const messages = await selfDestructMessageApi.getScreenshotDetectedMessages();
      runInAction(() => {
        this.screenshotDetectedMessages = messages;
      });
    } catch (error) {
      runInAction(() => {
        this.errorMessage = `获取截图检测消息失败: ${error}`;
      });
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  addNewMessage(message: SelfDestructMessage): void {
    if (!this.messages.some(m => m.id === message.id)) {
      this.messages.unshift(message);
      this.receivedMessages.unshift(message);
      this.unreadCount++;
    }
  }

  updateMessage(message: SelfDestructMessage): void {
    const index = this.messages.findIndex(m => m.id === message.id);
    if (index !== -1) {
      this.messages[index] = message;
    }
  }

  clearError(): void {
    this.errorMessage = null;
  }

  clearCurrentMessage(): void {
    this.currentMessage = null;
    this.countdownSeconds = null;
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }

  reset(): void {
    this.messages = [];
    this.sentMessages = [];
    this.receivedMessages = [];
    this.screenshotDetectedMessages = [];
    this.currentMessage = null;
    this.config = null;
    this.unreadCount = 0;
    this.errorMessage = null;
    this.countdownSeconds = null;
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }
}

export const selfDestructMessageStore = new SelfDestructMessageStore();
