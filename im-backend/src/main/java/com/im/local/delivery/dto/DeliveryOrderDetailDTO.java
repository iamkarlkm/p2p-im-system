package com.im.local.delivery.dto;

import com.im.local.delivery.enums.DeliveryStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 配送订单详情DTO
 */
@Data
public class DeliveryOrderDetailDTO {
    
    private Long id;
    private String orderNo;
    private DeliveryStatus status;
    private String statusText;
    
    // 取货信息
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupContactName;
    private String pickupContactPhone;
    
    // 送货信息
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String deliveryContactName;
    private String deliveryContactPhone;
    
    // 订单信息
    private List<DeliveryItemDTO> items;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private String remark;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime acceptTime;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime expectedDeliveryTime;
    private Integer actualDeliveryMinutes;
    
    // 骑手信息
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private String riderAvatar;
    private Double riderRating;
    
    // 取消信息
    private String cancelReason;
    private LocalDateTime cancelTime;
}
