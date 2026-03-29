package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券核销请求DTO
 */
@Data
public class UseCouponRequest {

    /** 用户优惠券ID */
    private Long userCouponId;

    /** 订单ID */
    private Long orderId;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 商品ID列表(用于校验适用范围) */
    private List<Long> productIds;

    /** 商品分类ID列表 */
    private List<Long> categoryIds;
}
