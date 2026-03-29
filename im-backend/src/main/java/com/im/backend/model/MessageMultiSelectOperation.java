package com.im.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息多选操作记录实体
 * 支持批量删除、转发、收藏等操作
 */
@Entity
@Table(name = "message_multi_select_operations")
public class MessageMultiSelectOperation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "conversation_id", nullable = false)
    private String conversationId;
    
    @Column(name = "conversation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConversationType conversationType;
    
    @ElementCollection
    @CollectionTable(name = "selected_message_ids", joinColumns = @JoinColumn(name = "operation_id"))
    @Column(name = "message_id")
    private List<Long> selectedMessageIds;
    
    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    
    @Column(name = "target_conversation_id")
    private String targetConversationId;
    
    @Column(name = "operation_result")
    @Enumerated(EnumType.STRING)
    private OperationResult operationResult;
    
    @Column(name = "success_count")
    private Integer successCount;
    
    @Column(name = "fail_count")
    private Integer failCount;
    
    @Column(name = "fail_reasons", length = 2000)
    private String failReasons;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "device_info")
    private String deviceInfo;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    // 会话类型枚举
    public enum ConversationType {
        PRIVATE, GROUP, CHANNEL
    }
    
    // 操作类型枚举
    public enum OperationType {
        DELETE,      // 批量删除
        FORWARD,     // 批量转发
        FAVORITE,    // 批量收藏
        COPY,        // 批量复制
        EXPORT,      // 批量导出
        PIN,         // 批量置顶
        UNPIN        // 批量取消置顶
    }
    
    // 操作结果枚举
    public enum OperationResult {
        SUCCESS,     // 全部成功
        PARTIAL,     // 部分成功
        FAILED       // 全部失败
    }
    
    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 构造方法
    public MessageMultiSelectOperation() {}
    
    public MessageMultiSelectOperation(Long userId, String conversationId, 
                                       ConversationType conversationType,
                                       List<Long> selectedMessageIds, 
                                       OperationType operationType) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.selectedMessageIds = selectedMessageIds;
        this.operationType = operationType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public ConversationType getConversationType() { return conversationType; }
    public void setConversationType(ConversationType conversationType) { this.conversationType = conversationType; }
    
    public List<Long> getSelectedMessageIds() { return selectedMessageIds; }
    public void setSelectedMessageIds(List<Long> selectedMessageIds) { this.selectedMessageIds = selectedMessageIds; }
    
    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }
    
    public String getTargetConversationId() { return targetConversationId; }
    public void setTargetConversationId(String targetConversationId) { this.targetConversationId = targetConversationId; }
    
    public OperationResult getOperationResult() { return operationResult; }
    public void setOperationResult(OperationResult operationResult) { this.operationResult = operationResult; }
    
    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }
    
    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }
    
    public String getFailReasons() { return failReasons; }
    public void setFailReasons(String failReasons) { this.failReasons = failReasons; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    // 业务方法
    public int getTotalCount() {
        return selectedMessageIds != null ? selectedMessageIds.size() : 0;
    }
    
    public double getSuccessRate() {
        int total = getTotalCount();
        if (total == 0) return 0.0;
        return (double) (successCount != null ? successCount : 0) / total * 100;
    }
    
    public void complete(OperationResult result, int success, int fail, String reasons) {
        this.operationResult = result;
        this.successCount = success;
        this.failCount = fail;
        this.failReasons = reasons;
        this.completedAt = LocalDateTime.now();
    }
    
    public boolean isCompleted() {
        return this.completedAt != null;
    }
    
    @Override
    public String toString() {
        return "MessageMultiSelectOperation{" +
                "id=" + id +
                ", userId=" + userId +
                ", operationType=" + operationType +
                ", selectedCount=" + getTotalCount() +
                ", result=" + operationResult +
                '}';
    }
}
