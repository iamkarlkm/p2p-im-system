package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单配送进度VO
 */
@Data
public class OrderProgressVO {

    private Long orderId;
    private String status;
    private String statusText;
    private Integer progressPercent;

    /** 骑手位置 */
    private BigDecimal riderLat;
    private BigDecimal riderLng;
    private LocalDateTime riderLocationUpdatedAt;

    /** 距离商家距离(米) */
    private Integer distanceToMerchant;

    /** 距离用户距离(米) */
    private Integer distanceToUser;

    /** 预计到达商家时间 */
    private LocalDateTime etaToMerchant;

    /** 预计送达时间 */
    private LocalDateTime estimatedDeliveryTime;

    /** 剩余配送时间(分钟) */
    private Integer remainingMinutes;

    /** 配送轨迹 */
    private java.util.List<LocationPointVO> trajectory;
}
