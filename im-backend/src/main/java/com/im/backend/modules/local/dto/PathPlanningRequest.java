package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 路径规划请求
 */
@Schema(description = "路径规划请求")
public class PathPlanningRequest {
    
    @Schema(description = "起点经度")
    private BigDecimal startLongitude;
    
    @Schema(description = "起点纬度")
    private BigDecimal startLatitude;
    
    @Schema(description = "终点经度")
    private BigDecimal endLongitude;
    
    @Schema(description = "终点纬度")
    private BigDecimal endLatitude;
    
    @Schema(description = "途经点")
    private String waypoints;
    
    @Schema(description = "规划策略：1-最快，2-最短，3-避堵，4-经济")
    private Integer strategy;
    
    @Schema(description = "预计出发时间")
    private LocalDateTime departureTime;
    
    // Getters and Setters
    public BigDecimal getStartLongitude() { return startLongitude; }
    public void setStartLongitude(BigDecimal startLongitude) { this.startLongitude = startLongitude; }
    
    public BigDecimal getStartLatitude() { return startLatitude; }
    public void setStartLatitude(BigDecimal startLatitude) { this.startLatitude = startLatitude; }
    
    public BigDecimal getEndLongitude() { return endLongitude; }
    public void setEndLongitude(BigDecimal endLongitude) { this.endLongitude = endLongitude; }
    
    public BigDecimal getEndLatitude() { return endLatitude; }
    public void setEndLatitude(BigDecimal endLatitude) { this.endLatitude = endLatitude; }
    
    public String getWaypoints() { return waypoints; }
    public void setWaypoints(String waypoints) { this.waypoints = waypoints; }
    
    public Integer getStrategy() { return strategy; }
    public void setStrategy(Integer strategy) { this.strategy = strategy; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
}
