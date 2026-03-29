package com.im.dto.discovery;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

/**
 * 探店推荐DTO
 */
@Data
public class DiscoveryRecommendationDTO {
    
    private Long id;
    private Long poiId;
    private String poiName;
    private String coverImage;
    private BigDecimal rating;
    private BigDecimal avgPrice;
    private String categoryName;
    private Double distance;
    private String distanceText;
    private Double recommendScore;
    private String recommendReason;
    private List<String> tags;
    private String status;
    private Boolean hasPromotion;
    private String businessHours;
    private Integer checkInCount;
}
