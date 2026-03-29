package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 路线响应
 */
@Data
public class RouteResponse {
    
    private Long routeId;
    
    private String routeNo;
    
    private Long riderId;
    
    private Integer taskCount;
    
    private BigDecimal totalDistance;
    
    private Integer estimatedDuration;
    
    private Integer optimizeStrategy;
    
    private String routePolyline;
    
    private List<RouteNodeDTO> nodes;
    
    private Integer status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime createTime;
}
