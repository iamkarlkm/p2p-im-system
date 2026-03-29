package com.im.backend.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户会员实体 - 用户在各商户/平台的会员身份
 * 
 * 功能说明:
 * 1. 记录用户在特定商户或平台的会员信息
 * 2. 支持平台会员和商户会员双轨制
 * 3. 会员等级、积分、成长值管理
 * 4. 会员有效期与保级追踪
 * 5. 支持多商户多会员身份
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_user_member")
public class UserMember implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础关联 ====================
    
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会员类型
     * 1: 平台会员
     * 2: 商户会员
     */
    private Integer memberType;
    
    /**
     * 商户ID - 商户会员时使用
     * 0表示平台会员
     */
    private Long merchantId;
    
    /**
     * 商户名称
     */
    private String merchantName;
    
    // ==================== 等级信息 ====================
    
    /**
     * 当前等级ID
     */
    private Long currentLevelId;
    
    /**
     * 当前等级编码
     */
    private String currentLevelCode;
    
    /**
     * 当前等级名称
     */
    private String currentLevelName;
    
    /**
     * 当前等级图标
     */
    private String currentLevelIcon;
    
    // ==================== 成长值系统 ====================
    
    /**
     * 当前成长值
     */
    private Integer currentGrowthValue;
    
    /**
     * 累计成长值 - 历史累计，不降级时不会减少
     */
    private Integer totalGrowthValue;
    
    /**
     * 升级所需成长值
     * 达到下一等级所需成长值
     */
    private Integer nextLevelGrowthValue;
    
    /**
     * 升级进度百分比
     * 0-100
     */
    private Integer upgradeProgress;
    
    // ==================== 积分系统 ====================
    
    /**
     * 当前可用积分
     */
    private Integer availablePoints;
    
    /**
     * 累计获取积分 - 历史累计
     */
    private Integer totalEarnedPoints;
    
    /**
     * 累计消费积分
     */
    private Integer totalUsedPoints;
    
    /**
     * 即将过期积分
     */
    private Integer expiringPoints;
    
    /**
     * 即将过期时间
     */
    private LocalDateTime expiringTime;
    
    // ==================== 消费统计 ====================
    
    /**
     * 累计消费金额
     */
    private BigDecimal totalSpendAmount;
    
    /**
     * 累计消费订单数
     */
    private Integer totalOrderCount;
    
    /**
     * 本年消费金额 - 用于保级计算
     */
    private BigDecimal yearSpendAmount;
    
    /**
     * 本年消费订单数
     */
    private Integer yearOrderCount;
    
    /**
     * 上次消费时间
     */
    private LocalDateTime lastSpendTime;
    
    /**
     * 平均客单价
     */
    private BigDecimal avgOrderAmount;
    
    // ==================== 有效期信息 ====================
    
    /**
     * 会员开始时间
     */
    private LocalDateTime memberStartTime;
    
    /**
     * 会员到期时间
     */
    private LocalDateTime memberExpireTime;
    
    /**
     * 等级获取时间
     */
    private LocalDateTime levelObtainTime;
    
    /**
     * 等级到期时间
     */
    private LocalDateTime levelExpireTime;
    
    /**
     * 保级评估时间
     */
    private LocalDateTime retainAssessmentTime;
    
    /**
     * 是否处于宽限期
     */
    private Boolean inGracePeriod;
    
    /**
     * 宽限期结束时间
     */
    private LocalDateTime gracePeriodEndTime;
    
    // ==================== 来源与邀请 ====================
    
    /**
     * 成为会员来源
     * 1: 自主开通
     * 2: 消费自动成为
     * 3: 邀请加入
     * 4: 活动赠送
     * 5: 积分兑换
     * 6: 付费开通
     */
    private Integer joinSource;
    
    /**
     * 邀请人ID
     */
    private Long inviterId;
    
    /**
     * 成功邀请人数 - 用于升级计算
     */
    private Integer inviteCount;
    
    // ==================== 状态 ====================
    
    /**
     * 会员状态
     * 0: 已失效
     * 1: 正常
     * 2: 冻结
     * 3: 保级宽限期
     */
    private Integer status;
    
    /**
     * 是否自动续期
     * true: 到期自动评估保级
     */
    private Boolean autoRenew;
    
    // ==================== 扩展 ====================
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 版本号
     */
    @Version
    private Integer version;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ==================== 便捷方法 ====================
    
    /**
     * 检查会员是否有效
     */
    public boolean isValid() {
        if (status != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(memberExpireTime);
    }
    
    /**
     * 检查是否即将过期（30天内）
     */
    public boolean isExpiringSoon() {
        if (status != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysLater = now.plusDays(30);
        return memberExpireTime.isBefore(thirtyDaysLater) && memberExpireTime.isAfter(now);
    }
    
    /**
     * 计算升级还需成长值
     */
    public int getGrowthValueToNextLevel() {
        return Math.max(0, nextLevelGrowthValue - currentGrowthValue);
    }
    
    /**
     * 添加成长值
     */
    public void addGrowthValue(int value) {
        this.currentGrowthValue += value;
        this.totalGrowthValue += value;
        updateUpgradeProgress();
    }
    
    /**
     * 添加积分
     */
    public void addPoints(int points) {
        this.availablePoints += points;
        this.totalEarnedPoints += points;
    }
    
    /**
     * 消费积分
     */
    public boolean deductPoints(int points) {
        if (availablePoints < points) {
            return false;
        }
        this.availablePoints -= points;
        this.totalUsedPoints += points;
        return true;
    }
    
    /**
     * 更新升级进度
     */
    private void updateUpgradeProgress() {
        if (nextLevelGrowthValue <= currentGrowthValue) {
            this.upgradeProgress = 100;
        } else {
            this.upgradeProgress = (int) ((currentGrowthValue * 100L) / nextLevelGrowthValue);
        }
    }
    
    /**
     * 更新消费统计
     */
    public void updateSpendStats(BigDecimal orderAmount) {
        this.totalSpendAmount = this.totalSpendAmount.add(orderAmount);
        this.totalOrderCount++;
        this.yearSpendAmount = this.yearSpendAmount.add(orderAmount);
        this.yearOrderCount++;
        this.lastSpendTime = LocalDateTime.now();
        this.avgOrderAmount = this.totalSpendAmount.divide(
            new BigDecimal(this.totalOrderCount), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case 0: return "已失效";
            case 1: return "正常";
            case 2: return "冻结";
            case 3: return "保级宽限期";
            default: return "未知";
        }
    }
}
