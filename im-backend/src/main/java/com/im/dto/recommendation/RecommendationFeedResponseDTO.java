package com.im.dto.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 推荐信息流响应DTO
 * 返回用户的个性化推荐信息流
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFeedResponseDTO {
    
    /**
     * 响应状态
     * SUCCESS: 成功
     * PARTIAL: 部分成功
     * ERROR: 失败
     */
    private String status;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 状态消息
     */
    private String message;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 会话ID（用于分页追踪）
     */
    private String sessionId;
    
    /**
     * 推荐项列表
     */
    private List<RecommendationItemDTO> items;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总数量（不一定提供）
     */
    private Long totalCount;
    
    /**
     * 是否还有更多
     */
    private Boolean hasMore;
    
    /**
     * 下一次翻页游标
     */
    private String nextCursor;
    
    /**
     * 推荐场景
     */
    private String scene;
    
    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 推荐策略信息
     */
    private StrategyInfo strategyInfo;
    
    /**
     * 场景上下文
     */
    private SceneContextDTO sceneContext;
    
    /**
     * 推荐统计
     */
    private FeedStatsDTO statistics;
    
    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 推荐项DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationItemDTO {
        /**
         * 推荐项ID
         */
        private String itemId;
        
        /**
         * 推荐项类型
         * POI: 兴趣点
         * ACTIVITY: 活动
         * COUPON: 优惠券
         * GROUP: 拼团
         * EVENT: 事件
         * CONTENT: 内容
         */
        private String itemType;
        
        /**
         * 业务ID
         */
        private String businessId;
        
        /**
         * 标题
         */
        private String title;
        
        /**
         * 副标题
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
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
        
        /**
         * 地址
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
         * 分类名称
         */
        private String categoryName;
        
        /**
         * 评分
         */
        private Double rating;
        
        /**
         * 评分人数
         */
        private Integer ratingCount;
        
        /**
         * 价格信息
         */
        private PriceInfoDTO priceInfo;
        
        /**
         * 标签列表
         */
        private List<String> tags;
        
        /**
         * 推荐理由
         */
        private String recommendReason;
        
        /**
         * 推荐标签
         */
        private List<String> recommendTags;
        
        /**
         * 召回来源
         */
        private String recallSource;
        
        /**
         * 商户信息
         */
        private MerchantInfoDTO merchantInfo;
        
        /**
         * 活动信息（如果类型为活动）
         */
        private ActivityInfoDTO activityInfo;
        
        /**
         * 优惠信息（如果类型为优惠券）
         */
        private CouponInfoDTO couponInfo;
        
        /**
         * 社交信息
         */
        private SocialInfoDTO socialInfo;
        
        /**
         * 场景标签
         */
        private List<String> sceneTags;
        
        /**
         * 是否置顶
         */
        private Boolean isPinned;
        
        /**
         * 是否推广
         */
        private Boolean isPromoted;
        
        /**
         * 卡片样式类型
         */
        private String cardStyle;
        
        /**
         * 扩展数据
         */
        private Map<String, Object> extraData;
        
        /**
         * 发布时间
         */
        private LocalDateTime publishTime;
    }
    
    /**
     * 价格信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceInfoDTO {
        /**
         * 原价
         */
        private Double originalPrice;
        
        /**
         * 现价
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
         * 价格描述
         */
        private String priceDescription;
        
        /**
         * 是否有优惠
         */
        private Boolean hasDiscount;
    }
    
    /**
     * 商户信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantInfoDTO {
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
         * 总评分
         */
        private Double overallRating;
    }
    
    /**
     * 活动信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityInfoDTO {
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
         * 是否已报名
         */
        private Boolean isRegistered;
        
        /**
         * 活动标签
         */
        private List<String> activityTags;
        
        /**
         * 开始时间
         */
        private LocalDateTime startTime;
        
        /**
         * 结束时间
         */
        private LocalDateTime endTime;
    }
    
    /**
     * 优惠券信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponInfoDTO {
        /**
         * 优惠券类型
         */
        private String couponType;
        
        /**
         * 优惠价值
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
         * 是否已领取
         */
        private Boolean isClaimed;
        
        /**
         * 是否可用
         */
        private Boolean isAvailable;
        
        /**
         * 过期时间
         */
        private LocalDateTime expireTime;
    }
    
    /**
     * 社交信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialInfoDTO {
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
         * 好友推荐语
         */
        private String friendRecommendText;
        
        /**
         * 好友头像列表
         */
        private List<String> friendAvatarList;
    }
    
    /**
     * 策略信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategyInfo {
        /**
         * 使用的召回策略
         */
        private List<String> recallStrategies;
        
        /**
         * 排序策略版本
         */
        private String sortStrategyVersion;
        
        /**
         * A/B测试分组
         */
        private String abTestGroup;
        
        /**
         * 算法版本
         */
        private String algorithmVersion;
    }
    
    /**
     * 场景上下文DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneContextDTO {
        /**
         * 当前时段
         */
        private String timeSegment;
        
        /**
         * 是否周末
         */
        private Boolean isWeekend;
        
        /**
         * 是否节假日
         */
        private Boolean isHoliday;
        
        /**
         * 节假日名称
         */
        private String holidayName;
        
        /**
         * 天气状况
         */
        private String weatherCondition;
        
        /**
         * 温度
         */
        private Integer temperature;
        
        /**
         * 场景标签
         */
        private List<String> sceneTags;
    }
    
    /**
     * 信息流统计DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedStatsDTO {
        /**
         * 总推荐项数
         */
        private Integer totalItems;
        
        /**
         * 各类型数量
         */
        private Map<String, Integer> typeCounts;
        
        /**
         * 召回耗时（毫秒）
         */
        private Long recallTimeMs;
        
        /**
         * 排序耗时（毫秒）
         */
        private Long sortTimeMs;
        
        /**
         * 总生成耗时（毫秒）
         */
        private Long totalGenerateTimeMs;
    }
}
