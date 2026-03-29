package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资源调度请求
 */
@Schema(description = "资源调度请求")
public class ResourceDispatchRequest {
    
    @Schema(description = "围栏ID")
    private String geofenceId;
    
    @Schema(description = "服务人员ID")
    private String staffId;
    
    @Schema(description = "调度类型：1-预调度，2-紧急调度")
    private Integer dispatchType;
    
    @Schema(description = "调度原因")
    private String reason;
    
    @Schema(description = "预计到达时间")
    private LocalDateTime estimatedArrivalTime;
    
    @Schema(description = "目标位置经度")
    private BigDecimal targetLongitude;
    
    @Schema(description = "目标位置纬度")
    private BigDecimal targetLatitude;
    
    // Getters and Setters
    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public Integer getDispatchType() { return dispatchType; }
    public void setDispatchType(Integer dispatchType) { this.dispatchType = dispatchType; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    
    public BigDecimal getTargetLongitude() { return targetLongitude; }
    public void setTargetLongitude(BigDecimal targetLongitude) { this.targetLongitude = targetLongitude; }
    
    public BigDecimal getTargetLatitude() { return targetLatitude; }
    public void setTargetLatitude(BigDecimal targetLatitude) { this.targetLatitude = targetLatitude; }
}
