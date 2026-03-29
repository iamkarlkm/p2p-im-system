package com.im.entity.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 推荐排序特征实体
 * 封装排序模型所需的全部特征
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingFeature {
    
    /**
     * 特征ID
     */
    private String featureId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 物品ID
     */
    private String itemId;
    
    /**
     * 召回源
     */
    private String recallSource;
    
    /**
     * 召回分数
     */
    private Double recallScore;
    
    // ==================== 用户特征 ====================
    
    /**
     * 用户画像特征
     */
    private UserProfileFeature userProfile;
    
    /**
     * 用户行为特征
     */
    private UserBehaviorFeature userBehavior;
    
    /**
     * 用户上下文特征
     */
    private UserContextFeature userContext;
    
    // ==================== 物品特征 ====================
    
    /**
     * 物品基础特征
     */
    private ItemFeature itemFeature;
    
    /**
     * 物品统计特征
     */
    private ItemStatsFeature itemStats;
    
    // ==================== 交叉特征 ====================
    
    /**
     * 用户-物品交叉特征
     */
    private CrossFeature crossFeature;
    
    /**
     * 场景特征
     */
    private SceneFeature sceneFeature;
    
    /**
     * 最终排序分数
     */
    private Double finalScore;
    
    /**
     * 各子模型分数
     */
    private Map<String, Double> modelScores;
    
    /**
     * 特征生成时间
     */
    private LocalDateTime featureGenerateTime;
    
    /**
     * 特征版本
     */
    private String featureVersion;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 用户画像特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileFeature {
        /**
         * 用户年龄
         */
        private Integer age;
        
        /**
         * 用户性别
         */
        private String gender;
        
        /**
         * 用户等级
         */
        private Integer userLevel;
        
        /**
         * 用户会员类型
         */
        private String membershipType;
        
        /**
         * 用户消费偏好标签
         */
        private List<String> consumptionPreferenceTags;
        
        /**
         * 用户价格敏感度
         */
        private Double priceSensitivity;
        
        /**
         * 用户品质偏好
         */
        private Double qualityPreference;
        
        /**
         * 用户距离敏感度
         */
        private Double distanceSensitivity;
        
        /**
         * 用户活跃程度
         */
        private String activityLevel;
        
        /**
         * 用户注册天数
         */
        private Integer registerDays;
        
        /**
         * 用户常用分类
         */
        private List<String> favoriteCategories;
        
        /**
         * 用户平均消费金额
         */
        private Double avgConsumptionAmount;
        
        /**
         * 用户Embedding向量
         */
        private List<Double> userEmbedding;
    }
    
    /**
     * 用户行为特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBehaviorFeature {
        /**
         * 最近7天曝光数
         */
        private Integer last7dImpressions;
        
        /**
         * 最近7天点击数
         */
        private Integer last7dClicks;
        
        /**
         * 最近7天点击率
         */
        private Double last7dCtr;
        
        /**
         * 最近30天消费次数
         */
        private Integer last30dConsumptions;
        
        /**
         * 最近30天消费金额
         */
        private Double last30dConsumptionAmount;
        
        /**
         * 历史偏好分类
         */
        private Map<String, Double> categoryPreference;
        
        /**
         * 历史偏好POI
         */
        private List<String> favoritePoiIds;
        
        /**
         * 最近搜索关键词
         */
        private List<String> recentSearchKeywords;
        
        /**
         * 最近浏览POI
         */
        private List<String> recentViewPoiIds;
        
        /**
         * 收藏POI数
         */
        private Integer favoritePoiCount;
        
        /**
         * 平均每次会话浏览数
         */
        private Double avgSessionViews;
        
        /**
         * 平均点击到消费的转化时间（小时）
         */
        private Double avgClickToConversionHours;
    }
    
    /**
     * 用户上下文特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserContextFeature {
        /**
         * 当前经度
         */
        private Double currentLongitude;
        
        /**
         * 当前纬度
         */
        private Double currentLatitude;
        
        /**
         * 当前城市
         */
        private String currentCity;
        
        /**
         * 当前时段
         */
        private String currentTimeSegment;
        
        /**
         * 当前星期几
         */
        private Integer currentDayOfWeek;
        
        /**
         * 是否周末
         */
        private Boolean isWeekend;
        
        /**
         * 是否节假日
         */
        private Boolean isHoliday;
        
        /**
         * 当前天气
         */
        private String currentWeather;
        
        /**
         * 使用设备类型
         */
        private String deviceType;
        
        /**
         * 应用版本
         */
        private String appVersion;
        
        /**
         * 网络类型
         */
        private String networkType;
        
        /**
         * 是否WiFi
         */
        private Boolean isWifi;
    }
    
    /**
     * 物品基础特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFeature {
        /**
         * 物品类型
         */
        private String itemType;
        
        /**
         * 分类ID
         */
        private String categoryId;
        
        /**
         * 分类层级
         */
        private Integer categoryLevel;
        
        /**
         * 所属商户ID
         */
        private String merchantId;
        
        /**
         * 商户等级
         */
        private Integer merchantLevel;
        
        /**
         * 商户评分
         */
        private Double merchantRating;
        
        /**
         * 评分星级
         */
        private Double rating;
        
        /**
         * 评分人数
         */
        private Integer ratingCount;
        
        /**
         * 人均价格
         */
        private Double avgPrice;
        
        /**
         * 价格档位
         */
        private String priceLevel;
        
        /**
         * 标签列表
         */
        private List<String> tags;
        
        /**
         * 营业时间
         */
        private String businessHours;
        
        /**
         * 是否营业中
         */
        private Boolean isOpenNow;
        
        /**
         * 物品Embedding向量
         */
        private List<Double> itemEmbedding;
    }
    
    /**
     * 物品统计特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemStatsFeature {
        /**
         * 总曝光数
         */
        private Long totalImpressions;
        
        /**
         * 总点击数
         */
        private Long totalClicks;
        
        /**
         * 总点击率
         */
        private Double totalCtr;
        
        /**
         * 近7天曝光数
         */
        private Integer last7dImpressions;
        
        /**
         * 近7天点击数
         */
        private Integer last7dClicks;
        
        /**
         * 近7天点击率
         */
        private Double last7dCtr;
        
        /**
         * 近30天收藏数
         */
        private Integer last30dFavorites;
        
        /**
         * 近30天分享数
         */
        private Integer last30dShares;
        
        /**
         * 近30天消费数
         */
        private Integer last30dConversions;
        
        /**
         * 近30天转化率
         */
        private Double last30dConversionRate;
        
        /**
         * 热度分数
         */
        private Integer heatScore;
        
        /**
         * 热度趋势
         * RISING: 上升
         * STABLE: 稳定
         * DECLINING: 下降
         */
        private String heatTrend;
        
        /**
         * 新鲜度分数
         */
        private Integer freshnessScore;
    }
    
    /**
     * 用户-物品交叉特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrossFeature {
        /**
         * 用户到物品的距离（米）
         */
        private Integer distance;
        
        /**
         * 距离分档
         */
        private String distanceBucket;
        
        /**
         * 用户是否偏好该分类
         */
        private Boolean isPreferredCategory;
        
        /**
         * 分类偏好匹配度
         */
        private Double categoryMatchScore;
        
        /**
         * 价格匹配度
         */
        private Double priceMatchScore;
        
        /**
         * 用户-物品Embedding相似度
         */
        private Double embeddingSimilarity;
        
        /**
         * 用户历史是否浏览过该商户
         */
        private Boolean hasViewedMerchant;
        
        /**
         * 用户历史是否消费过该商户
         */
        private Boolean hasConsumedAtMerchant;
        
        /**
         * 用户历史是否收藏过该分类
         */
        private Boolean hasFavoritedCategory;
        
        /**
         * 好友是否推荐过
         */
        private Boolean isRecommendedByFriend;
        
        /**
         * 好友推荐人数
         */
        private Integer friendRecommendCount;
        
        /**
         * 交叉特征向量
         */
        private List<Double> crossEmbedding;
    }
    
    /**
     * 场景特征
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneFeature {
        /**
         * 场景类型
         */
        private String sceneType;
        
        /**
         * 时间段匹配度
         */
        private Double timeMatchScore;
        
        /**
         * 天气匹配度
         */
        private Double weatherMatchScore;
        
        /**
         * 周末匹配度
         */
        private Double weekendMatchScore;
        
        /**
         * 节假日匹配度
         */
        private Double holidayMatchScore;
        
        /**
         * 场景标签匹配数
         */
        private Integer sceneTagMatchCount;
        
        /**
         * 场景标签匹配分数
         */
        private Double sceneTagMatchScore;
        
        /**
         * 实时需求强度
         */
        private Double realtimeDemandStrength;
    }
}
