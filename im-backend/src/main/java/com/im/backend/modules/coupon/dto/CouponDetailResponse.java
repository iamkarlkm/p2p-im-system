package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券详情响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class CouponDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String couponCode;
    private String name;
    private String description;
    private Integer couponType;
    private String typeDescription;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private BigDecimal maxDiscount;
    private Long merchantId;
    private String merchantName;
    private Integer remainingStock;
    private Integer perUserLimit;
    private LocalDateTime issueStartTime;
    private LocalDateTime issueEndTime;
    private LocalDateTime useStartTime;
    private LocalDateTime useEndTime;
    private Integer status;
    private String statusDescription;
    private String coverImage;
    private List<String> tags;
    private Boolean geoLimited;
    private BigDecimal centerLongitude;
    private BigDecimal centerLatitude;
    private Integer effectiveRadius;
    private Integer receivedCount;
    private Integer usedCount;
    private Integer viewCount;
    private Boolean canReceive;
    private String receiveReason;
    private Integer userReceivedCount;
    private String shareTitle;
    private String shareDescription;
    private String shareImage;
}
