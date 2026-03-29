package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 运力负载响应
 */
@Schema(description = "运力负载响应")
public class CapacityLoadResponse {
    
    @Schema(description = "围栏ID")
    private String geofenceId;
    
    @Schema(description = "围栏名称")
    private String geofenceName;
    
    @Schema(description = "当前订单数")
    private Integer currentOrders;
    
    @Schema(description = "在线服务人员数")
    private Integer onlineStaffCount;
    
    @Schema(description = "空闲服务人员数")
    private Integer idleStaffCount;
    
    @Schema(description = "忙碌服务人员数")
    private Integer busyStaffCount;
    
    @Schema(description = "平均负载率(%) 0-100")
    private Integer averageLoadRate;
    
    @Schema(description = "预计等待时间(分钟)")
    private Integer estimatedWaitTime;
    
    @Schema(description = "负载状态：1-空闲，2-正常，3-繁忙，4-超载")
    private Integer loadStatus;
    
    @Schema(description = "预警阈值")
    private Integer alertThreshold;
    
    @Schema(description = "数据更新时间")
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }
    
    public String getGeofenceName() { return geofenceName; }
    public void setGeofenceName(String geofenceName) { this.geofenceName = geofenceName; }
    
    public Integer getCurrentOrders() { return currentOrders; }
    public void setCurrentOrders(Integer currentOrders) { this.currentOrders = currentOrders; }
    
    public Integer getOnlineStaffCount() { return onlineStaffCount; }
    public void setOnlineStaffCount(Integer onlineStaffCount) { this.onlineStaffCount = onlineStaffCount; }
    
    public Integer getIdleStaffCount() { return idleStaffCount; }
    public void setIdleStaffCount(Integer idleStaffCount) { this.idleStaffCount = idleStaffCount; }
    
    public Integer getBusyStaffCount() { return busyStaffCount; }
    public void setBusyStaffCount(Integer busyStaffCount) { this.busyStaffCount = busyStaffCount; }
    
    public Integer getAverageLoadRate() { return averageLoadRate; }
    public void setAverageLoadRate(Integer averageLoadRate) { this.averageLoadRate = averageLoadRate; }
    
    public Integer getEstimatedWaitTime() { return estimatedWaitTime; }
    public void setEstimatedWaitTime(Integer estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }
    
    public Integer getLoadStatus() { return loadStatus; }
    public void setLoadStatus(Integer loadStatus) { this.loadStatus = loadStatus; }
    
    public Integer getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(Integer alertThreshold) { this.alertThreshold = alertThreshold; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
