export interface MessageSyncItem {
  messageId: number;
  conversationId: number;
  senderId: number;
  content: string;
  contentType: string;
  sentAt: string;
  deleted: boolean;
  syncAction: 'upsert' | 'delete';
}

export interface SyncRequest {
  deviceId: string;
  conversationId?: number;
  lastMessageId?: number;
  syncToken?: string;
  limit?: number;
  since?: string;
}

export interface SyncResponse {
  messages: MessageSyncItem[];
  nextMessageId?: number;
  nextSyncToken?: string;
  syncTimestamp: string;
  hasMore: boolean;
  totalSynced: number;
}

export interface SyncCheckpoint {
  id: number;
  userId: number;
  deviceId: string;
  conversationId: number;
  lastMessageId: number;
  lastSyncedAt: string;
  syncToken?: string;
}
