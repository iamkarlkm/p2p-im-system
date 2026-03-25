import axios from 'axios';
import { ForwardRequest, ForwardResponse, BatchForwardResponse, MessageForward } from '../types/message-forward';
import { useAuthStore } from '../stores/auth-store';

const api = axios.create({
  baseURL: '/api/messages/forward',
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers['X-User-Id'] = useAuthStore.getState().user?.id;
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

export class ForwardService {
  static async forwardMessage(request: ForwardRequest): Promise<ForwardResponse> {
    const response = await api.post<ForwardResponse>('/', request);
    return response.data;
  }

  static async batchForward(requests: ForwardRequest[]): Promise<BatchForwardResponse> {
    const response = await api.post<BatchForwardResponse>('/batch', requests);
    return response.data;
  }

  static async getForwardHistory(messageId: number): Promise<MessageForward[]> {
    const response = await api.get<MessageForward[]>(`/history/${messageId}`);
    return response.data;
  }
}

export class ForwardHelper {
  static buildForwardContent(senderName: string, content: string, sentAt: string): string {
    return `[Forwarded]\nFrom: ${senderName}\nTime: ${sentAt}\n${'─'.repeat(15)}\n${content}`;
  }

  static buildMergedTitle(count: number): string {
    return `Merged ${count} messages`;
  }

  static async forwardToMultiple(
    messageIds: number[],
    targetIds: number[],
    merged = false,
    title?: string
  ): Promise<BatchForwardResponse> {
    const requests: ForwardRequest[] = targetIds.map(targetId => ({
      messageIds,
      targetConversationId: targetId,
      merged: merged && messageIds.length > 1,
      mergedTitle: title || (merged ? this.buildMergedTitle(messageIds.length) : undefined),
    }));
    return this.batchForward(requests);
  }
}
