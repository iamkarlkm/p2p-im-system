package com.im.backend.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级实体 - 本地生活会员成长体系
 * 
 * 功能说明:
 * 1. 定义平台统一会员等级体系
 * 2. 支持商户自定义会员等级
 * 3. 等级权益配置（折扣率、专属券、优先服务等）
 * 4. 积分获取与消费规则
 * 5. 等级有效期与保级机制
 * 
 * 等级设计:
 * - 普通会员: 注册即成为
 * - 银卡会员: 消费满1000或积分满1000
 * - 金卡会员: 消费满5000或积分满5000
 * - 白金会员: 消费满20000或积分满20000
 * - 黑卡会员: 邀请制/消费满100000
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_member_level")
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础字段 ====================
    
    /**
     * 等级ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 等级编码 - 唯一标识
     * 如: NORMAL, SILVER, GOLD, PLATINUM, BLACK
     */
    private String levelCode;
    
    /**
     * 等级名称
     */
    private String levelName;
    
    /**
     * 等级图标URL
     */
    private String levelIcon;
    
    /**
     * 等级颜色 - 前端展示用
     */
    private String levelColor;
    
    /**
     * 等级描述
     */
    private String description;
    
    // ==================== 等级类型 ====================
    
    /**
     * 等级类型
     * 1: 平台统一等级
     * 2: 商户自定义等级
     */
    private Integer levelType;
    
    /**
     * 商户ID - 商户自定义等级时使用
     * 0表示平台等级
     */
    private Long merchantId;
    
    /**
     * 商户名称
     */
    private String merchantName;
    
    // ==================== 等级条件配置 ====================
    
    /**
     * 升级所需消费金额
     * 累计消费达到此金额自动升级
     * 0表示不通过消费升级
     */
    private BigDecimal upgradeSpendAmount;
    
    /**
     * 升级所需积分
     * 累计积分达到此数量自动升级
     * 0表示不通过积分升级
     */
    private Integer upgradePoints;
    
    /**
     * 升级所需订单数
     * 累计订单数达到此数量自动升级
     * 0表示不通过订单数升级
     */
    private Integer upgradeOrderCount;
    
    /**
     * 升级所需邀请人数
     * 成功邀请好友数达到此数量升级
     * 用于裂变场景
     */
    private Integer upgradeInviteCount;
    
    /**
     * 升级条件组合类型
     * 1: 满足任一条件即可
     * 2: 必须同时满足所有条件
     */
    private Integer upgradeConditionType;
    
    /**
     * 是否邀请制
     * true: 只能通过邀请或人工审核升级
     */
    private Boolean inviteOnly;
    
    // ==================== 等级权益配置 ====================
    
    /**
     * 专属折扣率
     * 0.95表示享受95折
     * 1.0表示无折扣
     */
    private BigDecimal discountRate;
    
    /**
     * 积分倍率
     * 消费1元获得积分 = 基础积分 * pointsMultiplier
     * 1.0为基准，2.0表示双倍积分
     */
    private BigDecimal pointsMultiplier;
    
    /**
     * 专属优惠券包 - JSON数组
     * 升级时自动发放的优惠券ID列表
     */
    private String upgradeCouponIds;
    
    /**
     * 每月专属优惠券 - JSON数组
     * 每月自动发放的优惠券
     */
    private String monthlyCouponIds;
    
    /**
     * 生日特权 - JSON对象
     * {"couponIds": [], "points": 100, "discount": 0.8}
     */
    private String birthdayBenefits;
    
    /**
     * 优先服务权益
     * true: 享受排队优先、客服优先等
     */
    private Boolean priorityService;
    
    /**
     * 专属客服权益
     * true: 享受1对1专属客服
     */
    private Boolean exclusiveService;
    
    /**
     * 免费配送权益
     * true: 享受免配送费
     */
    private Boolean freeDelivery;
    
    /**
     * 延长退款权益 - 退款期限延长天数
     * 0表示不延长
     */
    private Integer extendedRefundDays;
    
    /**
     * 提前购买权益
     * 新品/活动提前X小时购买
     */
    private Integer earlyAccessHours;
    
    /**
     * 专属活动邀请
     * true: 定期收到专属活动邀请
     */
    private Boolean exclusiveEvents;
    
    // ==================== 等级有效期配置 ====================
    
    /**
     * 等级有效期类型
     * 1: 永久有效
     * 2: 按年计算（每年需保级）
     * 3: 按月计算
     * 4: 按日计算
     */
    private Integer validityType;
    
    /**
     * 等级有效期时长
     * 配合validityType使用
     * 如validityType=2, validityValue=1表示有效期1年
     */
    private Integer validityValue;
    
    /**
     * 保级所需消费金额
     * 有效期内需消费此金额才能保级
     */
    private BigDecimal retainSpendAmount;
    
    /**
     * 保级所需积分
     */
    private Integer retainPoints;
    
    /**
     * 保级所需订单数
     */
    private Integer retainOrderCount;
    
    /**
     * 降级后等级ID
     * 未保级成功降级到哪个等级
     */
    private Long downgradeLevelId;
    
    /**
     * 是否允许保级
     * true: 未达标可保级一次
     */
    private Boolean allowGracePeriod;
    
    /**
     * 保级宽限期天数
     */
    private Integer gracePeriodDays;
    
    // ==================== 展示与排序 ====================
    
    /**
     * 等级顺序 - 数值越大等级越高
     */
    private Integer levelOrder;
    
    /**
     * 是否展示
     */
    private Boolean isVisible;
    
    /**
     * 等级展示背景图
     */
    private String backgroundImage;
    
    /**
     * 权益说明 - 富文本
     */
    private String benefitsDescription;
    
    // ==================== 统计字段 ====================
    
    /**
     * 当前等级用户数
     */
    private Integer userCount;
    
    /**
     * 累计发放权益价值
     */
    private BigDecimal totalBenefitsValue;
    
    // ==================== 状态字段 ====================
    
    /**
     * 状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;
    
    /**
     * 是否默认等级
     * 新用户默认加入的等级
     */
    private Boolean isDefault;
    
    // ==================== 扩展字段 ====================
    
    /**
     * 扩展配置 - JSON格式
     */
    private String extConfig;
    
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
    
    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;
    
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
    
    // ==================== 便捷方法 ====================
    
    /**
     * 检查是否满足升级条件
     */
    public boolean meetsUpgradeCondition(BigDecimal spendAmount, Integer points, 
                                          Integer orderCount, Integer inviteCount) {
        if (inviteOnly) {
            return false;
        }
        
        boolean meetsSpend = upgradeSpendAmount.compareTo(BigDecimal.ZERO) == 0 || 
                            spendAmount.compareTo(upgradeSpendAmount) >= 0;
        boolean meetsPoints = upgradePoints == 0 || points >= upgradePoints;
        boolean meetsOrders = upgradeOrderCount == 0 || orderCount >= upgradeOrderCount;
        boolean meetsInvites = upgradeInviteCount == 0 || inviteCount >= upgradeInviteCount;
        
        if (upgradeConditionType == 1) {
            return meetsSpend || meetsPoints || meetsOrders || meetsInvites;
        } else {
            boolean hasCondition = upgradeSpendAmount.compareTo(BigDecimal.ZERO) > 0 ||
                                 upgradePoints > 0 || upgradeOrderCount > 0 || upgradeInviteCount > 0;
            if (!hasCondition) return false;
            
            boolean result = true;
            if (upgradeSpendAmount.compareTo(BigDecimal.ZERO) > 0) result = result && meetsSpend;
            if (upgradePoints > 0) result = result && meetsPoints;
            if (upgradeOrderCount > 0) result = result && meetsOrders;
            if (upgradeInviteCount > 0) result = result && meetsInvites;
            return result;
        }
    }
    
    /**
     * 计算实际折扣后价格
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        return originalPrice.multiply(discountRate);
    }
    
    /**
     * 计算应得积分
     */
    public Integer calculatePoints(BigDecimal spendAmount, Integer basePointsPerYuan) {
        return spendAmount.multiply(new BigDecimal(basePointsPerYuan))
                         .multiply(pointsMultiplier)
                         .intValue();
    }
    
    /**
     * 获取有效期描述
     */
    public String getValidityDescription() {
        switch (validityType) {
            case 1: return "永久有效";
            case 2: return validityValue + "年";
            case 3: return validityValue + "月";
            case 4: return validityValue + "天";
            default: return "未知";
        }
    }
}
