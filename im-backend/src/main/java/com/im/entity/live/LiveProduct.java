package com.im.entity.live;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 直播商品实体类
 * 直播间带货商品管理
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_live_product")
public class LiveProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long productId;

    /** 直播间ID */
    private Long roomId;

    /** 商户ID */
    private Long merchantId;

    /** 商品名称 */
    private String name;

    /** 商品副标题 */
    private String subtitle;

    /** 商品主图URL */
    private String mainImage;

    /** 商品图片列表，JSON数组 */
    private String images;

    /** 商品描述（富文本） */
    private String description;

    /** 商品分类ID */
    private Long categoryId;

    /** 原价（分） */
    private Long originalPrice;

    /** 直播价（分） */
    private Long livePrice;

    /** 库存数量 */
    private Integer stock;

    /** 已售数量 */
    private Integer soldCount;

    /** 限购数量（0表示不限购） */
    private Integer limitPerUser;

    /** 商品状态：0-下架 1-上架 2-售罄 */
    private Integer status;

    /** 排序权重 */
    private Integer sortOrder;

    /** 是否推荐：0-否 1-是 */
    private Integer isRecommended;

    /** 讲解开始时间 */
    private LocalDateTime explainStartTime;

    /** 讲解结束时间 */
    private LocalDateTime explainEndTime;

    /** 是否正在讲解：0-否 1-是 */
    private Integer isExplaining;

    /** 商品详情页URL */
    private String detailUrl;

    /** 小程序页面路径 */
    private String mpPagePath;

    /** 商品规格，JSON格式 */
    private String specifications;

    /** 商品属性，JSON格式 */
    private String attributes;

    /** 运费（分），0表示包邮 */
    private Long freight;

    /** 重量（克） */
    private Integer weight;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除：0-否 1-是 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 判断是否在售
     */
    public boolean isOnSale() {
        return status != null && status == 1 && stock != null && stock > 0;
    }

    /**
     * 判断是否正在讲解
     */
    public boolean isCurrentlyExplaining() {
        return isExplaining != null && isExplaining == 1;
    }

    /**
     * 获取原价（元）
     */
    public BigDecimal getOriginalPriceYuan() {
        return originalPrice != null ? new BigDecimal(originalPrice).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * 获取直播价（元）
     */
    public BigDecimal getLivePriceYuan() {
        return livePrice != null ? new BigDecimal(livePrice).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * 计算折扣率
     */
    public BigDecimal getDiscountRate() {
        if (originalPrice == null || originalPrice == 0 || livePrice == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(livePrice * 100).divide(new BigDecimal(originalPrice), 0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 减少库存
     */
    public boolean decreaseStock(int quantity) {
        if (stock == null || stock < quantity) {
            return false;
        }
        stock -= quantity;
        soldCount = (soldCount == null ? 0 : soldCount) + quantity;
        if (stock == 0) {
            status = 2; // 售罄
        }
        return true;
    }

    /**
     * 开始讲解
     */
    public void startExplain() {
        this.isExplaining = 1;
        this.explainStartTime = LocalDateTime.now();
    }

    /**
     * 结束讲解
     */
    public void endExplain() {
        this.isExplaining = 0;
        this.explainEndTime = LocalDateTime.now();
    }

    /**
     * 检查用户购买限制
     */
    public boolean checkPurchaseLimit(int userBoughtCount) {
        if (limitPerUser == null || limitPerUser == 0) {
            return true;
        }
        return userBoughtCount < limitPerUser;
    }

    /**
     * 获取剩余可购买数量
     */
    public int getAvailableStock(int userBoughtCount) {
        int available = stock != null ? stock : 0;
        if (limitPerUser != null && limitPerUser > 0) {
            int limitRemaining = limitPerUser - userBoughtCount;
            available = Math.min(available, Math.max(0, limitRemaining));
        }
        return available;
    }
}
