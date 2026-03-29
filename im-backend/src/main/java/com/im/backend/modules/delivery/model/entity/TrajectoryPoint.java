package com.im.backend.modules.delivery.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 轨迹点实体
 */
@Data
@TableName("trajectory_point")
public class TrajectoryPoint {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long riderId;
    
    private Long taskId;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private BigDecimal accuracy;
    
    private BigDecimal altitude;
    
    private BigDecimal speed;
    
    private BigDecimal direction;
    
    private String geohash;
    
    private Integer locationType;
    
    private String deviceInfo;
    
    private Integer batteryLevel;
    
    private String networkType;
    
    private LocalDateTime locationTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
