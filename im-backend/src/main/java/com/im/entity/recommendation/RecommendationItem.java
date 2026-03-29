package com.im.entity.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 推荐信息流项实体
 * 封装单个推荐内容的完整信息，包含POI、活动、优惠券等多种类型
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationItem {
    
    /**
     * 推荐项唯一ID
     */
    private String itemId;
    
    /**
     * 推荐项类型
     * POI: 兴趣点/商户
     * ACTIVITY: 活动
     * COUPON: 优惠券
     * GROUP: 拼团
     * EVENT: 事件
     * CONTENT: 内容/笔记
     */
    private String itemType;
    
    /**
     * 业务ID（关联的具体业务对象ID）
     */
    private String businessId;
    
    /**
     * 推荐项标题
     */
    private String title;
    
    /**
     * 推荐项副标题/描述
     */
    private String subtitle;
    
    /**
     * 主图URL
     */
    private String mainImage;
    
    /**
     * 图片列表
     */
    private List<String> imageList;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 地址信息
     */
    private String address;
    
    /**
     * 距离（米）
     */
    private Integer distance;
    
    /**
     * 距离显示文本
     */
    private String distanceText;
    
    /**
     * 分类ID
     */
    private String categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 评分（1-5分）
     */
    private Double rating;
    
    /**
     * 评分人数
     */
    private Integer ratingCount;
    
    /**
     * 价格信息
     */
    private PriceInfo priceInfo;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 推荐理由
     */
    private String recommendReason;
    
    /**
     * 推荐来源
     * GEO: 地理位置推荐
     * HOT: 热门推荐
     * CF: 协同过滤
     * VECTOR: 向量召回
     * PERSONAL: 个性化
     * SOCIAL: 社交推荐
     * SCENE: 场景化推荐
     */
    private String recallSource;
    
    /**
     * 召回分数
     */
    private Double recallScore;
    
    /**
     * 排序分数
     */
    private Double sortScore;
    
    /**
     * 点击率预估
     */
    private Double ctrPrediction;
    
    /**
     * 转化率预估
     */
    private Double cvrPrediction;
    
    /**
     * 热度值
     */
    private Integer heatScore;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 开始时间（活动/优惠券等）
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间（活动/优惠券等）
     */
    private LocalDateTime endTime;
    
    /**
     * 商户信息
     */
    private MerchantInfo merchantInfo;
    
    /**
     * 活动信息（如果类型为活动）
     */
    private ActivityInfo activityInfo;
    
    /**
     * 优惠信息（如果类型为优惠券）
     */
    private CouponInfo couponInfo;
    
    /**
     * 社交信息
     */
    private SocialInfo socialInfo;
    
    /**
     * 场景标签
     */
    private List<String> sceneTags;
    
    /**
     * 时效性标签
     * BREAKFAST: 早餐时段
     * LUNCH: 午餐时段
     * DINNER: 晚餐时段
     * NIGHT: 夜宵时段
     * WEEKEND: 周末
     * HOLIDAY: 节假日
     */
    private Set<String> timeTags;
    
    /**
     * 天气标签
     * SUNNY: 晴天
     * RAINY: 雨天
     * SNOWY: 雪天
     * CLOUDY: 阴天
     */
    private Set<String> weatherTags;
    
    /**
     * 用户行为统计
     */
    private UserBehaviorStats behaviorStats;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> extraProperties;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否置顶
     */
    private Boolean isPinned;
    
    /**
     * 是否推广
     */
    private Boolean isPromoted;
    
    /**
     * 推广权重
     */
    private Double promoteWeight;
    
    /**
     * 状态
     * ACTIVE: 有效
     * INACTIVE: 无效
     * EXPIRED: 过期
     */
    private String status;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 价格信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceInfo {
        /**
         * 原价
         */
        private Double originalPrice;
        
        /**
         * 现价/折扣价
         */
        private Double currentPrice;
        
        /**
         * 人均消费
         */
        private Double avgPrice;
        
        /**
         * 折扣信息
         */
        private String discountInfo;
        
        /**
         * 价格单位
         */
        private String priceUnit;
        
        /**
         * 价格描述
         */
        private String priceDescription;
        
        /**
         * 是否有优惠
         */
        private Boolean hasDiscount;
        
        /**
         * 优惠力度
         */
        private Integer discountPercent;
    }
    
    /**
     * 商户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantInfo {
        /**
         * 商户ID
         */
        private String merchantId;
        
        /**
         * 商户名称
         */
        private String merchantName;
        
        /**
         * 商户LOGO
         */
        private String merchantLogo;
        
        /**
         * 商户等级
         */
        private Integer merchantLevel;
        
        /**
         * 是否认证
         */
        private Boolean isVerified;
        
        /**
         * 认证类型
         */
        private String verifyType;
        
        /**
         * 总评分
         */
        private Double overallRating;
        
        /**
         * 月销量
         */
        private Integer monthlySales;
    }
    
    /**
     * 活动信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityInfo {
        /**
         * 活动类型
         */
        private String activityType;
        
        /**
         * 活动状态
         */
        private String activityStatus;
        
        /**
         * 参与人数
         */
        private Integer participantCount;
        
        /**
         * 剩余名额
         */
        private Integer remainingSlots;
        
        /**
         * 是否需要报名
         */
        private Boolean needRegister;
        
        /**
         * 是否已报名
         */
        private Boolean isRegistered;
        
        /**
         * 活动标签
         */
        private List<String> activityTags;
    }
    
    /**
     * 优惠券信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponInfo {
        /**
         * 优惠券类型
         */
        private String couponType;
        
        /**
         * 优惠金额/折扣
         */
        private String couponValue;
        
        /**
         * 使用门槛
         */
        private Double minOrderAmount;
        
        /**
         * 剩余数量
         */
        private Integer remainingCount;
        
        /**
         * 总数量
         */
        private Integer totalCount;
        
        /**
         * 是否已领取
         */
        private Boolean isClaimed;
        
        /**
         * 是否可用
         */
        private Boolean isAvailable;
        
        /**
         * 使用规则
         */
        private String usageRules;
    }
    
    /**
     * 社交信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialInfo {
        /**
         * 点赞数
         */
        private Integer likeCount;
        
        /**
         * 是否已点赞
         */
        private Boolean isLiked;
        
        /**
         * 收藏数
         */
        private Integer favoriteCount;
        
        /**
         * 是否已收藏
         */
        private Boolean isFavorited;
        
        /**
         * 评论数
         */
        private Integer commentCount;
        
        /**
         * 分享数
         */
        private Integer shareCount;
        
        /**
         * 好友互动数
         */
        private Integer friendInteractionCount;
        
        /**
         * 好友头像列表
         */
        private List<String> friendAvatarList;
        
        /**
         * 好友推荐语
         */
        private String friendRecommendText;
    }
    
    /**
     * 用户行为统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBehaviorStats {
        /**
         * 曝光次数
         */
        private Long impressionCount;
        
        /**
         * 点击次数
         */
        private Long clickCount;
        
        /**
         * 点击率
         */
        private Double ctr;
        
        /**
         * 转化率
         */
        private Double conversionRate;
        
        /**
         * 平均停留时长（秒）
         */
        private Integer avgStaySeconds;
        
        /**
         * 收藏率
         */
        private Double favoriteRate;
        
        /**
         * 分享率
         */
        private Double shareRate;
    }
}
