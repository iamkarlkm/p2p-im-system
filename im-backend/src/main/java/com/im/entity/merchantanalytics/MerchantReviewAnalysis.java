// 商户评价分析实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MerchantReviewAnalysis {
    private Long id;
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
    private Integer fiveStarCount;
    private Integer fourStarCount;
    private Integer threeStarCount;
    private Integer twoStarCount;
    private Integer oneStarCount;
    
    // 评分分布比例
    private BigDecimal fiveStarRate;
    private BigDecimal fourStarRate;
    private BigDecimal threeStarRate;
    private BigDecimal twoStarRate;
    private BigDecimal oneStarRate;
    
    // 好评率
    private BigDecimal positiveRate;
    private BigDecimal negativeRate;
    private BigDecimal neutralRate;
    
    // 今日新增
    private Integer todayNewReviews;
    private BigDecimal todayAvgRating;
    
    // 评价趋势
    private String sevenDayReviewTrend;
    private String ratingTrend;
    
    // 评价内容分析
    private Integer withPhotoReviews;
    private Integer withVideoReviews;
    private Integer withTextReviews;
    
    // 情感分析
    private Integer positiveSentimentCount;
    private Integer negativeSentimentCount;
    private Integer neutralSentimentCount;
    
    // 关键词提取
    private String topPositiveKeywords; // JSON
    private String topNegativeKeywords; // JSON
    private String topMentionedDishes; // JSON
    
    // 回复统计
    private Integer repliedReviews;
    private Integer unrepliedReviews;
    private BigDecimal replyRate;
    private BigDecimal avgReplyTimeHours;
    
    // 竞品对比
    private BigDecimal categoryAvgRating;
    private BigDecimal districtAvgRating;
    private Integer categoryRank;
    private Integer districtRank;
    
    // 差评预警
    private Integer oneStarToday;
    private Integer negativeSentimentToday;
    private Boolean negativeAlert;
    private String alertReason;
    
    // 评价来源
    private Integer searchReviewCount;
    private Integer orderReviewCount;
    private Integer activityReviewCount;
    private Integer checkInReviewCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 深度分析
    private String reviewTags; // JSON - 标签云
    private String serviceIssues; // JSON - 服务问题统计
    private String foodIssues; // JSON - 菜品问题统计
    private String environmentIssues; // JSON - 环境问题统计
    
    // 用户画像
    private String reviewerDemographics; // JSON - 评价者 demographics
    private BigDecimal vipReviewerRatio;
    private Integer verifiedPurchaseReviews;
    
    // 改进建议
    private String improvementSuggestions; // AI生成
    
    // 评价转化
    private BigDecimal reviewToVisitRate;
    private BigDecimal reviewToOrderRate;
    
    // 热门评价
    private String topReviews; // JSON - 置顶好评
    private String recentReviews; // JSON - 最新评价
    
    // 月累计
    private Integer monthTotalReviews;
    private BigDecimal monthAvgRating;
    private Integer monthFiveStarCount;
    private Integer monthOneStarCount;
}
