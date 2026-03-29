package com.im.backend.modules.geofence.scheduling.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏资源调度实体
 * 用于管理围栏内的服务资源和订单分配
 */
public class GeofenceResourceScheduler implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 调度ID */
    private Long schedulerId;
    
    /** 关联围栏ID */
    private Long geofenceId;
    
    /** 围栏名称 */
    private String geofenceName;
    
    /** 服务类型：DELIVERY-配送, SERVICE-服务, RIDE-网约车 */
    private String serviceType;
    
    /** 调度策略：NEAREST-最近优先, BALANCE-负载均衡, CAPACITY-容量优先 */
    private String schedulingStrategy;
    
    /** 围栏内在线服务人员数量 */
    private Integer onlineStaffCount;
    
    /** 围栏内忙碌人员数量 */
    private Integer busyStaffCount;
    
    /** 围栏内空闲人员数量 */
    private Integer idleStaffCount;
    
    /** 围栏内待分配订单数量 */
    private Integer pendingOrderCount;
    
    /** 围栏内进行中订单数量 */
    private Integer processingOrderCount;
    
    /** 围栏容量饱和度 (0-100%) */
    private Integer saturationLevel;
    
    /** 高峰时段配置 */
    private String peakHoursConfig;
    
    /** 动态边界扩展半径(米) - 低峰期扩大范围 */
    private Integer dynamicExpandRadius;
    
    /** 调度状态：ACTIVE-活跃, PAUSED-暂停, CLOSED-关闭 */
    private String status;
    
    /** 最后统计时间 */
    private LocalDateTime lastStatsTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public Long getSchedulerId() {
        return schedulerId;
    }
    
    public void setSchedulerId(Long schedulerId) {
        this.schedulerId = schedulerId;
    }
    
    public Long getGeofenceId() {
        return geofenceId;
    }
    
    public void setGeofenceId(Long geofenceId) {
        this.geofenceId = geofenceId;
    }
    
    public String getGeofenceName() {
        return geofenceName;
    }
    
    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }
    
    public String getServiceType() {
        return serviceType;
    }
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public String getSchedulingStrategy() {
        return schedulingStrategy;
    }
    
    public void setSchedulingStrategy(String schedulingStrategy) {
        this.schedulingStrategy = schedulingStrategy;
    }
    
    public Integer getOnlineStaffCount() {
        return onlineStaffCount;
    }
    
    public void setOnlineStaffCount(Integer onlineStaffCount) {
        this.onlineStaffCount = onlineStaffCount;
    }
    
    public Integer getBusyStaffCount() {
        return busyStaffCount;
    }
    
    public void setBusyStaffCount(Integer busyStaffCount) {
        this.busyStaffCount = busyStaffCount;
    }
    
    public Integer getIdleStaffCount() {
        return idleStaffCount;
    }
    
    public void setIdleStaffCount(Integer idleStaffCount) {
        this.idleStaffCount = idleStaffCount;
    }
    
    public Integer getPendingOrderCount() {
        return pendingOrderCount;
    }
    
    public void setPendingOrderCount(Integer pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }
    
    public Integer getProcessingOrderCount() {
        return processingOrderCount;
    }
    
    public void setProcessingOrderCount(Integer processingOrderCount) {
        this.processingOrderCount = processingOrderCount;
    }
    
    public Integer getSaturationLevel() {
        return saturationLevel;
    }
    
    public void setSaturationLevel(Integer saturationLevel) {
        this.saturationLevel = saturationLevel;
    }
    
    public String getPeakHoursConfig() {
        return peakHoursConfig;
    }
    
    public void setPeakHoursConfig(String peakHoursConfig) {
        this.peakHoursConfig = peakHoursConfig;
    }
    
    public Integer getDynamicExpandRadius() {
        return dynamicExpandRadius;
    }
    
    public void setDynamicExpandRadius(Integer dynamicExpandRadius) {
        this.dynamicExpandRadius = dynamicExpandRadius;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getLastStatsTime() {
        return lastStatsTime;
    }
    
    public void setLastStatsTime(LocalDateTime lastStatsTime) {
        this.lastStatsTime = lastStatsTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}