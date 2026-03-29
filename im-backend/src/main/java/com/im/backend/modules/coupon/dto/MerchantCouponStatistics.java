package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户优惠券统计DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class MerchantCouponStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long merchantId;
    private Integer totalCouponCount;
    private Integer activeCouponCount;
    private Long totalReceivedCount;
    private Long totalUsedCount;
    private BigDecimal totalOrderAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal avgUsageRate;
}
