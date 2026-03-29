package com.im.backend.dto.search;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 语义搜索响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticSearchResponseDTO {
    
    /**
     * 响应状态
     * SUCCESS: 成功
     * CLARIFICATION_NEEDED: 需要澄清
     * NO_RESULTS: 无结果
     * ERROR: 错误
     */
    private String status;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 解析后的查询意图
     */
    private SearchIntentDTO intent;
    
    /**
     * 澄清问题（当需要澄清时）
     */
    private String clarificationQuestion;
    
    /**
     * 澄清选项
     */
    private List<String> clarificationOptions;
    
    /**
     * 搜索结果列表
     */
    private List<SearchResultDTO> results;
    
    /**
     * 推荐问题（用于多轮对话）
     */
    private List<String> suggestedQueries;
    
    /**
     * 总结果数
     */
    private Long totalCount;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 是否还有更多结果
     */
    private Boolean hasMore;
    
    /**
     * 搜索耗时（毫秒）
     */
    private Long searchTimeMs;
    
    /**
     * 搜索意图DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchIntentDTO {
        /**
         * 主要意图
         */
        private String primaryIntent;
        
        /**
         * 意图置信度
         */
        private Double confidence;
        
        /**
         * 意图描述
         */
        private String description;
        
        /**
         * 提取的关键词
         */
        private List<String> keywords;
        
        /**
         * POI分类
         */
        private String poiCategory;
        
        /**
         * 解析后的约束条件
         */
        private Map<String, Object> constraints;
    }
    
    /**
     * 搜索结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultDTO {
        /**
         * POI ID
         */
        private Long poiId;
        
        /**
         * POI名称
         */
        private String name;
        
        /**
         * POI分类
         */
        private String category;
        
        /**
         * 分类名称
         */
        private String categoryName;
        
        /**
         * 评分
         */
        private Double rating;
        
        /**
         * 评价数量
         */
        private Integer reviewCount;
        
        /**
         * 人均消费
         */
        private String avgPrice;
        
        /**
         * 地址
         */
        private String address;
        
        /**
         * 距离（米）
         */
        private Integer distance;
        
        /**
         * 距离描述
         */
        private String distanceDesc;
        
        /**
         * 主图URL
         */
        private String mainImage;
        
        /**
         * 图片列表
         */
        private List<String> images;
        
        /**
         * 营业时间
         */
        private String businessHours;
        
        /**
         * 是否营业中
         */
        private Boolean isOpen;
        
        /**
         * 营业时间状态描述
         */
        private String openStatusDesc;
        
        /**
         * 特色标签
         */
        private List<String> tags;
        
        /**
         * 推荐菜/服务
         */
        private List<String> recommendations;
        
        /**
         * 优惠信息
         */
        private List<DiscountInfoDTO> discounts;
        
        /**
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
        
        /**
         * 电话号码
         */
        private String phone;
        
        /**
         * 排序得分
         */
        private Double score;
        
        /**
         * 推荐理由
         */
        private String recommendReason;
        
        /**
         * 快捷操作
         */
        private List<String> quickActions;
    }
    
    /**
     * 优惠信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountInfoDTO {
        /**
         * 优惠类型
         */
        private String type;
        
        /**
         * 优惠标题
         */
        private String title;
        
        /**
         * 优惠内容
         */
        private String content;
        
        /**
         * 原价
         */
        private Double originalPrice;
        
        /**
         * 现价
         */
        private Double currentPrice;
    }
    
    // ========== 静态工厂方法 ==========
    
    /**
     * 创建成功响应
     */
    public static SemanticSearchResponseDTO success(String sessionId, List<SearchResultDTO> results) {
        return SemanticSearchResponseDTO.builder()
                .status("SUCCESS")
                .sessionId(sessionId)
                .results(results)
                .hasMore(false)
                .build();
    }
    
    /**
     * 创建需要澄清的响应
     */
    public static SemanticSearchResponseDTO needsClarification(String sessionId, String question, List<String> options) {
        return SemanticSearchResponseDTO.builder()
                .status("CLARIFICATION_NEEDED")
                .sessionId(sessionId)
                .clarificationQuestion(question)
                .clarificationOptions(options)
                .build();
    }
    
    /**
     * 创建无结果响应
     */
    public static SemanticSearchResponseDTO noResults(String sessionId, List<String> suggestions) {
        return SemanticSearchResponseDTO.builder()
                .status("NO_RESULTS")
                .sessionId(sessionId)
                .suggestedQueries(suggestions)
                .build();
    }
}
