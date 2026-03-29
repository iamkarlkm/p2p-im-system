package com.im.mapstream.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 热点响应
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
public class HotSpotResponse {
    
    private String hotSpotId;
    private String name;
    private String description;
    private String hotSpotType;
    private Double longitude;
    private Double latitude;
    private Double coverageRadius;
    private Double heatValue;
    private Integer rank;
    private String trend;
    private Double trendRate;
    private List<String> keywords;
    private List<MapStreamResponse> previewStreams;
    private Integer participantCount;
    private Long totalViews;
    private LocalDateTime discoverTime;
    private String status;
}
