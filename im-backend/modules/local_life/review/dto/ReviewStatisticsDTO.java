package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价统计DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReviewStatisticsDTO {
    
    /** 商户ID */
    private Long merchantId;
    
    /** 总评价数 */
    private Integer totalReviews;
    
    /** 新增评价数 (今日) */
    private Integer newReviewsToday;
    
    /** 本周新增 */
    private Integer newReviewsWeek;
    
    /** 本月新增 */
    private Integer newReviewsMonth;
    
    /** 平均评分 */
    private BigDecimal averageRating;
    
    /** 好评率 */
    private BigDecimal positiveRate;
    
    /** 评分分布 */
    private List<RatingCountDTO> ratingDistribution;
    
    /** 热门标签 */
    private List<TagStatDTO> hotTags;
    
    /** 趋势数据 */
    private List<TrendDataDTO> trendData;
    
    @Data
    public static class RatingCountDTO {
        private Integer rating;
        private Integer count;
        private BigDecimal percentage;
    }
    
    @Data
    public static class TagStatDTO {
        private String tag;
        private Integer count;
    }
    
    @Data
    public static class TrendDataDTO {
        private String date;
        private Integer reviewCount;
        private BigDecimal avgRating;
    }
}
