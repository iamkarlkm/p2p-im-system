package com.im.backend.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券关联实体 - 用户领取的优惠券记录
 * 
 * 功能说明:
 * 1. 记录用户领取优惠券的完整生命周期
 * 2. 支持多种优惠券状态流转（未使用/已使用/已过期/已转赠）
 * 3. 关联原始优惠券模板和用户订单
 * 4. 支持转赠记录追踪
 * 5. 支持使用限制校验
 * 
 * 技术要点:
 * - 状态机驱动，确保状态流转正确
 * - 乐观锁防止并发使用冲突
 * - 独立过期时间计算（动态有效期）
 * 
 * 状态流转:
 * 未使用 → 已使用（下单核销）
 * 未使用 → 已过期（超时未用）
 * 未使用 → 已转赠（转给好友）
 * 未使用 → 已冻结（订单锁定中）
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础关联字段 ====================
    
    /**
     * 用户优惠券ID - 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 用户ID - 券归属用户
     */
    private Long userId;
    
    /**
     * 优惠券模板ID - 关联Coupon表
     */
    private Long couponId;
    
    /**
     * 优惠券编码 - 冗余存储
     */
    private String couponCode;
    
    /**
     * 优惠券名称 - 冗余存储
     */
    private String couponName;
    
    // ==================== 状态字段 ====================
    
    /**
     * 优惠券状态
     * 0: 未使用 - 可正常使用
     * 1: 已使用 - 已核销
     * 2: 已过期 - 超时未用
     * 3: 已转赠 - 转给好友
     * 4: 已冻结 - 订单锁定中
     * 5: 已作废 - 商户操作作废
     * 6: 退款退回 - 订单退款后退回
     */
    private Integer status;
    
    /**
     * 状态变更历史 - JSON数组记录
     * [{"from": 0, "to": 1, "time": "2026-03-28T10:00:00", "reason": "订单核销"}]
     */
    private String statusHistory;
    
    // ==================== 时间字段 ====================
    
    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 有效期开始时间 - 动态计算
     * 若为固定有效期，则等于Coupon.useStartTime
     * 若为动态有效期，则等于receiveTime
     */
    private LocalDateTime validStartTime;
    
    /**
     * 有效期结束时间 - 动态计算
     * 动态有效期: receiveTime + validDays
     * 固定有效期: Coupon.useEndTime
     */
    private LocalDateTime validEndTime;
    
    /**
     * 使用时间 - 核销时间
     */
    private LocalDateTime useTime;
    
    /**
     * 过期时间 - 实际过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 转赠时间
     */
    private LocalDateTime transferTime;
    
    // ==================== 使用关联字段 ====================
    
    /**
     * 使用订单ID - 核销时记录
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 订单金额 - 使用时订单总金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 实际优惠金额 - 本次使用的优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 商户ID - 使用时记录
     */
    private Long usedMerchantId;
    
    /**
     * POI ID - 使用时记录
     */
    private Long usedPoiId;
    
    // ==================== 转赠字段 ====================
    
    /**
     * 原始领取人ID - 防止循环转赠追踪
     */
    private Long originalUserId;
    
    /**
     * 转赠来源用户ID - 谁转赠给我的
     */
    private Long transferredFromUserId;
    
    /**
     * 转赠目标用户ID - 我转赠给了谁
     */
    private Long transferredToUserId;
    
    /**
     * 转赠次数 - 限制转赠次数
     */
    private Integer transferCount;
    
    /**
     * 最大转赠次数限制
     */
    private Integer maxTransferCount;
    
    // ==================== 提醒字段 ====================
    
    /**
     * 是否已发送过期提醒
     */
    private Boolean expireReminderSent;
    
    /**
     * 过期提醒发送时间
     */
    private LocalDateTime expireReminderTime;
    
    /**
     * 是否已发送即将过期提醒（提前3天）
     */
    private Boolean upcomingExpireReminderSent;
    
    // ==================== 来源追踪字段 ====================
    
    /**
     * 领取来源类型
     * 1: 主动领取
     * 2: 地理围栏触发
     * 3: 分享获得
     * 4: 活动赠送
     * 5: 消费返券
     * 6: 签到获得
     * 7: 会员权益
     * 8: 邀请奖励
     * 9: 积分兑换
     * 10: 转赠获得
     */
    private Integer receiveSource;
    
    /**
     * 领取来源详情 - JSON格式
     * 存储具体来源信息，如分享者ID、活动ID等
     */
    private String receiveSourceDetail;
    
    /**
     * 活动ID - 关联营销活动
     */
    private Long activityId;
    
    /**
     * 活动名称
     */
    private String activityName;
    
    // ==================== 地理位置字段 ====================
    
    /**
     * 领取时经度 - 用于分析
     */
    private BigDecimal receiveLongitude;
    
    /**
     * 领取时纬度
     */
    private BigDecimal receiveLatitude;
    
    /**
     * 领取时位置地址
     */
    private String receiveLocation;
    
    /**
     * 使用时经度
     */
    private BigDecimal useLongitude;
    
    /**
     * 使用时纬度
     */
    private BigDecimal useLatitude;
    
    /**
     * 使用位置与商户距离（米）- 用于防刷分析
     */
    private Integer useDistance;
    
    // ==================== 扩展字段 ====================
    
    /**
     * 使用限制快照 - JSON格式
     * 领取时记录的Coupon限制条件快照
     * 防止模板修改后影响已领取的券
     */
    private String restrictionSnapshot;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 版本号 - 乐观锁
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
     * 检查优惠券是否可用（未使用且未过期）
     */
    public boolean isAvailable() {
        if (status != 0) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(validStartTime) && now.isBefore(validEndTime);
    }
    
    /**
     * 检查是否即将过期（3天内）
     */
    public boolean isExpiringSoon() {
        if (status != 0) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        return validEndTime.isBefore(threeDaysLater) && validEndTime.isAfter(now);
    }
    
    /**
     * 获取剩余有效天数
     */
    public long getRemainingDays() {
        if (status != 0) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(validEndTime)) {
            return 0;
        }
        return java.time.Duration.between(now, validEndTime).toDays();
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case 0: return "未使用";
            case 1: return "已使用";
            case 2: return "已过期";
            case 3: return "已转赠";
            case 4: return "已冻结";
            case 5: return "已作废";
            case 6: return "退款退回";
            default: return "未知状态";
        }
    }
    
    /**
     * 获取来源描述
     */
    public String getSourceDescription() {
        switch (receiveSource) {
            case 1: return "主动领取";
            case 2: return "地理围栏触发";
            case 3: return "分享获得";
            case 4: return "活动赠送";
            case 5: return "消费返券";
            case 6: return "签到获得";
            case 7: return "会员权益";
            case 8: return "邀请奖励";
            case 9: return "积分兑换";
            case 10: return "转赠获得";
            default: return "其他";
        }
    }
    
    /**
     * 是否可转赠
     */
    public boolean canTransfer() {
        return status == 0 && 
               (maxTransferCount == null || transferCount < maxTransferCount);
    }
}
