package com.im.dto.discovery;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

/**
 * 探店路线DTO
 */
@Data
public class DiscoveryRouteDTO {
    
    private Long id;
    private String routeName;
    private String description;
    private String sceneTag;
    private String budgetLevel;
    private BigDecimal estimatedTotalCost;
    private Integer estimatedTotalMinutes;
    private Double totalDistance;
    private String distanceText;
    private Integer poiCount;
    private List<DiscoveryRoutePoiDTO> poiList;
    private String coverImage;
    private List<String> tags;
    private BigDecimal recommendScore;
    private Integer usageCount;
    private Boolean isFeatured;
}
