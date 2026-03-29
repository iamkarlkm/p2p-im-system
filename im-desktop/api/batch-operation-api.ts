/**
 * 批量操作API
 */
import apiClient from './client';
import { 
  BatchMessageOperationRequest, 
  BatchOperationResult,
  BatchOperationHistoryQuery 
} from '../types/batch-operation-request';
import { ApiResponse } from '../types/api';

const BASE_URL = '/batch-operations';

export const batchOperationApi = {
  /**
   * 执行批量操作
   */
  executeBatchOperation: async (
    request: BatchMessageOperationRequest
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      BASE_URL, 
      request
    );
    return response.data;
  },

  /**
   * 预览批量操作
   */
  previewBatchOperation: async (
    request: BatchMessageOperationRequest
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/preview`, 
      request
    );
    return response.data;
  },

  /**
   * 获取批量操作结果
   */
  getBatchOperationResult: async (
    batchId: string
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.get<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/${batchId}`
    );
    return response.data;
  },

  /**
   * 取消批量操作
   */
  cancelBatchOperation: async (
    batchId: string
  ): Promise<ApiResponse<boolean>> => {
    const response = await apiClient.post<ApiResponse<boolean>>(
      `${BASE_URL}/${batchId}/cancel`
    );
    return response.data;
  },

  /**
   * 获取批量操作历史
   */
  getBatchOperationHistory: async (
    query: BatchOperationHistoryQuery = {}
  ): Promise<ApiResponse<BatchOperationResult[]>> => {
    const { page = 0, size = 20, ...rest } = query;
    const response = await apiClient.get<ApiResponse<BatchOperationResult[]>>(
      `${BASE_URL}/history`,
      { params: { page, size, ...rest } }
    );
    return response.data;
  },

  /**
   * 批量转发
   */
  batchForward: async (
    messageIds: string[],
    targetConversationId: string,
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/forward`,
      {
        messageIds,
        operationType: 'FORWARD',
        targetConversationId,
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量删除
   */
  batchDelete: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/delete`,
      {
        messageIds,
        operationType: 'DELETE',
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量撤回
   */
  batchRecall: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/recall`,
      {
        messageIds,
        operationType: 'RECALL',
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量收藏
   */
  batchFavorite: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/favorite`,
      {
        messageIds,
        operationType: 'FAVORITE',
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量置顶
   */
  batchPin: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/pin`,
      {
        messageIds,
        operationType: 'PIN',
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量标记已读
   */
  batchMarkRead: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/mark-read`,
      {
        messageIds,
        operationType: 'MARK_READ',
        ...options,
      }
    );
    return response.data;
  },

  /**
   * 批量归档
   */
  batchArchive: async (
    messageIds: string[],
    options: Partial<BatchMessageOperationRequest> = {}
  ): Promise<ApiResponse<BatchOperationResult>> => {
    const response = await apiClient.post<ApiResponse<BatchOperationResult>>(
      `${BASE_URL}/archive`,
      {
        messageIds,
        operationType: 'ARCHIVE',
        ...options,
      }
    );
    return response.data;
  },
};

export default batchOperationApi;
