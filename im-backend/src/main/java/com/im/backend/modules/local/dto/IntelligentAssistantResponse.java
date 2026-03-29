package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 智能对话助手响应DTO
 * 包含语义理解结果、推荐POI、对话回复等
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能对话助手响应")
public class IntelligentAssistantResponse {
    
    @Schema(description = "响应ID", example = "resp_1234567890")
    private String responseId;
    
    @Schema(description = "会话ID", example = "conv_1234567890")
    private String conversationId;
    
    @Schema(description = "识别到的搜索意图")
    private SearchIntentInfo intent;
    
    @Schema(description = "自然语言回复", example = "为您找到以下附近的热门火锅店：")
    private String naturalReply;
    
    @Schema(description = "推荐POI列表")
    private List<RecommendedPOI> recommendations;
    
    @Schema(description = "相关问答")
    private List<QAItem> relatedQA;
    
    @Schema(description = "是否需要澄清", example = "false")
    private Boolean needClarification;
    
    @Schema(description = "澄清问题（如需要）", example = "您更偏好哪种口味？")
    private String clarificationQuestion;
    
    @Schema(description = "建议的后续操作")
    private List<SuggestedAction> suggestedActions;
    
    @Schema(description = "处理耗时（毫秒）", example = "156")
    private Long processTimeMs;
    
    @Schema(description = "响应元数据")
    private Map<String, Object> metadata;
    
    /**
     * 搜索意图信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "搜索意图信息")
    public static class SearchIntentInfo {
        @Schema(description = "意图类型：SEARCH/NAVIGATE/INQUIRE/COMPARE", example = "SEARCH")
        private String type;
        
        @Schema(description = "置信度", example = "0.92")
        private Double confidence;
        
        @Schema(description = "实体识别结果")
        private List<ExtractedEntity> entities;
        
        @Schema(description = "搜索分类", example = "美食-火锅")
        private String category;
        
        @Schema(description = "筛选条件")
        private FilterConditions filters;
    }
    
    /**
     * 提取的实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提取的实体")
    public static class ExtractedEntity {
        @Schema(description = "实体类型：LOCATION/CATEGORY/PRICE/TIME", example = "CATEGORY")
        private String type;
        
        @Schema(description = "实体值", example = "火锅")
        private String value;
        
        @Schema(description = "开始位置", example = "4")
        private Integer startPos;
        
        @Schema(description = "结束位置", example = "6")
        private Integer endPos;
    }
    
    /**
     * 筛选条件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "筛选条件")
    public static class FilterConditions {
        @Schema(description = "距离范围（米）", example = "3000")
        private Integer distance;
        
        @Schema(description = "价格区间", example = "人均100-200")
        private String priceRange;
        
        @Schema(description = "评分要求", example = "4.5")
        private Double minRating;
        
        @Schema(description = "营业状态", example = "营业中")
        private String businessStatus;
        
        @Schema(description = "排序方式", example = "距离优先")
        private String sortBy;
    }
    
    /**
     * 推荐POI
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "推荐POI")
    public static class RecommendedPOI {
        @Schema(description = "POI ID", example = "poi_123456")
        private String poiId;
        
        @Schema(description = "商户名称", example = "海底捞火锅")
        private String name;
        
        @Schema(description = "商户地址", example = "上海市浦东新区陆家嘴环路1000号")
        private String address;
        
        @Schema(description = "距离（米）", example = "1200")
        private Integer distance;
        
        @Schema(description = "评分", example = "4.8")
        private Double rating;
        
        @Schema(description = "人均消费", example = "150")
        private Integer avgPrice;
        
        @Schema(description = "分类", example = "火锅")
        private String category;
        
        @Schema(description = "推荐原因", example = "距离最近的高分火锅店")
        private String recommendReason;
        
        @Schema(description = "营业时间", example = "10:00-02:00")
        private String businessHours;
        
        @Schema(description = "是否营业中", example = "true")
        private Boolean isOpen;
        
        @Schema(description = "图片URL列表")
        private List<String> images;
        
        @Schema(description = "标签列表", example = "[\"网红店\", \"服务热情\"]")
        private List<String> tags;
    }
    
    /**
     * 问答项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "问答项")
    public static class QAItem {
        @Schema(description = "问题", example = "这家店的营业时间是什么？")
        private String question;
        
        @Schema(description = "答案", example = "营业时间为10:00-22:00")
        private String answer;
        
        @Schema(description = "信息来源", example = "商户信息")
        private String source;
    }
    
    /**
     * 建议的操作
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "建议的操作")
    public static class SuggestedAction {
        @Schema(description = "操作类型：NAVIGATE/CALL/BOOK/SHARE", example = "NAVIGATE")
        private String type;
        
        @Schema(description = "操作名称", example = "导航前往")
        private String name;
        
        @Schema(description = "操作参数")
        private Map<String, Object> params;
    }
}
