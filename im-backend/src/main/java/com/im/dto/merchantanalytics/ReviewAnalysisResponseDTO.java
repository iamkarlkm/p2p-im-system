// 评价分析响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReviewAnalysisResponseDTO {
    private Long merchantId;
    private LocalDate statDate;
    
    // 评分概览
    private BigDecimal overallRating;
    private BigDecimal tasteRating;
    private BigDecimal environmentRating;
    private BigDecimal serviceRating;
    private BigDecimal valueRating;
    
    // 评价数量
    private Integer totalReviews;
    private RatingDistributionDTO ratingDistribution;
    
    // 好评率
    private BigDecimal positiveRate;
    private BigDecimal negativeRate;
    private BigDecimal neutralRate;
    
    // 今日新增
    private Integer todayNewReviews;
    private BigDecimal todayAvgRating;
    
    // 内容分析
    private Integer withPhotoReviews;
    private Integer withVideoReviews;
    
    // 回复统计
    private BigDecimal replyRate;
    private BigDecimal avgReplyTimeHours;
    private Integer pendingReplyCount;
    
    // 关键词
    private List<KeywordDTO> positiveKeywords;
    private List<KeywordDTO> negativeKeywords;
    
    // 竞品对比
    private BigDecimal categoryAvgRating;
    private Integer categoryRank;
    
    // 预警
    private Boolean negativeAlert;
    private String alertMessage;
}
