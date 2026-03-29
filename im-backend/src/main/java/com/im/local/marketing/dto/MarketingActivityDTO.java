package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 创建营销活动请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingActivityDTO {
    
    private String activityId;
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    @NotBlank(message = "活动名称不能为空")
    private String activityName;
    
    private String description;
    
    /**
     * 活动类型
     */
    @NotBlank(message = "活动类型不能为空")
    private String activityType;
    
    /**
     * 活动开始时间
     */
    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime endTime;
    
    /**
     * 活动规则
     */
    @NotNull(message = "活动规则不能为空")
    private ActivityRuleDTO rule;
    
    /**
     * 活动封面图
     */
    private String coverImage;
    
    /**
     * 活动详情图
     */
    private List<String> detailImages;
    
    /**
     * 适用商品
     */
    private List<String> applicableProducts;
    
    /**
     * 适用分类
     */
    private List<String> applicableCategories;
    
    /**
     * 关联优惠券模板ID
     */
    private String couponTemplateId;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
    
    /**
     * 活动规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityRuleDTO {
        /**
         * 满减规则
         */
        private List<FullReductionRuleDTO> fullReductionRules;
        
        /**
         * 折扣规则
         */
        private DiscountRuleDTO discountRule;
        
        /**
         * 秒杀规则
         */
        private FlashSaleRuleDTO flashSaleRule;
        
        /**
         * 拼团规则
         */
        private GroupBuyRuleDTO groupBuyRule;
        
        /**
         * 砍价规则
         */
        private BargainRuleDTO bargainRule;
        
        /**
         * 参与次数限制
         */
        @Min(value = 1, message = "参与次数至少为1")
        private Integer participationLimit;
        
        /**
         * 每日参与次数限制
         */
        private Integer dailyLimit;
        
        /**
         * 用户等级限制
         */
        private Integer minMemberLevel;
        
        /**
         * 新用户专享
         */
        private Boolean newUserOnly;
    }
    
    /**
     * 满减规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullReductionRuleDTO {
        @NotNull(message = "满减门槛不能为空")
        private BigDecimal threshold;
        
        @NotNull(message = "减免金额不能为空")
        private BigDecimal reduction;
    }
    
    /**
     * 折扣规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountRuleDTO {
        /**
         * 折扣率(0-1)
         */
        @NotNull(message = "折扣率不能为空")
        private BigDecimal discountRate;
        
        /**
         * 最高优惠金额
         */
        private BigDecimal maxDiscount;
        
        /**
         * 适用商品
         */
        private List<String> applicableProducts;
        
        /**
         * 排除商品
         */
        private List<String> excludedProducts;
    }
    
    /**
     * 秒杀规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleRuleDTO {
        /**
         * 商品库存
         */
        @NotNull(message = "库存不能为空")
        @Min(value = 1, message = "库存至少为1")
        private Integer stock;
        
        /**
         * 每人限购
         */
        @NotNull(message = "每人限购不能为空")
        @Min(value = 1, message = "每人限购至少为1")
        private Integer limitPerUser;
        
        /**
         * 原价
         */
        @NotNull(message = "原价不能为空")
        private BigDecimal originalPrice;
        
        /**
         * 秒杀价
         */
        @NotNull(message = "秒杀价不能为空")
        private BigDecimal flashPrice;
    }
    
    /**
     * 拼团规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupBuyRuleDTO {
        /**
         * 成团人数
         */
        @NotNull(message = "成团人数不能为空")
        @Min(value = 2, message = "成团人数至少为2")
        private Integer groupSize;
        
        /**
         * 成团有效期（小时）
         */
        @NotNull(message = "成团有效期不能为空")
        @Min(value = 1, message = "成团有效期至少为1小时")
        private Integer validHours;
        
        /**
         * 团长优惠
         */
        private BigDecimal leaderDiscount;
        
        /**
         * 拼团价格
         */
        @NotNull(message = "拼团价格不能为空")
        private BigDecimal groupPrice;
        
        /**
         * 原价
         */
        @NotNull(message = "原价不能为空")
        private BigDecimal originalPrice;
        
        /**
         * 模拟成团
         */
        private Boolean autoGroup;
        
        /**
         * 每人开团次数限制
         */
        private Integer maxGroupsPerUser;
    }
    
    /**
     * 砍价规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BargainRuleDTO {
        /**
         * 商品原价
         */
        @NotNull(message = "商品原价不能为空")
        private BigDecimal originalPrice;
        
        /**
         * 底价
         */
        @NotNull(message = "底价不能为空")
        private BigDecimal floorPrice;
        
        /**
         * 砍价次数上限
         */
        @NotNull(message = "砍价次数上限不能为空")
        @Min(value = 1, message = "砍价次数至少为1")
        private Integer maxBargainTimes;
        
        /**
         * 每次砍价最小金额
         */
        @NotNull(message = "砍价最小金额不能为空")
        private BigDecimal minBargainAmount;
        
        /**
         * 每次砍价最大金额
         */
        @NotNull(message = "砍价最大金额不能为空")
        private BigDecimal maxBargainAmount;
        
        /**
         * 砍价有效期（小时）
         */
        @NotNull(message = "砍价有效期不能为空")
        private Integer validHours;
        
        /**
         * 新用户助力加成
         */
        private BigDecimal newUserBonus;
    }
}
