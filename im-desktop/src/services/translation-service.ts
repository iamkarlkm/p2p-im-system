import axios from 'axios';
import { TranslationRequest, TranslationSettings, MessageTranslation } from '../types/message-translation';
import { useAuthStore } from '../stores/auth-store';

const API_BASE = '/api/translation';

function getHeaders() {
  const token = useAuthStore.getState().token;
  return { Authorization: `Bearer ${token}` };
}

/**
 * 翻译文本
 */
export async function translate(request: TranslationRequest): Promise<MessageTranslation> {
  const response = await axios.post(`${API_BASE}/translate`, request, { headers: getHeaders() });
  return response.data.data;
}

/**
 * 批量翻译
 */
export async function batchTranslate(requests: TranslationRequest[]): Promise<MessageTranslation[]> {
  const response = await axios.post(`${API_BASE}/translate/batch`, requests, { headers: getHeaders() });
  return response.data.data;
}

/**
 * 获取翻译设置
 */
export async function getSettings(userId: number): Promise<TranslationSettings> {
  const response = await axios.get(`${API_BASE}/settings`, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.data;
}

/**
 * 更新翻译设置
 */
export async function updateSettings(userId: number, settings: TranslationSettings): Promise<TranslationSettings> {
  const response = await axios.put(`${API_BASE}/settings`, settings, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.data;
}
