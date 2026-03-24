package com.im.backend.controller;

import com.im.backend.dto.BatchOperationRequest;
import com.im.backend.dto.BatchOperationResponse;
import com.im.backend.service.ConversationBatchOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations/batch")
@RequiredArgsConstructor
public class ConversationBatchOperationController {
    
    private final ConversationBatchOperationService batchOperationService;
    
    @PostMapping("/execute")
    public ResponseEntity<BatchOperationResponse> executeBatchOperation(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BatchOperationRequest request) {
        BatchOperationResponse response = batchOperationService.executeBatchOperation(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<BatchOperationResponse>> getBatchOperationHistory(
            @RequestHeader("X-User-Id") Long userId) {
        List<BatchOperationResponse> history = batchOperationService.getUserBatchOperations(userId);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/mark-read")
    public ResponseEntity<BatchOperationResponse> batchMarkAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, List<Long>> request) {
        BatchOperationRequest batchRequest = BatchOperationRequest.builder()
                .conversationIds(request.get("conversationIds"))
                .operationType(BatchOperationRequest.OP_MARK_READ)
                .build();
        BatchOperationResponse response = batchOperationService.executeBatchOperation(userId, batchRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/archive")
    public ResponseEntity<BatchOperationResponse> batchArchive(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, List<Long>> request) {
        BatchOperationRequest batchRequest = BatchOperationRequest.builder()
                .conversationIds(request.get("conversationIds"))
                .operationType(BatchOperationRequest.OP_ARCHIVE)
                .build();
        BatchOperationResponse response = batchOperationService.executeBatchOperation(userId, batchRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/delete")
    public ResponseEntity<BatchOperationResponse> batchDelete(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, List<Long>> request) {
        BatchOperationRequest batchRequest = BatchOperationRequest.builder()
                .conversationIds(request.get("conversationIds"))
                .operationType(BatchOperationRequest.OP_DELETE)
                .build();
        BatchOperationResponse response = batchOperationService.executeBatchOperation(userId, batchRequest);
        return ResponseEntity.ok(response);
    }
}
