import { BatchOperationRequest, BatchOperationResponse, BatchOperationHistory } from '../types/conversation-batch-operation';
import { apiClient } from './api-client';

export class BatchOperationService {
  async executeBatchOperation(request: BatchOperationRequest): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/execute',
      request
    );
    return response.data;
  }

  async getBatchOperationHistory(): Promise<BatchOperationHistory[]> {
    const response = await apiClient.get<BatchOperationHistory[]>(
      '/conversations/batch/history'
    );
    return response.data;
  }

  async batchMarkAsRead(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/mark-read',
      { conversationIds }
    );
    return response.data;
  }

  async batchArchive(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/archive',
      { conversationIds }
    );
    return response.data;
  }

  async batchDelete(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/delete',
      { conversationIds }
    );
    return response.data;
  }

  async batchPin(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/execute',
      {
        conversationIds,
        operationType: 'pin',
      }
    );
    return response.data;
  }

  async batchUnpin(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/execute',
      {
        conversationIds,
        operationType: 'unpin',
      }
    );
    return response.data;
  }

  async batchMute(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/execute',
      {
        conversationIds,
        operationType: 'mute',
      }
    );
    return response.data;
  }

  async batchUnmute(conversationIds: number[]): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      '/conversations/batch/execute',
      {
        conversationIds,
        operationType: 'unmute',
      }
    );
    return response.data;
  }
}

export const batchOperationService = new BatchOperationService();
