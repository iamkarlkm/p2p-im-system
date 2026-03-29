package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 语义理解结果DTO
 */
@Data
public class SemanticUnderstandingResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原始查询
     */
    private String originalQuery;

    /**
     * 清洗后的查询
     */
    private String cleanedQuery;

    /**
     * 搜索意图类型
     * NAVIGATION-导航, GROUP_BUY-团购, RESERVATION-预约, 
     * PRICE_COMPARE-比价, INFO-了解详情, NEARBY-附近搜索
     */
    private String intentType;

    /**
     * 意图置信度 (0.0-1.0)
     */
    private Double intentConfidence;

    /**
     * 提取的实体列表
     */
    private List<ExtractedEntityDTO> entities;

    /**
     * 是否需要澄清
     */
    private Boolean needClarification;

    /**
     * 澄清提示
     */
    private String clarificationPrompt;

    /**
     * 澄清选项
     */
    private List<String> clarificationOptions;

    /**
     * 时间解析结果
     */
    private TimeParseResultDTO timeResult;

    /**
     * 位置解析结果
     */
    private LocationParseResultDTO locationResult;

    /**
     * 价格解析结果
     */
    private PriceParseResultDTO priceResult;

    /**
     * 结构化查询条件
     */
    private Map<String, Object> structuredQuery;

    /**
     * 提取的实体DTO
     */
    @Data
    public static class ExtractedEntityDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 实体类型
         */
        private String type;

        /**
         * 实体值
         */
        private String value;

        /**
         * 原始文本
         */
        private String originalText;

        /**
         * 开始位置
         */
        private Integer startPos;

        /**
         * 结束位置
         */
        private Integer endPos;

        /**
         * 置信度
         */
        private Double confidence;
    }

    /**
     * 时间解析结果DTO
     */
    @Data
    public static class TimeParseResultDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 时间类型: NOW-现在, TODAY-今天, TOMORROW-明天, WEEKEND-周末, SPECIFIC-具体时间
         */
        private String timeType;

        /**
         * 具体日期时间
         */
        private String specificTime;

        /**
         * 时间段开始
         */
        private String startTime;

        /**
         * 时间段结束
         */
        private String endTime;

        /**
         * 星期几 (1-7)
         */
        private Integer dayOfWeek;

        /**
         * 是否营业时间
         */
        private Boolean isBusinessHours;
    }

    /**
     * 位置解析结果DTO
     */
    @Data
    public static class LocationParseResultDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 位置类型: NEARBY-附近, CURRENT-当前位置, DISTRICT-商圈, ADDRESS-具体地址
         */
        private String locationType;

        /**
         * 地址描述
         */
        private String address;

        /**
         * 商圈ID
         */
        private Long districtId;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 纬度
         */
        private Double latitude;

        /**
         * 半径(米)
         */
        private Integer radius;
    }

    /**
     * 价格解析结果DTO
     */
    @Data
    public static class PriceParseResultDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 价格类型: CHEAP-便宜, MODERATE-适中, EXPENSIVE-贵, SPECIFIC-具体范围
         */
        private String priceType;

        /**
         * 最低价格
         */
        private Integer minPrice;

        /**
         * 最高价格
         */
        private Integer maxPrice;

        /**
         * 人均消费级别描述
         */
        private String priceLevel;
    }
}
