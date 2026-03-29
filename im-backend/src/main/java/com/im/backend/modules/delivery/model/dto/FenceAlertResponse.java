package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏告警响应
 */
@Data
public class FenceAlertResponse {
    
    private Long id;
    
    private String alertNo;
    
    private Long riderId;
    
    private String riderName;
    
    private Long taskId;
    
    private String taskNo;
    
    private Integer alertType;
    
    private String alertTypeDesc;
    
    private Integer severity;
    
    private BigDecimal triggerLat;
    
    private BigDecimal triggerLng;
    
    private String alertMessage;
    
    private BigDecimal deviationDistance;
    
    private Integer status;
    
    private String handleResult;
    
    private Long handlerId;
    
    private LocalDateTime handleTime;
    
    private LocalDateTime alertTime;
    
    private LocalDateTime createTime;
}
