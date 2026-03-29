package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 平台优惠券概览DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class PlatformCouponOverview implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalCouponCount;
    private Long activeCouponCount;
    private Long totalReceivedCount;
    private Long totalUsedCount;
    private BigDecimal totalOrderAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal todayReceiveCount;
    private BigDecimal todayUseCount;
}
