// Message Draft Types
export interface MessageDraft {
  id: number;
  userId: number;
  conversationId: string;
  content: string;
  mentionIds: string;
  replyMessageId: string;
  messageType: string;
  updatedAt: number;
}

export interface DraftRequest {
  userId: number;
  conversationId: string;
  content: string;
  mentionIds?: string;
  replyMessageId?: string;
  messageType?: string;
}

export interface DraftSyncPayload {
  userId: number;
  conversationId: string;
  deviceId: number;
  draft: MessageDraft | null;
}
