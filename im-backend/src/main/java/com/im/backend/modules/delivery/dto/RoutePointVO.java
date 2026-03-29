package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 路径点VO
 */
@Data
public class RoutePointVO {

    private Integer sequence;
    private BigDecimal lat;
    private BigDecimal lng;
    private String type; // PICKUP-取货点, DELIVERY-配送点
    private Long orderId;
    private String address;
    private Integer etaMinutes;
    private Integer distanceMeters;
}
