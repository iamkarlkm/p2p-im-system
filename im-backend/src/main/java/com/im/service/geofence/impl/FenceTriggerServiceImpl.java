package com.im.service.geofence.impl;

import com.im.entity.geofence.*;
import com.im.service.geofence.FenceTriggerService;
import com.im.service.geofence.GeoFenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 围栏触发服务实现类
 */
@Service
public class FenceTriggerServiceImpl implements FenceTriggerService {
    
    @Autowired
    private GeoFenceService geoFenceService;
    
    private final Map<String, UserFenceStatus> userFenceStatusStore = new ConcurrentHashMap<>();
    private final Map<String, FenceTriggerMessage> messageStore = new ConcurrentHashMap<>();
    
    // 状态常量
    private static final String STATUS_OUTSIDE = "OUTSIDE";
    private static final String STATUS_INSIDE = "INSIDE";
    private static final String STATUS_DWELLING = "DWELLING";
    
    // 触发类型常量
    private static final String TRIGGER_ENTER = "ENTER";
    private static final String TRIGGER_DWELL = "DWELL";
    private static final String TRIGGER_EXIT = "EXIT";
    
    @Override
    public void processLocationUpdate(String userId, Double longitude, Double latitude) {
        // 1. 获取所有启用的围栏
        List<GeoFence> activeFences = geoFenceService.getAllActiveFences();
        
        // 2. 检查用户位置与每个围栏的关系
        for (GeoFence fence : activeFences) {
            processFenceForUser(userId, longitude, latitude, fence);
        }
    }
    
    /**
     * 处理单个围栏的用户状态
     */
    private void processFenceForUser(String userId, Double longitude, Double latitude, GeoFence fence) {
        String statusKey = userId + ":" + fence.getFenceId();
        UserFenceStatus status = userFenceStatusStore.getOrDefault(statusKey, 
                createNewUserFenceStatus(userId, fence.getFenceId()));
        
        // 更新位置信息
        status.setCurrentLongitude(longitude);
        status.setCurrentLatitude(latitude);
        status.setLocationUpdateTime(LocalDateTime.now());
        
        // 计算距离围栏中心距离
        double distance = haversineDistance(
                longitude, latitude,
                fence.getCenterLongitude(), fence.getCenterLatitude());
        status.setDistanceToCenter(distance);
        
        // 检查是否在围栏内
        boolean isInside = geoFenceService.isPointInFence(longitude, latitude, fence);
        String currentStatus = status.getStatus();
        
        if (isInside) {
            // 用户进入或在围栏内
            if (STATUS_OUTSIDE.equals(currentStatus) || currentStatus == null) {
                // 状态从外部变为内部 - 触发进入事件
                status.setStatus(STATUS_INSIDE);
                status.setFirstEnterTime(LocalDateTime.now());
                status.setLastEnterTime(LocalDateTime.now());
                status.setCurrentDwellStartTime(LocalDateTime.now());
                status.setDwellMessageTriggered(false);
                status.setTotalVisits(status.getTotalVisits() + 1);
                userFenceStatusStore.put(statusKey, status);
                handleEnterFence(userId, fence.getFenceId());
            } else if (STATUS_INSIDE.equals(currentStatus)) {
                // 检查停留超时
                checkDwellTimeout(userId, fence, status);
                userFenceStatusStore.put(statusKey, status);
            }
        } else {
            // 用户在围栏外
            if (STATUS_INSIDE.equals(currentStatus) || STATUS_DWELLING.equals(currentStatus)) {
                // 状态从内部变为外部 - 触发离开事件
                status.setStatus(STATUS_OUTSIDE);
                status.setLastExitTime(LocalDateTime.now());
                
                // 计算本次停留时长
                if (status.getCurrentDwellStartTime() != null) {
                    long dwellMinutes = ChronoUnit.MINUTES.between(
                            status.getCurrentDwellStartTime(), LocalDateTime.now());
                    status.setCurrentDwellMinutes((int) dwellMinutes);
                    status.setTotalDwellMinutes(status.getTotalDwellMinutes() + (int) dwellMinutes);
                }
                
                userFenceStatusStore.put(statusKey, status);
                handleExitFence(userId, fence.getFenceId());
            }
        }
    }
    
    /**
     * 检查停留超时
     */
    private void checkDwellTimeout(String userId, GeoFence fence, UserFenceStatus status) {
        if (fence.getDwellTimeout() == null || fence.getDwellTimeout() <= 0) return;
        if (Boolean.TRUE.equals(status.getDwellMessageTriggered())) return;
        
        if (status.getCurrentDwellStartTime() != null) {
            long dwellMinutes = ChronoUnit.MINUTES.between(
                    status.getCurrentDwellStartTime(), LocalDateTime.now());
            status.setCurrentDwellMinutes((int) dwellMinutes);
            
            if (dwellMinutes >= fence.getDwellTimeout()) {
                status.setStatus(STATUS_DWELLING);
                status.setDwellMessageTriggered(true);
                handleDwellTimeout(userId, fence.getFenceId());
            }
        }
    }
    
