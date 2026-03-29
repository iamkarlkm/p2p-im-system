package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐商品VO
 */
@Data
@Schema(description = "推荐商品")
public class RecommendedProductVO {
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "商品名称")
    private String name;
    
    @Schema(description = "商品图片")
    private String image;
    
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    
    @Schema(description = "现价")
    private BigDecimal currentPrice;
    
    @Schema(description = "推荐理由")
    private String recommendReason;
    
    @Schema(description = "推荐得分")
    private BigDecimal score;
}
