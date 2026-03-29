package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券查询请求DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class CouponQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
    private Long merchantId;
    private Integer couponType;
    private Boolean isPlatformCoupon;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer radius;
}
