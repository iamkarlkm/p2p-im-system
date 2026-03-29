/**
 * 批量操作状态管理 Store
 */
import { makeAutoObservable, runInAction } from 'mobx';
import { 
  BatchMessageOperationRequest, 
  BatchOperationResult, 
  BatchOperationStatus,
  BatchOperationHistoryQuery 
} from '../types/batch-operation-request';
import { BatchOperationType, getMaxBatchSize } from '../types/batch-operation';
import { batchOperationApi } from '../api/batch-operation-api';

export class BatchOperationStore {
  // 状态
  selectedMessageIds: string[] = [];
  currentOperation: BatchOperationType | null = null;
  operationResult: BatchOperationResult | null = null;
  operationHistory: BatchOperationResult[] = [];
  isLoading = false;
  error: string | null = null;
  
  // 批量选择状态
  isBatchSelecting = false;
  lastBatchOperationId: string | null = null;
  
  // 进度追踪
  progressMap: Map<string, number> = new Map();
  
  constructor() {
    makeAutoObservable(this);
  }

  // ============ 选择管理 ============
  
  /**
   * 开始批量选择
   */
  startBatchSelection() {
    this.isBatchSelecting = true;
    this.selectedMessageIds = [];
  }

  /**
   * 结束批量选择
   */
  endBatchSelection() {
    this.isBatchSelecting = false;
    this.selectedMessageIds = [];
  }

  /**
   * 切换消息选择状态
   */
  toggleMessageSelection(messageId: string) {
    const index = this.selectedMessageIds.indexOf(messageId);
    if (index === -1) {
      this.selectedMessageIds.push(messageId);
    } else {
      this.selectedMessageIds.splice(index, 1);
    }
  }

  /**
   * 选择单个消息
   */
  selectMessage(messageId: string) {
    if (!this.selectedMessageIds.includes(messageId)) {
      this.selectedMessageIds.push(messageId);
    }
  }

  /**
   * 取消选择单个消息
   */
  deselectMessage(messageId: string) {
    const index = this.selectedMessageIds.indexOf(messageId);
    if (index !== -1) {
      this.selectedMessageIds.splice(index, 1);
    }
  }

  /**
   * 全选
   */
  selectAll(messageIds: string[], maxCount?: number) {
    const limit = maxCount ?? 100;
    this.selectedMessageIds = messageIds.slice(0, limit);
  }

  /**
   * 清空选择
   */
  clearSelection() {
    this.selectedMessageIds = [];
  }

  /**
   * 是否已选中某条消息
   */
  isSelected(messageId: string): boolean {
    return this.selectedMessageIds.includes(messageId);
  }

  /**
   * 获取选中数量
   */
  get selectedCount(): number {
    return this.selectedMessageIds.length;
  }

  /**
   * 检查是否超过最大批量数
   */
  isOverLimit(operationType: BatchOperationType): boolean {
    return this.selectedCount > getMaxBatchSize(operationType);
  }

  // ============ 批量操作执行 ============

  /**
   * 执行批量操作
   */
  async executeBatchOperation(
    operationType: BatchOperationType,
    additionalParams?: Record<string, any>
  ): Promise<BatchOperationResult | null> {
    if (this.selectedMessageIds.length === 0) {
      this.error = '请先选择消息';
      return null;
    }

    const maxSize = getMaxBatchSize(operationType);
    if (this.selectedCount > maxSize) {
      this.error = `批量操作最多支持 ${maxSize} 条消息，当前已选择 ${this.selectedCount} 条`;
      return null;
    }

    this.isLoading = true;
    this.error = null;
    this.currentOperation = operationType;

    try {
      const request: BatchMessageOperationRequest = {
        messageIds: [...this.selectedMessageIds],
        operationType,
        asyncExecution: this.selectedCount > 50,
        ...additionalParams,
      };

      const response = await batchOperationApi.executeBatchOperation(request);
      
      runInAction(() => {
        if (response.success && response.data) {
          this.operationResult = response.data;
          this.lastBatchOperationId = response.data.batchId;
          
          // 如果操作完成，清空选择
          if (response.data.status === BatchOperationStatus.COMPLETED) {
            this.clearSelection();
          }
        } else {
          this.error = response.message || '操作失败';
        }
        this.isLoading = false;
      });

      return this.operationResult;
    } catch (err: any) {
      runInAction(() => {
        this.error = err.message || '执行批量操作失败';
        this.isLoading = false;
      });
      return null;
    }
  }

