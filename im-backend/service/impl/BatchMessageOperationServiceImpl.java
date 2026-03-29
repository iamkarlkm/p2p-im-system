// 批量消息操作服务实现
package com.im.backend.service.impl;

import com.im.backend.dto.BatchMessageOperationRequest;
import com.im.backend.dto.BatchOperationResultDTO;
import com.im.backend.model.*;
import com.im.backend.repository.BatchOperationTaskRepository;
import com.im.backend.repository.MessageRepository;
import com.im.backend.service.BatchMessageOperationService;
import com.im.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchMessageOperationServiceImpl implements BatchMessageOperationService {

    private final BatchOperationTaskRepository taskRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Override
    @Transactional
    public BatchOperationResultDTO executeBatchOperation(String userId, BatchMessageOperationRequest request) {
        String batchId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        BatchOperationTask task = BatchOperationTask.builder()
            .id(batchId)
            .operatorId(userId)
            .operationType(request.getOperationType())
            .totalCount(request.getMessageIds().size())
            .successCount(0)
            .failureCount(0)
            .skippedCount(0)
            .messageIds(request.getMessageIds())
            .targetConversationId(request.getTargetConversationId())
            .reason(request.getReason())
            .status(BatchOperationTask.TaskStatus.RUNNING)
            .startTime(startTime)
            .asyncExecution(request.getAsyncExecution() != null && request.getAsyncExecution())
            .build();
        
        taskRepository.save(task);
        
        List<String> successIds = new ArrayList<>();
        List<BatchOperationResultDTO.FailedOperationDTO> failures = new ArrayList<>();
        List<BatchOperationResultDTO.SkippedOperationDTO> skipped = new ArrayList<>();
        
        for (String messageId : request.getMessageIds()) {
            try {
                boolean result = processSingleMessage(userId, messageId, request);
                if (result) {
                    successIds.add(messageId);
                    task.incrementSuccess();
                } else {
                    skipped.add(BatchOperationResultDTO.SkippedOperationDTO.builder()
                        .messageId(messageId)
                        .reason(BatchOperationResultDTO.SkipReason.FILTERED_OUT)
                        .description("Message filtered out")
                        .build());
                    task.incrementSkipped();
                }
            } catch (Exception e) {
                failures.add(BatchOperationResultDTO.FailedOperationDTO.builder()
                    .messageId(messageId)
                    .errorCode("PROCESS_ERROR")
                    .errorMessage(e.getMessage())
                    .reason(BatchOperationResultDTO.FailureReason.SERVER_ERROR)
                    .build());
                task.incrementFailure();
            }
        }
        
        LocalDateTime endTime = LocalDateTime.now();
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        
        BatchOperationTask.TaskStatus finalStatus = determineFinalStatus(
            task.getSuccessCount(), task.getFailureCount(), task.getSkippedCount(), task.getTotalCount()
        );
        
        task.setStatus(finalStatus);
        task.setEndTime(endTime);
        task.setDurationMs(durationMs);
        task.setSuccessMessageIds(successIds);
        taskRepository.save(task);
        
        return BatchOperationResultDTO.builder()
            .batchId(batchId)
            .operationType(request.getOperationType())
            .totalCount(request.getMessageIds().size())
            .successCount(task.getSuccessCount())
            .failureCount(task.getFailureCount())
            .skippedCount(task.getSkippedCount())
            .successMessageIds(successIds)
            .failures(failures)
            .skipped(skipped)
            .startTime(startTime)
            .endTime(endTime)
            .durationMs(durationMs)
            .status(BatchOperationResultDTO.BatchOperationStatus.valueOf(finalStatus.name()))
            .asyncExecution(task.getAsyncExecution())
            .operatorId(userId)
            .build();
    }

    private boolean processSingleMessage(String userId, String messageId, BatchMessageOperationRequest request) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            return false;
        }
        
        Message message = messageOpt.get();
        
        // 权限检查
        if (!hasPermission(userId, message, request.getOperationType())) {
            return false;
        }
        
        switch (request.getOperationType()) {
            case DELETE:
                return deleteMessage(message);
            case RECALL:
                return recallMessage(userId, message);
            case FAVORITE:
                return favoriteMessage(userId, message);
            case PIN:
                return pinMessage(message);
            case MARK_READ:
                return markAsRead(userId, message);
            case ARCHIVE:
                return archiveMessage(userId, message);
            case FORWARD:
                return forwardMessage(userId, message, request.getTargetConversationId());
            case COPY:
                return copyMessage(userId, message);
            default:
                return false;
        }
    }

    private boolean hasPermission(String userId, Message message, BatchOperationType operationType) {
        if (operationType == BatchOperationType.DELETE || operationType == BatchOperationType.RECALL) {
            return message.getSenderId().equals(userId) || isConversationAdmin(userId, message.getConversationId());
        }
        return message.getConversation().getParticipants().stream()
            .anyMatch(p -> p.getUserId().equals(userId));
    }

    private boolean isConversationAdmin(String userId, String conversationId) {
        // 实现管理员检查逻辑
        return false;
    }

    private boolean deleteMessage(Message message) {
        message.setDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);
        return true;
    }

    private boolean recallMessage(String userId, Message message) {
        if (!message.getSenderId().equals(userId)) {
            return false;
        }
        if (message.getCreatedAt().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return false;
        }
        message.setRecalled(true);
        message.setRecalledAt(LocalDateTime.now());
        messageRepository.save(message);
        return true;
    }

    private boolean favoriteMessage(String userId, Message message) {
        // 实现收藏逻辑
        return true;
    }

    private boolean pinMessage(Message message) {
        message.setPinned(true);
        message.setPinnedAt(LocalDateTime.now());
        messageRepository.save(message);
        return true;
    }

    private boolean markAsRead(String userId, Message message) {
        // 实现已读标记逻辑
        return true;
    }

    private boolean archiveMessage(String userId, Message message) {
        // 实现归档逻辑
        return true;
    }

    private boolean forwardMessage(String userId, Message message, String targetConversationId) {
        // 实现转发逻辑
        return true;
    }

    private boolean copyMessage(String userId, Message message) {
        // 实现复制逻辑
        return true;
    }

    private BatchOperationTask.TaskStatus determineFinalStatus(int success, int failure, int skipped, int total) {
        if (success == total) return BatchOperationTask.TaskStatus.COMPLETED;
        if (success > 0 && failure > 0) return BatchOperationTask.TaskStatus.PARTIAL_SUCCESS;
        if (success > 0) return BatchOperationTask.TaskStatus.PARTIAL_SUCCESS;
        if (failure == total) return BatchOperationTask.TaskStatus.FAILED;
        return BatchOperationTask.TaskStatus.COMPLETED;
    }

    @Override
    @Async
    public CompletableFuture<BatchOperationResultDTO> executeBatchOperationAsync(String userId, BatchMessageOperationRequest request) {
        return CompletableFuture.completedFuture(executeBatchOperation(userId, request));
    }

    @Override
    public BatchOperationResultDTO getBatchOperationResult(String batchId) {
        Optional<BatchOperationTask> taskOpt = taskRepository.findById(batchId);
        if (taskOpt.isEmpty()) {
            return null;
        }
        
        BatchOperationTask task = taskOpt.get();
        return BatchOperationResultDTO.builder()
            .batchId(task.getId())
            .operationType(task.getOperationType())
            .totalCount(task.getTotalCount())
            .successCount(task.getSuccessCount())
            .failureCount(task.getFailureCount())
            .skippedCount(task.getSkippedCount())
            .startTime(task.getStartTime())
            .endTime(task.getEndTime())
            .durationMs(task.getDurationMs())
            .status(BatchOperationResultDTO.BatchOperationStatus.valueOf(task.getStatus().name()))
            .asyncExecution(task.getAsyncExecution())
            .operatorId(task.getOperatorId())
            .build();
    }

    @Override
    public boolean cancelBatchOperation(String batchId, String userId) {
        Optional<BatchOperationTask> taskOpt = taskRepository.findByIdAndOperatorId(batchId, userId);
        if (taskOpt.isPresent() && taskOpt.get().getStatus() == BatchOperationTask.TaskStatus.RUNNING) {
            BatchOperationTask task = taskOpt.get();
            task.setStatus(BatchOperationTask.TaskStatus.CANCELLED);
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
            return true;
        }
        return false;
    }

    @Override
    public List<BatchOperationResultDTO> getUserBatchOperationHistory(String userId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return taskRepository.findByOperatorId(userId, pageable).getContent().stream()
            .map(task -> BatchOperationResultDTO.builder()
                .batchId(task.getId())
                .operationType(task.getOperationType())
                .totalCount(task.getTotalCount())
                .successCount(task.getSuccessCount())
                .failureCount(task.getFailureCount())
                .skippedCount(task.getSkippedCount())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .durationMs(task.getDurationMs())
                .status(BatchOperationResultDTO.BatchOperationStatus.valueOf(task.getStatus().name()))
                .operatorId(task.getOperatorId())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public BatchOperationResultDTO previewBatchOperation(String userId, BatchMessageOperationRequest request) {
        // 预览模式不实际执行，只返回预估结果
        int total = request.getMessageIds().size();
        return BatchOperationResultDTO.builder()
            .batchId("PREVIEW-" + UUID.randomUUID().toString())
            .operationType(request.getOperationType())
            .totalCount(total)
            .successCount(total)
            .failureCount(0)
            .skippedCount(0)
            .status(BatchOperationResultDTO.BatchOperationStatus.PENDING)
            .asyncExecution(request.getAsyncExecution())
            .operatorId(userId)
            .build();
    }
}
