package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 围栏检测结果
 */
@Data
public class FenceCheckResult {
    
    private Boolean isNormal;
    
    private Boolean isDeviated;
    
    private BigDecimal deviationDistance;
    
    private Integer alertType;
    
    private String alertMessage;
    
    private BigDecimal nearestPointLat;
    
    private BigDecimal nearestPointLng;
    
    private BigDecimal nearestDistance;
}
