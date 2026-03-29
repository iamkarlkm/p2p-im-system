package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资源调度响应
 */
@Schema(description = "资源调度响应")
public class ResourceDispatchResponse {
    
    @Schema(description = "调度ID")
    private String dispatchId;
    
    @Schema(description = "围栏ID")
    private String geofenceId;
    
    @Schema(description = "服务人员ID")
    private String staffId;
    
    @Schema(description = "服务人员名称")
    private String staffName;
    
    @Schema(description = "调度状态")
    private Integer status;
    
    @Schema(description = "预计到达时间")
    private LocalDateTime estimatedArrivalTime;
    
    @Schema(description = "实际到达时间")
    private LocalDateTime actualArrivalTime;
    
    @Schema(description = "调度时间")
    private LocalDateTime dispatchTime;
    
    @Schema(description = "起点经度")
    private BigDecimal fromLongitude;
    
    @Schema(description = "起点纬度")
    private BigDecimal fromLatitude;
    
    @Schema(description = "终点经度")
    private BigDecimal toLongitude;
    
    @Schema(description = "终点纬度")
    private BigDecimal toLatitude;
    
    // Getters and Setters
    public String getDispatchId() { return dispatchId; }
    public void setDispatchId(String dispatchId) { this.dispatchId = dispatchId; }
    
    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    
    public LocalDateTime getActualArrivalTime() { return actualArrivalTime; }
    public void setActualArrivalTime(LocalDateTime actualArrivalTime) { this.actualArrivalTime = actualArrivalTime; }
    
    public LocalDateTime getDispatchTime() { return dispatchTime; }
    public void setDispatchTime(LocalDateTime dispatchTime) { this.dispatchTime = dispatchTime; }
    
    public BigDecimal getFromLongitude() { return fromLongitude; }
    public void setFromLongitude(BigDecimal fromLongitude) { this.fromLongitude = fromLongitude; }
    
    public BigDecimal getFromLatitude() { return fromLatitude; }
    public void setFromLatitude(BigDecimal fromLatitude) { this.fromLatitude = fromLatitude; }
    
    public BigDecimal getToLongitude() { return toLongitude; }
    public void setToLongitude(BigDecimal toLongitude) { this.toLongitude = toLongitude; }
    
    public BigDecimal getToLatitude() { return toLatitude; }
    public void setToLatitude(BigDecimal toLatitude) { this.toLatitude = toLatitude; }
}
