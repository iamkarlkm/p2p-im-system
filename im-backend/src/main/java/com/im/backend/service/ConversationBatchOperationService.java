package com.im.backend.service;

import com.im.backend.entity.ConversationBatchOperation;
import com.im.backend.dto.BatchOperationRequest;
import com.im.backend.dto.BatchOperationResponse;
import com.im.backend.repository.ConversationBatchOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationBatchOperationService {
    
    private final ConversationBatchOperationRepository batchOperationRepository;
    private final ConversationService conversationService;
    
    @Transactional
    public BatchOperationResponse executeBatchOperation(Long userId, BatchOperationRequest request) {
        ConversationBatchOperation operation = ConversationBatchOperation.builder()
                .userId(userId)
                .operationType(request.getOperationType())
                .conversationIds(request.getConversationIds())
                .status(ConversationBatchOperation.STATUS_PROCESSING)
                .successCount(0)
                .failureCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        
        operation = batchOperationRepository.save(operation);
        
        List<Long> failedIds = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (Long conversationId : request.getConversationIds()) {
            try {
                boolean success = processConversationOperation(userId, conversationId, request.getOperationType());
                if (success) {
                    successCount++;
                } else {
                    failureCount++;
                    failedIds.add(conversationId);
                }
            } catch (Exception e) {
                log.error("Failed to process conversation {}: {}", conversationId, e.getMessage());
                failureCount++;
                failedIds.add(conversationId);
            }
        }
        
        operation.setSuccessCount(successCount);
        operation.setFailureCount(failureCount);
        operation.setStatus(failureCount == 0 ? 
                ConversationBatchOperation.STATUS_COMPLETED : 
                ConversationBatchOperation.STATUS_PARTIAL);
        operation.setCompletedAt(LocalDateTime.now());
        batchOperationRepository.save(operation);
        
        return BatchOperationResponse.builder()
                .operationId(operation.getId())
                .operationType(request.getOperationType())
                .totalCount(request.getConversationIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .status(operation.getStatus())
                .failedConversationIds(failedIds)
                .createdAt(operation.getCreatedAt())
                .completedAt(operation.getCompletedAt())
                .build();
    }
    
    private boolean processConversationOperation(Long userId, Long conversationId, String operationType) {
        switch (operationType) {
            case BatchOperationRequest.OP_MARK_READ:
                return conversationService.markAsRead(userId, conversationId);
            case BatchOperationRequest.OP_ARCHIVE:
                return conversationService.archiveConversation(userId, conversationId);
            case BatchOperationRequest.OP_DELETE:
                return conversationService.deleteConversation(userId, conversationId);
            case BatchOperationRequest.OP_PIN:
                return conversationService.pinConversation(userId, conversationId);
            case BatchOperationRequest.OP_UNPIN:
                return conversationService.unpinConversation(userId, conversationId);
            case BatchOperationRequest.OP_MUTE:
                return conversationService.muteConversation(userId, conversationId);
            case BatchOperationRequest.OP_UNMUTE:
                return conversationService.unmuteConversation(userId, conversationId);
            default:
                return false;
        }
    }
    
    public List<BatchOperationResponse> getUserBatchOperations(Long userId) {
        List<ConversationBatchOperation> operations = batchOperationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return operations.stream().map(this::toResponse).toList();
    }
    
    private BatchOperationResponse toResponse(ConversationBatchOperation operation) {
        return BatchOperationResponse.builder()
                .operationId(operation.getId())
                .operationType(operation.getOperationType())
                .totalCount(operation.getConversationIds() != null ? operation.getConversationIds().size() : 0)
                .successCount(operation.getSuccessCount())
                .failureCount(operation.getFailureCount())
                .status(operation.getStatus())
                .createdAt(operation.getCreatedAt())
                .completedAt(operation.getCompletedAt())
                .build();
    }
}
