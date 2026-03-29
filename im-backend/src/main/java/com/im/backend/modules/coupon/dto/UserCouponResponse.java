package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户优惠券响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class UserCouponResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long couponId;
    private String couponCode;
    private String couponName;
    private Integer status;
    private String statusDescription;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Long remainingDays;
    private LocalDateTime useTime;
    private String orderNo;
    private BigDecimal discountAmount;
    private Integer receiveSource;
    private String sourceDescription;
    private BigDecimal minSpend;
    private BigDecimal discountValue;
    private String coverImage;
    private String merchantName;
}
