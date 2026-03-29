/**
 * AI 聊天机器人服务 - TypeScript API 封装
 * 支持 OpenAI/Claude/Gemini/Custom Webhook 多平台 AI 模型
 */

const API_BASE = '/api/bots';

export interface Bot {
  botId: string;
  name: string;
  description: string;
  avatarUrl?: string;
  ownerId: string;
  botType: 'OPENAI' | 'CLAUDE' | 'GEMINI' | 'CUSTOM' | 'LOCAL';
  modelName: string;
  apiKey?: string;
  apiBaseUrl?: string;
  webhookUrl?: string;
  webhookSecret?: string;
  systemPrompt?: string;
  maxTokens: number;
  temperature: number;
  status: 'ACTIVE' | 'INACTIVE' | 'DELETED';
  isPublic: boolean;
  enableImageGen: boolean;
  enableSpeechToText: boolean;
  rateLimit: number;
  sessionCount: number;
  messageCount: number;
  totalTokensUsed: number;
  accessToken?: string;
  createdAt: string;
  updatedAt: string;
  lastActiveAt?: string;
}

export interface BotSession {
  sessionId: string;
  botId: string;
  userId: string;
  conversationId: string;
  contextTokens: number;
  turnCount: number;
  totalTokensUsed: number;
  status: 'ACTIVE' | 'ENDED' | 'TIMEOUT';
  endReason?: string;
  promptVersion?: string;
  createdAt: string;
  lastMessageAt: string;
  endedAt?: string;
}

export interface ChatResponse {
  reply: string;
  botId: string;
  timestamp: string;
}

export interface BotStats {
  totalSessions: number;
  activeSessions: number;
  totalTokens: number;
  avgTurns: number;
  totalMessages: number;
  totalTokensUsed: number;
}

export interface BotPreset {
  type: string;
  model: string;
  name: string;
}

// ========== Bot CRUD ==========

export async function createBot(data: {
  name: string;
  description?: string;
  botType?: string;
  modelName?: string;
  ownerId: string;
}): Promise<Bot> {
  const res = await fetch(`${API_BASE}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error(`创建机器人失败: ${res.status}`);
  return res.json();
}

export async function getBot(botId: string): Promise<Bot> {
  const res = await fetch(`${API_BASE}/${botId}`);
  if (!res.ok) throw new Error(`获取机器人失败: ${res.status}`);
  return res.json();
}

export async function getBotByToken(token: string): Promise<Bot> {
  const res = await fetch(`${API_BASE}/token/${token}`);
  if (!res.ok) throw new Error(`Token 无效: ${res.status}`);
  return res.json();
}

export async function getMyBots(ownerId: string): Promise<Bot[]> {
  const res = await fetch(`${API_BASE}/my?ownerId=${ownerId}`);
  if (!res.ok) throw new Error(`获取我的机器人失败: ${res.status}`);
  return res.json();
}

export async function getPublicBots(): Promise<Bot[]> {
  const res = await fetch(`${API_BASE}/public`);
  if (!res.ok) throw new Error(`获取公开机器人失败: ${res.status}`);
  return res.json();
}

export async function updateBot(botId: string, updates: Partial<Bot>): Promise<Bot> {
  const res = await fetch(`${API_BASE}/${botId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updates),
  });
  if (!res.ok) throw new Error(`更新机器人失败: ${res.status}`);
  return res.json();
}

export async function deleteBot(botId: string): Promise<void> {
  const res = await fetch(`${API_BASE}/${botId}`, { method: 'DELETE' });
  if (!res.ok) throw new Error(`删除机器人失败: ${res.status}`);
}

// ========== Chat ==========

