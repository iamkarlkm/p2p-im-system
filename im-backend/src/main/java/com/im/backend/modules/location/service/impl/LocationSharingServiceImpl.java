package com.im.backend.modules.location.service.impl;

import com.im.backend.modules.location.model.dto.*;
import com.im.backend.modules.location.model.entity.*;
import com.im.backend.modules.location.model.enums.*;
import com.im.backend.modules.location.repository.*;
import com.im.backend.modules.location.service.IGeofenceDetectionService;
import com.im.backend.modules.location.service.ILocationSharingService;
import com.im.backend.modules.location.service.ILocationSharingWebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 位置共享服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationSharingServiceImpl implements ILocationSharingService {

    private final LocationSharingSessionMapper sessionMapper;
    private final LocationSharingMemberMapper memberMapper;
    private final SharedLocationRecordMapper locationRecordMapper;
    private final LocationGeofenceEventMapper eventMapper;
    private final IGeofenceDetectionService geofenceService;
    private final ILocationSharingWebSocketService webSocketService;

    @Override
    @Transactional
    public LocationShareResponse createSession(Long creatorId, CreateLocationShareRequest request) {
        String sessionId = "LS" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        
        LocationSharingSession session = new LocationSharingSession();
        session.setSessionId(sessionId);
        session.setSessionType(request.getSessionType());
        session.setCreatorId(creatorId);
        session.setTargetUserId(request.getTargetUserId());
        session.setGroupId(request.getGroupId());
        session.setTitle(request.getTitle());
        session.setDestinationName(request.getDestinationName());
        session.setDestinationLat(request.getDestinationLat());
        session.setDestinationLng(request.getDestinationLng());
        session.setDestinationRadius(request.getDestinationRadius());
        session.setLocationPrecision(request.getLocationPrecision());
        session.setExpectedEndTime(request.getExpectedEndTime());
        session.setArrivalNotificationEnabled(request.getArrivalNotificationEnabled());
        session.setDepartureNotificationEnabled(request.getDepartureNotificationEnabled());
        session.setStatus(SharingSessionStatus.ACTIVE.getCode());
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.insert(session);

        // 添加创建者为成员
        LocationSharingMember member = new LocationSharingMember();
        member.setSessionId(sessionId);
        member.setUserId(creatorId);
        member.setRole("CREATOR");
        member.setStatus(SharingMemberStatus.ACTIVE.getCode());
        member.setJoinedAt(LocalDateTime.now());
        member.setLastActiveAt(LocalDateTime.now());
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        memberMapper.insert(member);

        return convertToResponse(session);
    }

    @Override
    public LocationShareResponse getSessionDetail(String sessionId, Long userId) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在或已结束");
        }
        return convertToResponse(session);
    }

    @Override
    @Transactional
    public void joinSession(Long userId, JoinLocationShareRequest request) {
        LocationSharingSession session = sessionMapper.selectBySessionId(request.getSessionId());
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        if (!SharingSessionStatus.ACTIVE.getCode().equals(session.getStatus())) {
            throw new RuntimeException("会话已结束");
        }

        LocationSharingMember existing = memberMapper.selectBySessionIdAndUserId(request.getSessionId(), userId);
        if (existing != null) {
            memberMapper.updateStatus(request.getSessionId(), userId, SharingMemberStatus.ACTIVE.getCode());
        } else {
            LocationSharingMember member = new LocationSharingMember();
            member.setSessionId(request.getSessionId());
            member.setUserId(userId);
            member.setRole("MEMBER");
            member.setStatus(SharingMemberStatus.ACTIVE.getCode());
            member.setJoinedAt(LocalDateTime.now());
            member.setLastActiveAt(LocalDateTime.now());
            member.setCreatedAt(LocalDateTime.now());
            member.setUpdatedAt(LocalDateTime.now());
            memberMapper.insert(member);
            webSocketService.broadcastMemberJoined(request.getSessionId(), member);
        }
    }

    @Override
    @Transactional
    public void leaveSession(String sessionId, Long userId) {
        memberMapper.updateStatus(sessionId, userId, SharingMemberStatus.LEFT.getCode());
        webSocketService.broadcastMemberLeft(sessionId, userId);
        
        int activeCount = memberMapper.countActiveMembers(sessionId);
        if (activeCount == 0) {
            endSession(sessionId, userId);
        }
    }

    @Override
    public void pauseSession(String sessionId, Long userId) {
        memberMapper.updateStatus(sessionId, userId, SharingMemberStatus.PAUSED.getCode());
    }

    @Override
    public void resumeSession(String sessionId, Long userId) {
        memberMapper.updateStatus(sessionId, userId, SharingMemberStatus.ACTIVE.getCode());
    }

    @Override
    @Transactional
    public void endSession(String sessionId, Long userId) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null && session.getCreatorId().equals(userId)) {
            sessionMapper.updateStatus(sessionId, SharingSessionStatus.ENDED.getCode(), LocalDateTime.now());
            webSocketService.notifySessionEnded(sessionId, "创建者已结束会话");
        }
    }

    @Override
    @Transactional
    public void updateLocation(Long userId, LocationUpdateRequest request) {
        LocationSharingSession session = sessionMapper.selectBySessionId(request.getSessionId());
        if (session == null || !SharingSessionStatus.ACTIVE.getCode().equals(session.getStatus())) {
            return;
        }

        LocationSharingMember member = memberMapper.selectBySessionIdAndUserId(request.getSessionId(), userId);
        if (member == null || !SharingMemberStatus.ACTIVE.getCode().equals(member.getStatus())) {
            return;
        }

        // 保存位置记录
        SharedLocationRecord record = new SharedLocationRecord();
        record.setSessionId(request.getSessionId());
        record.setUserId(userId);
        record.setLatitude(request.getLatitude());
        record.setLongitude(request.getLongitude());
        record.setAccuracy(request.getAccuracy());
        record.setAltitude(request.getAltitude());
        record.setSpeed(request.getSpeed());
        record.setBearing(request.getBearing());
        record.setBatteryLevel(request.getBatteryLevel());
        record.setNetworkType(request.getNetworkType());
        record.setLocationProvider(request.getLocationProvider());
        record.setIsMoving(request.getIsMoving());
        record.setLocationTime(LocalDateTime.now());
        record.setGeoHash(geofenceService.getGeoHash(request.getLatitude(), request.getLongitude(), 7));
        record.setCreatedAt(LocalDateTime.now());
        locationRecordMapper.insert(record);

        // 计算距离目的地
        Integer distanceToDest = null;
        LocalDateTime eta = null;
        if (session.getDestinationLat() != null && session.getDestinationLng() != null) {
            double distance = geofenceService.calculateDistance(
                request.getLatitude(), request.getLongitude(),
                session.getDestinationLat(), session.getDestinationLng()
            );
            distanceToDest = (int) distance;
            
            if (request.getSpeed() != null && request.getSpeed() > 0) {
                eta = LocalDateTime.now().plusMinutes((long)(distance / request.getSpeed() / 60));
            }

            // 检查是否到达
            if (!member.getHasArrived() && distance <= session.getDestinationRadius()) {
                memberMapper.markAsArrived(request.getSessionId(), userId);
                webSocketService.broadcastMemberArrived(request.getSessionId(), userId, session.getDestinationName());
            }
        }

        // 更新成员位置
        memberMapper.updateLocation(request.getSessionId(), userId, 
            request.getLatitude(), request.getLongitude(),
            request.getAccuracy(), LocalDateTime.now(),
            request.getSpeed(), request.getBatteryLevel(),
            distanceToDest, eta);

        // 广播位置更新
        webSocketService.broadcastLocationUpdate(request.getSessionId(), userId, 
            request.getLatitude(), request.getLongitude());

        // 处理围栏事件
        geofenceService.processGeofenceEvent(request.getSessionId(), userId, 
            request.getLatitude(), request.getLongitude());
    }

    @Override
    public List<SharedLocationDTO> getMemberLocations(String sessionId, Long userId) {
        List<SharedLocationRecord> records = locationRecordMapper.selectLatestLocations(sessionId);
        return records.stream().map(this::convertToLocationDTO).collect(Collectors.toList());
    }

    @Override
    public List<LocationShareResponse> getUserActiveSessions(Long userId) {
        List<LocationSharingSession> sessions = sessionMapper.selectActiveSessionsByUserId(userId);
        return sessions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<GeofenceEventDTO> getSessionEvents(String sessionId, Long userId) {
        List<LocationGeofenceEvent> events = eventMapper.selectBySessionId(sessionId, 50);
        return events.stream().map(this::convertToEventDTO).collect(Collectors.toList());
    }

    private LocationShareResponse convertToResponse(LocationSharingSession session) {
        LocationShareResponse response = new LocationShareResponse();
        response.setSessionId(session.getSessionId());
        response.setSessionType(session.getSessionType());
        response.setCreatorId(session.getCreatorId());
        response.setTitle(session.getTitle());
        response.setDestinationName(session.getDestinationName());
        response.setDestinationLat(session.getDestinationLat());
        response.setDestinationLng(session.getDestinationLng());
        response.setDestinationRadius(session.getDestinationRadius());
        response.setLocationPrecision(session.getLocationPrecision());
        response.setStatus(session.getStatus());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        response.setExpectedEndTime(session.getExpectedEndTime());
        
        List<LocationSharingMember> members = memberMapper.selectBySessionId(session.getSessionId());
        response.setMembers(members.stream().map(this::convertToMemberDTO).collect(Collectors.toList()));
        response.setMemberCount(members.size());
        return response;
    }

    private LocationShareMemberDTO convertToMemberDTO(LocationSharingMember member) {
        LocationShareMemberDTO dto = new LocationShareMemberDTO();
        dto.setUserId(member.getUserId());
        dto.setUserNickname(member.getUserNickname());
        dto.setUserAvatar(member.getUserAvatar());
        dto.setRole(member.getRole());
        dto.setStatus(member.getStatus());
        dto.setLastLat(member.getLastLat());
        dto.setLastLng(member.getLastLng());
        dto.setLastLocationTime(member.getLastLocationTime());
        dto.setLastAccuracy(member.getLastAccuracy());
        dto.setHasArrived(member.getHasArrived());
        dto.setArrivedAt(member.getArrivedAt());
        dto.setDistanceToDestination(member.getDistanceToDestination());
        dto.setEstimatedArrivalTime(member.getEstimatedArrivalTime());
        dto.setBatteryLevel(member.getBatteryLevel());
        dto.setSpeed(member.getSpeed());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }

    private SharedLocationDTO convertToLocationDTO(SharedLocationRecord record) {
        SharedLocationDTO dto = new SharedLocationDTO();
        dto.setUserId(record.getUserId());
        dto.setLatitude(record.getLatitude());
        dto.setLongitude(record.getLongitude());
        dto.setAccuracy(record.getAccuracy());
        dto.setAltitude(record.getAltitude());
        dto.setSpeed(record.getSpeed());
        dto.setBearing(record.getBearing());
        dto.setBatteryLevel(record.getBatteryLevel());
        dto.setIsMoving(record.getIsMoving());
        dto.setLocationTime(record.getLocationTime());
        return dto;
    }

    private GeofenceEventDTO convertToEventDTO(LocationGeofenceEvent event) {
        GeofenceEventDTO dto = new GeofenceEventDTO();
        dto.setId(event.getId());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType());
        dto.setGeofenceName(event.getGeofenceName());
        dto.setTriggerLat(event.getTriggerLat());
        dto.setTriggerLng(event.getTriggerLng());
        dto.setTriggerTime(event.getTriggerTime());
        dto.setImMessageId(event.getImMessageId());
        return dto;
    }
}
