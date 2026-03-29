package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单实体
 */
@Data
@TableName("delivery_order")
public class DeliveryOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    private Long merchantId;
    private Long userId;
    private Long riderId;
    
    private Integer status;
    private Integer type;
    
    private String pickupAddress;
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    private String pickupContactName;
    private String pickupContactPhone;
    
    private String deliveryAddress;
    private BigDecimal deliveryLat;
    private BigDecimal deliveryLng;
    private String deliveryContactName;
    private String deliveryContactPhone;
    
    private BigDecimal distance;
    private Integer estimatedTime;
    private LocalDateTime estimatedDeliveryTime;
    
    private BigDecimal deliveryFee;
    private BigDecimal tipAmount;
    private BigDecimal totalAmount;
    
    private String remark;
    private String cancelReason;
    private Integer cancelType;
    
    private LocalDateTime assignedTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completionTime;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
