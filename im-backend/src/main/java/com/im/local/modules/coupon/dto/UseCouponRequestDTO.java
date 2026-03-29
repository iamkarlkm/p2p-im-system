package com.im.local.modules.coupon.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 使用优惠券请求DTO
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class UseCouponRequestDTO {

    /**
     * 用户优惠券ID
     */
    @NotNull(message = "优惠券ID不能为空")
    private Long userCouponId;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单金额
     */
    @NotNull(message = "订单金额不能为空")
    private BigDecimal orderAmount;

    /**
     * 商品IDs（用于校验适用范围）
     */
    private List<Long> productIds;

    /**
     * 商户ID
     */
    private Long merchantId;
}
