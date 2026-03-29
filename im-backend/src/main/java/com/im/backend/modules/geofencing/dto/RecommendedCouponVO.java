package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐优惠券VO
 */
@Data
@Schema(description = "推荐优惠券")
public class RecommendedCouponVO {
    
    @Schema(description = "优惠券ID")
    private Long couponId;
    
    @Schema(description = "优惠券名称")
    private String name;
    
    @Schema(description = "优惠券类型")
    private String type;
    
    @Schema(description = "优惠金额")
    private BigDecimal amount;
    
    @Schema(description = "最低消费金额")
    private BigDecimal minSpend;
    
    @Schema(description = "有效期至")
    private LocalDateTime validUntil;
    
    @Schema(description = "使用说明")
    private String usageNote;
}
