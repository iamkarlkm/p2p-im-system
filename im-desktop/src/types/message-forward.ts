export interface ForwardRequest {
  messageIds: number[];
  targetConversationId: number;
  comment?: string;
  merged: boolean;
  mergedTitle?: string;
}

export interface ForwardResponse {
  success: boolean;
  message: string;
  newMessageIds?: number[];
  mergedForwardId?: string;
  forwardedAt: string;
}

export interface BatchForwardResponse {
  total: number;
  success: number;
  failed: number;
  results: ForwardResponse[];
}

export interface MessageForward {
  id: number;
  originalMessageId: number;
  targetConversationId: number;
  forwardedBy: number;
  forwardedAt: string;
  comment?: string;
  forwardType: 'SINGLE' | 'MERGED';
}

export interface MergedForward {
  mergedForwardId: string;
  title?: string;
  messages: MergedForwardMessage[];
}

export interface MergedForwardMessage {
  originalMessageId: number;
  senderName: string;
  content: string;
  sentAt: string;
  contentType: string;
}
