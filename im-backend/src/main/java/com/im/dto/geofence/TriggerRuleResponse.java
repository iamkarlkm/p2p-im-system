package com.im.dto.geofence;

import java.time.LocalDateTime;

/**
 * 触发规则响应DTO
 * Trigger Rule Response DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class TriggerRuleResponse {
    
    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private String ruleType;
    
    private String poiId;
    private String poiName;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radius;
    private String fenceType;
    
    private String triggerEvent;
    private Integer minStaySeconds;
    private Integer maxTriggerCount;
    private Integer triggerCooldownMinutes;
    
    private String messageType;
    private String messageTitle;
    private String actionType;
    private String actionUrl;
    
    private String status;
    private Long triggerCount;
    private Long messageSentCount;
    private Long messageOpenCount;
    private Double conversionRate;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleDescription() { return ruleDescription; }
    public void setRuleDescription(String ruleDescription) { this.ruleDescription = ruleDescription; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }
    public String getPoiName() { return poiName; }
    public void setPoiName(String poiName) { this.poiName = poiName; }
    public Double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }
    public Double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }
    public Double getRadius() { return radius; }
    public void setRadius(Double radius) { this.radius = radius; }
    public String getFenceType() { return fenceType; }
    public void setFenceType(String fenceType) { this.fenceType = fenceType; }
    public String getTriggerEvent() { return triggerEvent; }
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }
    public Integer getMinStaySeconds() { return minStaySeconds; }
    public void setMinStaySeconds(Integer minStaySeconds) { this.minStaySeconds = minStaySeconds; }
    public Integer getMaxTriggerCount() { return maxTriggerCount; }
    public void setMaxTriggerCount(Integer maxTriggerCount) { this.maxTriggerCount = maxTriggerCount; }
    public Integer getTriggerCooldownMinutes() { return triggerCooldownMinutes; }
    public void setTriggerCooldownMinutes(Integer triggerCooldownMinutes) { this.triggerCooldownMinutes = triggerCooldownMinutes; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTriggerCount() { return triggerCount; }
    public void setTriggerCount(Long triggerCount) { this.triggerCount = triggerCount; }
    public Long getMessageSentCount() { return messageSentCount; }
    public void setMessageSentCount(Long messageSentCount) { this.messageSentCount = messageSentCount; }
    public Long getMessageOpenCount() { return messageOpenCount; }
    public void setMessageOpenCount(Long messageOpenCount) { this.messageOpenCount = messageOpenCount; }
    public Double getConversionRate() { return conversionRate; }
    public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
