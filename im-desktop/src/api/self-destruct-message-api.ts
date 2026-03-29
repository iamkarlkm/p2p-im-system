/**
 * 阅后即焚消息API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */

import axios from 'axios';
import {
  SelfDestructMessage,
  SelfDestructConfig,
  CreateSelfDestructMessageRequest,
  ReadSelfDestructMessageResponse,
  ScreenshotDetectResponse,
  UnreadCountResponse,
  DestroyedStatusResponse,
  RemainingSecondsResponse,
} from '../types/self-destruct-message';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器添加token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const selfDestructMessageApi = {
  getConfig(): Promise<SelfDestructConfig> {
    return api.get('/self-destruct-messages/config').then(res => res.data);
  },

  createMessage(request: CreateSelfDestructMessageRequest): Promise<SelfDestructMessage> {
    return api.post('/self-destruct-messages', request).then(res => res.data);
  },

  readMessage(messageId: string): Promise<ReadSelfDestructMessageResponse> {
    return api.post(`/self-destruct-messages/${messageId}/read`).then(res => res.data);
  },

  getMessage(messageId: string): Promise<SelfDestructMessage> {
    return api.get(`/self-destruct-messages/${messageId}`).then(res => res.data);
  },

  getMessagesByConversation(conversationId: string, page: number = 0, size: number = 20): Promise<SelfDestructMessage[]> {
    return api.get(`/self-destruct-messages/conversation/${conversationId}`, {
      params: { page, size },
    }).then(res => res.data);
  },

  getSentMessages(): Promise<SelfDestructMessage[]> {
    return api.get('/self-destruct-messages/sent').then(res => res.data);
  },

  getReceivedMessages(): Promise<SelfDestructMessage[]> {
    return api.get('/self-destruct-messages/received').then(res => res.data);
  },

  getUnreadCount(): Promise<UnreadCountResponse> {
    return api.get('/self-destruct-messages/unread/count').then(res => res.data);
  },

  getUnreadCountByConversation(conversationId: string): Promise<UnreadCountResponse> {
    return api.get(`/self-destruct-messages/conversation/${conversationId}/unread/count`).then(res => res.data);
  },

  deleteMessage(messageId: string): Promise<void> {
    return api.delete(`/self-destruct-messages/${messageId}`).then(() => undefined);
  },

  destroyMessage(messageId: string): Promise<void> {
    return api.post(`/self-destruct-messages/${messageId}/destroy`).then(() => undefined);
  },

  detectScreenshot(messageId: string): Promise<ScreenshotDetectResponse> {
    return api.post(`/self-destruct-messages/${messageId}/screenshot-detect`, { messageId }).then(res => res.data);
  },

  getScreenshotDetectedMessages(): Promise<SelfDestructMessage[]> {
    return api.get('/self-destruct-messages/screenshot-detected').then(res => res.data);
  },

  isMessageDestroyed(messageId: string): Promise<DestroyedStatusResponse> {
    return api.get(`/self-destruct-messages/${messageId}/destroyed`).then(res => res.data);
  },

  getRemainingSeconds(messageId: string): Promise<RemainingSecondsResponse> {
    return api.get(`/self-destruct-messages/${messageId}/remaining-seconds`).then(res => res.data);
  },
};