    /**
     * 创建新的用户围栏状态
     */
    private UserFenceStatus createNewUserFenceStatus(String userId, String fenceId) {
        UserFenceStatus status = new UserFenceStatus();
        status.setStatusId(UUID.randomUUID().toString());
        status.setUserId(userId);
        status.setFenceId(fenceId);
        status.setStatus(STATUS_OUTSIDE);
        status.setTotalVisits(0);
        status.setTotalDwellMinutes(0);
        status.setDwellMessageTriggered(false);
        status.setCreateTime(LocalDateTime.now());
        status.setUpdateTime(LocalDateTime.now());
        return status;
    }
    
    /**
     * Haversine公式计算距离
     */
    private double haversineDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        final double R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    @Override
    public void handleEnterFence(String userId, String fenceId) {
        GeoFence fence = geoFenceService.getFenceById(fenceId);
        if (fence == null || fence.getEnterMessageTemplateId() == null) return;
        
        // 构建进入消息
        FenceTriggerMessage message = new FenceTriggerMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setFenceId(fenceId);
        message.setUserId(userId);
        message.setTriggerType(TRIGGER_ENTER);
        message.setMessageType("WELCOME");
        message.setTitle("欢迎光临" + fence.getPoiName());
        message.setContent("您已到达" + fence.getPoiName() + ", 点击领取专属优惠!");
        message.setSendStatus("PENDING");
        message.setTriggerTime(LocalDateTime.now());
        message.setCreateTime(LocalDateTime.now());
        message.setDeduplicated(true);
        message.setDedupKey(userId + ":" + fenceId + ":" + TRIGGER_ENTER);
        
        messageStore.put(message.getMessageId(), message);
        sendTriggerMessage(message);
    }
    
    @Override
    public void handleDwellTimeout(String userId, String fenceId) {
        GeoFence fence = geoFenceService.getFenceById(fenceId);
        if (fence == null || fence.getDwellMessageTemplateId() == null) return;
        
        FenceTriggerMessage message = new FenceTriggerMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setFenceId(fenceId);
        message.setUserId(userId);
        message.setTriggerType(TRIGGER_DWELL);
        message.setMessageType("SERVICE");
        message.setTitle("需要帮忙吗?");
        message.setContent("您在" + fence.getPoiName() + "已停留较长时间, 有什么可以帮您的吗?");
        message.setSendStatus("PENDING");
        message.setTriggerTime(LocalDateTime.now());
        message.setCreateTime(LocalDateTime.now());
        
        messageStore.put(message.getMessageId(), message);
        sendTriggerMessage(message);
    }
    
    @Override
    public void handleExitFence(String userId, String fenceId) {
        GeoFence fence = geoFenceService.getFenceById(fenceId);
        if (fence == null || fence.getExitMessageTemplateId() == null) return;
        
        FenceTriggerMessage message = new FenceTriggerMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setFenceId(fenceId);
        message.setUserId(userId);
        message.setTriggerType(TRIGGER_EXIT);
        message.setMessageType("SURVEY");
        message.setTitle("感谢您的光临");
        message.setContent("感谢您的到访! 请对我们的服务进行评价, 下次光临享更多优惠!");
        message.setSendStatus("PENDING");
        message.setTriggerTime(LocalDateTime.now());
        message.setCreateTime(LocalDateTime.now());
        
        messageStore.put(message.getMessageId(), message);
        sendTriggerMessage(message);
    }
    
    @Override
    public void sendTriggerMessage(FenceTriggerMessage message) {
        // 模拟发送消息
        message.setSendStatus("SENT");
        message.setSendTime(LocalDateTime.now());
        messageStore.put(message.getMessageId(), message);
        
        // 这里可以集成实际的消息推送服务
        System.out.println("[围栏消息] 发送给 " + message.getUserId() + ": " + message.getContent());
    }
    
    @Override
    public List<UserFenceStatus> getUserFenceStatuses(String userId) {
        return userFenceStatusStore.values().stream()
                .filter(s -> userId.equals(s.getUserId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserFenceStatus> getUsersInFence(String fenceId) {
        return userFenceStatusStore.values().stream()
                .filter(s -> fenceId.equals(s.getFenceId()))
                .filter(s -> STATUS_INSIDE.equals(s.getStatus()) || STATUS_DWELLING.equals(s.getStatus()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FenceTriggerMessage> getUserMessageHistory(String userId, Integer limit) {
        return messageStore.values().stream()
                .filter(m -> userId.equals(m.getUserId()))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(limit != null ? limit : 50)
                .collect(Collectors.toList());
    }
}
