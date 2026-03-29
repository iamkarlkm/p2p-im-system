package com.im.entity.geofence;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 用户地理围栏状态实体
 * User GeoFence State Entity
 * 
 * 功能：记录用户在各个围栏中的状态
 * 包含：进入时间、停留时长、当前状态等
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class UserGeofenceState {
    
    // ==================== 主键ID ====================
    private Long id;
    private String stateId;
    
    // ==================== 用户信息 ====================
    private Long userId;
    private String deviceId;
    private String sessionId;
    
    // ==================== 围栏信息 ====================
    private String fenceId;
    private String ruleId;
    private String poiId;
    private String poiName;
    
    // ==================== 状态信息 ====================
    private String fenceState;
    private LocalDateTime enterTime;
    private LocalDateTime exitTime;
    private LocalDateTime lastUpdateTime;
    private Long dwellSeconds;
    private Integer triggerCount;
    private LocalDateTime lastTriggerTime;
    
    // ==================== 位置信息 ====================
    private Double currentLatitude;
    private Double currentLongitude;
    private Double enterLatitude;
    private Double enterLongitude;
    private Double exitLatitude;
    private Double exitLongitude;
    private Double distanceToCenter;
    private String locationAccuracy;
    
    // ==================== 围栏层级 ====================
    private Integer fenceLevel;
    private String parentFenceId;
    private String nestedFencePath;
    
    // ==================== 群组围栏 ====================
    private String groupId;
    private Boolean isGroupFence;
    private String groupMemberStatus;
    private LocalDateTime groupArrivalNotifyTime;
    
    // ==================== 触发记录 ====================
    private String triggeredEvents;
    private String triggeredRuleIds;
    private String messageIds;
    
    // ==================== 扩展字段 ====================
    private Map<String, Object> extendedFields;
    
    // ==================== 时间戳 ====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // ==================== 构造函数 ====================
    public UserGeofenceState() {
        this.extendedFields = new HashMap<>();
        this.fenceState = STATE_OUTSIDE;
        this.dwellSeconds = 0L;
        this.triggerCount = 0;
    }
    
    public UserGeofenceState(String stateId, Long userId, String fenceId) {
        this();
        this.stateId = stateId;
        this.userId = userId;
        this.fenceId = fenceId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // ==================== 围栏状态常量 ====================
    public static final String STATE_OUTSIDE = "OUTSIDE";
    public static final String STATE_ENTERING = "ENTERING";
    public static final String STATE_INSIDE = "INSIDE";
    public static final String STATE_DWELLING = "DWELLING";
    public static final String STATE_EXITING = "EXITING";
    
    // ==================== 群组成员状态 ====================
    public static final String GROUP_STATUS_NOT_ARRIVED = "NOT_ARRIVED";
    public static final String GROUP_STATUS_ARRIVED = "ARRIVED";
    public static final String GROUP_STATUS_NEARBY = "NEARBY";
    public static final String GROUP_STATUS_LEFT = "LEFT";
    
    // ==================== Getter & Setter ====================
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStateId() {
        return stateId;
    }
    
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getFenceId() {
        return fenceId;
    }
    
    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getPoiId() {
        return poiId;
    }
    
    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }
    
    public String getPoiName() {
        return poiName;
    }
    
    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }
    
    public String getFenceState() {
        return fenceState;
    }
    
    public void setFenceState(String fenceState) {
        this.fenceState = fenceState;
    }
    
    public LocalDateTime getEnterTime() {
        return enterTime;
    }
    
    public void setEnterTime(LocalDateTime enterTime) {
        this.enterTime = enterTime;
    }
    
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public Long getDwellSeconds() {
        return dwellSeconds;
    }
    
    public void setDwellSeconds(Long dwellSeconds) {
        this.dwellSeconds = dwellSeconds;
    }
    
    public Integer getTriggerCount() {
        return triggerCount;
    }
    
    public void setTriggerCount(Integer triggerCount) {
        this.triggerCount = triggerCount;
    }
    
    public LocalDateTime getLastTriggerTime() {
        return lastTriggerTime;
    }
    
    public void setLastTriggerTime(LocalDateTime lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public Double getEnterLatitude() {
        return enterLatitude;
    }
    
    public void setEnterLatitude(Double enterLatitude) {
        this.enterLatitude = enterLatitude;
    }
    
    public Double getEnterLongitude() {
        return enterLongitude;
    }
    
    public void setEnterLongitude(Double enterLongitude) {
        this.enterLongitude = enterLongitude;
    }
    
    public Double getExitLatitude() {
        return exitLatitude;
    }
    
    public void setExitLatitude(Double exitLatitude) {
        this.exitLatitude = exitLatitude;
    }
    
    public Double getExitLongitude() {
        return exitLongitude;
    }
    
    public void setExitLongitude(Double exitLongitude) {
        this.exitLongitude = exitLongitude;
    }
    
    public Double getDistanceToCenter() {
        return distanceToCenter;
    }
    
    public void setDistanceToCenter(Double distanceToCenter) {
        this.distanceToCenter = distanceToCenter;
    }
    
    public String getLocationAccuracy() {
        return locationAccuracy;
    }
    
    public void setLocationAccuracy(String locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }
    
    public Integer getFenceLevel() {
        return fenceLevel;
    }
    
    public void setFenceLevel(Integer fenceLevel) {
        this.fenceLevel = fenceLevel;
    }
    
    public String getParentFenceId() {
        return parentFenceId;
    }
    
    public void setParentFenceId(String parentFenceId) {
        this.parentFenceId = parentFenceId;
    }
    
    public String getNestedFencePath() {
        return nestedFencePath;
    }
    
    public void setNestedFencePath(String nestedFencePath) {
        this.nestedFencePath = nestedFencePath;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public Boolean getIsGroupFence() {
        return isGroupFence;
    }
    
    public void setIsGroupFence(Boolean isGroupFence) {
        this.isGroupFence = isGroupFence;
    }
    
    public String getGroupMemberStatus() {
        return groupMemberStatus;
    }
    
    public void setGroupMemberStatus(String groupMemberStatus) {
        this.groupMemberStatus = groupMemberStatus;
    }
    
    public LocalDateTime getGroupArrivalNotifyTime() {
        return groupArrivalNotifyTime;
    }
    
    public void setGroupArrivalNotifyTime(LocalDateTime groupArrivalNotifyTime) {
        this.groupArrivalNotifyTime = groupArrivalNotifyTime;
    }
    
    public String getTriggeredEvents() {
        return triggeredEvents;
    }
    
    public void setTriggeredEvents(String triggeredEvents) {
        this.triggeredEvents = triggeredEvents;
    }
    
    public String getTriggeredRuleIds() {
        return triggeredRuleIds;
    }
    
    public void setTriggeredRuleIds(String triggeredRuleIds) {
        this.triggeredRuleIds = triggeredRuleIds;
    }
    
    public String getMessageIds() {
        return messageIds;
    }
    
    public void setMessageIds(String messageIds) {
        this.messageIds = messageIds;
    }
    
    public Map<String, Object> getExtendedFields() {
        return extendedFields;
    }
    
    public void setExtendedFields(Map<String, Object> extendedFields) {
        this.extendedFields = extendedFields;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 更新停留时长
     */
    public void updateDwellSeconds() {
        if (enterTime != null) {
            this.dwellSeconds = java.time.Duration.between(enterTime, LocalDateTime.now()).getSeconds();
        }
    }
    
    /**
     * 增加触发计数
     */
    public void incrementTriggerCount() {
        if (this.triggerCount == null) {
            this.triggerCount = 0;
        }
        this.triggerCount++;
        this.lastTriggerTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否满足最小停留时间
     */
    public boolean hasMinDwellTime(Integer minSeconds) {
        if (minSeconds == null || minSeconds <= 0) {
            return true;
        }
        return dwellSeconds != null && dwellSeconds >= minSeconds;
    }
    
    /**
     * 检查冷却时间是否已过
     */
    public boolean isCooldownExpired(Integer cooldownMinutes) {
        if (cooldownMinutes == null || cooldownMinutes <= 0) {
            return true;
        }
        if (lastTriggerTime == null) {
            return true;
        }
        long minutesSince = java.time.Duration.between(lastTriggerTime, LocalDateTime.now()).toMinutes();
        return minutesSince >= cooldownMinutes;
    }
    
    /**
     * 记录触发事件
     */
    public void recordTriggeredEvent(String event, String ruleId, String messageId) {
        if (this.triggeredEvents == null) {
            this.triggeredEvents = event;
        } else {
            this.triggeredEvents += "," + event;
        }
        if (this.triggeredRuleIds == null) {
            this.triggeredRuleIds = ruleId;
        } else {
            this.triggeredRuleIds += "," + ruleId;
        }
        if (this.messageIds == null) {
            this.messageIds = messageId;
        } else {
            this.messageIds += "," + messageId;
        }
    }
    
    /**
     * 标记进入围栏
     */
    public void markEntered(Double latitude, Double longitude) {
        this.fenceState = STATE_INSIDE;
        this.enterTime = LocalDateTime.now();
        this.enterLatitude = latitude;
        this.enterLongitude = longitude;
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.dwellSeconds = 0L;
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    /**
     * 标记离开围栏
     */
    public void markExited(Double latitude, Double longitude) {
        this.fenceState = STATE_OUTSIDE;
        this.exitTime = LocalDateTime.now();
        this.exitLatitude = latitude;
        this.exitLongitude = longitude;
        updateDwellSeconds();
        this.lastUpdateTime = LocalDateTime.now();
    }
    
    /**
     * 更新当前位置
     */
    public void updateLocation(Double latitude, Double longitude, String accuracy) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.locationAccuracy = accuracy;
        this.lastUpdateTime = LocalDateTime.now();
        if (STATE_INSIDE.equals(this.fenceState) || STATE_DWELLING.equals(this.fenceState)) {
            updateDwellSeconds();
        }
    }
    
    @Override
    public String toString() {
        return "UserGeofenceState{" +
                "stateId='" + stateId + '\'' +
                ", userId=" + userId +
                ", fenceId='" + fenceId + '\'' +
                ", fenceState='" + fenceState + '\'' +
                ", dwellSeconds=" + dwellSeconds +
                '}';
    }
}
