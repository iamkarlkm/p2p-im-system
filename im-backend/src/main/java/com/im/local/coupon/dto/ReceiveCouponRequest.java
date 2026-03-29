package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 领取优惠券请求DTO
 */
@Data
public class ReceiveCouponRequest {

    /** 优惠券ID */
    private Long couponId;

    /** 用户经度(用于LBS校验) */
    private Double longitude;

    /** 用户纬度(用于LBS校验) */
    private Double latitude;

    /** 领取渠道: 1-主动领取 2-系统发放 3-活动赠送 */
    private Integer channel;

    /** 领取场景 */
    private Integer scene;
}
