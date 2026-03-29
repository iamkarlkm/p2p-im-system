package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商户口碑统计响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class MerchantReputationDTO {
    
    /** 商户ID */
    private Long merchantId;
    
    /** 总评价数 */
    private Integer totalReviews;
    
    /** 有效评价数 */
    private Integer validReviews;
    
    /** 有图评价数 */
    private Integer imageReviews;
    
    /** 有视频评价数 */
    private Integer videoReviews;
    
    /** 总体平均评分 */
    private BigDecimal overallRating;
    
    /** 口味评分 */
    private BigDecimal tasteRating;
    
    /** 环境评分 */
    private BigDecimal environmentRating;
    
    /** 服务评分 */
    private BigDecimal serviceRating;
    
    /** 性价比评分 */
    private BigDecimal valueRating;
    
    /** 评分分布 */
    private RatingDistributionDTO ratingDistribution;
    
    /** 好评率 */
    private BigDecimal positiveRate;
    
    /** 口碑分 */
    private BigDecimal reputationScore;
    
    /** 口碑等级 S/A/B/C/D */
    private String reputationLevel;
    
    /** 是否上榜 */
    private Boolean onReputationList;
    
    /** 榜单类型 */
    private String listType;
    
    /** 榜单排名 */
    private Integer listRank;
    
    /** 口味排名 */
    private String tasteRankText;
    
    /** 服务排名 */
    private String serviceRankText;
    
    /** 综合排名 */
    private String overallRankText;
    
    /** 趋势 UP/DOWN/STABLE */
    private String trend;
    
    /** 最近7天评价数 */
    private Integer last7DaysReviews;
    
    /** 热门评价标签 */
    private List<TagCountDTO> hotTags;
    
    /**
     * 评分分布DTO
     */
    @Data
    public static class RatingDistributionDTO {
        private Integer fiveStar;
        private Integer fourStar;
        private Integer threeStar;
        private Integer twoStar;
        private Integer oneStar;
    }
    
    /**
     * 标签统计DTO
     */
    @Data
    public static class TagCountDTO {
        private String tag;
        private Integer count;
    }
}
