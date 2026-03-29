package com.im.entity.geofence;

import java.time.LocalDateTime;

/**
 * 地理围栏触发日志实体
 * GeoFence Trigger Log Entity
 * 
 * 功能：记录每次围栏触发事件的详细日志
 * 用于：审计、分析、统计
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class GeofenceTriggerLog {
    
    // ==================== 主键ID ====================
    private Long id;
    private String logId;
    
    // ==================== 触发信息 ====================
    private String triggerEvent;
    private String ruleId;
    private String ruleName;
    private String ruleType;
    
    // ==================== 用户信息 ====================
    private Long userId;
    private String deviceId;
    private String userType;
    private Integer userLevel;
    private Boolean isMember;
    
    // ==================== 围栏信息 ====================
    private String fenceId;
    private String poiId;
    private String poiName;
    private String fenceType;
    
    // ==================== 位置信息 ====================
    private Double latitude;
    private Double longitude;
    private String locationAccuracy;
    private Double distanceToCenter;
    private String geohash;
    
    // ==================== 状态信息 ====================
    private Long dwellSeconds;
    private Integer triggerCount;
    private LocalDateTime enterTime;
    private String previousState;
    private String currentState;
    
    // ==================== 消息信息 ====================
    private Boolean messageSent;
    private String messageId;
    private String messageType;
    private LocalDateTime messageSentTime;
    private Boolean messageOpened;
    private LocalDateTime messageOpenTime;
    private Boolean converted;
    private LocalDateTime conversionTime;
    
    // ==================== 执行结果 ====================
    private String executionStatus;
    private String errorCode;
    private String errorMessage;
    private Long executionTimeMs;
    
    // ==================== 扩展数据 ====================
    private String extraData;
    private String userAgent;
    private String ipAddress;
    
    // ==================== 时间戳 ====================
    private LocalDateTime createdAt;
    
    // ==================== 执行状态 ====================
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_SKIPPED = "SKIPPED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_BLOCKED = "BLOCKED";
    
    // ==================== 构造函数 ====================
    public GeofenceTriggerLog() {
        this.createdAt = LocalDateTime.now();
        this.messageSent = false;
        this.messageOpened = false;
        this.converted = false;
    }
    
    // ==================== Getter & Setter ====================
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLogId() {
        return logId;
    }
    
    public void setLogId(String logId) {
        this.logId = logId;
    }
    
    public String getTriggerEvent() {
        return triggerEvent;
    }
    
    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    public String getRuleType() {
        return ruleType;
    }
    
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
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
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public Integer getUserLevel() {
        return userLevel;
    }
    
    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }
    
    public Boolean getIsMember() {
        return isMember;
    }
    
    public void setIsMember(Boolean isMember) {
        this.isMember = isMember;
    }
    
    public String getFenceId() {
        return fenceId;
    }
    
    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
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
    
    public String getFenceType() {
        return fenceType;
    }
    
    public void setFenceType(String fenceType) {
        this.fenceType = fenceType;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getLocationAccuracy() {
        return locationAccuracy;
    }
    
    public void setLocationAccuracy(String locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }
    
    public Double getDistanceToCenter() {
        return distanceToCenter;
    }
    
    public void setDistanceToCenter(Double distanceToCenter) {
        this.distanceToCenter = distanceToCenter;
    }
    
    public String getGeohash() {
        return geohash;
    }
    
    public void setGeohash(String geohash) {
        this.geohash = geohash;
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
    
    public LocalDateTime getEnterTime() {
        return enterTime;
    }
    
    public void setEnterTime(LocalDateTime enterTime) {
        this.enterTime = enterTime;
    }
    
    public String getPreviousState() {
        return previousState;
    }
    
    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }
    
    public String getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    
    public Boolean getMessageSent() {
        return messageSent;
    }
    
    public void setMessageSent(Boolean messageSent) {
        this.messageSent = messageSent;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public LocalDateTime getMessageSentTime() {
        return messageSentTime;
    }
    
    public void setMessageSentTime(LocalDateTime messageSentTime) {
        this.messageSentTime = messageSentTime;
    }
    
    public Boolean getMessageOpened() {
        return messageOpened;
    }
    
    public void setMessageOpened(Boolean messageOpened) {
        this.messageOpened = messageOpened;
    }
    
    public LocalDateTime getMessageOpenTime() {
        return messageOpenTime;
    }
    
    public void setMessageOpenTime(LocalDateTime messageOpenTime) {
        this.messageOpenTime = messageOpenTime;
    }
    
    public Boolean getConverted() {
        return converted;
    }
    
    public void setConverted(Boolean converted) {
        this.converted = converted;
    }
    
    public LocalDateTime getConversionTime() {
        return conversionTime;
    }
    
    public void setConversionTime(LocalDateTime conversionTime) {
        this.conversionTime = conversionTime;
    }
    
    public String getExecutionStatus() {
        return executionStatus;
    }
    
    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 标记消息已发送
     */
    public void markMessageSent(String messageId) {
        this.messageSent = true;
        this.messageId = messageId;
        this.messageSentTime = LocalDateTime.now();
    }
    
    /**
     * 标记消息已打开
     */
    public void markMessageOpened() {
        this.messageOpened = true;
        this.messageOpenTime = LocalDateTime.now();
    }
    
    /**
     * 标记已转化
     */
    public void markConverted() {
        this.converted = true;
        this.conversionTime = LocalDateTime.now();
    }
    
    /**
     * 标记执行失败
     */
    public void markFailed(String errorCode, String errorMessage) {
        this.executionStatus = STATUS_FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 计算执行时间
     */
    public void calculateExecutionTime(LocalDateTime startTime) {
        if (startTime != null) {
            this.executionTimeMs = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
        }
    }
    
    @Override
    public String toString() {
        return "GeofenceTriggerLog{" +
                "logId='" + logId + '\'' +
                ", triggerEvent='" + triggerEvent + '\'' +
                ", ruleId='" + ruleId + '\'' +
                ", userId=" + userId +
                ", poiName='" + poiName + '\'' +
                ", executionStatus='" + executionStatus + '\'' +
                '}';
    }
}
