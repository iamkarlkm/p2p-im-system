export interface BatchOperationRequest {
  conversationIds: number[];
  operationType: BatchOperationType;
  notifyParticipants?: boolean;
  reason?: string;
}

export interface BatchOperationResponse {
  operationId: number;
  operationType: BatchOperationType;
  totalCount: number;
  successCount: number;
  failureCount: number;
  status: BatchOperationStatus;
  failedConversationIds: number[];
  message?: string;
  createdAt: string;
  completedAt?: string;
}

export type BatchOperationType =
  | 'mark_read'
  | 'archive'
  | 'delete'
  | 'pin'
  | 'unpin'
  | 'mute'
  | 'unmute';

export type BatchOperationStatus =
  | 'pending'
  | 'processing'
  | 'completed'
  | 'partial'
  | 'failed';

export interface BatchOperationHistory {
  operationId: number;
  operationType: BatchOperationType;
  totalCount: number;
  successCount: number;
  failureCount: number;
  status: BatchOperationStatus;
  createdAt: string;
  completedAt?: string;
}

export const BATCH_OPERATION_LABELS: Record<BatchOperationType, string> = {
  mark_read: 'Mark as Read',
  archive: 'Archive',
  delete: 'Delete',
  pin: 'Pin',
  unpin: 'Unpin',
  mute: 'Mute',
  unmute: 'Unmute',
};

export const BATCH_OPERATION_ICONS: Record<BatchOperationType, string> = {
  mark_read: '✓',
  archive: '📁',
  delete: '🗑️',
  pin: '📌',
  unpin: '📍',
  mute: '🔇',
  unmute: '🔊',
};
