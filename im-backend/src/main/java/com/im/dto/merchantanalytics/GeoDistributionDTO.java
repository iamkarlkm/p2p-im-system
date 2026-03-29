// 地域分布DTO
package com.im.dto.merchantanalytics;

import lombok.Data;

@Data
public class GeoDistributionDTO {
    private String district;
    private Integer count;
    private Double ratio;
}
