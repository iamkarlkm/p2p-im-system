package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券列表响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class CouponListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer couponType;
    private String typeDescription;
    private BigDecimal discountValue;
    private BigDecimal minSpend;
    private String merchantName;
    private Integer remainingStock;
    private String coverImage;
    private List<String> tags;
    private Boolean isTop;
    private Integer timeStatus; // 0-即将开始 1-进行中 2-已结束
    private LocalDateTime issueStartTime;
    private LocalDateTime issueEndTime;
}
