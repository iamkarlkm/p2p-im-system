package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 匹配优惠券VO
 */
@Data
@Schema(description = "匹配优惠券")
public class MatchedCouponVO {
    
    @Schema(description = "优惠券ID")
    private Long couponId;
    
    @Schema(description = "优惠券名称")
    private String name;
    
    @Schema(description = "优惠券类型")
    private String type;
    
    @Schema(description = "优惠金额")
    private BigDecimal amount;
    
    @Schema(description = "匹配得分")
    private BigDecimal matchScore;
    
    @Schema(description = "匹配原因")
    private String matchReason;
}
