package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券详情响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class UserCouponDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long couponId;
    private String couponName;
    private String description;
    private String merchantName;
    private Integer status;
    private LocalDateTime validEndTime;
    private Long remainingDays;
    private String coverImage;
    private String restrictionSnapshot;
    private String useRules;
}
