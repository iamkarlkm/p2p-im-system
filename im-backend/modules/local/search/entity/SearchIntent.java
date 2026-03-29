package com.im.backend.modules.local.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * POI搜索意图实体
 * 存储NLP解析后的搜索意图信息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("local_search_intent")
public class SearchIntent {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的搜索查询ID
     */
    private Long queryId;
    
    /**
     * 主意图类型
     */
    private String primaryIntent;
    
    /**
     * 子意图类型
     */
    private String subIntent;
    
    /**
     * 意图置信度
     */
    private Double confidence;
    
    /**
     * 意图分类器版本
     */
    private String classifierVersion;
    
    /**
     * 提取的实体列表 (JSON格式)
     * 如：["火锅", "附近", "人均100以下"]
     */
    private String entities;
    
    /**
     * 实体与类型的映射 (JSON格式)
     * 如：{"火锅": " cuisine_type", "附近": "location_indicator"}
     */
    private String entityTypes;
    
    /**
     * 时间约束
     * NOW - 现在
     * TODAY - 今天
     * WEEKEND - 周末
     * SPECIFIC - 指定时间
     */
    private String timeConstraint;
    
    /**
     * 具体时间点
     */
    private LocalDateTime specificTime;
    
    /**
     * 价格约束类型
     * CHEAP - 便宜
     * MODERATE - 中等
     * EXPENSIVE - 昂贵
     * RANGE - 价格范围
     */
    private String priceConstraint;
    
    /**
     * 最低价格
     */
    private Double minPrice;
    
    /**
     * 最高价格
     */
    private Double maxPrice;
    
    /**
     * 评分约束 (最低评分)
     */
    private Double minRating;
    
    /**
     * 距离约束 (米)
     */
    private Integer maxDistance;
    
    /**
     * 排序偏好
     * DISTANCE - 距离最近
     * RATING - 评分最高
     * POPULARITY - 人气最高
     * PRICE_ASC - 价格从低到高
     * PRICE_DESC - 价格从高到低
     * SMART - 智能排序
     */
    private String sortPreference;
    
    /**
     * 特色筛选 (JSON格式)
     * 如：["免费停车", "包间", "可以带宠物"]
     */
    private String features;
    
    /**
     * 场景标签
     * DATE - 约会
     * FAMILY - 家庭聚餐
     * BUSINESS - 商务宴请
     * FRIENDS - 朋友聚会
     * SOLO - 独自用餐
     */
    private String sceneTag;
    
    /**
     * 是否包含否定词
     */
    private Boolean hasNegation;
    
    /**
     * 否定词列表 (JSON格式)
     */
    private String negatedTerms;
    
    /**
     * 情感倾向
     * POSITIVE - 积极
     * NEUTRAL - 中性
     * NEGATIVE - 消极
     */
    private String sentiment;
    
    /**
     * 情感强度 (0.0 - 1.0)
     */
    private Double sentimentIntensity;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ==================== 业务方法 ====================
    
    /**
     * 检查意图是否为导航类
     */
    public boolean isNavigation() {
        return "NAVIGATION".equals(this.primaryIntent);
    }
    
    /**
     * 检查意图是否为团购类
     */
    public boolean isGroupon() {
        return "GROUPON".equals(this.primaryIntent);
    }
    
    /**
     * 检查意图是否为预约类
     */
    public boolean isReservation() {
        return "RESERVATION".equals(this.primaryIntent);
    }
    
    /**
     * 检查意图是否为比价类
     */
    public boolean isComparison() {
        return "COMPARISON".equals(this.primaryIntent);
    }
    
    /**
     * 检查意图是否为信息查询类
     */
    public boolean isInfoQuery() {
        return "INFO".equals(this.primaryIntent);
    }
    
    /**
     * 检查是否有价格约束
     */
    public boolean hasPriceConstraint() {
        return this.minPrice != null || this.maxPrice != null || this.priceConstraint != null;
    }
    
    /**
     * 检查是否有评分约束
     */
    public boolean hasRatingConstraint() {
        return this.minRating != null && this.minRating > 0;
    }
    
