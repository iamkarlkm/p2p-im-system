package com.im.local.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

/**
 * 路线优化结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteOptimizationResult {
    
    private Long riderId;
    private List<RoutePointDTO> points;
    private Double totalDistance;
    private Duration estimatedDuration;
    private Integer orderCount;
    private String routePolyline;
    
    public static RouteOptimizationResult empty() {
        return RouteOptimizationResult.builder()
            .points(List.of())
            .totalDistance(0.0)
            .estimatedDuration(Duration.ZERO)
            .orderCount(0)
            .build();
    }
}
