package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationBatchOperation {
    
    private Long id;
    
    private Long userId;
    
    private String operationType;
    
    private List<Long> conversationIds;
    
    private String status;
    
    private Integer successCount;
    
    private Integer failureCount;
    
    private String failureReason;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    public static final String TYPE_READ = "read";
    public static final String TYPE_ARCHIVE = "archive";
    public static final String TYPE_DELETE = "delete";
    public static final String TYPE_PIN = "pin";
    public static final String TYPE_UNPIN = "unpin";
    public static final String TYPE_MUTE = "mute";
    public static final String TYPE_UNMUTE = "unmute";
    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    
    public static final String TYPE_MARK_READ = "mark_read";
    public static final String TYPE_ARCHIVE_CONVERSATION = "archive_conversation";
    public static final String TYPE_DELETE_CONVERSATION = "delete_conversation";
    public static final String TYPE_PIN_CONVERSATION = "pin_conversation";
    public static final String TYPE_UNPIN_CONVERSATION = "unpin_conversation";
    public static final String TYPE_MUTE_CONVERSATION = "mute_conversation";
    public static final String TYPE_UNMUTE_CONVERSATION = "unmute_conversation";
}
