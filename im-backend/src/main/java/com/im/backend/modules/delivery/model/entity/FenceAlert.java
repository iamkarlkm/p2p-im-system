package com.im.backend.modules.delivery.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏告警实体
 */
@Data
@TableName("fence_alert")
public class FenceAlert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String alertNo;
    
    private Long riderId;
    
    private Long taskId;
    
    private Integer alertType;
    
    private Integer severity;
    
    private BigDecimal triggerLat;
    
    private BigDecimal triggerLng;
    
    private String geohash;
    
    private String alertMessage;
    
    private String plannedRoute;
    
    private BigDecimal deviationDistance;
    
    private Integer status;
    
    private String handleResult;
    
    private Long handlerId;
    
    private LocalDateTime handleTime;
    
    private LocalDateTime alertTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
