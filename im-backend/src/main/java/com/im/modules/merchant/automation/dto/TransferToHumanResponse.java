package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能转人工响应DTO
 */
@Data
@Builder
public class TransferToHumanResponse {
    
    private String transferId;
    
    private String sessionId;
    
    private String merchantId;
    
    private String userId;
    
    private Integer status;
    
    private String statusName;
    
    private String assignedAgentId;
    
    private String assignedAgentName;
    
    private Integer queuePosition;
    
    private Integer estimatedWaitTime;
    
    private LocalDateTime transferTime;
    
    private LocalDateTime acceptTime;
    
    private String reason;
    
    private String priority;
    
    private List<String> conversationHistory;
}
