// Read Receipt Types
export interface ReadReceipt {
  userId: number;
  conversationId: string;
  messageId: string;
  readAt: number;
  readByUsers?: number[];
}

export interface ReadReceiptRequest {
  userId: number;
  conversationId: string;
  messageId?: string;
  messageIds?: string[];
  readAt?: number;
}
