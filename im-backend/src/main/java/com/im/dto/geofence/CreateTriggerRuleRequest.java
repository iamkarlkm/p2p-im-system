package com.im.dto.geofence;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 触发规则创建请求DTO
 * Create Trigger Rule Request DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class CreateTriggerRuleRequest {
    
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
    
    private Boolean timeRestricted;
    private String effectiveTimeStart;
    private String effectiveTimeEnd;
    private String effectiveDays;
    
    private String targetUserType;
    private String targetUserTags;
    private Boolean memberOnly;
    private Integer minUserLevel;
    private Boolean firstTimeVisitor;
    private Boolean regularCustomer;
    
    private String messageType;
    private String messageTitle;
    private String messageContent;
    private String messageTemplateId;
    
    private String actionType;
    private String actionUrl;
    private String actionMiniProgramId;
    private String actionMiniProgramPath;
    
    private Boolean deduplicationEnabled;
    private String deduplicationScope;
    private Integer deduplicationTimeWindow;
    
    private String priority;
    private Map<String, Object> extendedFields;
    
    // Getters and Setters
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
    public Boolean getTimeRestricted() { return timeRestricted; }
    public void setTimeRestricted(Boolean timeRestricted) { this.timeRestricted = timeRestricted; }
    public String getEffectiveTimeStart() { return effectiveTimeStart; }
    public void setEffectiveTimeStart(String effectiveTimeStart) { this.effectiveTimeStart = effectiveTimeStart; }
    public String getEffectiveTimeEnd() { return effectiveTimeEnd; }
    public void setEffectiveTimeEnd(String effectiveTimeEnd) { this.effectiveTimeEnd = effectiveTimeEnd; }
    public String getEffectiveDays() { return effectiveDays; }
    public void setEffectiveDays(String effectiveDays) { this.effectiveDays = effectiveDays; }
    public String getTargetUserType() { return targetUserType; }
    public void setTargetUserType(String targetUserType) { this.targetUserType = targetUserType; }
    public String getTargetUserTags() { return targetUserTags; }
    public void setTargetUserTags(String targetUserTags) { this.targetUserTags = targetUserTags; }
    public Boolean getMemberOnly() { return memberOnly; }
    public void setMemberOnly(Boolean memberOnly) { this.memberOnly = memberOnly; }
    public Integer getMinUserLevel() { return minUserLevel; }
    public void setMinUserLevel(Integer minUserLevel) { this.minUserLevel = minUserLevel; }
    public Boolean getFirstTimeVisitor() { return firstTimeVisitor; }
    public void setFirstTimeVisitor(Boolean firstTimeVisitor) { this.firstTimeVisitor = firstTimeVisitor; }
    public Boolean getRegularCustomer() { return regularCustomer; }
    public void setRegularCustomer(Boolean regularCustomer) { this.regularCustomer = regularCustomer; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public String getMessageTemplateId() { return messageTemplateId; }
    public void setMessageTemplateId(String messageTemplateId) { this.messageTemplateId = messageTemplateId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    public String getActionMiniProgramId() { return actionMiniProgramId; }
    public void setActionMiniProgramId(String actionMiniProgramId) { this.actionMiniProgramId = actionMiniProgramId; }
    public String getActionMiniProgramPath() { return actionMiniProgramPath; }
    public void setActionMiniProgramPath(String actionMiniProgramPath) { this.actionMiniProgramPath = actionMiniProgramPath; }
    public Boolean getDeduplicationEnabled() { return deduplicationEnabled; }
    public void setDeduplicationEnabled(Boolean deduplicationEnabled) { this.deduplicationEnabled = deduplicationEnabled; }
    public String getDeduplicationScope() { return deduplicationScope; }
    public void setDeduplicationScope(String deduplicationScope) { this.deduplicationScope = deduplicationScope; }
    public Integer getDeduplicationTimeWindow() { return deduplicationTimeWindow; }
    public void setDeduplicationTimeWindow(Integer deduplicationTimeWindow) { this.deduplicationTimeWindow = deduplicationTimeWindow; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Map<String, Object> getExtendedFields() { return extendedFields; }
    public void setExtendedFields(Map<String, Object> extendedFields) { this.extendedFields = extendedFields; }
}
