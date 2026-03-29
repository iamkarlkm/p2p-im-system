package com.im.entity.geofence;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 地理围栏群组场景实体
 * GeoFence Group Scenario Entity
 * 
 * 功能：定义群组围栏场景，如聚餐、会议、活动等
 * 包含：群组信息、成员状态、到达同步等
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public class GeofenceGroupScenario {
    
    // ==================== 主键ID ====================
    private Long id;
    private String groupId;
    
    // ==================== 群组基本信息 ====================
    private String groupName;
    private String groupDescription;
    private String scenarioType;
    private String organizerId;
    private String organizerName;
    
    // ==================== 围栏信息 ====================
    private String fenceId;
    private String poiId;
    private String poiName;
    private Double centerLatitude;
    private Double centerLongitude;
    private String locationAddress;
    
    // ==================== 时间设置 ====================
    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer remindBeforeMinutes;
    private Boolean autoCheckinEnabled;
    
    // ==================== 成员管理 ====================
    private Integer maxMembers;
    private Integer currentMemberCount;
    private String memberIds;
    private List<GroupMember> members;
    
    // ==================== 到达通知 ====================
    private Boolean arrivalNotificationEnabled;
    private String arrivalNotificationType;
    private String arrivalMessageTemplate;
    private Boolean notifyAllOnArrival;
    
    // ==================== 防走散设置 ====================
    private Boolean antiLostEnabled;
    private Integer antiLostRadius;
    private String antiLostAlertType;
    private String protectedMemberIds;
    
    // ==================== 状态统计 ====================
    private String status;
    private Integer arrivedCount;
    private Integer notArrivedCount;
    private Integer nearbyCount;
    private Integer leftCount;
    private LocalDateTime lastStatusUpdateTime;
    
    // ==================== 扩展字段 ====================
    private Map<String, Object> extendedFields;
    
    // ==================== 时间戳 ====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // ==================== 场景类型 ====================
    public static final String SCENARIO_GATHERING = "GATHERING";
    public static final String SCENARIO_MEETING = "MEETING";
    public static final String SCENARIO_EVENT = "EVENT";
    public static final String SCENARIO_DINNER = "DINNER";
    public static final String SCENARIO_TRAVEL = "TRAVEL";
    public static final String SCENARIO_PARENT_WATCH = "PARENT_WATCH";
    
    // ==================== 状态 ====================
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    // ==================== 构造函数 ====================
    public GeofenceGroupScenario() {
        this.extendedFields = new HashMap<>();
        this.members = new ArrayList<>();
        this.arrivedCount = 0;
        this.notArrivedCount = 0;
        this.nearbyCount = 0;
        this.leftCount = 0;
    }
    
    // ==================== 内部类：群组成员 ====================
    public static class GroupMember {
        private String userId;
        private String userName;
        private String avatarUrl;
        private String status;
        private LocalDateTime arrivalTime;
        private Double arrivalLatitude;
        private Double arrivalLongitude;
        private Boolean isProtected;
        private String phoneNumber;
        
        public GroupMember() {}
        
        public GroupMember(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
            this.status = UserGeofenceState.GROUP_STATUS_NOT_ARRIVED;
        }
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
        public Double getArrivalLatitude() { return arrivalLatitude; }
        public void setArrivalLatitude(Double arrivalLatitude) { this.arrivalLatitude = arrivalLatitude; }
        public Double getArrivalLongitude() { return arrivalLongitude; }
        public void setArrivalLongitude(Double arrivalLongitude) { this.arrivalLongitude = arrivalLongitude; }
        public Boolean getIsProtected() { return isProtected; }
        public void setIsProtected(Boolean isProtected) { this.isProtected = isProtected; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
    
    // ==================== Getter & Setter ====================
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupDescription() {
        return groupDescription;
    }
    
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
    
    public String getScenarioType() {
        return scenarioType;
    }
    
    public void setScenarioType(String scenarioType) {
        this.scenarioType = scenarioType;
    }
    
    public String getOrganizerId() {
        return organizerId;
    }
    
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    
    public String getOrganizerName() {
        return organizerName;
    }
    
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
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
    
    public String getLocationAddress() {
        return locationAddress;
    }
    
    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }
    
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getRemindBeforeMinutes() {
        return remindBeforeMinutes;
    }
    
    public void setRemindBeforeMinutes(Integer remindBeforeMinutes) {
        this.remindBeforeMinutes = remindBeforeMinutes;
    }
    
    public Boolean getAutoCheckinEnabled() {
        return autoCheckinEnabled;
    }
    
    public void setAutoCheckinEnabled(Boolean autoCheckinEnabled) {
        this.autoCheckinEnabled = autoCheckinEnabled;
    }
    
    public Integer getMaxMembers() {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }
    
    public Integer getCurrentMemberCount() {
        return currentMemberCount;
    }
    
    public void setCurrentMemberCount(Integer currentMemberCount) {
        this.currentMemberCount = currentMemberCount;
    }
    
    public String getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }
    
    public List<GroupMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }
    
    public Boolean getArrivalNotificationEnabled() {
        return arrivalNotificationEnabled;
    }
    
    public void setArrivalNotificationEnabled(Boolean arrivalNotificationEnabled) {
        this.arrivalNotificationEnabled = arrivalNotificationEnabled;
    }
    
    public String getArrivalNotificationType() {
        return arrivalNotificationType;
    }
    
    public void setArrivalNotificationType(String arrivalNotificationType) {
        this.arrivalNotificationType = arrivalNotificationType;
    }
    
    public String getArrivalMessageTemplate() {
        return arrivalMessageTemplate;
    }
    
    public void setArrivalMessageTemplate(String arrivalMessageTemplate) {
        this.arrivalMessageTemplate = arrivalMessageTemplate;
    }
    
    public Boolean getNotifyAllOnArrival() {
        return notifyAllOnArrival;
    }
    
    public void setNotifyAllOnArrival(Boolean notifyAllOnArrival) {
        this.notifyAllOnArrival = notifyAllOnArrival;
    }
    
    public Boolean getAntiLostEnabled() {
        return antiLostEnabled;
    }
    
    public void setAntiLostEnabled(Boolean antiLostEnabled) {
        this.antiLostEnabled = antiLostEnabled;
    }
    
    public Integer getAntiLostRadius() {
        return antiLostRadius;
    }
    
    public void setAntiLostRadius(Integer antiLostRadius) {
        this.antiLostRadius = antiLostRadius;
    }
    
    public String getAntiLostAlertType() {
        return antiLostAlertType;
    }
    
    public void setAntiLostAlertType(String antiLostAlertType) {
        this.antiLostAlertType = antiLostAlertType;
    }
    
    public String getProtectedMemberIds() {
        return protectedMemberIds;
    }
    
    public void setProtectedMemberIds(String protectedMemberIds) {
        this.protectedMemberIds = protectedMemberIds;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getArrivedCount() {
        return arrivedCount;
    }
    
    public void setArrivedCount(Integer arrivedCount) {
        this.arrivedCount = arrivedCount;
    }
    
    public Integer getNotArrivedCount() {
        return notArrivedCount;
    }
    
    public void setNotArrivedCount(Integer notArrivedCount) {
        this.notArrivedCount = notArrivedCount;
    }
    
    public Integer getNearbyCount() {
        return nearbyCount;
    }
    
    public void setNearbyCount(Integer nearbyCount) {
        this.nearbyCount = nearbyCount;
    }
    
    public Integer getLeftCount() {
        return leftCount;
    }
    
    public void setLeftCount(Integer leftCount) {
        this.leftCount = leftCount;
    }
    
    public LocalDateTime getLastStatusUpdateTime() {
        return lastStatusUpdateTime;
    }
    
    public void setLastStatusUpdateTime(LocalDateTime lastStatusUpdateTime) {
        this.lastStatusUpdateTime = lastStatusUpdateTime;
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
     * 添加成员
     */
    public void addMember(GroupMember member) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        this.members.add(member);
        updateMemberCounts();
    }
    
    /**
     * 更新成员状态
     */
    public void updateMemberStatus(String userId, String status, Double lat, Double lng) {
        if (this.members == null) return;
        for (GroupMember member : members) {
            if (member.getUserId().equals(userId)) {
                member.setStatus(status);
                if (UserGeofenceState.GROUP_STATUS_ARRIVED.equals(status)) {
                    member.setArrivalTime(LocalDateTime.now());
                    member.setArrivalLatitude(lat);
                    member.setArrivalLongitude(lng);
                }
                break;
            }
        }
        updateMemberCounts();
        this.lastStatusUpdateTime = LocalDateTime.now();
    }
    
    /**
     * 更新成员统计
     */
    private void updateMemberCounts() {
        if (this.members == null) return;
        this.arrivedCount = 0;
        this.notArrivedCount = 0;
        this.nearbyCount = 0;
        this.leftCount = 0;
        for (GroupMember member : members) {
            switch (member.getStatus()) {
                case UserGeofenceState.GROUP_STATUS_ARRIVED:
                    this.arrivedCount++;
                    break;
                case UserGeofenceState.GROUP_STATUS_NOT_ARRIVED:
                    this.notArrivedCount++;
                    break;
                case UserGeofenceState.GROUP_STATUS_NEARBY:
                    this.nearbyCount++;
                    break;
                case UserGeofenceState.GROUP_STATUS_LEFT:
                    this.leftCount++;
                    break;
            }
        }
        this.currentMemberCount = members.size();
    }
    
    /**
     * 检查是否所有成员已到达
     */
    public boolean isAllArrived() {
        return currentMemberCount != null && currentMemberCount > 0 && 
               arrivedCount != null && arrivedCount.equals(currentMemberCount);
    }
    
    /**
     * 获取到达率
     */
    public double getArrivalRate() {
        if (currentMemberCount == null || currentMemberCount == 0) {
            return 0.0;
        }
        return (double) (arrivedCount != null ? arrivedCount : 0) / currentMemberCount * 100;
    }
    
    @Override
    public String toString() {
        return "GeofenceGroupScenario{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", scenarioType='" + scenarioType + '\'' +
                ", status='" + status + '\'' +
                ", arrivedCount=" + arrivedCount +
                ", currentMemberCount=" + currentMemberCount +
                '}';
    }
}
