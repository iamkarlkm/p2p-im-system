package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路径规划响应
 */
@Schema(description = "路径规划响应")
public class PathPlanningResponse {
    
    @Schema(description = "路径ID")
    private String pathId;
    
    @Schema(description = "总距离(米)")
    private Integer totalDistance;
    
    @Schema(description = "预计耗时(分钟)")
    private Integer estimatedDuration;
    
    @Schema(description = "路径点列表")
    private List<PathPoint> pathPoints;
    
    @Schema(description = "路况信息")
    private String trafficInfo;
    
    @Schema(description = "规划策略")
    private Integer strategy;
    
    @Schema(description = "路径polyline")
    private String polyline;
    
    // Getters and Setters
    public String getPathId() { return pathId; }
    public void setPathId(String pathId) { this.pathId = pathId; }
    
    public Integer getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Integer totalDistance) { this.totalDistance = totalDistance; }
    
    public Integer getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Integer estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public List<PathPoint> getPathPoints() { return pathPoints; }
    public void setPathPoints(List<PathPoint> pathPoints) { this.pathPoints = pathPoints; }
    
    public String getTrafficInfo() { return trafficInfo; }
    public void setTrafficInfo(String trafficInfo) { this.trafficInfo = trafficInfo; }
    
    public Integer getStrategy() { return strategy; }
    public void setStrategy(Integer strategy) { this.strategy = strategy; }
    
    public String getPolyline() { return polyline; }
    public void setPolyline(String polyline) { this.polyline = polyline; }
    
    /**
     * 路径点
     */
    @Schema(description = "路径点")
    public static class PathPoint {
        @Schema(description = "经度")
        private BigDecimal longitude;
        
        @Schema(description = "纬度")
        private BigDecimal latitude;
        
        @Schema(description = "路段距离(米)")
        private Integer distance;
        
        @Schema(description = "预计耗时(秒)")
        private Integer duration;
        
        @Schema(description = "动作：直行/左转/右转等")
        private String action;
        
        @Schema(description = "道路名称")
        private String roadName;
        
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        
        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }
        
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getRoadName() { return roadName; }
        public void setRoadName(String roadName) { this.roadName = roadName; }
    }
}
