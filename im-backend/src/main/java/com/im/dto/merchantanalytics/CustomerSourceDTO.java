// 客户来源DTO
package com.im.dto.merchantanalytics;

import lombok.Data;

@Data
public class CustomerSourceDTO {
    private Integer searchSourceCount;
    private Integer recommendationSourceCount;
    private Integer directSourceCount;
    private Integer shareSourceCount;
    private Integer adSourceCount;
    
    // 占比
    private BigDecimal searchSourceRatio;
    private BigDecimal recommendationSourceRatio;
    private BigDecimal directSourceRatio;
    private BigDecimal shareSourceRatio;
    private BigDecimal adSourceRatio;
}
