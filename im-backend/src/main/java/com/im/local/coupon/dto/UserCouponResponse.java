package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券响应DTO
 */
@Data
public class UserCouponResponse {

    /** 用户优惠券ID */
    private Long id;

    /** 优惠券ID */
    private Long couponId;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 商户Logo */
    private String merchantLogo;

    /** 优惠券名称 */
    private String couponName;

    /** 优惠券类型 */
    private Integer couponType;

    /** 优惠券类型名称 */
    private String couponTypeName;

    /** 优惠券码 */
    private String couponCode;

    /** 优惠面值 */
    private BigDecimal couponValue;

    /** 使用门槛 */
    private BigDecimal minSpend;

    /** 最大优惠金额 */
    private BigDecimal maxDiscount;

    /** 有效期开始时间 */
    private LocalDateTime validStartTime;

    /** 有效期结束时间 */
    private LocalDateTime validEndTime;

    /** 状态: 0-未使用 1-已使用 2-已过期 */
    private Integer status;

    /** 状态标签 */
    private String statusTag;

    /** 领取时间 */
    private LocalDateTime receiveTime;

    /** 使用时间 */
    private LocalDateTime useTime;

    /** 是否即将过期(3天内) */
    private Boolean expiringSoon;
}
