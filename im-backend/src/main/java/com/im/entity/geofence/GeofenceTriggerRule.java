package com.im.entity.geofence;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 地理围栏场景化触发规则实体
 * GeoFence Scenario Trigger Rule Entity
 * 
 * 功能：定义基于地理围栏的消息触发规则
 * 包含：围栏类型、触发条件、消息模板、目标用户等
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class GeofenceTriggerRule {
    
    // ==================== 主键ID ====================
    private Long id;
    private String ruleId;
    
    // ==================== 规则基本信息 ====================
    private String ruleName;
    private String ruleDescription;
    private String ruleType;
    
    // ==================== 围栏配置 ====================
    private String poiId;
    private String poiName;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radius;
    private String fenceGeometry;
    private String fenceType;
    
    // ==================== 触发条件 ====================
    private String triggerEvent;
    private Integer minStaySeconds;
    private Integer maxTriggerCount;
    private Integer triggerCooldownMinutes;
    private Boolean timeRestricted;
    private String effectiveTimeStart;
    private String effectiveTimeEnd;
    private String effectiveDays;
    
    // ==================== 用户筛选 ====================
    private String targetUserType;
    private String targetUserTags;
    private Boolean memberOnly;
    private Integer minUserLevel;
    private Boolean firstTimeVisitor;
    private Boolean regularCustomer;
    
    // ==================== 消息配置 ====================
    private String messageType;
    private String messageTitle;
    private String messageContent;
    private String messageTemplateId;
    private String messageExtras;
    private String actionType;
    private String actionUrl;
    private String actionMiniProgramId;
    private String actionMiniProgramPath;
    
    // ==================== 高级设置 ====================
    private Boolean deduplicationEnabled;
    private String deduplicationScope;
    private Integer deduplicationTimeWindow;
    private Boolean aBTestEnabled;
    private String aBTestGroup;
    private String priority;
    
    // ==================== 状态统计 ====================
    private String status;
    private Long triggerCount;
    private Long messageSentCount;
    private Long messageOpenCount;
    private Double conversionRate;
    
    // ==================== 嵌套围栏 ====================
    private String parentFenceId;
    private Integer fenceLevel;
    private Boolean nestedFenceEnabled;
    
    // ==================== 扩展字段 ====================
    private Map<String, Object> extendedFields;
    
    // ==================== 时间戳 ====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // ==================== 构造函数 ====================
    public GeofenceTriggerRule() {
        this.extendedFields = new HashMap<>();
    }
    
    public GeofenceTriggerRule(String ruleId, String ruleName) {
        this();
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.createdAt = LocalDateTime.now();
    }
    
    // ==================== 触发事件枚举 ====================
    public static final String TRIGGER_ENTER = "ENTER";
    public static final String TRIGGER_EXIT = "EXIT";
    public static final String TRIGGER_DWELL = "DWELL";
    public static final String TRIGGER_DWELL_EXCEED = "DWELL_EXCEED";
    public static final String TRIGGER_NEARBY = "NEARBY";
    
    // ==================== 围栏类型 ====================
    public static final String FENCE_TYPE_CIRCLE = "CIRCLE";
    public static final String FENCE_TYPE_POLYGON = "POLYGON";
    public static final String FENCE_TYPE_POLYLINE = "POLYLINE";
    
    // ==================== 规则类型 ====================
    public static final String RULE_TYPE_WELCOME = "WELCOME";
    public static final String RULE_TYPE_PROMOTION = "PROMOTION";
    public static final String RULE_TYPE_SERVICE = "SERVICE";
    public static final String RULE_TYPE_REMINDER = "REMINDER";
    public static final String RULE_TYPE_SURVEY = "SURVEY";
    
    // ==================== 消息类型 ====================
    public static final String MSG_TYPE_PUSH = "PUSH";
    public static final String MSG_TYPE_IM = "IM";
    public static final String MSG_TYPE_SMS = "SMS";
    public static final String MSG_TYPE_WECHAT = "WECHAT";
    
    // ==================== 状态 ====================
    public static final String STATUS_ENABLED = "ENABLED";
    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_PAUSED = "PAUSED";
    
    // ==================== Getter & Setter ====================
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getRuleDescription() {
        return ruleDescription;
    }
    
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
    
    public String getRuleType() {
        return ruleType;
    }
    
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
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
    
    public Double getCenterLatitude() {
        return centerLatitude;
    }
    
    public void setCenterLatitude(Double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }
    
    public Double getCenterLongitude() {
        return centerLongitude;
    }
    
    public void setCenterLongitude(Double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }
    
    public Double getRadius() {
        return radius;
    }
    
    public void setRadius(Double radius) {
        this.radius = radius;
    }
    
    public String getFenceGeometry() {
        return fenceGeometry;
    }
    
    public void setFenceGeometry(String fenceGeometry) {
        this.fenceGeometry = fenceGeometry;
    }
    
    public String getFenceType() {
        return fenceType;
    }
    
    public void setFenceType(String fenceType) {
        this.fenceType = fenceType;
    }
    
    public String getTriggerEvent() {
        return triggerEvent;
    }
    
    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }
    
    public Integer getMinStaySeconds() {
        return minStaySeconds;
    }
    
    public void setMinStaySeconds(Integer minStaySeconds) {
        this.minStaySeconds = minStaySeconds;
    }
    
    public Integer getMaxTriggerCount() {
        return maxTriggerCount;
    }
    
    public void setMaxTriggerCount(Integer maxTriggerCount) {
        this.maxTriggerCount = maxTriggerCount;
    }
    
    public Integer getTriggerCooldownMinutes() {
        return triggerCooldownMinutes;
    }
    
    public void setTriggerCooldownMinutes(Integer triggerCooldownMinutes) {
        this.triggerCooldownMinutes = triggerCooldownMinutes;
    }
    
    public Boolean getTimeRestricted() {
        return timeRestricted;
    }
    
    public void setTimeRestricted(Boolean timeRestricted) {
        this.timeRestricted = timeRestricted;
    }
    
    public String getEffectiveTimeStart() {
        return effectiveTimeStart;
    }
    
    public void setEffectiveTimeStart(String effectiveTimeStart) {
        this.effectiveTimeStart = effectiveTimeStart;
    }
    
    public String getEffectiveTimeEnd() {
        return effectiveTimeEnd;
    }
    
    public void setEffectiveTimeEnd(String effectiveTimeEnd) {
        this.effectiveTimeEnd = effectiveTimeEnd;
    }
    
    public String getEffectiveDays() {
        return effectiveDays;
    }
    
    public void setEffectiveDays(String effectiveDays) {
        this.effectiveDays = effectiveDays;
    }
    
    public String getTargetUserType() {
        return targetUserType;
    }
    
    public void setTargetUserType(String targetUserType) {
        this.targetUserType = targetUserType;
    }
    
    public String getTargetUserTags() {
        return targetUserTags;
    }
    
    public void setTargetUserTags(String targetUserTags) {
        this.targetUserTags = targetUserTags;
    }
    
    public Boolean getMemberOnly() {
        return memberOnly;
    }
    
    public void setMemberOnly(Boolean memberOnly) {
        this.memberOnly = memberOnly;
    }
    
    public Integer getMinUserLevel() {
        return minUserLevel;
    }
    
    public void setMinUserLevel(Integer minUserLevel) {
        this.minUserLevel = minUserLevel;
    }
    
    public Boolean getFirstTimeVisitor() {
        return firstTimeVisitor;
    }
    
    public void setFirstTimeVisitor(Boolean firstTimeVisitor) {
        this.firstTimeVisitor = firstTimeVisitor;
    }
    
    public Boolean getRegularCustomer() {
        return regularCustomer;
    }
    
    public void setRegularCustomer(Boolean regularCustomer) {
        this.regularCustomer = regularCustomer;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getMessageTitle() {
        return messageTitle;
    }
    
    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }
    
    public String getMessageContent() {
        return messageContent;
    }
    
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    
    public String getMessageTemplateId() {
        return messageTemplateId;
    }
    
    public void setMessageTemplateId(String messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }
    
    public String getMessageExtras() {
        return messageExtras;
    }
    
    public void setMessageExtras(String messageExtras) {
        this.messageExtras = messageExtras;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public String getActionMiniProgramId() {
        return actionMiniProgramId;
    }
    
    public void setActionMiniProgramId(String actionMiniProgramId) {
        this.actionMiniProgramId = actionMiniProgramId;
    }
    
    public String getActionMiniProgramPath() {
        return actionMiniProgramPath;
    }
    
    public void setActionMiniProgramPath(String actionMiniProgramPath) {
        this.actionMiniProgramPath = actionMiniProgramPath;
    }
    
    public Boolean getDeduplicationEnabled() {
        return deduplicationEnabled;
    }
    
    public void setDeduplicationEnabled(Boolean deduplicationEnabled) {
        this.deduplicationEnabled = deduplicationEnabled;
    }
    
    public String getDeduplicationScope() {
        return deduplicationScope;
    }
    
    public void setDeduplicationScope(String deduplicationScope) {
        this.deduplicationScope = deduplicationScope;
    }
    
    public Integer getDeduplicationTimeWindow() {
        return deduplicationTimeWindow;
    }
    
    public void setDeduplicationTimeWindow(Integer deduplicationTimeWindow) {
        this.deduplicationTimeWindow = deduplicationTimeWindow;
    }
    
    public Boolean getABTestEnabled() {
        return aBTestEnabled;
    }
    
    public void setABTestEnabled(Boolean aBTestEnabled) {
        this.aBTestEnabled = aBTestEnabled;
    }
    
    public String getABTestGroup() {
        return aBTestGroup;
    }
    
    public void setABTestGroup(String aBTestGroup) {
        this.aBTestGroup = aBTestGroup;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getTriggerCount() {
        return triggerCount;
    }
    
    public void setTriggerCount(Long triggerCount) {
        this.triggerCount = triggerCount;
    }
    
    public Long getMessageSentCount() {
        return messageSentCount;
    }
    
    public void setMessageSentCount(Long messageSentCount) {
        this.messageSentCount = messageSentCount;
    }
    
    public Long getMessageOpenCount() {
        return messageOpenCount;
    }
    
    public void setMessageOpenCount(Long messageOpenCount) {
        this.messageOpenCount = messageOpenCount;
    }
    
    public Double getConversionRate() {
        return conversionRate;
    }
    
    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }
    
    public String getParentFenceId() {
        return parentFenceId;
    }
    
    public void setParentFenceId(String parentFenceId) {
        this.parentFenceId = parentFenceId;
    }
    
    public Integer getFenceLevel() {
        return fenceLevel;
    }
    
    public void setFenceLevel(Integer fenceLevel) {
        this.fenceLevel = fenceLevel;
    }
    
    public Boolean getNestedFenceEnabled() {
        return nestedFenceEnabled;
    }
    
    public void setNestedFenceEnabled(Boolean nestedFenceEnabled) {
        this.nestedFenceEnabled = nestedFenceEnabled;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 检查规则是否在生效时间段内
     */
    public boolean isInEffectiveTime() {
        if (!Boolean.TRUE.equals(timeRestricted)) {
            return true;
        }
        // 简化实现，实际应解析时间范围
        return true;
    }
    
    /**
     * 检查用户是否符合目标用户条件
     */
    public boolean matchesUserCriteria(Integer userLevel, boolean isMember, boolean isFirstVisit, boolean isRegular) {
        if (Boolean.TRUE.equals(memberOnly) && !isMember) {
            return false;
        }
        if (minUserLevel != null && (userLevel == null || userLevel < minUserLevel)) {
            return false;
        }
        if (Boolean.TRUE.equals(firstTimeVisitor) && !isFirstVisit) {
            return false;
        }
        if (Boolean.TRUE.equals(regularCustomer) && !isRegular) {
            return false;
        }
        return true;
    }
    
    /**
     * 增加触发计数
     */
    public void incrementTriggerCount() {
        if (this.triggerCount == null) {
            this.triggerCount = 0L;
        }
        this.triggerCount++;
    }
    
    /**
     * 检查是否达到最大触发次数限制
     */
    public boolean hasReachedMaxTriggerCount() {
        if (maxTriggerCount == null || maxTriggerCount <= 0) {
            return false;
        }
        return triggerCount != null && triggerCount >= maxTriggerCount;
    }
    
    @Override
    public String toString() {
        return "GeofenceTriggerRule{" +
                "ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", poiId='" + poiId + '\'' +
                ", triggerEvent='" + triggerEvent + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
