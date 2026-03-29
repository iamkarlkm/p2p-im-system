package com.im.local.marketing.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户优惠券实体
 * 记录用户领取的优惠券
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_coupons")
public class UserCoupon {
    
    @Id
    private String userCouponId;
    
    private String userId;
    
    private String couponId;
    
    private String templateId;
    
    private String merchantId;
    
    private String merchantName;
    
    private String title;
    
    private String description;
    
    private String couponType;
    
    private BigDecimal discountValue;
    
    private BigDecimal minOrderAmount;
    
    private BigDecimal maxDiscountAmount;
    
    private String couponImage;
    
    private String merchantLogo;
    
    /**
     * 有效期开始时间
     */
    private LocalDateTime validStartTime;
    
    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;
    
    /**
     * 领取时间
     */
    private LocalDateTime claimTime;
    
    /**
     * 使用时间
     */
    private LocalDateTime useTime;
    
    /**
     * 使用订单ID
     */
    private String orderId;
    
    /**
     * 使用订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 实际优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 优惠券状态
     * UNUSED: 未使用
     * USED: 已使用
     * EXPIRED: 已过期
     * FROZEN: 冻结中（订单待支付）
     * REFUNDED: 已退款返还
     */
    private String status;
    
    /**
     * 领取渠道
     */
    private String claimChannel;
    
    /**
     * 领取来源
     * SELF: 自主领取
     * GIFT: 好友赠送
     * SHARE: 分享获得
     * EVENT: 活动发放
     * PUSH: 推送发放
     * SYSTEM: 系统发放
     */
    private String claimSource;
    
    /**
     * 赠送人ID（如果是好友赠送）
     */
    private String giftFromUserId;
    
    /**
     * 转赠记录
     */
    private TransferRecord transferRecord;
    
    /**
     * 转赠记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRecord {
        private String fromUserId;
        private String fromUserName;
        private String toUserId;
        private String toUserName;
        private LocalDateTime transferTime;
        private String message;
    }
    
    /**
     * 使用限制
     */
    private String useTimeRange;
    
    private String usageInstructions;
    
    private Map<String, Object> extraData;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    /**
     * 检查优惠券是否可用
     */
    public boolean isUsable() {
        if (!"UNUSED".equals(status)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (validStartTime != null && now.isBefore(validStartTime)) {
            return false;
        }
        if (validEndTime != null && now.isAfter(validEndTime)) {
            return false;
        }
        return true;
    }
    
    /**
     * 检查优惠券是否已过期
     */
    public boolean isExpired() {
        if ("EXPIRED".equals(status)) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        if (validEndTime != null && now.isAfter(validEndTime)) {
            return true;
        }
        return false;
    }
    
    /**
     * 计算订单可用优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isUsable()) {
            return BigDecimal.ZERO;
        }
        if (minOrderAmount != null && orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (couponType) {
            case "FULL_REDUCTION":
            case "CASH":
                discount = discountValue;
                break;
            case "DISCOUNT":
                // 折扣券
                BigDecimal reduced = orderAmount.multiply(BigDecimal.ONE.subtract(discountValue));
                discount = reduced;
                if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                    discount = maxDiscountAmount;
                }
                break;
            default:
                break;
        }
        
        // 优惠金额不能超过订单金额
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        
        return discount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 标记为已使用
     */
    public void markAsUsed(String orderId, BigDecimal orderAmount, BigDecimal discountAmount) {
        this.status = "USED";
        this.useTime = LocalDateTime.now();
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.discountAmount = discountAmount;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 标记为已过期
     */
    public void markAsExpired() {
        this.status = "EXPIRED";
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 冻结优惠券（订单待支付）
     */
    public void freeze() {
        if ("UNUSED".equals(this.status)) {
            this.status = "FROZEN";
            this.updateTime = LocalDateTime.now();
        }
    }
    
    /**
     * 解冻优惠券（取消订单）
     */
    public void unfreeze() {
        if ("FROZEN".equals(this.status)) {
            this.status = "UNUSED";
            this.updateTime = LocalDateTime.now();
        }
    }
}
