package com.im.local.delivery.dto;

import com.im.local.delivery.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送订单DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderDTO {
    
    private Long id;
    private String orderNo;
    private DeliveryStatus status;
    private String statusText;
    
    private Long merchantId;
    private Long userId;
    private Long riderId;
    
    private String pickupAddress;
    private String deliveryAddress;
    
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    
    private LocalDateTime createTime;
    private LocalDateTime expectedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
}
