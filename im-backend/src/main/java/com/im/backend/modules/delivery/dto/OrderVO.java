package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息VO
 */
@Data
public class OrderVO {

    private Long id;
    private String orderNo;
    private Long bizOrderId;
    private String bizType;
    private String status;
    private Long merchantId;
    private String merchantName;
    private BigDecimal merchantLat;
    private BigDecimal merchantLng;
    private String merchantAddress;
    private String merchantPhone;
    private Long userId;
    private String userName;
    private String userPhone;
    private BigDecimal deliveryLat;
    private BigDecimal deliveryLng;
    private String deliveryAddress;
    private String houseNumber;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private Integer deliveryDistance;
    private BigDecimal deliveryFee;
    private BigDecimal riderTip;
    private BigDecimal orderAmount;
    private Integer itemCount;
    private Integer weight;
    private String remark;
    private Long riderId;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime createdAt;

    /** 骑手信息 */
    private RiderVO rider;

    /** 配送进度 */
    private OrderProgressVO progress;
}
