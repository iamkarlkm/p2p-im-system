package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 附近优惠券响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class NearbyCouponResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long couponId;
    private String name;
    private Integer couponType;
    private String typeDescription;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private String merchantName;
    private Integer distance; // 距离（米）
    private String coverImage;
    private Integer remainingStock;
}
