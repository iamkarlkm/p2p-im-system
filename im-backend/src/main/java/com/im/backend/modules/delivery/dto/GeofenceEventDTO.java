package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 地理围栏事件DTO
 */
@Data
public class GeofenceEventDTO {

    private Long riderId;
    private Long orderId;
    private String eventType; // ENTER_MERCHANT-进入商家, LEAVE_MERCHANT-离开商家, ENTER_USER-进入用户区域, LEAVE_USER-离开用户区域
    private BigDecimal lat;
    private BigDecimal lng;
    private String fenceId;
    private String triggeredAt;
}
