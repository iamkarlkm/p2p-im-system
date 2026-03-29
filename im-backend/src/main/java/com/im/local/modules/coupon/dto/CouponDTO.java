package com.im.local.modules.coupon.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券DTO
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class CouponDTO {

    private Long id;
    private Long templateId;
    private Long merchantId;
    private String name;
    private String description;
    private Integer type;
    private String typeName;
    private BigDecimal value;
    private BigDecimal minSpend;
    private BigDecimal maxDiscount;
    private Integer totalQuantity;
    private Integer receivedQuantity;
    private Integer usedQuantity;
    private Integer remainingQuantity;
    private Integer limitPerUser;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer scopeType;
    private String scopeTypeName;
    private Integer merchantScope;
    private Boolean newUserOnly;
    private Boolean stackable;
    private Integer status;
    private String statusName;
    private LocalDateTime createTime;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户Logo
     */
    private String merchantLogo;

    /**
     * 距离（米）
     */
    private Double distance;

    /**
     * 是否已领取
     */
    private Boolean hasReceived;

    /**
     * 用户已领取数量
     */
    private Integer userReceivedCount;
}
