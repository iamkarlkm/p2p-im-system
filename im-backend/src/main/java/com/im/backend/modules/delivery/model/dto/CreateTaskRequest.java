package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建配送任务请求
 */
@Data
public class CreateTaskRequest {
    
    @NotNull
    private Long orderId;
    
    @NotNull
    private Long merchantId;
    
    @NotNull
    private Long userId;
    
    private String merchantName;
    
    private String merchantAddress;
    
    private BigDecimal merchantLat;
    
    private BigDecimal merchantLng;
    
    private String userAddress;
    
    private BigDecimal userLat;
    
    private BigDecimal userLng;
    
    private String userPhone;
    
    private String userName;
    
    private Integer priority;
    
    private BigDecimal deliveryFee;
    
    private String remark;
}
