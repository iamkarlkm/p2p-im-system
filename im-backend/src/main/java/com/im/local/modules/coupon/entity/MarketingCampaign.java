package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动实体类
 * 支持满减、折扣、秒杀、拼团等多种营销玩法
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_marketing_campaign")
public class MarketingCampaign {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动类型: 1-满减活动 2-折扣活动 3-秒杀活动 4-拼团活动 5-砍价活动 6-抽奖活动
     */
    private Integer type;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动规则（JSON格式）
     */
    private String rules;

    /**
     * 满减门槛（满减活动）
     */
    private BigDecimal thresholdAmount;

    /**
     * 满减金额（满减活动）
     */
    private BigDecimal discountAmount;

    /**
     * 折扣比例（折扣活动）
     */
    private BigDecimal discountRate;

    /**
     * 秒杀商品ID
     */
    private Long seckillProductId;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    private Integer seckillStock;

    /**
     * 每人限购数量
     */
    private Integer limitPerUser;

    /**
     * 参与人数
     */
    private Integer participantCount;

    /**
     * 成交订单数
     */
    private Integer orderCount;

    /**
     * 成交金额
     */
    private BigDecimal orderAmount;

    /**
     * 活动封面图
     */
    private String coverImage;

    /**
     * 活动分享图
     */
    private String shareImage;

    /**
     * 活动状态: 0-未开始 1-进行中 2-已结束 3-已暂停
     */
    private Integer status;

    /**
     * 排序权重
     */
    private Integer sortOrder;

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
     * 创建者
     */
    private Long createBy;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    // === 业务方法 ===

    /**
     * 检查活动是否进行中
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 
            && now.isAfter(startTime) 
            && now.isBefore(endTime);
    }

    /**
     * 计算满减后的金额
     */
    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (type == 1 && amount.compareTo(thresholdAmount) >= 0) {
            return amount.subtract(discountAmount);
        } else if (type == 2) {
            return amount.multiply(discountRate);
        }
        return amount;
    }

    /**
     * 增加参与人数
     */
    public void incrementParticipant() {
        this.participantCount++;
    }

    /**
     * 增加成交
     */
    public void addOrder(BigDecimal amount) {
        this.orderCount++;
        this.orderAmount = this.orderAmount.add(amount);
    }
}
