package com.im.local.marketing.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 营销活动实体
 * 支持满减、折扣、秒杀、拼团、砍价等多种活动类型
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "marketing_activities")
public class MarketingActivity {
    
    @Id
    private String activityId;
    
    private String merchantId;
    
    private String merchantName;
    
    private String activityName;
    
    private String description;
    
    /**
     * 活动类型
     * FULL_REDUCTION: 满减活动
     * DISCOUNT: 折扣活动
     * FLASH_SALE: 秒杀活动
     * GROUP_BUY: 拼团活动
     * BARGAIN: 砍价活动
     * GIVEAWAY: 买赠活动
     * COUPON: 优惠券发放
     * LUCKY_DRAW: 抽奖活动
     */
    private String activityType;
    
    /**
     * 活动状态
     * DRAFT: 草稿
     * PENDING: 待开始
     * ACTIVE: 进行中
     * PAUSED: 已暂停
     * ENDED: 已结束
     * CANCELLED: 已取消
     */
    private String status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    /**
     * 活动规则配置
     */
    private ActivityRule rule;
    
    /**
     * 活动规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityRule {
        /**
         * 满减规则
         */
        private List<FullReductionRule> fullReductionRules;
        
        /**
         * 折扣规则
         */
        private DiscountRule discountRule;
        
        /**
         * 秒杀规则
         */
        private FlashSaleRule flashSaleRule;
        
        /**
         * 拼团规则
         */
        private GroupBuyRule groupBuyRule;
        
        /**
         * 砍价规则
         */
        private BargainRule bargainRule;
        
        /**
         * 参与次数限制
         */
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
     * 满减规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullReductionRule {
        private BigDecimal threshold;
        private BigDecimal reduction;
    }
    
    /**
     * 折扣规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountRule {
        /**
         * 折扣率(0-1)
         */
        private BigDecimal discountRate;
        
        /**
         * 最高优惠金额
         */
        private BigDecimal maxDiscount;
        
        /**
         * 适用商品（空表示全店）
         */
        private List<String> applicableProducts;
        
        /**
         * 排除商品
         */
        private List<String> excludedProducts;
    }
    
    /**
     * 秒杀规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleRule {
        /**
         * 秒杀场次时间
         */
        private List<FlashSaleSession> sessions;
        
        /**
         * 商品库存
         */
        private Integer stock;
        
        /**
         * 每人限购
         */
        private Integer limitPerUser;
        
        /**
         * 原价
         */
        private BigDecimal originalPrice;
        
        /**
         * 秒杀价
         */
        private BigDecimal flashPrice;
    }
    
    /**
     * 秒杀场次
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleSession {
        private LocalDateTime sessionTime;
        private Integer sessionStock;
    }
    
    /**
     * 拼团规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupBuyRule {
        /**
         * 成团人数
         */
        private Integer groupSize;
        
        /**
         * 成团有效期（小时）
         */
        private Integer validHours;
        
        /**
         * 团长优惠
         */
        private BigDecimal leaderDiscount;
        
        /**
         * 拼团价格
         */
        private BigDecimal groupPrice;
        
        /**
         * 原价
         */
        private BigDecimal originalPrice;
        
        /**
         * 模拟成团（人数不够自动成团）
         */
        private Boolean autoGroup;
        
        /**
         * 每人开团次数限制
         */
        private Integer maxGroupsPerUser;
    }
    
    /**
     * 砍价规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BargainRule {
        /**
         * 商品原价
         */
        private BigDecimal originalPrice;
        
        /**
         * 底价
         */
        private BigDecimal floorPrice;
        
        /**
         * 砍价次数上限
         */
        private Integer maxBargainTimes;
        
        /**
         * 每次砍价随机范围
         */
        private BargainRange bargainRange;
        
        /**
         * 砍价有效期（小时）
         */
        private Integer validHours;
        
        /**
         * 新用户助力加成
         */
        private BigDecimal newUserBonus;
    }
    
    /**
     * 砍价范围
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BargainRange {
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
    }
    
    /**
     * 活动统计数据
     */
    private ActivityStatistics statistics;
    
    /**
     * 活动统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityStatistics {
        /**
         * 浏览次数
         */
        private Integer viewCount;
        
        /**
         * 参与人数
         */
        private Integer participantCount;
        
        /**
         * 成交订单数
         */
        private Integer orderCount;
        
        /**
         * 成交金额
         */
        private BigDecimal orderAmount;
        
        /**
         * 优惠金额
         */
        private BigDecimal discountAmount;
        
        /**
         * 新增用户数
         */
        private Integer newUserCount;
        
        /**
         * 分享次数
         */
        private Integer shareCount;
    }
    
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
     * 关联优惠券ID
     */
    private String couponTemplateId;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String createBy;
    
    private String updateBy;
    
    /**
     * 检查活动是否有效
     */
    public boolean isActive() {
        if (!"ACTIVE".equals(status)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        return true;
    }
    
    /**
     * 增加浏览数
     */
    public void incrementViewCount() {
        if (statistics == null) {
            statistics = new ActivityStatistics();
        }
        statistics.setViewCount(statistics.getViewCount() + 1);
    }
    
    /**
     * 增加参与数
     */
    public void incrementParticipantCount() {
        if (statistics == null) {
            statistics = new ActivityStatistics();
        }
        statistics.setParticipantCount(statistics.getParticipantCount() + 1);
    }
}
