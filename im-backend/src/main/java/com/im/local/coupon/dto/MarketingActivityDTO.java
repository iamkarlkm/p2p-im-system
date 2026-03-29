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
 * 营销活动DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingActivityDTO {
    
    private String id;
    private String name;
    private String description;
    private String activityType;
    private String activityTypeName;
    private String merchantId;
    private String merchantName;
    private String coverImage;
    private List<String> bannerImages;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime preheatTime;
    private String status;
    private String statusName;
    private Integer totalLimit;
    private Integer participatedCount;
    private Integer remainingCount;
    private BigDecimal totalSalesAmount;
    private Integer totalOrderCount;
    private List<ActivityProductDTO> products;
    private ActivityRuleDTO rules;
    private ShareConfigDTO shareConfig;
    private List<String> tags;
    private Integer sortOrder;
    private Boolean recommended;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean hasParticipated;
    private Integer userParticipateCount;
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityProductDTO {
        private String productId;
        private String productName;
        private String productImage;
        private BigDecimal originalPrice;
        private BigDecimal activityPrice;
        private Integer stockQuantity;
        private Integer soldQuantity;
        private Integer limitPerUser;
        private BigDecimal discountRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityRuleDTO {
        private List<FullReductionRuleDTO> fullReductionRules;
        private BigDecimal discountRate;
        private Integer groupSize;
        private Integer groupExpireHours;
        private BigDecimal bargainMinPrice;
        private List<BigDecimal> bargainRange;
        private Integer flashSaleQuantity;
        private List<String> flashSaleTimes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullReductionRuleDTO {
        private BigDecimal threshold;
        private BigDecimal reduction;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareConfigDTO {
        private String title;
        private String description;
        private String imageUrl;
        private Integer rewardPoints;
    }
}
