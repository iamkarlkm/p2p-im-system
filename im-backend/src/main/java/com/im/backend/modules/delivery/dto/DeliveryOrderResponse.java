package com.im.backend.modules.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送订单响应
 */
@Data
public class DeliveryOrderResponse {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private Long userId;
    private Long riderId;
    private String riderName;
    private String riderPhone;
    
    private Integer status;
    private String statusDesc;
    private Integer type;
    
    private String pickupAddress;
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    
    private String deliveryAddress;
    private BigDecimal deliveryLat;
    private BigDecimal deliveryLng;
    
    private BigDecimal distance;
    private Integer estimatedTime;
    private LocalDateTime estimatedDeliveryTime;
    
    private BigDecimal deliveryFee;
    private BigDecimal tipAmount;
    private BigDecimal totalAmount;
    
    private String remark;
    private LocalDateTime assignedTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime createTime;
}