export async function chatWithBot(
  botId: string,
  userId: string,
  conversationId: string,
  message: string
): Promise<ChatResponse> {
  const res = await fetch(`${API_BASE}/${botId}/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, conversationId, message }),
  });
  if (!res.ok) throw new Error(`AI 对话失败: ${res.status}`);
  return res.json();
}

export async function sendWebhookMessage(
  token: string,
  message: string,
  userId?: string,
  conversationId?: string
): Promise<{ reply: string }> {
  const res = await fetch(`${API_BASE}/webhook/${token}/receive`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, userId, conversationId }),
  });
  if (!res.ok) throw new Error(`Webhook 调用失败: ${res.status}`);
  return res.json();
}

// ========== Sessions ==========

export async function getSession(sessionId: string): Promise<BotSession> {
  const res = await fetch(`${API_BASE}/sessions/${sessionId}`);
  if (!res.ok) throw new Error(`获取会话失败: ${res.status}`);
  return res.json();
}

export async function getUserSessions(userId: string): Promise<BotSession[]> {
  const res = await fetch(`${API_BASE}/sessions/user/${userId}`);
  if (!res.ok) throw new Error(`获取用户会话失败: ${res.status}`);
  return res.json();
}

export async function endSession(sessionId: string, reason?: string): Promise<void> {
  const res = await fetch(`${API_BASE}/sessions/${sessionId}/end`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ reason: reason || 'USER_END' }),
  });
  if (!res.ok) throw new Error(`结束会话失败: ${res.status}`);
}

// ========== Stats ==========

export async function getBotStats(botId: string): Promise<BotStats> {
  const res = await fetch(`${API_BASE}/${botId}/stats`);
  if (!res.ok) throw new Error(`获取统计失败: ${res.status}`);
  return res.json();
}

export async function getBotLeaderboard(): Promise<Array<{
  botId: string;
  name: string;
  messageCount: number;
  sessionCount: number;
  totalTokensUsed: number;
}>> {
  const res = await fetch(`${API_BASE}/leaderboard`);
  if (!res.ok) throw new Error(`获取排行榜失败: ${res.status}`);
  return res.json();
}

// ========== Presets ==========

export async function getBotPresets(): Promise<BotPreset[]> {
  const res = await fetch(`${API_BASE}/presets`);
  if (!res.ok) throw new Error(`获取预设失败: ${res.status}`);
  return res.json();
}

// ========== Preset Config Templates ==========

export const PRESET_CONFIGS: Record<string, Partial<Bot>> = {
  OPENAI_GPT4: {
    botType: 'OPENAI',
    modelName: 'gpt-4',
    maxTokens: 8192,
    temperature: 0.7,
    systemPrompt: '你是一个有帮助的 AI 助手。',
  },
  OPENAI_GPT4_TURBO: {
    botType: 'OPENAI',
    modelName: 'gpt-4-turbo',
    maxTokens: 128000,
    temperature: 0.7,
    systemPrompt: '你是一个快速而准确的 AI 助手。',
  },
  OPENAI_GPT35: {
    botType: 'OPENAI',
    modelName: 'gpt-3.5-turbo',
    maxTokens: 16384,
    temperature: 0.7,
    systemPrompt: '你是一个经济实惠的 AI 助手。',
  },
  CLAUDE_OPUS: {
    botType: 'CLAUDE',
    modelName: 'claude-3-opus-20240229',
    maxTokens: 4096,
    temperature: 0.7,
    systemPrompt: 'You are a helpful AI assistant.',
  },
  CLAUDE_SONNET: {
    botType: 'CLAUDE',
    modelName: 'claude-3-sonnet-20240229',
    maxTokens: 4096,
    temperature: 0.7,
    systemPrompt: 'You are a fast and capable AI assistant.',
  },
  GEMINI_PRO: {
    botType: 'GEMINI',
    modelName: 'gemini-pro',
    maxTokens: 32768,
    temperature: 0.9,
    systemPrompt: '你是一个有用的 AI 助手。',
  },
  CUSTOM: {
    botType: 'CUSTOM',
    modelName: 'custom',
    maxTokens: 4096,
    temperature: 0.7,
    systemPrompt: 'Custom webhook bot.',
  },
};

export default {
  createBot,
  getBot,
  getBotByToken,
  getMyBots,
  getPublicBots,
  updateBot,
  deleteBot,
  chatWithBot,
  sendWebhookMessage,
  getSession,
  getUserSessions,
  endSession,
  getBotStats,
  getBotLeaderboard,
  getBotPresets,
  PRESET_CONFIGS,
};
