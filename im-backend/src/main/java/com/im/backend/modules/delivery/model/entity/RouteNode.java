package com.im.backend.modules.delivery.model.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 路线节点
 */
@Data
public class RouteNode {
    
    private Integer sequence;
    
    private Long taskId;
    
    private Integer nodeType;
    
    private String address;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private String geohash;
    
    private BigDecimal distanceFromStart;
    
    private Integer estimatedArrival;
    
    private Integer stayDuration;
    
    private String action;
    
    private String contactName;
    
    private String contactPhone;
    
    private String remark;
}
