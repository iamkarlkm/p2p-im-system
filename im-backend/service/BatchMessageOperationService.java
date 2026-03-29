// 批量消息操作服务接口
package com.im.backend.service;

import com.im.backend.dto.BatchMessageOperationRequest;
import com.im.backend.dto.BatchOperationResultDTO;

import java.util.concurrent.CompletableFuture;

public interface BatchMessageOperationService {

    /**
     * 执行批量消息操作
     */
    BatchOperationResultDTO executeBatchOperation(String userId, BatchMessageOperationRequest request);

    /**
     * 异步执行批量操作
     */
    CompletableFuture<BatchOperationResultDTO> executeBatchOperationAsync(String userId, BatchMessageOperationRequest request);

    /**
     * 获取批量操作结果
     */
    BatchOperationResultDTO getBatchOperationResult(String batchId);

    /**
     * 取消正在执行的批量操作
     */
    boolean cancelBatchOperation(String batchId, String userId);

    /**
     * 获取用户的批量操作历史
     */
    java.util.List<BatchOperationResultDTO> getUserBatchOperationHistory(String userId, int page, int size);

    /**
     * 预览批量操作结果（不实际执行）
     */
    BatchOperationResultDTO previewBatchOperation(String userId, BatchMessageOperationRequest request);
}
