package com.im.backend.modules.local_life.review.entity;

import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价实体
 * 支持多维度评分、图文评价、短视频评价
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantReview extends BaseEntity {
    
    /** 评价ID */
    private Long reviewId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 用户ID */
    private Long userId;
    
    /** 订单ID（关联订单） */
    private Long orderId;
    
    /** 总体评分 (1-5分) */
    private Integer overallRating;
    
    /** 口味评分 (1-5分) */
    private Integer tasteRating;
    
    /** 环境评分 (1-5分) */
    private Integer environmentRating;
    
    /** 服务评分 (1-5分) */
    private Integer serviceRating;
    
    /** 性价比评分 (1-5分) */
    private Integer valueRating;
    
    /** 评价内容 */
    private String content;
    
    /** 评价图片列表 (JSON数组) */
    private String images;
    
    /** 评价视频URL */
    private String videoUrl;
    
    /** 视频封面图 */
    private String videoCover;
    
    /** 视频时长(秒) */
    private Integer videoDuration;
    
    /** 消费金额 */
    private BigDecimal consumeAmount;
    
    /** 人均消费 */
    private BigDecimal perCapita;
    
    /** 是否匿名评价 */
    private Boolean anonymous;
    
    /** 是否推荐 */
    private Boolean recommended;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 回复数 */
    private Integer replyCount;
    
    /** 浏览数 */
    private Integer viewCount;
    
    /** 评价质量分 (0-100) */
    private Integer qualityScore;
    
    /** 情感分析结果: POSITIVE/NEUTRAL/NEGATIVE */
    private String sentiment;
    
    /** 是否优质评价 */
    private Boolean highQuality;
    
    /** 是否虚假评价 (AI识别) */
    private Boolean fakeReview;
    
    /** 虚假评价置信度 */
    private BigDecimal fakeConfidence;
    
    /** 评价来源: APP/MINI_PROGRAM/WEB */
    private String source;
    
    /** 就餐日期 */
    private LocalDateTime diningDate;
    
    /** 评价时间 */
    private LocalDateTime reviewTime;
    
    /** 是否可见 */
    private Boolean visible;
    
    /** 是否被屏蔽 */
    private Boolean blocked;
    
    /** 屏蔽原因 */
    private String blockReason;
    
    /** 商家回复内容 */
    private String merchantReply;
    
    /** 商家回复时间 */
    private LocalDateTime merchantReplyTime;
    
    /** 是否已申诉 */
    private Boolean appealed;
    
    /** 申诉状态: PENDING/APPROVED/REJECTED */
    private String appealStatus;
    
    /** 帮助过的用户数 */
    private Integer helpfulCount;
    
    /** 标签列表 (JSON数组: 味道赞,服务好,环境优雅等) */
    private String tags;
    
    /** 加权评分 (用于排序) */
    private BigDecimal weightedScore;
    
    // ============ 业务方法 ============
    
    /**
     * 计算平均分
     */
    public BigDecimal calculateAverageRating() {
        if (overallRating != null) {
            return new BigDecimal(overallRating);
        }
        int sum = 0;
        int count = 0;
        if (tasteRating != null) { sum += tasteRating; count++; }
        if (environmentRating != null) { sum += environmentRating; count++; }
        if (serviceRating != null) { sum += serviceRating; count++; }
        if (valueRating != null) { sum += valueRating; count++; }
        return count > 0 ? new BigDecimal(sum).divide(new BigDecimal(count), 1, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }
    
    /**
     * 判断是否完整评价
     */
    public boolean isCompleteReview() {
        return overallRating != null && content != null && content.length() >= 10;
    }
    
    /**
     * 获取评价质量等级
     */
    public String getQualityLevel() {
        if (qualityScore == null) return "NORMAL";
        if (qualityScore >= 80) return "EXCELLENT";
        if (qualityScore >= 60) return "GOOD";
        return "NORMAL";
    }
    
    /**
     * 检查是否为有效评价
     */
    public boolean isValid() {
        return visible != null && visible && 
               (blocked == null || !blocked) && 
               (fakeReview == null || !fakeReview);
    }
}
