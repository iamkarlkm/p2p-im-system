package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 更新优惠券请求DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class UpdateCouponRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private BigDecimal maxDiscount;
    private Integer totalStock;
    private Integer perUserLimit;
    private Integer sortOrder;
    private Boolean isTop;
    private String coverImage;
    private List<String> tags;
}
