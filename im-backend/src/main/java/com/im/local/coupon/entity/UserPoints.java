package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户积分实体类
 * 记录用户在商户的积分账户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_points")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_merchant", def = "{'userId': 1, 'merchantId': 1}", unique = true),
    @CompoundIndex(name = "idx_merchant_level", def = "{'merchantId': 1, 'memberLevel': 1}")
})
public class UserPoints {
    
    @Id
    private String id;
    
    /** 用户ID */
    @Indexed
    private String userId;
    
    /** 商户ID */
    @Indexed
    private String merchantId;
    
    /** 商户名称 */
    private String merchantName;
    
    /** 当前积分余额 */
    private Integer currentPoints;
    
    /** 累计获得积分 */
    private Integer totalEarnedPoints;
    
    /** 累计使用积分 */
    private Integer totalUsedPoints;
    
    /** 累计过期积分 */
    private Integer totalExpiredPoints;
    
    /** 会员等级编码 */
    private Integer memberLevel;
    
    /** 会员等级名称 */
    private String memberLevelName;
    
    /** 升级所需积分 */
    private Integer pointsToNextLevel;
    
    /** 成为会员时间 */
    private LocalDateTime memberSince;
    
    /** 等级到期时间 */
    private LocalDateTime levelExpireTime;
    
    /** 积分明细记录 (最近50条) */
    private List<PointRecord> recentRecords;
    
    /** 即将过期积分 */
    private List<ExpiringPoints> expiringPoints;
    
    /** 扩展数据 */
    private Map<String, Object> extraData;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /**
     * 积分记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointRecord {
        /** 记录ID */
        private String recordId;
        
        /** 变动类型: EARN-获得, USE-使用, EXPIRE-过期, ADJUST-调整, GIFT-赠送 */
        private String changeType;
        
        /** 变动积分 (正值获得,负值使用) */
        private Integer points;
        
        /** 变动后余额 */
        private Integer balance;
        
        /** 变动来源: ORDER-消费, SIGNIN-签到, ACTIVITY-活动, EXCHANGE-兑换, INVITE-邀请 */
        private String sourceType;
        
        /** 来源ID (订单ID/活动ID等) */
        private String sourceId;
        
        /** 来源描述 */
        private String sourceDesc;
        
        /** 变动时间 */
        private LocalDateTime createTime;
        
        /** 备注 */
        private String remark;
    }
    
    /**
     * 即将过期积分
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpiringPoints {
        /** 积分数 */
        private Integer points;
        
        /** 过期时间 */
        private LocalDateTime expireTime;
    }
    
    /**
     * 增加积分
     */
    public void addPoints(Integer points, String sourceType, String sourceId, String sourceDesc) {
        if (points == null || points <= 0) {
            return;
        }
        this.currentPoints += points;
        this.totalEarnedPoints += points;
        
        PointRecord record = PointRecord.builder()
            .recordId(java.util.UUID.randomUUID().toString())
            .changeType("EARN")
            .points(points)
            .balance(this.currentPoints)
            .sourceType(sourceType)
            .sourceId(sourceId)
            .sourceDesc(sourceDesc)
            .createTime(LocalDateTime.now())
            .build();
        
        if (this.recentRecords == null) {
            this.recentRecords = new java.util.ArrayList<>();
        }
        this.recentRecords.add(0, record);
        if (this.recentRecords.size() > 50) {
            this.recentRecords = this.recentRecords.subList(0, 50);
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 使用积分
     */
    public boolean usePoints(Integer points, String sourceType, String sourceId, String sourceDesc) {
        if (points == null || points <= 0) {
            return false;
        }
        if (this.currentPoints < points) {
            return false;
        }
        
        this.currentPoints -= points;
        this.totalUsedPoints += points;
        
        PointRecord record = PointRecord.builder()
            .recordId(java.util.UUID.randomUUID().toString())
            .changeType("USE")
            .points(-points)
            .balance(this.currentPoints)
            .sourceType(sourceType)
            .sourceId(sourceId)
            .sourceDesc(sourceDesc)
            .createTime(LocalDateTime.now())
            .build();
        
        if (this.recentRecords == null) {
            this.recentRecords = new java.util.ArrayList<>();
        }
        this.recentRecords.add(0, record);
        if (this.recentRecords.size() > 50) {
            this.recentRecords = this.recentRecords.subList(0, 50);
        }
        
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * 检查是否满足升级条件
     */
    public boolean canUpgrade(Integer nextLevelRequiredPoints) {
        if (nextLevelRequiredPoints == null || nextLevelRequiredPoints <= 0) {
            return false;
        }
        return this.totalEarnedPoints >= nextLevelRequiredPoints;
    }
}
