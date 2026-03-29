package com.im.mapstream.dto;

import com.im.mapstream.enums.HeatStatus;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 热力图响应
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
public class HeatMapResponse {
    
    private String heatPointId;
    private String geohash;
    private Integer geohashLength;
    private Double longitude;
    private Double latitude;
    private Integer streamCount;
    private Integer userCount;
    private Double heatValue;
    private HeatStatus heatStatus;
    private String color;
    private Double radius;
    private Map<String, Integer> typeDistribution;
    private LocalDateTime timeWindow;
}
