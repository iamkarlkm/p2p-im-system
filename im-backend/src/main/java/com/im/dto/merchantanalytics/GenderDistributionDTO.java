// 性别分布DTO
package com.im.dto.merchantanalytics;

import lombok.Data;

@Data
public class GenderDistributionDTO {
    private Integer maleCount;
    private Integer femaleCount;
    private Integer unknownCount;
    
    private Double maleRatio;
    private Double femaleRatio;
    private Double unknownRatio;
}