  /**
   * 预览批量操作
   */
  async previewBatchOperation(
    operationType: BatchOperationType,
    additionalParams?: Record<string, any>
  ): Promise<BatchOperationResult | null> {
    if (this.selectedMessageIds.length === 0) {
      return null;
    }

    this.isLoading = true;
    
    try {
      const request: BatchMessageOperationRequest = {
        messageIds: [...this.selectedMessageIds],
        operationType,
        ...additionalParams,
      };

      const response = await batchOperationApi.previewBatchOperation(request);
      
      runInAction(() => {
        this.isLoading = false;
      });

      return response.success ? response.data : null;
    } catch (err) {
      runInAction(() => {
        this.isLoading = false;
      });
      return null;
    }
  }

  /**
   * 获取批量操作结果
   */
  async getOperationResult(batchId: string): Promise<BatchOperationResult | null> {
    try {
      const response = await batchOperationApi.getBatchOperationResult(batchId);
      if (response.success && response.data) {
        runInAction(() => {
          this.operationResult = response.data;
        });
        return response.data;
      }
    } catch (err) {
      console.error('获取操作结果失败:', err);
    }
    return null;
  }

  /**
   * 取消批量操作
   */
  async cancelBatchOperation(batchId: string): Promise<boolean> {
    try {
      const response = await batchOperationApi.cancelBatchOperation(batchId);
      return response.success && response.data;
    } catch (err) {
      return false;
    }
  }

  /**
   * 加载批量操作历史
   */
  async loadOperationHistory(query: BatchOperationHistoryQuery = {}) {
    this.isLoading = true;
    
    try {
      const response = await batchOperationApi.getBatchOperationHistory(query);
      
      runInAction(() => {
        if (response.success && response.data) {
          this.operationHistory = response.data;
        }
        this.isLoading = false;
      });
    } catch (err) {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 刷新当前操作状态
   */
  async refreshCurrentOperation() {
    if (this.lastBatchOperationId) {
      await this.getOperationResult(this.lastBatchOperationId);
    }
  }

  /**
   * 清空结果
   */
  clearResult() {
    this.operationResult = null;
    this.error = null;
  }

  /**
   * 快速操作：批量转发
   */
  async quickForward(targetConversationId: string, keepOriginal = false) {
    return this.executeBatchOperation(BatchOperationType.FORWARD, {
      targetConversationId,
      keepOriginal,
    });
  }

  /**
   * 快速操作：批量删除
   */
  async quickDelete(reason?: string) {
    return this.executeBatchOperation(BatchOperationType.DELETE, { reason });
  }

  /**
   * 快速操作：批量收藏
   */
  async quickFavorite() {
    return this.executeBatchOperation(BatchOperationType.FAVORITE);
  }

  /**
   * 快速操作：批量撤回
   */
  async quickRecall() {
    return this.executeBatchOperation(BatchOperationType.RECALL);
  }

  /**
   * 快速操作：批量置顶
   */
  async quickPin() {
    return this.executeBatchOperation(BatchOperationType.PIN);
  }

  /**
   * 快速操作：批量归档
   */
  async quickArchive() {
    return this.executeBatchOperation(BatchOperationType.ARCHIVE);
  }

  /**
   * 快速操作：批量标记已读
   */
  async quickMarkRead() {
    return this.executeBatchOperation(BatchOperationType.MARK_READ);
  }

  // ============ 计算属性 ============

  /**
   * 是否有选中的消息
   */
  get hasSelection(): boolean {
    return this.selectedCount > 0;
  }

  /**
   * 当前操作是否进行中
   */
  get isOperating(): boolean {
    return this.operationResult?.status === BatchOperationStatus.RUNNING;
  }

  /**
   * 当前操作是否完成
   */
  get isOperationComplete(): boolean {
    return this.operationResult?.status === BatchOperationStatus.COMPLETED ||
           this.operationResult?.status === BatchOperationStatus.PARTIAL_SUCCESS ||
           this.operationResult?.status === BatchOperationStatus.FAILED;
  }

  /**
   * 当前操作进度
   */
  get operationProgress(): number {
    if (!this.operationResult) return 0;
    if (this.operationResult.totalCount === 0) return 0;
    return ((this.operationResult.successCount + this.operationResult.failureCount + this.operationResult.skippedCount) / 
            this.operationResult.totalCount) * 100;
  }
}

// 单例导出
export const batchOperationStore = new BatchOperationStore();
export default batchOperationStore;
