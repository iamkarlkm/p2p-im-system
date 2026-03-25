import axios from 'axios';
import { PinConversationRequest, PinConversationResponse, PinnedConversation } from '../types/conversation-pin';
import { useAuthStore } from '../stores/auth-store';

const api = axios.create({ baseURL: '/api/conversations/pin', timeout: 10000 });

api.interceptors.request.use((config) => {
  const user = useAuthStore.getState().user;
  if (user) config.headers['X-User-Id'] = user.id;
  return config;
});

export class PinService {
  static async pin(request: PinConversationRequest): Promise<PinConversationResponse> {
    const response = await api.post<PinConversationResponse>('/', request);
    return response.data;
  }

  static async unpin(conversationId: number): Promise<PinConversationResponse> {
    const response = await api.delete<PinConversationResponse>(`/${conversationId}`);
    return response.data;
  }

  static async getPinned(): Promise<PinnedConversation[]> {
    const response = await api.get<PinnedConversation[]>('/');
    return response.data;
  }

  static async reorder(conversationIds: number[]): Promise<PinConversationResponse> {
    const response = await api.put<PinConversationResponse>('/reorder', { conversationIds });
    return response.data;
  }

  static async isPinned(conversationId: number): Promise<boolean> {
    const response = await api.get<{ pinned: boolean }>(`/${conversationId}/status`);
    return response.data.pinned;
  }
}
