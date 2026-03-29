package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 搜索意图实体
 * 用于存储用户搜索的自然语言意图解析结果
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("search_intent")
public class SearchIntent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 意图ID
     */
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
     * 意图类型：NAVIGATE-导航, GROUPON-团购, BOOKING-预约, COMPARE-比价, INFO-详情
     */
    private String intentType;

    /**
     * 置信度分数 0-1
     */
    private Double confidenceScore;

    /**
     * 提取的实体（JSON存储）
     * 如：{"category": "火锅", "location": "附近", "price_range": "人均100以下"}
     */
    private String extractedEntities;

    /**
     * POI分类列表（逗号分隔）
     */
    private String poiCategories;

    /**
     * 地理位置描述
     */
    private String locationDesc;

    /**
     * 价格约束
     */
    private String priceConstraint;

    /**
     * 时间约束
     */
    private String timeConstraint;

    /**
     * 场景标签（逗号分隔）
     */
    private String sceneTags;

    /**
     * 语义向量（用于相似度搜索）
     */
    private String semanticVector;

    /**
     * 对话轮次
     */
    private Integer turnNumber;

    /**
     * 上一轮意图ID（用于多轮对话）
     */
    private Long previousIntentId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 是否有效意图
     */
    private Boolean isValid;

    /**
     * 无效原因
     */
    private String invalidReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 处理耗时（毫秒）
     */
    private Integer processingTimeMs;

    /**
     * 使用的NLP模型版本
     */
    private String modelVersion;

    /**
     * 获取POI分类列表
     */
    public List<String> getPoiCategoryList() {
        if (poiCategories == null || poiCategories.isEmpty()) {
            return List.of();
        }
        return List.of(poiCategories.split(","));
    }

    /**
     * 获取场景标签列表
     */
    public List<String> getSceneTagList() {
        if (sceneTags == null || sceneTags.isEmpty()) {
            return List.of();
        }
        return List.of(sceneTags.split(","));
    }

    /**
     * 意图类型枚举
     */
    public enum IntentType {
        NAVIGATE("导航", "获取路线指引"),
        GROUPON("团购", "查找优惠活动"),
        BOOKING("预约", "在线预订服务"),
        COMPARE("比价", "价格对比"),
        INFO("详情", "了解商户信息"),
        RECOMMEND("推荐", "获取个性化推荐"),
        UNKNOWN("未知", "无法识别的意图");

        private final String label;
        private final String description;

        IntentType(String label, String description) {
            this.label = label;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getDescription() {
            return description;
        }
    }
}
