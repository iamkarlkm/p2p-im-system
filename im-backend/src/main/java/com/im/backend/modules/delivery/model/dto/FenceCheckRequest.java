package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 围栏检测请求
 */
@Data
public class FenceCheckRequest {
    
    private Long riderId;
    
    private Long taskId;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private String plannedRoute;
    
    private BigDecimal threshold;
}
