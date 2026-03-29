// 评分分布DTO
package com.im.dto.merchantanalytics;

import lombok.Data;

@Data
public class RatingDistributionDTO {
    private Integer fiveStarCount;
    private Integer fourStarCount;
    private Integer threeStarCount;
    private Integer twoStarCount;
    private Integer oneStarCount;
    
    private Double fiveStarRate;
    private Double fourStarRate;
    private Double threeStarRate;
    private Double twoStarRate;
    private Double oneStarRate;
}
