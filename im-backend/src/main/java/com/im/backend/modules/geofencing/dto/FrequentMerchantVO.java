package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 常去商户VO
 */
@Data
@Schema(description = "常去商户")
public class FrequentMerchantVO {
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "商户名称")
    private String merchantName;
    
    @Schema(description = "到店次数")
    private Integer visitCount;
    
    @Schema(description = "总停留时长（分钟）")
    private Integer totalDwellMinutes;
    
    @Schema(description = "平均停留时长（分钟）")
    private BigDecimal avgDwellMinutes;
    
    @Schema(description = "商户类型")
    private String merchantType;
    
    @Schema(description = "商户Logo")
    private String merchantLogo;
}
