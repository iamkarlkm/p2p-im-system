/**
 * 阅后即焚消息类型定义
 * 
 * @author IM Development Team
 * @since 1.0.0
 */

export interface SelfDestructMessage {
  id: string;
  conversationId: string;
  senderId: string;
  senderName?: string;
  senderAvatar?: string;
  receiverId: string;
  messageContent?: string;
  contentType: ContentType;
  durationSeconds: number;
  isRead: boolean;
  readAt?: string;
  remainingSeconds?: number;
  isDestroyed: boolean;
  destroyedAt?: string;
  screenshotDetected: boolean;
  screenshotCount: number;
  allowForward: boolean;
  allowScreenshot: boolean;
  blurPreview: boolean;
  notificationMessage?: string;
  createdAt: string;
  canRead: boolean;
}

export type ContentType = 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO' | 'FILE' | 'LOCATION';

export interface SelfDestructConfig {
  durationOptions: number[];
  defaultDuration: number;
  defaultBlurPreview: boolean;
  defaultAllowScreenshot: boolean;
  defaultAllowForward: boolean;
  contentTypes: string[];
}

export interface CreateSelfDestructMessageRequest {
  conversationId: string;
  receiverId: string;
  messageContent: string;
  contentType?: ContentType;
  durationSeconds?: number;
  allowForward?: boolean;
  allowScreenshot?: boolean;
  blurPreview?: boolean;
  notificationMessage?: string;
}

export interface ReadSelfDestructMessageResponse {
  messageId: string;
  messageContent?: string;
  remainingSeconds: number;
  durationSeconds: number;
  allowScreenshot: boolean;
}

export interface ScreenshotDetectResponse {
  detected: boolean;
  totalCount: number;
  warningMessage: string;
}

export interface UnreadCountResponse {
  count: number;
}

export interface DestroyedStatusResponse {
  destroyed: boolean;
}

export interface RemainingSecondsResponse {
  remainingSeconds: number;
}
