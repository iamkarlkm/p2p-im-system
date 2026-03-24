import { apiClient, wsClient } from './client';

export type TypingState = 'STARTED' | 'STOPPED';

export interface TypingUpdate {
  conversationId: string;
  userId: string;
  state: TypingState;
}

class TypingService {
  private debounceTimer: ReturnType<typeof setTimeout> | null = null;
  private readonly DEBOUNCE_MS = 2000;

  startTyping(conversationId: string): void {
    apiClient.post('/api/typing/start', null, {
      params: { conversationId, userId: apiClient.userId }
    }).catch(console.error);
  }

  stopTyping(conversationId: string): void {
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
      this.debounceTimer = null;
    }
    apiClient.post('/api/typing/stop', null, {
      params: { conversationId, userId: apiClient.userId }
    }).catch(console.error);
  }

  onTextChange(conversationId: string): void {
    if (this.debounceTimer) clearTimeout(this.debounceTimer);
    this.startTyping(conversationId);
    this.debounceTimer = setTimeout(() => {
      this.stopTyping(conversationId);
    }, this.DEBOUNCE_MS);
  }

  onTypingUpdate(callback: (update: TypingUpdate) => void): void {
    wsClient.on('typing', (data: TypingUpdate) => callback(data));
  }
}

export const typingService = new TypingService();
