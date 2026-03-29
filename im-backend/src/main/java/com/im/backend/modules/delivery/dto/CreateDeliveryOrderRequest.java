package com.im.backend.modules.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建配送订单请求
 */
@Data
public class CreateDeliveryOrderRequest {
    private Long merchantId;
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
    
    private BigDecimal deliveryFee;
    private BigDecimal tipAmount;
    private String remark;
}
