package com.im.search.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 搜索意图识别结果DTO
 * 解析用户自然语言查询的意图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchIntentDTO {

    /** 原始查询文本 */
    private String originalQuery;

    /** 意图类型 */
    private IntentType intentType;

    /** 意图置信度 0-1 */
    private Float confidence;

    /** 提取的实体列表 */
    private List<SearchEntityDTO> entities;

    /** 解析后的结构化查询条件 */
    private StructuredQueryDTO structuredQuery;

    /** 语义扩展的同义词 */
    private List<String> synonyms;

    /** 查询纠错建议 */
    private String correctedQuery;

    /** 是否需要纠错 */
    private Boolean needCorrection;

    /** 多轮对话上下文 */
    private DialogContextDTO dialogContext;

    /** 搜索建议 */
    private List<String> searchSuggestions;

    /**
     * 意图类型枚举
     */
    public enum IntentType {
        /** 附近搜索 */
        NEARBY_SEARCH,
        /** 导航意图 */
        NAVIGATION,
        /** 团购/优惠 */
        GROUP_BUYING,
        /** 预约意图 */
        RESERVATION,
        /** 比价意图 */
        PRICE_COMPARISON,
        /** 了解详情 */
        DETAIL_INQUIRY,
        /** 推荐请求 */
        RECOMMENDATION,
        /** 问答意图 */
        QA,
        /** 路线规划 */
        ROUTE_PLANNING,
        /** 模糊意图 */
        AMBIGUOUS,
        /** 未知意图 */
        UNKNOWN
    }

    /**
     * 搜索实体DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchEntityDTO {
        /** 实体类型 */
        private EntityType type;
        /** 实体值 */
        private String value;
        /** 原始文本 */
        private String rawText;
        /** 起始位置 */
        private Integer start;
        /** 结束位置 */
        private Integer end;
        /** 实体属性 */
        private Map<String, Object> attributes;
    }

    /**
     * 实体类型枚举
     */
    public enum EntityType {
        /** 地点 */
        LOCATION,
        /** POI名称 */
        POI_NAME,
        /** 分类 */
        CATEGORY,
        /** 品牌 */
        BRAND,
        /** 价格 */
        PRICE,
        /** 距离 */
        DISTANCE,
        /** 评分 */
        RATING,
        /** 时间 */
        TIME,
        /** 人数 */
        PEOPLE_COUNT,
        /** 特色标签 */
        TAG,
        /** 商圈 */
        BUSINESS_AREA,
        /** 城市 */
        CITY
    }

    /**
     * 结构化查询DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StructuredQueryDTO {
        /** 关键词 */
        private List<String> keywords;
        /** 分类 */
        private List<String> categories;
        /** 位置约束 */
        private LocationConstraintDTO location;
        /** 价格约束 */
        private PriceConstraintDTO price;
        /** 评分约束 */
        private Float minRating;
        /** 特色标签 */
        private List<String> tags;
        /** 排序偏好 */
        private SortPreferenceDTO sort;
        /** 场景标签 */
        private String scene;
    }

    /**
     * 位置约束DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationConstraintDTO {
        /** 参考点经度 */
        private Double longitude;
        /** 参考点纬度 */
        private Double latitude;
        /** 搜索半径（米） */
        private Integer radius;
        /** 指定区域 */
        private String area;
        /** 附近地标 */
        private String landmark;
    }

    /**
     * 价格约束DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceConstraintDTO {
        /** 最小价格 */
        private Integer min;
        /** 最大价格 */
        private Integer max;
        /** 价格级别：1-便宜 2-适中 3-较贵 4-高端 */
        private Integer level;
    }

    /**
     * 排序偏好DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortPreferenceDTO {
        /** 排序字段 */
        private String field;
        /** 是否升序 */
        private Boolean ascending;
        /** 多字段排序 */
        private List<Map<String, String>> multiSort;
    }

    /**
     * 对话上下文DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DialogContextDTO {
        /** 会话ID */
        private String sessionId;
        /** 轮次 */
        private Integer turn;
        /** 上一轮意图 */
        private String previousIntent;
        /** 上一轮实体 */
        private List<SearchEntityDTO> previousEntities;
        /** 是否需要追问澄清 */
        private Boolean needClarification;
        /** 追问问题 */
        private String clarificationQuestion;
    }
}
