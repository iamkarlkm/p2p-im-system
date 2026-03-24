// Message Draft Service - handles draft save/delete/sync across devices
import axios from 'axios';
import { MessageDraft, DraftRequest } from '../types/message-draft';

const API_BASE = 'http://localhost:8080/api/draft';

export class MessageDraftService {
  private userId: number;

  constructor(userId: number) {
    this.userId = userId;
  }

  async saveDraft(conversationId: string, content: string, options?: {
    mentionIds?: string;
    replyMessageId?: string;
    messageType?: string;
  }): Promise<MessageDraft> {
    const request: DraftRequest = {
      userId: this.userId,
      conversationId,
      content,
      mentionIds: options?.mentionIds,
      replyMessageId: options?.replyMessageId,
      messageType: options?.messageType,
    };
    const response = await axios.post(`${API_BASE}/save`, request);
    return response.data;
  }

  async deleteDraft(conversationId: string): Promise<void> {
    await axios.delete(`${API_BASE}/delete`, {
      params: { userId: this.userId, conversationId },
    });
  }

  async getDraft(conversationId: string): Promise<MessageDraft | null> {
    try {
      const response = await axios.get(`${API_BASE}/get`, {
        params: { userId: this.userId, conversationId },
      });
      return response.data;
    } catch (e: any) {
      if (e.response?.status === 204) return null;
      throw e;
    }
  }

  async getAllDrafts(): Promise<MessageDraft[]> {
    const response = await axios.get(`${API_BASE}/list`, {
      params: { userId: this.userId },
    });
    return response.data;
  }

  async syncDraftToDevice(conversationId: string, deviceId: number): Promise<void> {
    await axios.post(`${API_BASE}/sync`, null, {
      params: { userId: this.userId, conversationId, deviceId },
    });
  }
}
