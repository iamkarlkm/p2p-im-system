import { apiClient } from './apiClient';

const TYPING_DEBOUNCE_MS = 1500;
const STOP_TYPING_DELAY_MS = 3000;

interface TypingUser {
  userId: string;
  userName: string;
  conversationId: string;
  conversationType: string;
  updatedAt: string;
}

type TypingCallback = (users: TypingUser[]) => void;

class TypingService {
  private debounceTimer: Record<string, NodeJS.Timeout> = {};
  private stopTimer: Record<string, NodeJS.Timeout> = {};
  private listeners: Record<string, TypingCallback[]> = {};
  private typingUsers: Record<string, TypingUser[]> = {};

  /** 注册会话的Typing监听回调 */
  onTypingChanged(conversationId: string, callback: TypingCallback): () => void {
    if (!this.listeners[conversationId]) {
      this.listeners[conversationId] = [];
    }
    this.listeners[conversationId].push(callback);
    return () => {
      this.listeners[conversationId] = this.listeners[conversationId].filter(cb => cb !== callback);
    };
  }

  /** 用户开始输入 → 自动停止定时器+debounce+发API */
  startTyping(conversationId: string, conversationType: string): void {
    // 清除停止定时器
    if (this.stopTimer[conversationId]) {
      clearTimeout(this.stopTimer[conversationId]);
      delete this.stopTimer[conversationId];
    }

    // 清除旧的debounce
    if (this.debounceTimer[conversationId]) {
      clearTimeout(this.debounceTimer[conversationId]);
    }

    this.debounceTimer[conversationId] = setTimeout(async () => {
      try {
        await apiClient.post('/typing/start', {
          conversationId,
          conversationType,
        });
      } catch (e) {
        console.error('[TypingService] startTyping error:', e);
      }
      delete this.debounceTimer[conversationId];

      // 设置自动停止定时器
      this.stopTimer[conversationId] = setTimeout(() => {
        this.stopTyping(conversationId, conversationType);
      }, STOP_TYPING_DELAY_MS);
    }, TYPING_DEBOUNCE_MS);
  }

  /** 用户停止输入 */
  async stopTyping(conversationId: string, conversationType: string): Promise<void> {
    if (this.debounceTimer[conversationId]) {
      clearTimeout(this.debounceTimer[conversationId]);
      delete this.debounceTimer[conversationId];
    }
    if (this.stopTimer[conversationId]) {
      clearTimeout(this.stopTimer[conversationId]);
      delete this.stopTimer[conversationId];
    }
    try {
      await apiClient.post('/typing/stop', {
        conversationId,
        conversationType,
      });
    } catch (e) {
      console.error('[TypingService] stopTyping error:', e);
    }
  }

  /** 主动拉取当前Typing状态 */
  async fetchTypingStatus(conversationId: string): Promise<TypingUser[]> {
    try {
      const resp = await apiClient.get<TypingUser[]>(`/typing/${encodeURIComponent(conversationId)}`);
      return resp.data;
    } catch (e) {
      console.error('[TypingService] fetchTypingStatus error:', e);
      return [];
    }
  }

  /** WebSocket事件处理: 更新本地状态 */
  handleWebSocketEvent(event: { type: string; data: TypingUser }): void {
    const convId = event.data.conversationId;
    if (!convId) return;

    if (event.type === 'typing') {
      this.addTypingUser(convId, event.data);
    } else if (event.type === 'stop_typing') {
      this.removeTypingUser(convId, event.data.userId);
    }
  }

  private addTypingUser(convId: string, user: TypingUser): void {
    if (!this.typingUsers[convId]) {
      this.typingUsers[convId] = [];
    }
    const existing = this.typingUsers[convId].findIndex(u => u.userId === user.userId);
    if (existing >= 0) {
      this.typingUsers[convId][existing] = user;
    } else {
      this.typingUsers[convId].push(user);
    }
    this.notifyListeners(convId);
  }

  private removeTypingUser(convId: string, userId: string): void {
    if (!this.typingUsers[convId]) return;
    this.typingUsers[convId] = this.typingUsers[convId].filter(u => u.userId !== userId);
    this.notifyListeners(convId);
  }

  private notifyListeners(convId: string): void {
    const callbacks = this.listeners[convId] || [];
    const users = this.typingUsers[convId] || [];
    callbacks.forEach(cb => cb(users));
  }

  /** 清理资源 */
  destroy(): void {
    Object.values(this.debounceTimer).forEach(clearTimeout);
    Object.values(this.stopTimer).forEach(clearTimeout);
    this.debounceTimer = {};
    this.stopTimer = {};
    this.listeners = {};
    this.typingUsers = {};
  }
}

export const typingService = new TypingService();
export type { TypingUser };
