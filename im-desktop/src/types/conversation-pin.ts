export interface PinConversationRequest {
  conversationId: number;
  sortOrder?: number;
  pinNote?: string;
}

export interface PinnedConversation {
  conversationId: number;
  conversationName: string;
  sortOrder: number;
  pinnedAt: string;
  pinNote?: string;
}

export interface PinConversationResponse {
  success: boolean;
  message: string;
  pinnedConversations?: PinnedConversation[];
}
