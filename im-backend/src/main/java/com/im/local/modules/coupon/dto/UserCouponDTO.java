package com.im.local.modules.coupon.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券DTO
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class UserCouponDTO {

    private Long id;
    private Long userId;
    private Long couponId;
    private Long templateId;
    private String couponName;
    private Integer couponType;
    private String couponTypeName;
    private BigDecimal couponValue;
    private BigDecimal minSpend;
    private BigDecimal maxDiscount;
    private LocalDateTime validStartTime;
    private LocalDateTime validEndTime;
    private Integer status;
    private String statusName;
    private LocalDateTime useTime;
    private Long orderId;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private LocalDateTime receiveTime;
    private Integer receiveChannel;
    private String receiveChannelName;

    /**
     * 商户信息
     */
    private Long merchantId;
    private String merchantName;
    private String merchantLogo;

    /**
     * 是否即将过期（3天内）
     */
    private Boolean expiringSoon;

    /**
     * 剩余天数
     */
    private Long remainingDays;

    /**
     * 格式化显示（如：满100减20）
     */
    private String displayText;
}
