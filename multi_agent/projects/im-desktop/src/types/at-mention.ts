/**
 * @提及类型定义
 */

export interface AtMention {
  id: number;
  messageId: number;
  senderUserId: number;
  senderNickname?: string;
  mentionedUserId: number;
  roomId: number | null;
  isRead: boolean;
  isAtAll: boolean;
  notified: boolean;
  mentionedAt: string;
  messagePreview: string;
  conversationId: string;
  roomName?: string;
}

export interface AtMentionRequest {
  messageId: number;
  mentionedUserIds?: number[];
  isAtAll?: boolean;
  conversationId?: string;
  roomId?: number;
  senderUserId: number;
  messagePreview?: string;
}

export interface AtMentionSettings {
  id?: number;
  userId: number;
  enabled: boolean;
  onlyAtAll: boolean;
  allowStrangerAt: boolean;
  syncToOtherDevices: boolean;
  dndEnabled: boolean;
  dndStartTime?: string;
  dndEndTime?: string;
}

export interface AtMentionListResponse {
  success: boolean;
  data: AtMention[];
  totalPages: number;
  totalElements: number;
}

export interface AtMentionCountResponse {
  success: boolean;
  data: number;
}
