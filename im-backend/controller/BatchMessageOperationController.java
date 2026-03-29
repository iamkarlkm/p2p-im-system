// 批量消息操作控制器
package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.BatchMessageOperationRequest;
import com.im.backend.dto.BatchOperationResultDTO;
import com.im.backend.service.BatchMessageOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/batch-operations")
@RequiredArgsConstructor
@Tag(name = "批量消息操作", description = "批量操作消息相关接口")
public class BatchMessageOperationController {

    private final BatchMessageOperationService batchOperationService;

    @PostMapping
    @Operation(summary = "执行批量操作", description = "对多条消息执行批量操作")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> executeBatchOperation(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result;
        if (request.getAsyncExecution() != null && request.getAsyncExecution()) {
            CompletableFuture<BatchOperationResultDTO> future = batchOperationService
                .executeBatchOperationAsync(userId, request);
            result = future.join();
        } else {
            result = batchOperationService.executeBatchOperation(userId, request);
        }
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/preview")
    @Operation(summary = "预览批量操作", description = "预览批量操作结果，不实际执行")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> previewBatchOperation(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.previewBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "获取批量操作结果", description = "根据批次ID获取操作结果")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> getBatchOperationResult(
            @RequestAttribute("userId") String userId,
            @PathVariable String batchId) {
        
        BatchOperationResultDTO result = batchOperationService.getBatchOperationResult(batchId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{batchId}/cancel")
    @Operation(summary = "取消批量操作", description = "取消正在执行的批量操作")
    public ResponseEntity<ApiResponse<Boolean>> cancelBatchOperation(
            @RequestAttribute("userId") String userId,
            @PathVariable String batchId) {
        
        boolean cancelled = batchOperationService.cancelBatchOperation(batchId, userId);
        return ResponseEntity.ok(ApiResponse.success(cancelled));
    }

    @GetMapping("/history")
    @Operation(summary = "获取批量操作历史", description = "获取当前用户的批量操作历史记录")
    public ResponseEntity<ApiResponse<List<BatchOperationResultDTO>>> getBatchOperationHistory(
            @RequestAttribute("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<BatchOperationResultDTO> history = batchOperationService
            .getUserBatchOperationHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/forward")
    @Operation(summary = "批量转发消息", description = "将多条消息批量转发到指定会话")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchForward(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/delete")
    @Operation(summary = "批量删除消息", description = "批量删除多条消息")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchDelete(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/recall")
    @Operation(summary = "批量撤回消息", description = "批量撤回已发送的消息")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchRecall(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/favorite")
    @Operation(summary = "批量收藏消息", description = "批量收藏多条消息")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchFavorite(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/pin")
    @Operation(summary = "批量置顶消息", description = "批量置顶多条消息")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchPin(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/mark-read")
    @Operation(summary = "批量标记已读", description = "批量将消息标记为已读")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchMarkRead(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/archive")
    @Operation(summary = "批量归档消息", description = "批量归档多条消息")
    public ResponseEntity<ApiResponse<BatchOperationResultDTO>> batchArchive(
            @RequestAttribute("userId") String userId,
            @Valid @RequestBody BatchMessageOperationRequest request) {
        
        BatchOperationResultDTO result = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
