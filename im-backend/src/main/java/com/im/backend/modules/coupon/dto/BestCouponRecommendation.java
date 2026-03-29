package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 最优优惠券推荐DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class BestCouponRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userCouponId;
    private String couponName;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private List<UserCouponResponse> availableCoupons;
}
