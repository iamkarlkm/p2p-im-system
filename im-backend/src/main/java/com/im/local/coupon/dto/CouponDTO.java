package com.im.local.coupon.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
    
    private String id;
    private String templateId;
    private String name;
    private String description;
    private String couponType;
    private String couponTypeName;
    private String merchantId;
    private String merchantName;
    private List<String> applicableStoreIds;
    private List<String> applicableCategories;
    private BigDecimal faceValue;
    private BigDecimal minSpend;
    private BigDecimal discountRate;
    private BigDecimal maxDiscount;
    private Integer totalQuantity;
    private Integer claimedQuantity;
    private Integer usedQuantity;
    private Integer remainingQuantity;
    private Integer limitPerUser;
    private LocalDateTime validityStart;
    private LocalDateTime validityEnd;
    private Integer validDaysAfterClaim;
    private Double distance;
    private String coverImage;
    private List<String> detailImages;
    private List<String> usageRules;
    private String status;
    private String statusName;
    private Integer priority;
    private Boolean newUserOnly;
    private Boolean memberOnly;
    private Integer requiredPoints;
    private List<String> tags;
    private Map<String, Object> extraData;
    private LocalDateTime createdAt;
    private Boolean hasClaimed;
    private Integer userClaimCount;
}
