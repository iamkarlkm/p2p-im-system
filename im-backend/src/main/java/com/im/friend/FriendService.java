package com.im.friend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 好友服务
 * 功能 #4: 好友关系管理系统 - 核心服务
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class FriendService {
    
    private static final Logger logger = LoggerFactory.getLogger(FriendService.class);
    
    // 好友关系存储
    private final Map<String, Set<FriendRelation>> friendRelations = new ConcurrentHashMap<>();
    
    // 好友申请存储
    private final Map<String, List<FriendRequest>> friendRequests = new ConcurrentHashMap<>();
    
    // 好友分组
    private final Map<String, List<FriendGroup>> friendGroups = new ConcurrentHashMap<>();
    
    /**
     * 发送好友申请
     */
    public FriendResult sendFriendRequest(String fromUserId, String toUserId, String message, String remark) {
        // 检查是否已是好友
        if (isFriend(fromUserId, toUserId)) {
            return FriendResult.failure("Already friends");
        }
        
        // 检查是否已有待处理申请
        if (hasPendingRequest(fromUserId, toUserId)) {
            return FriendResult.failure("Friend request already sent");
        }
        
        // 创建申请
        FriendRequest request = new FriendRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setMessage(message);
        request.setRemark(remark);
        request.setStatus(FriendRequestStatus.PENDING);
        request.setCreateTime(new Date());
        
        friendRequests.computeIfAbsent(toUserId, k -> new ArrayList<>()).add(request);
        
        logger.info("Friend request sent: from={}, to={}", fromUserId, toUserId);
        return FriendResult.success(request.getRequestId(), "Friend request sent");
    }
    
    /**
     * 接受好友申请
     */
    public FriendResult acceptFriendRequest(String userId, String requestId) {
        FriendRequest request = findRequestById(userId, requestId);
        if (request == null) {
            return FriendResult.failure("Request not found");
        }
        
        if (request.getStatus() != FriendRequestStatus.PENDING) {
            return FriendResult.failure("Request already processed");
        }
        
        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setProcessTime(new Date());
        
        // 建立双向好友关系
        addFriendRelation(request.getFromUserId(), request.getToUserId(), request.getRemark());
        addFriendRelation(request.getToUserId(), request.getFromUserId(), null);
        
        logger.info("Friend request accepted: requestId={}, from={}, to={}", 
            requestId, request.getFromUserId(), request.getToUserId());
        return FriendResult.success(requestId, "Friend request accepted");
    }
    
    /**
     * 拒绝好友申请
     */
    public FriendResult rejectFriendRequest(String userId, String requestId, String reason) {
        FriendRequest request = findRequestById(userId, requestId);
        if (request == null) {
            return FriendResult.failure("Request not found");
        }
        
        request.setStatus(FriendRequestStatus.REJECTED);
        request.setProcessTime(new Date());
        request.setRejectReason(reason);
        
        logger.info("Friend request rejected: requestId={}", requestId);
        return FriendResult.success(requestId, "Friend request rejected");
    }
    
    /**
     * 删除好友
     */
    public FriendResult deleteFriend(String userId, String friendId) {
        // 删除双向关系
        removeFriendRelation(userId, friendId);
        removeFriendRelation(friendId, userId);
        
        logger.info("Friend deleted: userId={}, friendId={}", userId, friendId);
        return FriendResult.success(null, "Friend deleted");
    }
    
    /**
     * 获取好友列表
     */
    public List<FriendInfo> getFriendList(String userId) {
        Set<FriendRelation> relations = friendRelations.getOrDefault(userId, Collections.emptySet());
        
        return relations.stream()
            .map(r -> {
                FriendInfo info = new FriendInfo();
                info.setUserId(r.getFriendId());
                info.setRemark(r.getRemark());
                info.setGroupName(r.getGroupName());
                info.setCreateTime(r.getCreateTime());
                info.setStatus(FriendStatus.FRIEND);
                return info;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 获取好友申请列表
     */
    public List<FriendRequest> getFriendRequests(String userId, FriendRequestStatus status) {
        List<FriendRequest> requests = friendRequests.getOrDefault(userId, Collections.emptyList());
        
        if (status != null) {
            return requests.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
        }
        return requests;
    }
    
    /**
     * 修改好友备注
     */
    public FriendResult updateFriendRemark(String userId, String friendId, String remark) {
        Set<FriendRelation> relations = friendRelations.get(userId);
        if (relations != null) {
            relations.stream()
                .filter(r -> r.getFriendId().equals(friendId))
                .findFirst()
                .ifPresent(r -> r.setRemark(remark));
        }
        return FriendResult.success(null, "Remark updated");
    }
    
    /**
     * 移动好友到分组
     */
    public FriendResult moveToGroup(String userId, String friendId, String groupName) {
        Set<FriendRelation> relations = friendRelations.get(userId);
        if (relations != null) {
            relations.stream()
                .filter(r -> r.getFriendId().equals(friendId))
                .findFirst()
                .ifPresent(r -> r.setGroupName(groupName));
        }
        return FriendResult.success(null, "Moved to group: " + groupName);
    }
    
    /**
     * 获取好友分组
     */
    public List<FriendGroup> getFriendGroups(String userId) {
        return friendGroups.getOrDefault(userId, Collections.emptyList());
    }
    
    /**
     * 创建分组
     */
    public FriendResult createGroup(String userId, String groupName) {
        FriendGroup group = new FriendGroup();
        group.setGroupId(UUID.randomUUID().toString());
        group.setName(groupName);
        group.setCreateTime(new Date());
        
        friendGroups.computeIfAbsent(userId, k -> new ArrayList<>()).add(group);
        return FriendResult.success(group.getGroupId(), "Group created");
    }
    
    /**
     * 检查是否是好友
     */
    public boolean isFriend(String userId, String friendId) {
        Set<FriendRelation> relations = friendRelations.get(userId);
        if (relations == null) return false;
        
        return relations.stream()
            .anyMatch(r -> r.getFriendId().equals(friendId));
    }
    
    /**
     * 获取好友数量
     */
    public int getFriendCount(String userId) {
        Set<FriendRelation> relations = friendRelations.get(userId);
        return relations != null ? relations.size() : 0;
    }
    
    // ==================== 内部方法 ====================
    
    private void addFriendRelation(String userId, String friendId, String remark) {
        FriendRelation relation = new FriendRelation();
        relation.setFriendId(friendId);
        relation.setRemark(remark);
        relation.setCreateTime(new Date());
        relation.setGroupName("My Friends");
        
        friendRelations.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(relation);
    }
    
    private void removeFriendRelation(String userId, String friendId) {
        Set<FriendRelation> relations = friendRelations.get(userId);
        if (relations != null) {
            relations.removeIf(r -> r.getFriendId().equals(friendId));
        }
    }
    
    private boolean hasPendingRequest(String fromUserId, String toUserId) {
        List<FriendRequest> requests = friendRequests.get(toUserId);
        if (requests == null) return false;
        
        return requests.stream()
            .anyMatch(r -> r.getFromUserId().equals(fromUserId) && r.getStatus() == FriendRequestStatus.PENDING);
    }
    
    private FriendRequest findRequestById(String userId, String requestId) {
        List<FriendRequest> requests = friendRequests.get(userId);
        if (requests == null) return null;
        
        return requests.stream()
            .filter(r -> r.getRequestId().equals(requestId))
            .findFirst()
            .orElse(null);
    }
    
    // ==================== 内部类 ====================
    
    public enum FriendRequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
    
    public enum FriendStatus {
        FRIEND, BLOCKED, DELETED
    }
    
    public static class FriendRelation {
        private String friendId;
        private String remark;
        private String groupName;
        private Date createTime;
        
        // Getters and Setters
        public String getFriendId() { return friendId; }
        public void setFriendId(String friendId) { this.friendId = friendId; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
    }
    
    public static class FriendRequest {
        private String requestId;
        private String fromUserId;
        private String toUserId;
        private String message;
        private String remark;
        private FriendRequestStatus status;
        private Date createTime;
        private Date processTime;
        private String rejectReason;
        
        // Getters and Setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getFromUserId() { return fromUserId; }
        public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
        public String getToUserId() { return toUserId; }
        public void setToUserId(String toUserId) { this.toUserId = toUserId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public FriendRequestStatus getStatus() { return status; }
        public void setStatus(FriendRequestStatus status) { this.status = status; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public Date getProcessTime() { return processTime; }
        public void setProcessTime(Date processTime) { this.processTime = processTime; }
        public String getRejectReason() { return rejectReason; }
        public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    }
    
    public static class FriendInfo {
        private String userId;
        private String remark;
        private String groupName;
        private Date createTime;
        private FriendStatus status;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public FriendStatus getStatus() { return status; }
        public void setStatus(FriendStatus status) { this.status = status; }
    }
    
    public static class FriendGroup {
        private String groupId;
        private String name;
        private Date createTime;
        
        // Getters and Setters
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
    }
    
    public static class FriendResult {
        private boolean success;
        private String message;
        private String data;
        
        public static FriendResult success(String data, String message) {
            FriendResult result = new FriendResult();
            result.success = true;
            result.data = data;
            result.message = message;
            return result;
        }
        
        public static FriendResult failure(String message) {
            FriendResult result = new FriendResult();
            result.success = false;
            result.message = message;
            return result;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getData() { return data; }
    }
}
