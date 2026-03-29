package com.im.backend.dto;

import com.im.backend.model.MessageMultiSelectOperation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息多选操作 DTO
 */
public class MessageMultiSelectOperationDTO {
    
    private Long id;
    private Long userId;
    private String conversationId;
    private MessageMultiSelectOperation.ConversationType conversationType;
    private List<Long> selectedMessageIds;
    private MessageMultiSelectOperation.OperationType operationType;
    private String targetConversationId;
    private MessageMultiSelectOperation.OperationResult operationResult;
    private Integer successCount;
    private Integer failCount;
    private String failReasons;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    
    // 请求 DTO
    public static class CreateRequest {
        @NotNull(message = "会话ID不能为空")
        private String conversationId;
        
        @NotNull(message = "会话类型不能为空")
        private MessageMultiSelectOperation.ConversationType conversationType;
        
        @NotEmpty(message = "至少选择一条消息")
        @Size(max = 100, message = "单次最多选择100条消息")
        private List<Long> selectedMessageIds;
        
        @NotNull(message = "操作类型不能为空")
        private MessageMultiSelectOperation.OperationType operationType;
        
        private String targetConversationId;
        
        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        
        public MessageMultiSelectOperation.ConversationType getConversationType() { return conversationType; }
        public void setConversationType(MessageMultiSelectOperation.ConversationType conversationType) { this.conversationType = conversationType; }
        
        public List<Long> getSelectedMessageIds() { return selectedMessageIds; }
        public void setSelectedMessageIds(List<Long> selectedMessageIds) { this.selectedMessageIds = selectedMessageIds; }
        
        public MessageMultiSelectOperation.OperationType getOperationType() { return operationType; }
        public void setOperationType(MessageMultiSelectOperation.OperationType operationType) { this.operationType = operationType; }
        
        public String getTargetConversationId() { return targetConversationId; }
        public void setTargetConversationId(String targetConversationId) { this.targetConversationId = targetConversationId; }
    }
    
    // 响应 DTO
    public static class Response {
        private Long id;
        private String conversationId;
        private MessageMultiSelectOperation.ConversationType conversationType;
        private Integer selectedCount;
        private MessageMultiSelectOperation.OperationType operationType;
        private MessageMultiSelectOperation.OperationResult operationResult;
        private Integer successCount;
        private Integer failCount;
        private Double successRate;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private Boolean completed;
        
        public static Response fromEntity(MessageMultiSelectOperation entity) {
            Response dto = new Response();
            dto.setId(entity.getId());
            dto.setConversationId(entity.getConversationId());
            dto.setConversationType(entity.getConversationType());
            dto.setSelectedCount(entity.getTotalCount());
            dto.setOperationType(entity.getOperationType());
            dto.setOperationResult(entity.getOperationResult());
            dto.setSuccessCount(entity.getSuccessCount());
            dto.setFailCount(entity.getFailCount());
            dto.setSuccessRate(entity.getSuccessRate());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setCompletedAt(entity.getCompletedAt());
            dto.setCompleted(entity.isCompleted());
            return dto;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        
        public MessageMultiSelectOperation.ConversationType getConversationType() { return conversationType; }
        public void setConversationType(MessageMultiSelectOperation.ConversationType conversationType) { this.conversationType = conversationType; }
        
        public Integer getSelectedCount() { return selectedCount; }
        public void setSelectedCount(Integer selectedCount) { this.selectedCount = selectedCount; }
        
        public MessageMultiSelectOperation.OperationType getOperationType() { return operationType; }
        public void setOperationType(MessageMultiSelectOperation.OperationType operationType) { this.operationType = operationType; }
        
        public MessageMultiSelectOperation.OperationResult getOperationResult() { return operationResult; }
        public void setOperationResult(MessageMultiSelectOperation.OperationResult operationResult) { this.operationResult = operationResult; }
        
        public Integer getSuccessCount() { return successCount; }
        public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
        
        public Integer getFailCount() { return failCount; }
        public void setFailCount(Integer failCount) { this.failCount = failCount; }
        
        public Double getSuccessRate() { return successRate; }
        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }
    
    // 批量操作结果
    public static class BatchResult {
        private Long operationId;
        private MessageMultiSelectOperation.OperationResult result;
        private Integer totalCount;
        private Integer successCount;
        private Integer failCount;
        private List<FailDetail> failDetails;
        private LocalDateTime completedAt;
        
        public static class FailDetail {
            private Long messageId;
            private String reason;
            
            public FailDetail(Long messageId, String reason) {
                this.messageId = messageId;
                this.reason = reason;
            }
            
            public Long getMessageId() { return messageId; }
            public void setMessageId(Long messageId) { this.messageId = messageId; }
            
            public String getReason() { return reason; }
            public void setReason(String reason) { this.reason = reason; }
        }
        
        // Getters and Setters
        public Long getOperationId() { return operationId; }
        public void setOperationId(Long operationId) { this.operationId = operationId; }
        
        public MessageMultiSelectOperation.OperationResult getResult() { return result; }
        public void setResult(MessageMultiSelectOperation.OperationResult result) { this.result = result; }
        
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        
        public Integer getSuccessCount() { return successCount; }
        public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
        
        public Integer getFailCount() { return failCount; }
        public void setFailCount(Integer failCount) { this.failCount = failCount; }
        
        public List<FailDetail> getFailDetails() { return failDetails; }
        public void setFailDetails(List<FailDetail> failDetails) { this.failDetails = failDetails; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    }
    
    // 统计 DTO
    public static class Statistics {
        private Long userId;
        private Integer totalOperations;
        private Integer totalMessagesProcessed;
        private Integer totalSuccess;
        private Integer totalFail;
        private Double overallSuccessRate;
        private List<OperationTypeStat> byOperationType;
        
        public static class OperationTypeStat {
            private MessageMultiSelectOperation.OperationType type;
            private Integer count;
            private Double avgSuccessRate;
            
            public OperationTypeStat(MessageMultiSelectOperation.OperationType type, 
                                    Integer count, Double avgSuccessRate) {
                this.type = type;
                this.count = count;
                this.avgSuccessRate = avgSuccessRate;
            }
            
            public MessageMultiSelectOperation.OperationType getType() { return type; }
            public void setType(MessageMultiSelectOperation.OperationType type) { this.type = type; }
            
            public Integer getCount() { return count; }
            public void setCount(Integer count) { this.count = count; }
            
            public Double getAvgSuccessRate() { return avgSuccessRate; }
            public void setAvgSuccessRate(Double avgSuccessRate) { this.avgSuccessRate = avgSuccessRate; }
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Integer getTotalOperations() { return totalOperations; }
        public void setTotalOperations(Integer totalOperations) { this.totalOperations = totalOperations; }
        
        public Integer getTotalMessagesProcessed() { return totalMessagesProcessed; }
        public void setTotalMessagesProcessed(Integer totalMessagesProcessed) { this.totalMessagesProcessed = totalMessagesProcessed; }
        
        public Integer getTotalSuccess() { return totalSuccess; }
        public void setTotalSuccess(Integer totalSuccess) { this.totalSuccess = totalSuccess; }
        
        public Integer getTotalFail() { return totalFail; }
        public void setTotalFail(Integer totalFail) { this.totalFail = totalFail; }
        
        public Double getOverallSuccessRate() { return overallSuccessRate; }
        public void setOverallSuccessRate(Double overallSuccessRate) { this.overallSuccessRate = overallSuccessRate; }
        
        public List<OperationTypeStat> getByOperationType() { return byOperationType; }
        public void setByOperationType(List<OperationTypeStat> byOperationType) { this.byOperationType = byOperationType; }
    }
}
