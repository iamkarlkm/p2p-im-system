package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券统计响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class CouponStatisticsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long couponId;
    private String name;
    private Integer totalStock;
    private Integer receivedCount;
    private Integer usedCount;
    private Integer unusedCount;
    private Integer viewCount;
    private BigDecimal receiveRate;
    private BigDecimal usageRate;
    private BigDecimal totalOrderAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal roi;
}
