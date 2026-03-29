/**
 * 批量操作请求类型
 */
export interface BatchMessageOperationRequest {
  messageIds: string[];
  operationType: BatchOperationType;
  targetConversationId?: string;
  targetUserIds?: string[];
  keepOriginal?: boolean;
  additionalParams?: Record<string, any>;
  reason?: string;
  deleteAfterMove?: boolean;
  asyncExecution?: boolean;
  scheduledTime?: string;
}

/**
 * 批量操作结果类型
 */
export interface BatchOperationResult {
  batchId: string;
  operationType: BatchOperationType;
  totalCount: number;
  successCount: number;
  failureCount: number;
  skippedCount: number;
  successMessageIds: string[];
  failures: FailedOperation[];
  skipped: SkippedOperation[];
  startTime: string;
  endTime: string;
  durationMs: number;
  status: BatchOperationStatus;
  asyncExecution: boolean;
  asyncTaskId?: string;
  operatorId: string;
  operatorName?: string;
  targetConversationId?: string;
  extraData?: Record<string, any>;
  generatedMessageIds?: string[];
}

export interface FailedOperation {
  messageId: string;
  errorCode: string;
  errorMessage: string;
  reason: FailureReason;
}

export interface SkippedOperation {
  messageId: string;
  reason: SkipReason;
  description: string;
}

export enum BatchOperationStatus {
  PENDING = 'PENDING',
  RUNNING = 'RUNNING',
  COMPLETED = 'COMPLETED',
  PARTIAL_SUCCESS = 'PARTIAL_SUCCESS',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
}

export enum FailureReason {
  PERMISSION_DENIED = 'PERMISSION_DENIED',
  MESSAGE_NOT_FOUND = 'MESSAGE_NOT_FOUND',
  ALREADY_DELETED = 'ALREADY_DELETED',
  RECALL_TIMEOUT = 'RECALL_TIMEOUT',
  NETWORK_ERROR = 'NETWORK_ERROR',
  SERVER_ERROR = 'SERVER_ERROR',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
}

export enum SkipReason {
  ALREADY_PROCESSED = 'ALREADY_PROCESSED',
  NO_PERMISSION = 'NO_PERMISSION',
  FILTERED_OUT = 'FILTERED_OUT',
  DUPLICATE_REQUEST = 'DUPLICATE_REQUEST',
}

/**
 * 批量操作历史查询参数
 */
export interface BatchOperationHistoryQuery {
  page?: number;
  size?: number;
  status?: BatchOperationStatus;
  operationType?: BatchOperationType;
  startTime?: string;
  endTime?: string;
}

/**
 * 批量操作任务
 */
export interface BatchOperationTask {
  id: string;
  operatorId: string;
  operationType: BatchOperationType;
  totalCount: number;
  successCount: number;
  failureCount: number;
  skippedCount: number;
  messageIds: string[];
  status: BatchOperationStatus;
  progress: number;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

import { BatchOperationType } from './batch-operation';
