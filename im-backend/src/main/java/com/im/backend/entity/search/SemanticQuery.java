package com.im.backend.entity.search;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 语义查询实体类
 * 存储用户的自然语言搜索查询及其解析结果
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_semantic_query")
public class SemanticQuery {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 原始查询文本
     */
    private String rawQuery;
    
    /**
     * 规范化后的查询文本
     */
    private String normalizedQuery;
    
    /**
     * 查询意图类型
     * NAVIGATION: 导航
     * GROUP_BUY: 团购
     * RESERVATION: 预约
     * PRICE_COMPARE: 比价
     * DETAIL: 了解详情
     * GENERAL: 通用搜索
     */
    private String intentType;
    
    /**
     * POI分类（如：餐饮、娱乐、购物）
     */
    private String poiCategory;
    
    /**
     * 提取的关键词列表（JSON格式）
     */
    private String keywords;
    
    /**
     * 位置信息
     */
    private Double longitude;
    private Double latitude;
    private String locationName;
    
    /**
     * 价格约束
     */
    private Double minPrice;
    private Double maxPrice;
    
    /**
     * 距离约束（米）
     */
    private Integer maxDistance;
    
    /**
     * 评分约束
     */
    private Double minRating;
    
    /**
     * 时间约束
     */
    private String timeConstraint;
    
    /**
     * 是否多轮对话
     */
    private Boolean isMultiTurn;
    
    /**
     * 会话ID（多轮对话使用）
     */
    private String sessionId;
    
    /**
     * 是否为语音输入
     */
    private Boolean isVoiceInput;
    
    /**
     * 语音识别置信度
     */
    private Double voiceConfidence;
    
    /**
     * 语义解析置信度
     */
    private Double parseConfidence;
    
    /**
     * 用户当前城市代码
     */
    private String cityCode;
    
    /**
     * 用户当前区县代码
     */
    private String districtCode;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ========== 业务方法 ==========
    
    /**
     * 判断是否为餐饮类搜索
     */
    public boolean isFoodQuery() {
        return "餐饮".equals(poiCategory) || 
               (keywords != null && keywords.contains("美食")) ||
               (keywords != null && keywords.contains("餐厅"));
    }
    
    /**
     * 判断是否为价格敏感查询
     */
    public boolean isPriceSensitive() {
        return maxPrice != null || 
               (keywords != null && keywords.contains("便宜")) ||
               (keywords != null && keywords.contains("优惠"));
    }
    
    /**
     * 判断是否为品质优先查询
     */
    public boolean isQualityFirst() {
        return minRating != null && minRating >= 4.5;
    }
    
    /**
     * 判断是否为距离优先查询
     */
    public boolean isDistanceFirst() {
        return maxDistance != null && maxDistance <= 1000;
    }
    
    /**
     * 获取查询复杂度评分
     */
    public int getComplexityScore() {
        int score = 0;
        if (isMultiTurn) score += 2;
        if (minPrice != null || maxPrice != null) score += 1;
        if (maxDistance != null) score += 1;
        if (minRating != null) score += 1;
        if (timeConstraint != null) score += 1;
        return score;
    }
    
    /**
     * 转换为Elasticsearch查询条件
     */
    public Map<String, Object> toElasticsearchQuery() {
        Map<String, Object> query = new java.util.HashMap<>();
        query.put("query_text", normalizedQuery);
        if (poiCategory != null) query.put("category", poiCategory);
        if (maxDistance != null && longitude != null && latitude != null) {
            query.put("location", Map.of("lon", longitude, "lat", latitude));
            query.put("distance", maxDistance);
        }
        if (minRating != null) query.put("min_rating", minRating);
        return query;
    }
    
    /**
     * 构建多轮对话上下文
     */
    public SemanticQuery buildFollowUpQuery(String followUpText) {
        return SemanticQuery.builder()
                .userId(this.userId)
                .rawQuery(followUpText)
                .sessionId(this.sessionId)
                .isMultiTurn(true)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .cityCode(this.cityCode)
                .build();
    }
}
