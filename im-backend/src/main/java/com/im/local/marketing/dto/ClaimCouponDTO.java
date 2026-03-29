package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 领取优惠券请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCouponDTO {
    
    @NotBlank(message = "优惠券ID不能为空")
    private String couponId;
    
    /**
     * 领取渠道
     */
    private String claimChannel;
    
    /**
     * 领取来源
     * SELF: 自主领取
     * SHARE: 分享获得
     * GIFT: 好友赠送
     * PUSH: 推送
     */
    private String claimSource;
    
    /**
     * 赠送人ID（如果是好友赠送）
     */
    private String giftFromUserId;
    
    /**
     * 地理位置（用于LBS限制验证）
     */
    private Double userLng;
    
    /**
     * 地理位置（用于LBS限制验证）
     */
    private Double userLat;
    
    /**
     * 城市代码
     */
    private String cityCode;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
}
