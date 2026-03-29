package com.im.backend.modules.delivery.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送骑手实体
 */
@Data
@TableName("delivery_rider")
public class DeliveryRider {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String riderNo;
    
    private String realName;
    
    private String phone;
    
    private String idCard;
    
    private Integer status;
    
    private Integer workStatus;
    
    private BigDecimal currentLat;
    
    private BigDecimal currentLng;
    
    private String currentGeohash;
    
    private LocalDateTime locationUpdateTime;
    
    private Integer rating;
    
    private Integer totalOrders;
    
    private Integer completedOrders;
    
    private BigDecimal ratingScore;
    
    private String vehicleType;
    
    private String vehicleNo;
    
    private String workCity;
    
    private String workDistrict;
    
    private Integer todayOrderCount;
    
    private BigDecimal todayIncome;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
