package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送任务响应
 */
@Data
public class TaskResponse {
    
    private Long id;
    
    private String taskNo;
    
    private Long orderId;
    
    private Long riderId;
    
    private String riderName;
    
    private Long merchantId;
    
    private String merchantName;
    
    private String merchantAddress;
    
    private BigDecimal merchantLat;
    
    private BigDecimal merchantLng;
    
    private String userAddress;
    
    private BigDecimal userLat;
    
    private BigDecimal userLng;
    
    private String userPhone;
    
    private String userName;
    
    private Integer status;
    
    private String statusDesc;
    
    private Integer priority;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal tipAmount;
    
    private Integer estimatedTime;
    
    private LocalDateTime pickupTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime completedTime;
    
    private String remark;
    
    private Integer alertCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
