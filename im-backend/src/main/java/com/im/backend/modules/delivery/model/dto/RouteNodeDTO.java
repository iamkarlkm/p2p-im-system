package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 路线节点DTO
 */
@Data
public class RouteNodeDTO {
    
    private Integer sequence;
    
    private Long taskId;
    
    private Integer nodeType;
    
    private String address;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private BigDecimal distanceFromStart;
    
    private Integer estimatedArrival;
    
    private String action;
    
    private String contactName;
    
    private String contactPhone;
}
