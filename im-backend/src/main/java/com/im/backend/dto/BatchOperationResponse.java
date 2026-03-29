package com.im.backend.dto;

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
public class BatchOperationResponse {
    
    private Long operationId;
    
    private String operationType;
    
    private Integer totalCount;
    
    private Integer successCount;
    
    private Integer failureCount;
    
    private String status;
    
    private List<Long> failedConversationIds;
    
    private String message;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
}
