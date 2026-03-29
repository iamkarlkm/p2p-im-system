package com.im.local.modules.coupon.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 领取优惠券请求DTO
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class ReceiveCouponRequestDTO {

    /**
     * 优惠券ID
     */
    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;

    /**
     * 领取渠道: 1-主动领取 2-系统发放 3-活动赠送 4-分享获得
     */
    private Integer receiveChannel = 1;

    /**
     * 来源用户ID（分享获得时）
     */
    private Long sourceUserId;

    /**
     * 经度（用于记录领取位置）
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;
}
