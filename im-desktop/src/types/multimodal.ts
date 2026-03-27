/**
 * 多模态消息类型定义
 */

export enum MessageType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  AUDIO = 'AUDIO',
  VIDEO = 'VIDEO',
  FILE = 'FILE',
  MIXED = 'MIXED',
  SYSTEM = 'SYSTEM'
}

export enum MessageStatus {
  PENDING = 'PENDING',
  SENDING = 'SENDING',
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  READ = 'READ',
  FAILED = 'FAILED'
}

export interface MessageAttachment {
  id: string;
  name: string;
  size: number;
  mimeType: string;
  url: string;
  thumbnail?: string;
  duration?: number;
  waveform?: number[];
  metadata?: Record<string, any>;
}

export interface MultimodalMessage {
  id: string;
  conversationId: string;
  senderId: string;
  senderName?: string;
  senderAvatar?: string;
  type: MessageType;
  content: string;
  renderedContent?: string;
  attachments?: MessageAttachment[];
  status: MessageStatus;
  timestamp: number;
  editedAt?: number;
  replyTo?: string;
  reactions?: MessageReaction[];
  metadata?: Record<string, any>;
}

export interface MessageReaction {
  emoji: string;
  userId: string;
  timestamp: number;
}

export interface ProcessingStatus {
  messageId: string;
  type: MessageType;
  stage: 'downloading' | 'decoding' | 'extracting' | 'rendering' | 'parsing' | 'formatting' | 'preparing' | 'compressing' | 'uploading' | 'completed' | 'error';
  progress: number;
  error?: string;
  timestamp: number;
}

export interface Conversation {
  id: string;
  type: 'direct' | 'group';
  title?: string;
  avatar?: string;
  participants: Participant[];
  lastMessage?: MultimodalMessage;
  unreadCount: number;
  createdAt: number;
  updatedAt: number;
  pinned?: boolean;
  muted?: boolean;
  archived?: boolean;
}

export interface Participant {
  id: string;
  name: string;
  avatar?: string;
  status: 'online' | 'away' | 'offline';
  lastSeen?: number;
  role?: 'owner' | 'admin' | 'member';
}

export interface AIAssistant {
  id: string;
  name: string;
  description?: string;
  avatar?: string;
  capabilities: AICapability[];
  personality?: string;
  modelProvider: string;
  modelName: string;
  settings: AIAssistantSettings;
  isActive: boolean;
  createdAt: number;
  updatedAt: number;
}

export enum AICapability {
  TEXT_GENERATION = 'TEXT_GENERATION',
  IMAGE_GENERATION = 'IMAGE_GENERATION',
  IMAGE_ANALYSIS = 'IMAGE_ANALYSIS',
  AUDIO_TRANSCRIPTION = 'AUDIO_TRANSCRIPTION',
  AUDIO_SYNTHESIS = 'AUDIO_SYNTHESIS',
  VIDEO_ANALYSIS = 'VIDEO_ANALYSIS',
  CODE_GENERATION = 'CODE_GENERATION',
  TRANSLATION = 'TRANSLATION',
  SUMMARIZATION = 'SUMMARIZATION'
}

export interface AIAssistantSettings {
  temperature?: number;
  maxTokens?: number;
  topP?: number;
  frequencyPenalty?: number;
  presencePenalty?: number;
  contextWindow?: number;
  systemPrompt?: string;
}

export interface AIConversation {
  id: string;
  assistantId: string;
  title?: string;
  messages: MultimodalMessage[];
  context: AIContextEntry[];
  createdAt: number;
  updatedAt: number;
}

export interface AIContextEntry {
  role: 'system' | 'user' | 'assistant';
  content: string;
  timestamp: number;
  metadata?: Record<string, any>;
}

export interface StreamChunk {
  id: string;
  conversationId: string;
  content: string;
  isComplete: boolean;
  timestamp: number;
}

export interface AIResponse {
  text: string;
  attachments?: MessageAttachment[];
  suggestions?: string[];
  actions?: AIAction[];
  metadata?: Record<string, any>;
}

export interface AIAction {
  type: string;
  label: string;
  payload?: any;
}
