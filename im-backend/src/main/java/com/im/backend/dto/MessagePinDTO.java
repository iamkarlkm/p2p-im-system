package com.im.backend.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 消息置顶请求DTO
 */
public class MessagePinDTO {
    
    @NotNull(message = "会话ID不能为空")
    private Long conversationId;
    
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
    
    @Size(max = 200, message = "置顶备注不能超过200字符")
    private String pinNote;
    
    private Integer pinOrder;
    
    private LocalDateTime expiresAt;
    
    private Boolean sendNotification = true;
    
    // 构造函数
    public MessagePinDTO() {}
    
    public MessagePinDTO(Long conversationId, Long messageId) {
        this.conversationId = conversationId;
        this.messageId = messageId;
    }
    
    // Getters and Setters
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public String getPinNote() {
        return pinNote;
    }
    
    public void setPinNote(String pinNote) {
        this.pinNote = pinNote;
    }
    
    public Integer getPinOrder() {
        return pinOrder;
    }
    
    public void setPinOrder(Integer pinOrder) {
        this.pinOrder = pinOrder;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Boolean getSendNotification() {
        return sendNotification;
    }
    
    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
    
    /**
     * 置顶排序更新DTO
     */
    public static class PinOrderUpdateDTO {
        @NotNull(message = "置顶ID不能为空")
        private Long pinId;
        
        @NotNull(message = "排序顺序不能为空")
        private Integer newOrder;
        
        public Long getPinId() {
            return pinId;
        }
        
        public void setPinId(Long pinId) {
            this.pinId = pinId;
        }
        
        public Integer getNewOrder() {
            return newOrder;
        }
        
        public void setNewOrder(Integer newOrder) {
            this.newOrder = newOrder;
        }
    }
    
    /**
     * 批量置顶操作DTO
     */
    public static class BatchPinOperationDTO {
        @NotNull(message = "会话ID不能为空")
        private Long conversationId;
        
        @NotNull(message = "操作类型不能为空")
        private OperationType operation;
        
        public enum OperationType {
            UNPIN_ALL, REORDER_BY_TIME, REORDER_BY_IMPORTANCE
        }
        
        public Long getConversationId() {
            return conversationId;
        }
        
        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }
        
        public OperationType getOperation() {
            return operation;
        }
        
        public void setOperation(OperationType operation) {
            this.operation = operation;
        }
    }
    
    @Override
    public String toString() {
        return "MessagePinDTO{conversationId=" + conversationId + 
               ", messageId=" + messageId + "}";
    }
}
