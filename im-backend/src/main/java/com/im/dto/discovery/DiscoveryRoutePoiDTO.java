package com.im.dto.discovery;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 路线POI节点DTO
 */
@Data
public class DiscoveryRoutePoiDTO {
    
    private Long id;
    private Long poiId;
    private Integer sequence;
    private String poiName;
    private String poiType;
    private String poiTypeName;
    private String address;
    private Double distanceFromPrev;
    private String distanceText;
    private Integer estimatedMinutesFromPrev;
    private Integer suggestedStayMinutes;
    private BigDecimal estimatedCost;
    private String recommendText;
    private String featureIntro;
    private String mustTryItems;
    private String imageUrl;
    private String businessHours;
    private BigDecimal rating;
    private BigDecimal avgPrice;
}
