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
 * 用户推荐信息流实体
 * 封装单个用户的个性化推荐信息流配置和状态
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationFeed {
    
    /**
     * 信息流ID
     */
    private String feedId;
    
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
    private List<RecommendationItem> items;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
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
     * NEARBY: 附近推荐
     * HOME: 首页信息流
     * DISCOVER: 发现页
     * FAVORITE: 猜你喜欢
     * SCENE: 场景化推荐
     */
    private String scene;
    
    /**
     * 场景上下文
     */
    private SceneContext sceneContext;
    
    /**
     * 使用的召回策略
     */
    private List<String> recallStrategies;
    
    /**
     * 排序策略版本
     */
    private String sortStrategyVersion;
    
    /**
     * 生成时间
     */
    private LocalDateTime generateTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 缓存时间（秒）
     */
    private Integer cacheTtl;
    
    /**
     * 用户当前位置
     */
    private UserLocation userLocation;
    
    /**
     * 推荐多样性配置
     */
    private DiversityConfig diversityConfig;
    
    /**
     * 推荐结果统计
     */
    private FeedStatistics statistics;
    
    /**
     * A/B测试分组
     */
    private String abTestGroup;
    
    /**
     * 算法版本
     */
    private String algorithmVersion;
    
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
    
    // ==================== 内部类定义 ====================
    
    /**
     * 场景上下文
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneContext {
        /**
         * 时间段
         * MORNING: 早晨 (6-10)
         * LUNCH: 午餐 (10-14)
         * AFTERNOON: 下午 (14-18)
         * DINNER: 晚餐 (18-22)
         * NIGHT: 夜宵 (22-6)
         */
        private String timeSegment;
        
        /**
         * 星期几
         */
        private Integer dayOfWeek;
        
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
        private Set<String> sceneTags;
        
        /**
         * 用户当前活动
         * WORKING: 工作中
         * COMMUTING: 通勤中
         * RESTING: 休息中
         * DINING: 就餐中
         * SHOPPING: 购物中
         * ENTERTAINMENT: 娱乐中
         */
        private String userActivity;
    }
    
    /**
     * 用户位置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLocation {
        /**
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
        
        /**
         * 位置精度（米）
         */
        private Double accuracy;
        
        /**
         * 位置描述
         */
        private String locationDescription;
        
        /**
         * 城市代码
         */
        private String cityCode;
        
        /**
         * 城市名称
         */
        private String cityName;
        
        /**
         * 区县代码
         */
        private String districtCode;
        
        /**
         * 区县名称
         */
        private String districtName;
        
        /**
         * 商圈ID
         */
        private String businessDistrictId;
        
        /**
         * 商圈名称
         */
        private String businessDistrictName;
        
        /**
         * POI ID
         */
        private String poiId;
        
        /**
         * POI名称
         */
        private String poiName;
        
        /**
         * 位置更新时间
         */
        private LocalDateTime locationUpdateTime;
    }
    
    /**
     * 多样性配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiversityConfig {
        /**
         * 分类多样性比例
         */
        private Map<String, Double> categoryRatio;
        
        /**
         * 商户多样性（同一商户最大出现次数）
         */
        private Integer maxMerchantRepeat;
        
        /**
         * 类型多样性（POI/活动/优惠券等比例）
         */
        private Map<String, Double> typeRatio;
        
         /**
         * 距离多样性比例
         */
        private Map<String, Double> distanceRatio;
        
        /**
         * 价格区间多样性
         */
        private Map<String, Double> priceRangeRatio;
        
        /**
         * 是否启用多样性控制
         */
        private Boolean enableDiversity;
        
        /**
         * 多样性算法类型
         * MMR: 最大边缘相关
         * DPP: 行列式点过程
         * RULE: 规则控制
         */
        private String diversityAlgorithm;
    }
    
    /**
     * 信息流统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedStatistics {
        /**
         * 总推荐项数
         */
        private Integer totalItems;
        
        /**
         * 各类型数量
         */
        private Map<String, Integer> typeCounts;
        
        /**
         * 各召回源数量
         */
        private Map<String, Integer> recallSourceCounts;
        
        /**
         * 平均距离
         */
        private Double avgDistance;
        
        /**
         * 平均评分
         */
        private Double avgRating;
        
        /**
         * 价格分布
         */
        private Map<String, Integer> priceDistribution;
        
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
