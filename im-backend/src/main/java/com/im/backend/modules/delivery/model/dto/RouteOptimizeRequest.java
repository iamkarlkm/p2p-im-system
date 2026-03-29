package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 路径优化请求
 */
@Data
public class RouteOptimizeRequest {
    
    private Long riderId;
    
    private List<Long> taskIds;
    
    private Integer strategy;
    
    private Boolean considerTraffic;
    
    private Boolean allowReoptimize;
}
