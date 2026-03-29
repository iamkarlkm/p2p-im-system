package com.im.backend.modules.geofence.scheduling.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏内服务人员实时状态实体
 * 记录骑手/服务人员在围栏内的实时状态和位置
 */
public class GeofenceStaffStatus implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 状态记录ID */
    private Long statusId;
    
    /** 服务人员ID */
    private Long staffId;
    
    /** 服务人员姓名 */
    private String staffName;
    
    /** 服务人员手机号 */
    private String staffPhone;
    
    /** 当前所在围栏ID */
    private Long currentGeofenceId;
    
    /** 围栏名称 */
    private String geofenceName;
    
    /** 工作状态：IDLE-空闲, READY-待命, PICKING-取货中, DELIVERING-配送中, OFFLINE-离线 */
    private String workStatus;
    
    /** 当前订单ID */
    private Long currentOrderId;
    
    /** 当前订单数 */
    private Integer currentOrderCount;
    
    /** 今日完成订单数 */
    private Integer todayCompletedCount;
    
    /** 实时经度 */
    private BigDecimal longitude;
    
    /** 实时纬度 */
    private BigDecimal latitude;
    
    /** 位置精度(米) */
    private Integer locationAccuracy;
    
    /** 速度(km/h) */
    private BigDecimal speed;
    
    /** 方向角度(0-360) */
    private Integer heading;
    
    /** 当前位置地址 */
    private String currentAddress;
    
    /** 上次位置更新时间 */
    private LocalDateTime lastLocationUpdate;
    
    /** 状态变更时间 */
    private LocalDateTime statusChangeTime;
    
    /** 预计可接单时间 */
    private LocalDateTime availableTime;
    
    /** 负载评分 (0-100, 分数越高越忙) */
    private Integer loadScore;
    
    /** 服务评分 */
    private BigDecimal rating;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public Long getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }
    
    public Long getStaffId() {
        return staffId;
    }
    
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public String getStaffPhone() {
        return staffPhone;
    }
    
    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }
    
    public Long getCurrentGeofenceId() {
        return currentGeofenceId;
    }
    
    public void setCurrentGeofenceId(Long currentGeofenceId) {
        this.currentGeofenceId = currentGeofenceId;
    }
    
    public String getGeofenceName() {
        return geofenceName;
    }
    
    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }
    
    public String getWorkStatus() {
        return workStatus;
    }
    
    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }
    
    public Long getCurrentOrderId() {
        return currentOrderId;
    }
    
    public void setCurrentOrderId(Long currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
    
    public Integer getCurrentOrderCount() {
        return currentOrderCount;
    }
    
    public void setCurrentOrderCount(Integer currentOrderCount) {
        this.currentOrderCount = currentOrderCount;
    }
    
    public Integer getTodayCompletedCount() {
        return todayCompletedCount;
    }
    
    public void setTodayCompletedCount(Integer todayCompletedCount) {
        this.todayCompletedCount = todayCompletedCount;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public Integer getLocationAccuracy() {
        return locationAccuracy;
    }
    
    public void setLocationAccuracy(Integer locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }
    
    public BigDecimal getSpeed() {
        return speed;
    }
    
    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }
    
    public Integer getHeading() {
        return heading;
    }
    
    public void setHeading(Integer heading) {
        this.heading = heading;
    }
    
    public String getCurrentAddress() {
        return currentAddress;
    }
    
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
    
    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }
    
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }
    
    public LocalDateTime getStatusChangeTime() {
        return statusChangeTime;
    }
    
    public void setStatusChangeTime(LocalDateTime statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }
    
    public LocalDateTime getAvailableTime() {
        return availableTime;
    }
    
    public void setAvailableTime(LocalDateTime availableTime) {
        this.availableTime = availableTime;
    }
    
    public Integer getLoadScore() {
        return loadScore;
    }
    
    public void setLoadScore(Integer loadScore) {
        this.loadScore = loadScore;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
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