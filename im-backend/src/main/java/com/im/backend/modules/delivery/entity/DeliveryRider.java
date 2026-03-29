package com.im.backend.modules.delivery.entity;

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
    private String riderName;
    private String phone;
    private String idCard;
    private String avatar;
    
    private Integer status;
    private Integer authStatus;
    private BigDecimal rating;
    private Integer totalOrders;
    private Integer successOrders;
    
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private String currentLocation;
    private LocalDateTime locationUpdateTime;
    
    private Integer workStatus;
    private Long currentOrderId;
    private Integer todayOrderCount;
    private BigDecimal todayIncome;
    
    private LocalDateTime lastOnlineTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
