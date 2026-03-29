// 营收构成分布DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueCompositionDTO {
    private BigDecimal productRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal deliveryRevenue;
    private BigDecimal bookingRevenue;
    private BigDecimal otherRevenue;
    
    private BigDecimal productRatio;
    private BigDecimal serviceRatio;
    private BigDecimal deliveryRatio;
    private BigDecimal bookingRatio;
    private BigDecimal otherRatio;
}