    /**
     * 检查是否有距离约束
     */
    public boolean hasDistanceConstraint() {
        return this.maxDistance != null && this.maxDistance > 0;
    }
    
    /**
     * 获取价格范围描述
     */
    public String getPriceRangeDescription() {
        if (!hasPriceConstraint()) {
            return null;
        }
        
        if (this.priceConstraint != null) {
            switch (this.priceConstraint) {
                case "CHEAP": return "便宜";
                case "MODERATE": return "中等价位";
                case "EXPENSIVE": return "高档消费";
                case "RANGE": break;
            }
        }
        
        if (this.minPrice != null && this.maxPrice != null) {
            return String.format("%.0f-%.0f元", this.minPrice, this.maxPrice);
        } else if (this.maxPrice != null) {
            return String.format("%.0f元以下", this.maxPrice);
        } else if (this.minPrice != null) {
            return String.format("%.0f元以上", this.minPrice);
        }
        
        return null;
    }
    
    /**
     * 获取实体列表
     */
    public List<String> getEntityList() {
        if (this.entities == null) {
            return new java.util.ArrayList<>();
        }
        // 实际项目中使用 JSON 解析
        return java.util.Arrays.asList(this.entities.replace("[", "").replace("]", "").split(","));
    }
    
    /**
     * 获取特色筛选列表
     */
    public List<String> getFeatureList() {
        if (this.features == null) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.asList(this.features.replace("[", "").replace("]", "").split(","));
    }
    
    /**
     * 构建排序参数
     */
    public Map<String, Object> buildSortParams() {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("sortBy", this.sortPreference != null ? this.sortPreference : "SMART");
        
        // 根据意图调整排序权重
        if (isNavigation()) {
            params.put("distanceWeight", 0.6);
            params.put("ratingWeight", 0.2);
            params.put("popularityWeight", 0.2);
        } else if (isGroupon()) {
            params.put("discountWeight", 0.4);
            params.put("priceWeight", 0.3);
            params.put("ratingWeight", 0.3);
        } else {
            params.put("distanceWeight", 0.3);
            params.put("ratingWeight", 0.4);
            params.put("popularityWeight", 0.3);
        }
        
        return params;
    }
    
    /**
     * 检查是否适合智能排序
     */
    public boolean shouldUseSmartSort() {
        return "SMART".equals(this.sortPreference) || this.sortPreference == null;
    }
    
    /**
     * 获取时间约束描述
     */
    public String getTimeConstraintDescription() {
        if (this.timeConstraint == null) {
            return null;
        }
        
        switch (this.timeConstraint) {
            case "NOW": return "现在";
            case "TODAY": return "今天";
            case "WEEKEND": return "周末";
            case "SPECIFIC": 
                return this.specificTime != null ? this.specificTime.toString() : "指定时间";
            default: return null;
        }
    }
    
    /**
     * 转换为查询过滤条件
     */
    public Map<String, Object> toFilterParams() {
        Map<String, Object> params = new java.util.HashMap<>();
        
        if (hasRatingConstraint()) {
            params.put("minRating", this.minRating);
        }
        
        if (hasDistanceConstraint()) {
            params.put("maxDistance", this.maxDistance);
        }
        
        if (hasPriceConstraint()) {
            if (this.minPrice != null) {
                params.put("minPrice", this.minPrice);
            }
            if (this.maxPrice != null) {
                params.put("maxPrice", this.maxPrice);
            }
        }
        
        if (this.features != null) {
            params.put("features", getFeatureList());
        }
        
        if (this.sceneTag != null) {
            params.put("sceneTag", this.sceneTag);
        }
        
        return params;
    }
    
    /**
     * 计算意图匹配分数
     */
    public double calculateMatchScore(String queryIntent) {
        if (this.primaryIntent == null || queryIntent == null) {
            return 0.0;
        }
        
        if (this.primaryIntent.equals(queryIntent)) {
            return this.confidence != null ? this.confidence : 1.0;
        }
        
        return 0.0;
    }
}
