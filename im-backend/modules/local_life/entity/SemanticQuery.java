package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 语义查询实体
 * 存储经过NLP处理后的结构化语义查询
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("semantic_query")
public class SemanticQuery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 查询ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原始查询文本
     */
    private String rawQuery;

    /**
     * 标准化查询文本
     */
    private String normalizedQuery;

    /**
     * 查询分词结果（逗号分隔）
     */
    private String tokens;

    /**
     * 命名实体识别结果（JSON）
     * 如：[{"type": "LOCATION", "value": "附近", "start": 0, "end": 2}]
     */
    private String namedEntities;

    /**
     * 查询意图
     */
    private String intent;

    /**
     * 查询子意图
     */
    private String subIntent;

    /**
     * POI分类约束（逗号分隔）
     */
    private String poiCategories;

    /**
     * 地理位置约束（JSON）
     * 如：{"type": "nearby", "radius": 3000, "center": {"lat": 31.23, "lng": 121.47}}
     */
    private String locationConstraint;

    /**
     * 价格范围约束
     */
    private String priceRange;

    /**
     * 最低价格
     */
    private Integer minPrice;

    /**
     * 最高价格
     */
    private Integer maxPrice;

    /**
     * 时间约束（JSON）
     * 如：{"type": "now", "day_of_week": "weekend", "time_of_day": "dinner"}
     */
    private String timeConstraint;

    /**
     * 排序偏好：DISTANCE-距离, RATING-评分, POPULARITY-热度, PRICE_ASC-价格升序, PRICE_DESC-价格降序
     */
    private String sortPreference;

    /**
     * 筛选条件（JSON）
     * 如：{"has_parking": true, "is_open": true, "rating_above": 4.0}
     */
    private String filters;

    /**
     * 场景标签（逗号分隔）
     */
    private String sceneTags;

    /**
     * 语义向量（用于向量检索）
     */
    private String semanticVector;

    /**
     * 查询复杂度：SIMPLE-简单, COMPLEX-复杂, MULTI_INTENT-多意图
     */
    private String complexity;

    /**
     * 是否需要多轮对话澄清
     */
    private Boolean needsClarification;

    /**
     * 澄清问题（JSON列表）
     */
    private String clarificationQuestions;

    /**
     * 查询创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 查询处理耗时（毫秒）
     */
    private Integer processingTimeMs;

    /**
     * 使用的分词器/NLP模型
     */
    private String nlpEngine;

    /**
     * 查询哈希（用于缓存）
     */
    private String queryHash;

    /**
     * 是否命中缓存
     */
    private Boolean cacheHit;

    /**
     * 获取分词列表
     */
    public List<String> getTokenList() {
        if (tokens == null || tokens.isEmpty()) {
            return List.of();
        }
        return List.of(tokens.split(","));
    }

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
     * 查询复杂度枚举
     */
    public enum Complexity {
        SIMPLE("简单查询"),
        COMPLEX("复杂查询"),
        MULTI_INTENT("多意图查询"),
        CONVERSATIONAL("对话式查询");

        private final String label;

        Complexity(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * 排序偏好枚举
     */
    public enum SortPreference {
        DISTANCE("距离优先"),
        RATING("评分优先"),
        POPULARITY("热度优先"),
        PRICE_ASC("价格从低到高"),
        PRICE_DESC("价格从高到低"),
        COMPREHENSIVE("综合排序");

        private final String label;

        SortPreference(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
