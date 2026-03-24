package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchOperationRequest {
    
    @NotEmpty(message = "Conversation IDs cannot be empty")
    private List<Long> conversationIds;
    
    @NotNull(message = "Operation type is required")
    private String operationType;
    
    private Boolean notifyParticipants;
    
    private String reason;
    
    public static final String OP_MARK_READ = "mark_read";
    public static final String OP_ARCHIVE = "archive";
    public static final String OP_DELETE = "delete";
    public static final String OP_PIN = "pin";
    public static final String OP_UNPIN = "unpin";
    public static final String OP_MUTE = "mute";
    public static final String OP_UNMUTE = "unmute";
}
