// 经营洞察响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusinessInsightResponseDTO {
    private Long id;
    private Long merchantId;
    private String insightType;
    private String insightLevel;
    private String insightTitle;
    private String insightDescription;
    private String recommendedAction;
    private Double confidenceScore;
    private BigDecimal expectedImpact;
    
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
