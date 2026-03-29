package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 个性化优惠VO
 */
@Data
@Schema(description = "个性化优惠")
public class PersonalizedOfferVO {
    
    @Schema(description = "优惠标题")
    private String title;
    
    @Schema(description = "优惠内容")
    private String content;
    
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;
    
    @Schema(description = "折扣率")
    private BigDecimal discountRate;
    
    @Schema(description = "适用商品类目")
    private List<String> applicableCategories;
}
