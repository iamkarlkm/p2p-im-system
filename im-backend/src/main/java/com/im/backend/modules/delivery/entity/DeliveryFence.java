package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送站点/围栏实体
 */
@Data
@TableName("delivery_fence")
public class DeliveryFence {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fenceName;
    private Integer fenceType;
    private Long merchantId;
    
    private Integer shapeType;
    private String geoJson;
    private BigDecimal centerLat;
    private BigDecimal centerLng;
    private BigDecimal radius;
    
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    
    private Integer status;
    private Integer maxRiders;
    private Integer currentOrders;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
