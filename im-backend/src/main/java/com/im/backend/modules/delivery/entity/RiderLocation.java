package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置记录实体
 */
@Data
@TableName("rider_location")
public class RiderLocation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long riderId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String location;
    
    private BigDecimal accuracy;
    private BigDecimal altitude;
    private BigDecimal speed;
    private BigDecimal bearing;
    
    private Integer batteryLevel;
    private String deviceId;
    private String appVersion;
    
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
