package com.im.backend.modules.local_life.review.entity;

import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户口碑统计实体
 * 实时计算商户好评率、口碑榜单数据
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantReputation extends BaseEntity {
    
    /** 统计ID */
    private Long reputationId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 总评价数 */
    private Integer totalReviews;
    
    /** 有效评价数 (排除虚假/屏蔽评价) */
    private Integer validReviews;
    
    /** 优质评价数 */
    private Integer highQualityReviews;
    
    /** 有图评价数 */
    private Integer imageReviews;
    
    /** 有视频评价数 */
    private Integer videoReviews;
    
    /** 总体平均评分 (1-5) */
    private BigDecimal overallRating;
    
    /** 口味平均评分 */
    private BigDecimal tasteRating;
    
    /** 环境平均评分 */
    private BigDecimal environmentRating;
    
    /** 服务平均评分 */
    private BigDecimal serviceRating;
    
    /** 性价比平均评分 */
    private BigDecimal valueRating;
    
    /** 5星评价数 */
    private Integer fiveStarCount;
    
    /** 4星评价数 */
    private Integer fourStarCount;
    
    /** 3星评价数 */
    private Integer threeStarCount;
    
    /** 2星评价数 */
    private Integer twoStarCount;
    
    /** 1星评价数 */
    private Integer oneStarCount;
    
    /** 好评数 (4-5星) */
    private Integer positiveCount;
    
    /** 中评数 (3星) */
    private Integer neutralCount;
    
    /** 差评数 (1-2星) */
    private Integer negativeCount;
    
    /** 好评率 (百分比) */
    private BigDecimal positiveRate;
    
    /** 差评率 (百分比) */
    private BigDecimal negativeRate;
    
    /** 口碑分 (0-100，综合计算) */
    private BigDecimal reputationScore;
    
    /** 口碑等级: S/A/B/C/D */
    private String reputationLevel;
    
    /** 口味评分排名 (同商圈同类目) */
    private Integer tasteRank;
    
    /** 环境评分排名 */
    private Integer environmentRank;
    
    /** 服务评分排名 */
    private Integer serviceRank;
    
    /** 综合评分排名 */
    private Integer overallRank;
    
    /** 商圈内排名 */
    private Integer districtRank;
    
    /** 城市内排名 */
    private Integer cityRank;
    
    /** 是否口碑榜上榜 */
    private Boolean onReputationList;
    
    /** 榜单类型: TOP_QUALITY/TOP_POPULAR/TOP_SERVICE */
    private String listType;
    
    /** 榜单排名 */
    private Integer listRank;
    
    /** 推荐权重 (用于排序) */
    private BigDecimal recommendWeight;
    
    /** 最近7天评价数 */
    private Integer last7DaysReviews;
    
    /** 最近30天评价数 */
    private Integer last30DaysReviews;
    
    /** 趋势: UP/DOWN/STABLE */
    private String trend;
    
    /** 趋势变化值 */
    private BigDecimal trendValue;
    
    /** 统计更新时间 */
    private LocalDateTime statisticsUpdateTime;
    
    /** 榜单更新时间 */
    private LocalDateTime listUpdateTime;
    
    /** 计算版本号 (用于并发控制) */
    private Integer version;
    
    // ============ 业务方法 ============
    
    /**
     * 计算好评率
     */
    public BigDecimal calculatePositiveRate() {
        if (validReviews == null || validReviews == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(positiveCount * 100)
                .divide(new BigDecimal(validReviews), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算口碑分 (综合算法)
     */
    public BigDecimal calculateReputationScore() {
        if (validReviews == null || validReviews == 0) {
            return new BigDecimal("50.00");
        }
        
        // 基础评分 (40%)
        BigDecimal ratingWeight = overallRating.multiply(new BigDecimal("8"));
        
        // 好评率 (25%)
        BigDecimal positiveRateWeight = calculatePositiveRate().multiply(new BigDecimal("0.25"));
        
        // 评价质量 (20%): 优质评价占比
        BigDecimal qualityWeight = new BigDecimal(highQualityReviews * 20)
                .divide(new BigDecimal(validReviews), 2, BigDecimal.ROUND_HALF_UP);
        
        // 活跃度 (15%): 基于最近30天评价
        BigDecimal activityWeight = last30DaysReviews > 0 ? 
                new BigDecimal("15") : new BigDecimal("5");
        
        return ratingWeight.add(positiveRateWeight).add(qualityWeight).add(activityWeight)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取口碑等级
     */
    public String calculateReputationLevel() {
        BigDecimal score = calculateReputationScore();
        if (score.compareTo(new BigDecimal("90")) >= 0) return "S";
        if (score.compareTo(new BigDecimal("80")) >= 0) return "A";
        if (score.compareTo(new BigDecimal("70")) >= 0) return "B";
        if (score.compareTo(new BigDecimal("60")) >= 0) return "C";
        return "D";
    }
    
    /**
     * 计算推荐权重 (用于搜索排序)
     */
    public BigDecimal calculateRecommendWeight() {
        BigDecimal score = calculateReputationScore();
        // 口碑分 * log(评价数+1) * 时间衰减因子
        double logFactor = Math.log(validReviews + 1);
        return score.multiply(new BigDecimal(logFactor))
                .setScale(4, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 判断是否可以上榜
     */
    public boolean canBeOnList() {
        return validReviews >= 10 && 
               positiveRate.compareTo(new BigDecimal("80")) >= 0 &&
               calculateReputationScore().compareTo(new BigDecimal("75")) >= 0;
    }
    
    /**
     * 计算趋势
     */
    public void calculateTrend(MerchantReputation previous) {
        if (previous == null || previous.getReputationScore() == null) {
            this.trend = "STABLE";
            this.trendValue = BigDecimal.ZERO;
            return;
        }
        BigDecimal diff = this.reputationScore.subtract(previous.getReputationScore());
        this.trendValue = diff;
        if (diff.compareTo(new BigDecimal("2")) > 0) {
            this.trend = "UP";
        } else if (diff.compareTo(new BigDecimal("-2")) < 0) {
            this.trend = "DOWN";
        } else {
            this.trend = "STABLE";
        }
    }
}
