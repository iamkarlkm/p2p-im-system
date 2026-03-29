package com.im.backend.modules.local.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 语义搜索响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "语义搜索响应")
public class SemanticSearchResponse {
    
    @Schema(description = "语义解析结果")
    private SemanticParseResultDTO parseResult;
    
    @Schema(description = "搜索结果")
    private List<SemanticSearchResultDTO> results;
    
    @Schema(description = "对话回复（多轮对话时）")
    private String dialogueResponse;
    
    @Schema(description = "是否需要进一步澄清")
    private Boolean needClarification;
    
    @Schema(description = "澄清提示")
    private String clarificationPrompt;
    
    @Schema(description = "对话会话ID")
    private String conversationId;
    
    @Schema(description = "当前轮次")
    private Integer currentTurn;
    
    @Schema(description = "响应时间(毫秒)")
    private Long responseTime;
    
    // ==================== 内部DTO ====================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "语义解析结果")
    public static class SemanticParseResultDTO {
        @Schema(description = "原始查询")
        private String originalQuery;
        
        @Schema(description = "解析后的意图")
        private String intent;
        
        @Schema(description = "意图置信度")
        private Double confidence;
        
        @Schema(description = "提取的实体")
        private List<ExtractedEntityDTO> entities;
        
        @Schema(description = "约束条件")
        private ConstraintsDTO constraints;
        
        @Schema(description = "排序偏好")
        private String sortPreference;
        
        @Schema(description = "解析的查询参数")
        private Map<String, Object> queryParams;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提取的实体")
    public static class ExtractedEntityDTO {
        @Schema(description = "实体文本")
        private String text;
        
        @Schema(description = "实体类型")
        private String type;
        
        @Schema(description = "标准化值")
        private String normalizedValue;
        
        @Schema(description = "置信度")
        private Double confidence;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "约束条件")
    public static class ConstraintsDTO {
        @Schema(description = "位置约束")
        private LocationConstraintDTO location;
        
        @Schema(description = "时间约束")
        private TimeConstraintDTO time;
        
        @Schema(description = "价格约束")
        private PriceConstraintDTO price;
        
        @Schema(description = "评分约束")
        private Double minRating;
        
        @Schema(description = "特色筛选")
        private List<String> features;
        
        @Schema(description = "场景标签")
        private String sceneTag;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "位置约束")
    public static class LocationConstraintDTO {
        @Schema(description = "中心经度")
        private Double longitude;
        
        @Schema(description = "中心纬度")
        private Double latitude;
        
        @Schema(description = "搜索半径")
        private Integer radius;
        
        @Schema(description = "位置描述")
        private String locationText;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "时间约束")
    public static class TimeConstraintDTO {
        @Schema(description = "时间类型: NOW, TODAY, WEEKEND, SPECIFIC")
        private String type;
        
        @Schema(description = "具体时间")
        private LocalDateTime specificTime;
        
        @Schema(description = "时间描述")
        private String timeText;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "价格约束")
    public static class PriceConstraintDTO {
        @Schema(description = "最低价格")
        private Double min;
        
        @Schema(description = "最高价格")
        private Double max;
        
        @Schema(description = "价格描述")
        private String priceText;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "语义搜索结果")
    public static class SemanticSearchResultDTO {
        @Schema(description = "POI ID")
        private Long poiId;
        
        @Schema(description = "POI名称")
        private String name;
        
        @Schema(description = "评分")
        private Double rating;
        
        @Schema(description = "人均消费")
        private Double avgPrice;
        
        @Schema(description = "距离（米）")
        private Integer distance;
        
        @Schema(description = "地址")
        private String address;
        
        @Schema(description = "匹配原因")
        private String matchReason;
        
        @Schema(description = "匹配分数")
        private Double matchScore;
        
        @Schema(description = "是否符合语义")
        private Boolean semanticMatch;
    }
}
