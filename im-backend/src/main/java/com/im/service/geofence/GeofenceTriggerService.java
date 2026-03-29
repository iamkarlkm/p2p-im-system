package com.im.service.geofence;

import com.im.entity.geofence.*;
import java.util.List;

/**
 * 地理围栏场景化触发服务接口
 * GeoFence Scenario Trigger Service Interface
 * 
 * 功能：
 * - 围栏触发规则管理
 * - 用户围栏状态管理
 * - 群组场景管理
 * - 位置更新处理与触发判断
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface GeofenceTriggerService {
    
    // ==================== 触发规则管理 ====================
    
    /**
     * 创建触发规则
     */
    GeofenceTriggerRule createTriggerRule(GeofenceTriggerRule rule);
    
    /**
     * 更新触发规则
     */
    GeofenceTriggerRule updateTriggerRule(GeofenceTriggerRule rule);
    
    /**
     * 删除触发规则
     */
    boolean deleteTriggerRule(String ruleId);
    
    /**
     * 根据ID获取规则
     */
    GeofenceTriggerRule getTriggerRuleById(String ruleId);
    
    /**
     * 获取POI的所有触发规则
     */
    List<GeofenceTriggerRule> getTriggerRulesByPoiId(String poiId);
    
    /**
     * 获取指定类型的触发规则
     */
    List<GeofenceTriggerRule> getTriggerRulesByType(String ruleType);
    
    /**
     * 启用/禁用规则
     */
    boolean toggleRuleStatus(String ruleId, boolean enabled);
    
    /**
     * 获取所有生效的规则
     */
    List<GeofenceTriggerRule> getAllActiveRules();
    
    // ==================== 用户围栏状态管理 ====================
    
    /**
     * 获取用户围栏状态
     */
    UserGeofenceState getUserFenceState(Long userId, String fenceId);
    
    /**
     * 获取用户当前所在的所有围栏
     */
    List<UserGeofenceState> getUserActiveFences(Long userId);
    
    /**
     * 更新用户围栏状态
     */
    UserGeofenceState updateUserFenceState(UserGeofenceState state);
    
    /**
     * 清除用户围栏状态
     */
    boolean clearUserFenceState(Long userId, String fenceId);
    
    /**
     * 获取用户在指定围栏的停留时长
     */
    Long getUserDwellTime(Long userId, String fenceId);
    
    // ==================== 位置更新处理 ====================
    
    /**
     * 处理用户位置更新
     * 核心方法：判断围栏触发并执行相应操作
     */
    GeofenceProcessResult processLocationUpdate(Long userId, String deviceId, 
            Double latitude, Double longitude, String accuracy);
    
    /**
     * 批量处理位置更新
     */
    List<GeofenceProcessResult> batchProcessLocationUpdates(List<LocationUpdateRequest> requests);
    
    /**
     * 检查用户是否在围栏内
     */
    boolean isUserInFence(Long userId, String fenceId);
    
    /**
     * 获取用户附近的围栏
     */
    List<GeofenceInfo> getNearbyFences(Double latitude, Double longitude, Double radius);
    
    // ==================== 群组场景管理 ====================
    
    /**
     * 创建群组围栏场景
     */
    GeofenceGroupScenario createGroupScenario(GeofenceGroupScenario scenario);
    
    /**
     * 更新群组场景
     */
    GeofenceGroupScenario updateGroupScenario(GeofenceGroupScenario scenario);
    
    /**
     * 获取群组场景详情
     */
    GeofenceGroupScenario getGroupScenario(String groupId);
    
    /**
     * 删除群组场景
     */
    boolean deleteGroupScenario(String groupId);
    
    /**
     * 添加成员到群组场景
     */
    boolean addMemberToGroup(String groupId, String userId, String userName);
    
    /**
     * 从群组场景移除成员
     */
    boolean removeMemberFromGroup(String groupId, String userId);
    
    /**
     * 更新群组成员到达状态
     */
    boolean updateMemberArrivalStatus(String groupId, String userId, String status,
            Double latitude, Double longitude);
    
    /**
     * 获取用户的群组场景列表
     */
    List<GeofenceGroupScenario> getUserGroupScenarios(Long userId);
    
    /**
     * 获取群组成员到达统计
     */
    GroupArrivalStatistics getGroupArrivalStatistics(String groupId);
    
    // ==================== 触发日志与统计 ====================
    
    /**
     * 查询触发日志
     */
    List<GeofenceTriggerLog> queryTriggerLogs(TriggerLogQueryRequest request);
    
    /**
     * 获取规则触发统计
     */
    RuleTriggerStatistics getRuleTriggerStatistics(String ruleId);
    
    /**
     * 获取POI触发统计
     */
    PoiTriggerStatistics getPoiTriggerStatistics(String poiId);
    
    /**
     * 获取围栏场景整体统计
     */
    GeofenceSceneStatistics getSceneStatistics();
    
    // ==================== 消息去重与冷却 ====================
    
    /**
     * 检查消息去重
     */
    boolean checkDeduplication(String userId, String ruleId, String scope, Integer timeWindow);
    
    /**
     * 记录消息发送
     */
    void recordMessageSent(String userId, String ruleId, String messageId);
    
    /**
     * 检查冷却时间
     */
    boolean checkCooldown(String userId, String ruleId, Integer cooldownMinutes);
    
    // ==================== 内部类定义 ====================
    
    /**
     * 位置更新请求
     */
    class LocationUpdateRequest {
        private Long userId;
        private String deviceId;
        private Double latitude;
        private Double longitude;
        private String accuracy;
        private Long timestamp;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getAccuracy() { return accuracy; }
        public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 处理结果
     */
    class GeofenceProcessResult {
        private Long userId;
        private String fenceId;
        private String eventType;
        private boolean triggered;
        private String ruleId;
        private String messageId;
        private String status;
        private String errorMessage;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getFenceId() { return fenceId; }
        public void setFenceId(String fenceId) { this.fenceId = fenceId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public boolean isTriggered() { return triggered; }
        public void setTriggered(boolean triggered) { this.triggered = triggered; }
        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    /**
     * 围栏信息
     */
    class GeofenceInfo {
        private String fenceId;
        private String poiId;
        private String poiName;
        private Double centerLatitude;
        private Double centerLongitude;
        private Double radius;
        private String fenceType;
        private Double distance;
        
        public String getFenceId() { return fenceId; }
        public void setFenceId(String fenceId) { this.fenceId = fenceId; }
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
        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
    }
    
    /**
     * 群组到达统计
     */
    class GroupArrivalStatistics {
        private String groupId;
        private Integer totalMembers;
        private Integer arrivedCount;
        private Integer notArrivedCount;
        private Integer nearbyCount;
        private Integer leftCount;
        private Double arrivalRate;
        private List<GeofenceGroupScenario.GroupMember> memberDetails;
        
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public Integer getTotalMembers() { return totalMembers; }
        public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }
        public Integer getArrivedCount() { return arrivedCount; }
        public void setArrivedCount(Integer arrivedCount) { this.arrivedCount = arrivedCount; }
        public Integer getNotArrivedCount() { return notArrivedCount; }
        public void setNotArrivedCount(Integer notArrivedCount) { this.notArrivedCount = notArrivedCount; }
        public Integer getNearbyCount() { return nearbyCount; }
        public void setNearbyCount(Integer nearbyCount) { this.nearbyCount = nearbyCount; }
        public Integer getLeftCount() { return leftCount; }
        public void setLeftCount(Integer leftCount) { this.leftCount = leftCount; }
        public Double getArrivalRate() { return arrivalRate; }
        public void setArrivalRate(Double arrivalRate) { this.arrivalRate = arrivalRate; }
        public List<GeofenceGroupScenario.GroupMember> getMemberDetails() { return memberDetails; }
        public void setMemberDetails(List<GeofenceGroupScenario.GroupMember> memberDetails) { this.memberDetails = memberDetails; }
    }
    
    /**
     * 日志查询请求
     */
    class TriggerLogQueryRequest {
        private String ruleId;
        private String poiId;
        private Long userId;
        private String eventType;
        private String status;
        private String startTime;
        private String endTime;
        private Integer pageNum;
        private Integer pageSize;
        
        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getPoiId() { return poiId; }
        public void setPoiId(String poiId) { this.poiId = poiId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Integer getPageNum() { return pageNum; }
        public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    }
    
    /**
     * 规则触发统计
     */
    class RuleTriggerStatistics {
        private String ruleId;
        private String ruleName;
        private Long totalTriggers;
        private Long messageSentCount;
        private Long messageOpenCount;
        private Long conversionCount;
        private Double openRate;
        private Double conversionRate;
        private Double avgExecutionTimeMs;
        
        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public Long getTotalTriggers() { return totalTriggers; }
        public void setTotalTriggers(Long totalTriggers) { this.totalTriggers = totalTriggers; }
        public Long getMessageSentCount() { return messageSentCount; }
        public void setMessageSentCount(Long messageSentCount) { this.messageSentCount = messageSentCount; }
        public Long getMessageOpenCount() { return messageOpenCount; }
        public void setMessageOpenCount(Long messageOpenCount) { this.messageOpenCount = messageOpenCount; }
        public Long getConversionCount() { return conversionCount; }
        public void setConversionCount(Long conversionCount) { this.conversionCount = conversionCount; }
        public Double getOpenRate() { return openRate; }
        public void setOpenRate(Double openRate) { this.openRate = openRate; }
        public Double getConversionRate() { return conversionRate; }
        public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
        public Double getAvgExecutionTimeMs() { return avgExecutionTimeMs; }
        public void setAvgExecutionTimeMs(Double avgExecutionTimeMs) { this.avgExecutionTimeMs = avgExecutionTimeMs; }
    }
    
    /**
     * POI触发统计
     */
    class PoiTriggerStatistics {
        private String poiId;
        private String poiName;
        private Long totalEnterEvents;
        private Long totalExitEvents;
        private Long totalDwellEvents;
        private Long uniqueVisitors;
        private Double avgDwellSeconds;
        private Long totalMessagesSent;
        private Long totalMessagesOpened;
        
        public String getPoiId() { return poiId; }
        public void setPoiId(String poiId) { this.poiId = poiId; }
        public String getPoiName() { return poiName; }
        public void setPoiName(String poiName) { this.poiName = poiName; }
        public Long getTotalEnterEvents() { return totalEnterEvents; }
        public void setTotalEnterEvents(Long totalEnterEvents) { this.totalEnterEvents = totalEnterEvents; }
        public Long getTotalExitEvents() { return totalExitEvents; }
        public void setTotalExitEvents(Long totalExitEvents) { this.totalExitEvents = totalExitEvents; }
        public Long getTotalDwellEvents() { return totalDwellEvents; }
        public void setTotalDwellEvents(Long totalDwellEvents) { this.totalDwellEvents = totalDwellEvents; }
        public Long getUniqueVisitors() { return uniqueVisitors; }
        public void setUniqueVisitors(Long uniqueVisitors) { this.uniqueVisitors = uniqueVisitors; }
        public Double getAvgDwellSeconds() { return avgDwellSeconds; }
        public void setAvgDwellSeconds(Double avgDwellSeconds) { this.avgDwellSeconds = avgDwellSeconds; }
        public Long getTotalMessagesSent() { return totalMessagesSent; }
        public void setTotalMessagesSent(Long totalMessagesSent) { this.totalMessagesSent = totalMessagesSent; }
        public Long getTotalMessagesOpened() { return totalMessagesOpened; }
        public void setTotalMessagesOpened(Long totalMessagesOpened) { this.totalMessagesOpened = totalMessagesOpened; }
    }
    
    /**
     * 场景整体统计
     */
    class GeofenceSceneStatistics {
        private Long totalRules;
        private Long activeRules;
        private Long totalFences;
        private Long activeUsers;
        private Long todayTriggers;
        private Long todayMessagesSent;
        private Double avgTriggerLatencyMs;
        private Double messageOpenRate;
        private Double conversionRate;
        
        public Long getTotalRules() { return totalRules; }
        public void setTotalRules(Long totalRules) { this.totalRules = totalRules; }
        public Long getActiveRules() { return activeRules; }
        public void setActiveRules(Long activeRules) { this.activeRules = activeRules; }
        public Long getTotalFences() { return totalFences; }
        public void setTotalFences(Long totalFences) { this.totalFences = totalFences; }
        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
        public Long getTodayTriggers() { return todayTriggers; }
        public void setTodayTriggers(Long todayTriggers) { this.todayTriggers = todayTriggers; }
        public Long getTodayMessagesSent() { return todayMessagesSent; }
        public void setTodayMessagesSent(Long todayMessagesSent) { this.todayMessagesSent = todayMessagesSent; }
        public Double getAvgTriggerLatencyMs() { return avgTriggerLatencyMs; }
        public void setAvgTriggerLatencyMs(Double avgTriggerLatencyMs) { this.avgTriggerLatencyMs = avgTriggerLatencyMs; }
        public Double getMessageOpenRate() { return messageOpenRate; }
        public void setMessageOpenRate(Double messageOpenRate) { this.messageOpenRate = messageOpenRate; }
        public Double getConversionRate() { return conversionRate; }
        public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
    }
}
