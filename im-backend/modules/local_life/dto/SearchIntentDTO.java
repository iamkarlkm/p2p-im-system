package com.im.backend.modules.local_life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 搜索意图DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "搜索意图识别结果")
public class SearchIntentDTO {

    @Schema(description = "意图ID", example = "12345")
    private Long intentId;

    @Schema(description = "意图类型：NAVIGATE-导航, GROUPON-团购, BOOKING-预约, COMPARE-比价, INFO-详情, RECOMMEND-推荐", example = "RECOMMEND")
    private String intentType;

    @Schema(description = "意图类型中文标签", example = "推荐")
    private String intentTypeLabel;

    @Schema(description = "置信度分数 0-1", example = "0.95")
    private Double confidenceScore;

    @Schema(description = "提取的实体信息")
    private Map<String, Object> extractedEntities;

    @Schema(description = "POI分类列表", example = "[\"火锅\", \"川菜\"]")
    private List<String> poiCategories;

    @Schema(description = "地理位置描述", example = "附近")
    private String locationDesc;

    @Schema(description = "价格约束", example = "人均100以下")
    private String priceConstraint;

    @Schema(description = "时间约束", example = "现在")
    private String timeConstraint;

    @Schema(description = "场景标签", example = "[\"聚餐\", \"晚餐\"]")
    private List<String> sceneTags;

    @Schema(description = "是否需要澄清", example = "false")
    private Boolean needsClarification;

    @Schema(description = "澄清问题列表")
    private List<String> clarificationQuestions;

    @Schema(description = "处理耗时（毫秒）", example = "45")
    private Integer processingTimeMs;

    @Schema(description = "NLP模型版本", example = "BERT-v3.2")
    private String modelVersion;
}
