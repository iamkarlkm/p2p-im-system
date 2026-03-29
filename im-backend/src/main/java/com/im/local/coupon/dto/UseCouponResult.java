package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券核销结果DTO
 */
@Data
public class UseCouponResult {

    /** 是否可用 */
    private Boolean usable;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额 */
    private BigDecimal finalAmount;

    /** 不可用原因 */
    private String reason;

    /** 优惠券信息 */
    private UserCouponResponse couponInfo;
}
