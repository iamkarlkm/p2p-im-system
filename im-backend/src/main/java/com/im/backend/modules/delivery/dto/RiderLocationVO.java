package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 骑手位置VO
 */
@Data
public class RiderLocationVO {

    private Long riderId;
    private String riderName;
    private BigDecimal lat;
    private BigDecimal lng;
    private BigDecimal accuracy;
    private BigDecimal speed;
    private BigDecimal bearing;
    private Integer batteryLevel;
    private String source;
    private String updatedAt;
}
