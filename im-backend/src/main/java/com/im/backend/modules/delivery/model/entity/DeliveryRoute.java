package com.im.backend.modules.delivery.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送路线实体
 */
@Data
@TableName("delivery_route")
public class DeliveryRoute {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String routeNo;
    
    private Long riderId;
    
    private Integer taskCount;
    
    private BigDecimal totalDistance;
    
    private Integer estimatedDuration;
    
    private Integer optimizeStrategy;
    
    private String routePolyline;
    
    private String routeNodes;
    
    private Integer status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private BigDecimal startLat;
    
    private BigDecimal startLng;
    
    private BigDecimal endLat;
    
    private BigDecimal endLng;
    
    private String trafficCondition;
    
    private Boolean isOptimized;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
