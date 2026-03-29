package com.im.local.merchant.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商户经营数据实体
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
public class MerchantDailyStats {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 统计日期
     */
    private LocalDate statsDate;
    
    /**
     * 总营收
     */
    private BigDecimal totalRevenue;
    
    /**
     * 订单数量
     */
    private Integer orderCount;
    
    /**
     * 访客数量
     */
    private Integer visitorCount;
    
    /**
     * 支付订单数
     */
    private Integer paidOrderCount;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款订单数
     */
    private Integer refundCount;
    
    /**
     * 新访客数
     */
    private Integer newVisitorCount;
    
    /**
     * 老访客数
     */
    private Integer returningVisitorCount;
    
    /**
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 五星评价数
     */
    private Integer fiveStarCount;
    
    /**
     * 四星评价数
     */
    private Integer fourStarCount;
    
    /**
     * 三星及以下评价数
     */
    private Integer lowStarCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // ==================== 业务方法 ====================
    
    /**
     * 计算转化率
     */
    public BigDecimal calculateConversionRate() {
        if (visitorCount == null || visitorCount == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(paidOrderCount)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(visitorCount), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算客单价
     */
    public BigDecimal calculateAvgOrderValue() {
        if (orderCount == null || orderCount == 0) {
            return BigDecimal.ZERO;
        }
        return totalRevenue.divide(new BigDecimal(orderCount), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算退款率
     */
    public BigDecimal calculateRefundRate() {
        if (orderCount == null || orderCount == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(refundCount)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(orderCount), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算好评率
     */
    public BigDecimal calculatePositiveRate() {
        if (reviewCount == null || reviewCount == 0) {
            return BigDecimal.ZERO;
        }
        int positiveCount = (fiveStarCount != null ? fiveStarCount : 0) + 
                           (fourStarCount != null ? fourStarCount : 0);
        return new BigDecimal(positiveCount)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(reviewCount), 2, BigDecimal.ROUND_HALF_UP);
    }
}
