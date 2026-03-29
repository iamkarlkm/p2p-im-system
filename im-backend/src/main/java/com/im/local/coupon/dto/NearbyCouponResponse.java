package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 附近优惠券响应DTO
 */
@Data
public class NearbyCouponResponse {

    /** 优惠券ID */
    private Long couponId;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 商户Logo */
    private String merchantLogo;

    /** 商户分类 */
    private String merchantCategory;

    /** 优惠券名称 */
    private String couponName;

    /** 优惠券类型 */
    private Integer couponType;

    /** 优惠券面值 */
    private BigDecimal couponValue;

    /** 使用门槛 */
    private BigDecimal minSpend;

    /** 有效期开始 */
    private LocalDateTime validStartTime;

    /** 有效期结束 */
    private LocalDateTime validEndTime;

    /** 距离(米) */
    private Double distance;

    /** 是否已领取 */
    private Boolean received;

    /** 剩余数量 */
    private Integer remainingQuantity;
}
