package com.im.local.geofence.service;

import com.im.core.websocket.WebSocketPushService;
import com.im.local.geofence.dto.*;
import com.im.local.geofence.entity.*;
import com.im.local.geofence.enums.*;
import com.im.local.geofence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 地理围栏场景化触发服务
 * 基于地理围栏事件触发即时通讯消息
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceTriggerService {

    private final GeofenceRepository geofenceRepository;
    private final GeofenceEventRepository eventRepository;
    private final GeofenceTriggerRuleRepository ruleRepository;
    private final GroupLocationSharingRepository sharingRepository;
    private final WebSocketPushService webSocketPushService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key 前缀
    private static final String GEOFENCE_STATUS_KEY = "geofence:status:";
    private static final String USER_LOCATION_KEY = "user:location:";
    private static final String GROUP_SHARING_KEY = "group:sharing:";
    
    // 配置
    private static final long LOCATION_EXPIRE_MINUTES = 10;
    private static final double GEOFENCE_CHECK_RADIUS = 500; // 米
    
    /**
     * 处理用户位置更新
     * 检测地理围栏事件并触发相应动作
     */
    @Transactional
    public void processUserLocationUpdate(Long userId, LocationUpdate update) {
        // 保存用户位置
        saveUserLocation(userId, update);
        
        // 查询用户相关的所有围栏
        List<Geofence> geofences = findRelevantGeofences(userId, update);
        
        for (Geofence geofence : geofences) {
            // 检测围栏状态变化
            GeofenceEventType newEvent = detectGeofenceEvent(userId, geofence, update);
            
            if (newEvent != null) {
                // 创建围栏事件
                GeofenceEvent event = createGeofenceEvent(userId, geofence, newEvent, update);
                
                // 执行触发规则
                executeTriggerRules(geofence, event, userId);
                
                // 更新围栏状态缓存
                updateGeofenceStatus(userId, geofence, newEvent);
            }
        }
        
        // 检查群组位置共享
        checkGroupLocationSharing(userId, update);
    }
    
    /**
     * 创建地理围栏
     */
    @Transactional
    public Geofence createGeofence(CreateGeofenceRequest request) {
        log.info("创建地理围栏，名称: {}, 类型: {}", request.getName(), request.getType());
        
        Geofence geofence = Geofence.builder()
            .name(request.getName())
            .type(request.getType())
            .shape(request.getShape())
            .centerLatitude(request.getCenterLatitude())
            .centerLongitude(request.getCenterLongitude())
            .radius(request.getRadius())
            .coordinates(request.getCoordinates())
            .ownerId(request.getOwnerId())
            .ownerType(request.getOwnerType())
            .targetId(request.getTargetId())
            .targetType(request.getTargetType())
            .triggerEvents(request.getTriggerEvents())
            .status(GeofenceStatus.ACTIVE)
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .createTime(LocalDateTime.now())
            .build();
        
        geofenceRepository.save(geofence);
        
        // 创建默认触发规则
        createDefaultTriggerRules(geofence, request.getTriggerActions());
        
        log.info("地理围栏创建成功，ID: {}", geofence.getId());
        return geofence;
    }
    
    /**
     * 创建群组位置共享会话
     */
    @Transactional
    public GroupLocationSharing createGroupSharing(CreateGroupSharingRequest request) {
        log.info("创建群组位置共享，创建者: {}, 成员数: {}", 
            request.getCreatorId(), request.getMemberIds().size());
        
        // 创建共享会话
        GroupLocationSharing sharing = GroupLocationSharing.builder()
            .groupId(generateGroupId())
            .name(request.getName())
            .creatorId(request.getCreatorId())
            .memberIds(new HashSet<>(request.getMemberIds()))
            .destinationLatitude(request.getDestinationLatitude())
            .destinationLongitude(request.getDestinationLongitude())
            .destinationName(request.getDestinationName())
            .status(SharingStatus.ACTIVE)
            .sharingLevel(request.getSharingLevel())
            .startTime(LocalDateTime.now())
            .expireTime(LocalDateTime.now().plusHours(request.getDurationHours()))
            .build();
        
        sharingRepository.save(sharing);
        
        // 通知所有成员
        for (Long memberId : request.getMemberIds()) {
            notifyMemberSharingStarted(memberId, sharing);
        }
        
        // 缓存到Redis
        redisTemplate.opsForValue().set(
            GROUP_SHARING_KEY + sharing.getGroupId(),
            sharing,
            request.getDurationHours(),
            TimeUnit.HOURS
        );
        
        log.info("群组位置共享创建成功，GroupID: {}", sharing.getGroupId());
        return sharing;
    }
    
    /**
     * 更新群组位置
     */
    public void updateGroupMemberLocation(String groupId, Long memberId, LocationUpdate location) {
        GroupLocationSharing sharing = getGroupSharing(groupId);
        if (sharing == null || sharing.getStatus() != SharingStatus.ACTIVE) {
            return;
        }
        
        // 更新成员位置
        MemberLocation memberLocation = MemberLocation.builder()
            .userId(memberId)
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .updateTime(LocalDateTime.now())
            .status(determineMemberStatus(memberId, location, sharing))
            .build();
        
        sharing.getMemberLocations().put(memberId, memberLocation);
        sharingRepository.save(sharing);
        
        // 广播给所有成员
        broadcastMemberLocation(groupId, memberLocation);
        
        // 检查到达状态
        checkMemberArrival(memberId, location, sharing);
    }
    
    /**
     * 获取群组所有成员位置
     */
    public GroupLocationSnapshot getGroupLocations(String groupId, Long requestUserId) {
        GroupLocationSharing sharing = getGroupSharing(groupId);
        if (sharing == null) {
            throw new GeofenceException("群组不存在");
        }
        
        if (!sharing.getMemberIds().contains(requestUserId)) {
            throw new GeofenceException("无权查看此群组位置");
        }
        
        List<MemberLocationDTO> locations = sharing.getMemberLocations().values().stream()
            .map(loc -> MemberLocationDTO.builder()
                .userId(loc.getUserId())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .status(loc.getStatus())
                .updateTime(loc.getUpdateTime())
                .build())
            .collect(Collectors.toList());
        
        return GroupLocationSnapshot.builder()
            .groupId(groupId)
            .groupName(sharing.getName())
            .destination(GeoPoint.builder()
                .latitude(sharing.getDestinationLatitude())
                .longitude(sharing.getDestinationLongitude())
                .name(sharing.getDestinationName())
                .build())
            .memberLocations(locations)
            .build();
    }
    
    /**
     * 结束群组位置共享
     */
    @Transactional
    public void endGroupSharing(String groupId, Long userId) {
        GroupLocationSharing sharing = getGroupSharing(groupId);
        if (sharing == null) {
            return;
        }
        
        if (!sharing.getCreatorId().equals(userId)) {
            throw new GeofenceException("只有创建者可以结束共享");
        }
        
        sharing.setStatus(SharingStatus.ENDED);
        sharing.setEndTime(LocalDateTime.now());
        sharingRepository.save(sharing);
        
        // 通知所有成员
        for (Long memberId : sharing.getMemberIds()) {
            notifyMemberSharingEnded(memberId, sharing);
        }
        
        // 清除缓存
        redisTemplate.delete(GROUP_SHARING_KEY + groupId);
    }
    
    /**
     * 定时清理过期数据
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行
    public void cleanupExpiredData() {
        LocalDateTime now = LocalDateTime.now();
        
        // 清理过期围栏
        List<Geofence> expiredGeofences = geofenceRepository
            .findByEndTimeBeforeAndStatus(now, GeofenceStatus.ACTIVE);
        
        for (Geofence geofence : expiredGeofences) {
            geofence.setStatus(GeofenceStatus.EXPIRED);
            geofenceRepository.save(geofence);
        }
        
        // 清理过期群组共享
        List<GroupLocationSharing> expiredSharing = sharingRepository
            .findByExpireTimeBeforeAndStatus(now, SharingStatus.ACTIVE);
        
        for (GroupLocationSharing sharing : expiredSharing) {
            sharing.setStatus(SharingStatus.EXPIRED);
            sharingRepository.save(sharing);
            redisTemplate.delete(GROUP_SHARING_KEY + sharing.getGroupId());
        }
    }
    
    // ==================== 私有方法 ====================
    
    private void saveUserLocation(Long userId, LocationUpdate update) {
        String key = USER_LOCATION_KEY + userId;
        redisTemplate.opsForValue().set(key, update, LOCATION_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }
    
    private List<Geofence> findRelevantGeofences(Long userId, LocationUpdate location) {
        // 1. 获取用户关联的围栏
        List<Geofence> userGeofences = geofenceRepository
            .findByTargetIdAndTargetTypeAndStatus(userId, TargetType.USER, GeofenceStatus.ACTIVE);
        
        // 2. 获取附近的全局围栏
        List<Geofence> nearbyGeofences = geofenceRepository
            .findNearbyGeofences(location.getLatitude(), location.getLongitude(), GEOFENCE_CHECK_RADIUS);
        
        // 合并去重
        Set<Long> seen = new HashSet<>();
        List<Geofence> result = new ArrayList<>();
        
        for (Geofence geofence : userGeofences) {
            if (seen.add(geofence.getId())) {
                result.add(geofence);
            }
        }
        
        for (Geofence geofence : nearbyGeofences) {
            if (seen.add(geofence.getId())) {
                result.add(geofence);
            }
        }
        
        return result;
    }
    
    private GeofenceEventType detectGeofenceEvent(Long userId, Geofence geofence, LocationUpdate location) {
        String statusKey = GEOFENCE_STATUS_KEY + userId + ":" + geofence.getId();
        String previousStatus = (String) redisTemplate.opsForValue().get(statusKey);
        
        boolean isInside = isInsideGeofence(location, geofence);
        boolean wasInside = "INSIDE".equals(previousStatus);
        
        if (isInside && !wasInside) {
            return GeofenceEventType.ENTER;
        } else if (!isInside && wasInside) {
            return GeofenceEventType.EXIT;
        } else if (isInside && wasInside) {
            // 检查是否满足停留条件
            Long enterTime = (Long) redisTemplate.opsForValue().get(statusKey + ":enter_time");
            if (enterTime != null) {
                long durationMinutes = (System.currentTimeMillis() - enterTime) / 60000;
                Integer dwellThreshold = geofence.getDwellTimeMinutes();
                if (dwellThreshold != null && durationMinutes >= dwellThreshold) {
                    Boolean dwellTriggered = (Boolean) redisTemplate.opsForValue()
                        .get(statusKey + ":dwell_triggered");
                    if (dwellTriggered == null || !dwellTriggered) {
                        redisTemplate.opsForValue().set(statusKey + ":dwell_triggered", true);
                        return GeofenceEventType.DWELL;
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean isInsideGeofence(LocationUpdate location, Geofence geofence) {
        switch (geofence.getShape()) {
            case CIRCLE:
                return calculateDistance(
                    location.getLatitude(), location.getLongitude(),
                    geofence.getCenterLatitude(), geofence.getCenterLongitude()
                ) <= geofence.getRadius();
                
            case POLYGON:
                return isPointInPolygon(
                    location.getLatitude(), location.getLongitude(),
                    geofence.getCoordinates()
                );
                
            default:
                return false;
        }
    }
    
    private boolean isPointInPolygon(double lat, double lng, List<GeoCoordinate> coordinates) {
        boolean inside = false;
        int j = coordinates.size() - 1;
        
        for (int i = 0; i < coordinates.size(); j = i++) {
            GeoCoordinate pi = coordinates.get(i);
            GeoCoordinate pj = coordinates.get(j);
            
            if (((pi.getLongitude() > lng) != (pj.getLongitude() > lng)) &&
                (lat < (pj.getLatitude() - pi.getLatitude()) * 
                       (lng - pi.getLongitude()) / 
                       (pj.getLongitude() - pi.getLongitude()) + pi.getLatitude())) {
                inside = !inside;
            }
        }
        
        return inside;
    }
    
    private GeofenceEvent createGeofenceEvent(Long userId, Geofence geofence, 
                                               GeofenceEventType eventType, 
                                               LocationUpdate location) {
        GeofenceEvent event = GeofenceEvent.builder()
            .userId(userId)
            .geofenceId(geofence.getId())
            .eventType(eventType)
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .accuracy(location.getAccuracy())
            .eventTime(LocalDateTime.now())
            .build();
        
        return eventRepository.save(event);
    }
    
    private void executeTriggerRules(Geofence geofence, GeofenceEvent event, Long userId) {
        List<GeofenceTriggerRule> rules = ruleRepository
            .findByGeofenceIdAndEventType(geofence.getId(), event.getEventType());
        
        for (GeofenceTriggerRule rule : rules) {
            if (rule.isActive() && isRuleConditionMet(rule, event)) {
                executeTriggerAction(rule, event, userId);
            }
        }
    }
    
    private boolean isRuleConditionMet(GeofenceTriggerRule rule, GeofenceEvent event) {
        // 检查时间窗口
        LocalDateTime now = LocalDateTime.now();
        if (rule.getStartTime() != null && now.isBefore(rule.getStartTime())) {
            return false;
        }
        if (rule.getEndTime() != null && now.isAfter(rule.getEndTime())) {
            return false;
        }
        
        // 检查触发次数限制
        if (rule.getMaxTriggers() != null && rule.getTriggerCount() >= rule.getMaxTriggers()) {
            return false;
        }
        
        return true;
    }
    
    private void executeTriggerAction(GeofenceTriggerRule rule, GeofenceEvent event, Long userId) {
        switch (rule.getActionType()) {
            case SEND_MESSAGE:
                sendGeofenceMessage(userId, rule, event);
                break;
            case PUSH_NOTIFICATION:
                pushGeofenceNotification(userId, rule, event);
                break;
            case UPDATE_STATUS:
                updateUserStatus(userId, rule.getActionData());
                break;
            case CALL_API:
                callExternalApi(rule.getActionData());
                break;
            default:
                break;
        }
        
        // 更新触发计数
        rule.setTriggerCount(rule.getTriggerCount() + 1);
        rule.setLastTriggerTime(LocalDateTime.now());
        ruleRepository.save(rule);
    }
    
    private void sendGeofenceMessage(Long userId, GeofenceTriggerRule rule, GeofenceEvent event) {
        Map<String, String> data = rule.getActionData();
        String content = data.get("message");
        if (content == null) return;
        
        // 替换变量
        content = content.replace("${eventType}", event.getEventType().getDescription());
        
        GeofenceMessageDTO message = GeofenceMessageDTO.builder()
            .type(MessageType.GEOFENCE_TRIGGER)
            .content(content)
            .geofenceId(event.getGeofenceId())
            .eventType(event.getEventType())
            .timestamp(event.getEventTime())
            .build();
        
        webSocketPushService.pushToUser(userId, "GEOFENCE_MESSAGE", message);
    }
    
    private void pushGeofenceNotification(Long userId, GeofenceTriggerRule rule, GeofenceEvent event) {
        Map<String, String> data = rule.getActionData();
        
        GeofenceNotificationDTO notification = GeofenceNotificationDTO.builder()
            .title(data.get("title"))
            .body(data.get("body"))
            .geofenceId(event.getGeofenceId())
            .eventType(event.getEventType())
            .build();
        
        webSocketPushService.pushToUser(userId, "GEOFENCE_NOTIFICATION", notification);
    }
    
    private void updateUserStatus(Long userId, Map<String, String> data) {
        String status = data.get("status");
        // 实现状态更新逻辑
    }
    
    private void callExternalApi(Map<String, String> data) {
        String apiUrl = data.get("apiUrl");
        // 实现外部API调用
    }
    
    private void updateGeofenceStatus(Long userId, Geofence geofence, GeofenceEventType event) {
        String statusKey = GEOFENCE_STATUS_KEY + userId + ":" + geofence.getId();
        
        switch (event) {
            case ENTER:
                redisTemplate.opsForValue().set(statusKey, "INSIDE", 24, TimeUnit.HOURS);
                redisTemplate.opsForValue().set(
                    statusKey + ":enter_time", 
                    System.currentTimeMillis(), 
                    24, TimeUnit.HOURS
                );
                redisTemplate.delete(statusKey + ":dwell_triggered");
                break;
            case EXIT:
                redisTemplate.opsForValue().set(statusKey, "OUTSIDE", 24, TimeUnit.HOURS);
                redisTemplate.delete(statusKey + ":enter_time");
                redisTemplate.delete(statusKey + ":dwell_triggered");
                break;
            default:
                break;
        }
    }
    
    private void checkGroupLocationSharing(Long userId, LocationUpdate location) {
        // 获取用户参与的所有活跃群组共享
        List<GroupLocationSharing> activeSharings = sharingRepository
            .findByMemberIdsContainingAndStatus(userId, SharingStatus.ACTIVE);
        
        for (GroupLocationSharing sharing : activeSharings) {
            updateGroupMemberLocation(sharing.getGroupId(), userId, location);
        }
    }
    
    private MemberStatus determineMemberStatus(Long memberId, LocationUpdate location, 
                                                GroupLocationSharing sharing) {
        double distanceToDestination = calculateDistance(
            location.getLatitude(), location.getLongitude(),
            sharing.getDestinationLatitude(), sharing.getDestinationLongitude()
        );
        
        if (distanceToDestination <= 100) { // 100米内视为到达
            return MemberStatus.ARRIVED;
        }
        
        // 计算ETA
        if (location.getSpeed() != null && location.getSpeed() > 0) {
            double etaMinutes = distanceToDestination / (location.getSpeed() * 60);
            if (etaMinutes <= 5) {
                return MemberStatus.NEARBY;
            }
        }
        
        return MemberStatus.EN_ROUTE;
    }
    
    private void checkMemberArrival(Long memberId, LocationUpdate location, 
                                     GroupLocationSharing sharing) {
        double distance = calculateDistance(
            location.getLatitude(), location.getLongitude(),
            sharing.getDestinationLatitude(), sharing.getDestinationLongitude()
        );
        
        if (distance <= 100) { // 到达范围
            // 通知其他成员
            for (Long otherMemberId : sharing.getMemberIds()) {
                if (!otherMemberId.equals(memberId)) {
                    notifyMemberArrived(otherMemberId, memberId, sharing);
                }
            }
        }
    }
    
    private void broadcastMemberLocation(String groupId, MemberLocation location) {
        GroupLocationSharing sharing = getGroupSharing(groupId);
        if (sharing == null) return;
        
        MemberLocationUpdateDTO update = MemberLocationUpdateDTO.builder()
            .groupId(groupId)
            .userId(location.getUserId())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .status(location.getStatus())
            .updateTime(location.getUpdateTime())
            .build();
        
        for (Long memberId : sharing.getMemberIds()) {
            webSocketPushService.pushToUser(memberId, "GROUP_LOCATION_UPDATE", update);
        }
    }
    
    private GroupLocationSharing getGroupSharing(String groupId) {
        // 先查缓存
        GroupLocationSharing cached = (GroupLocationSharing) redisTemplate
            .opsForValue().get(GROUP_SHARING_KEY + groupId);
        
        if (cached != null) {
            return cached;
        }
        
        // 查数据库
        return sharingRepository.findByGroupId(groupId).orElse(null);
    }
    
    private void notifyMemberSharingStarted(Long memberId, GroupLocationSharing sharing) {
        webSocketPushService.pushToUser(memberId, "GROUP_SHARING_STARTED",
            GroupSharingNotification.builder()
                .groupId(sharing.getGroupId())
                .groupName(sharing.getName())
                .creatorId(sharing.getCreatorId())
                .destination(sharing.getDestinationName())
                .build());
    }
    
    private void notifyMemberSharingEnded(Long memberId, GroupLocationSharing sharing) {
        webSocketPushService.pushToUser(memberId, "GROUP_SHARING_ENDED",
            GroupSharingNotification.builder()
                .groupId(sharing.getGroupId())
                .groupName(sharing.getName())
                .build());
    }
    
    private void notifyMemberArrived(Long memberId, Long arrivedMemberId, 
                                      GroupLocationSharing sharing) {
        webSocketPushService.pushToUser(memberId, "MEMBER_ARRIVED",
            MemberArrivedNotification.builder()
                .groupId(sharing.getGroupId())
                .memberId(arrivedMemberId)
                .destination(sharing.getDestinationName())
                .build());
    }
    
    private String generateGroupId() {
        return "GRP" + System.currentTimeMillis() + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
    
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000;
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(lat1Rad) * Math.cos(lat2Rad) *
            Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private void createDefaultTriggerRules(Geofence geofence, List<TriggerAction> actions) {
        // 创建默认触发规则
        for (TriggerAction action : actions) {
            GeofenceTriggerRule rule = GeofenceTriggerRule.builder()
                .geofenceId(geofence.getId())
                .eventType(action.getEventType())
                .actionType(action.getActionType())
                .actionData(action.getData())
                .active(true)
                .triggerCount(0)
                .createTime(LocalDateTime.now())
                .build();
            
            ruleRepository.save(rule);
        }
    }
}
