package com.im.backend.modules.navigation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路线规划响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDTO {

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 起点信息
     */
    private LocationDTO start;

    /**
     * 终点信息
     */
    private LocationDTO end;

    /**
     * 出行方式
     */
    private String travelMode;

    /**
     * 路线策略
     */
    private String routeStrategy;

    /**
     * 总距离(米)
     */
    private Integer totalDistance;

    /**
     * 总距离(格式化)
     */
    private String totalDistanceText;

    /**
     * 预计时间(秒)
     */
    private Integer estimatedDuration;

    /**
     * 预计时间(格式化)
     */
    private String estimatedDurationText;

    /**
     * 预计到达时间
     */
    private String estimatedArrivalTime;

    /**
     * 预计费用(元)
     */
    private BigDecimal estimatedCost;

    /**
     * 路线坐标点串
     */
    private String routePolyline;

    /**
     * 路线步骤列表
     */
    private List<RouteStepDTO> steps;

    /**
     * 路线分段列表
     */
    private List<RouteSegmentDTO> segments;

    /**
     * 途经点列表
     */
    private List<WaypointDTO> waypoints;

    /**
     * 实时路况信息
     */
    private TrafficInfoDTO trafficInfo;

    /**
     * 收费信息
     */
    private TollInfoDTO tollInfo;

    /**
     * 限行信息
     */
    private RestrictionInfoDTO restrictionInfo;

    /**
     * 路线标签
     */
    private List<String> tags;

    /**
     * 位置信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private Long poiId;
        private String name;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String address;
    }

    /**
     * 路线步骤DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStepDTO {
        private Integer stepIndex;
        private String instruction;
        private Integer distance;
        private String distanceText;
        private Integer duration;
        private String durationText;
        private String turnType;
        private String roadName;
        private String actionIcon;
        private String polyline;
    }

    /**
     * 路线分段DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteSegmentDTO {
        private Integer segmentIndex;
        private String segmentType;
        private LocationDTO start;
        private LocationDTO end;
        private Integer distance;
        private Integer duration;
        private String roadType;
        private String roadName;
        private String trafficStatus;
        private Boolean tollRoad;
        private BigDecimal tollFee;
        private String turnType;
        private String turnInstruction;
    }

    /**
     * 途经点DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaypointDTO {
        private Integer index;
        private Long poiId;
        private String name;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private Integer distanceFromStart;
        private Integer estimatedArrivalTime;
    }

    /**
     * 路况信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficInfoDTO {
        private Integer smoothDistance;
        private Integer slowDistance;
        private Integer congestedDistance;
        private Integer severelyCongestedDistance;
        private String overallStatus;
        private String overallStatusText;
        private String updateTime;
    }

    /**
     * 收费信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TollInfoDTO {
        private Integer tollCount;
        private BigDecimal totalTollFee;
        private List<TollGateDTO> tollGates;
    }

    /**
     * 收费站DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TollGateDTO {
        private String name;
        private BigDecimal fee;
        private BigDecimal longitude;
        private BigDecimal latitude;
    }

    /**
     * 限行信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestrictionInfoDTO {
        private Boolean hasRestriction;
        private String restrictionType;
        private String restrictionDesc;
        private List<String> restrictedRoads;
    }
}
