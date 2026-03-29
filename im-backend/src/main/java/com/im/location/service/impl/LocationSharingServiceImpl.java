package com.im.location.service.impl;

import com.im.common.utils.SnowflakeIdGenerator;
import com.im.location.dto.*;
import com.im.location.entity.LocationSharingMember;
import com.im.location.entity.LocationSharingSession;
import com.im.location.enums.*;
import com.im.location.repository.LocationSharingMemberMapper;
import com.im.location.repository.LocationSharingSessionMapper;
import com.im.location.service.IGeofenceService;
import com.im.location.service.ILocationSharingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private final IGeofenceService geofenceService;
    
    @Override
    @Transactional
    public LocationSharingSessionResponse createSession(Long userId, CreateLocationSharingRequest request) {
        String sessionId = "LS" + SnowflakeIdGenerator.nextId();
        
        LocationSharingSession session = new LocationSharingSession();
        session.setSessionId(sessionId);
        session.setSessionType(request.getSessionType());
        session.setGroupId(request.getGroupId());
        session.setCreatorId(userId);
        session.setTitle(request.getTitle());
        session.setDestinationName(request.getDestinationName());
        session.setDestLongitude(request.getDestLongitude());
        session.setDestLatitude(request.getDestLatitude());
        session.setGeofenceRadius(request.getGeofenceRadius() != null ? request.getGeofenceRadius() : 500);
        session.setPrecisionLevel(request.getPrecisionLevel());
        session.setStatus(SharingSessionStatus.CREATED.getCode());
        session.setExpireTime(LocalDateTime.now().plusHours(request.getValidityHours() != null ? request.getValidityHours() : 24));
        session.setUpdateInterval(request.getUpdateInterval() != null ? request.getUpdateInterval() : 10);
        session.setParticipantCount(1);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        
        sessionMapper.insert(session);
        
        // 创建者自动加入
        LocationSharingMember creator = new LocationSharingMember();
        creator.setSessionId(sessionId);
        creator.setUserId(userId);
        creator.setMemberStatus(SharingMemberStatus.JOINED.getCode());
        creator.setJoinTime(LocalDateTime.now());
        creator.setCreateTime(LocalDateTime.now());
        creator.setUpdateTime(LocalDateTime.now());
        memberMapper.insert(creator);
        
        // 邀请其他成员
        if (request.getInviteeIds() != null) {
            for (Long inviteeId : request.getInviteeIds()) {
                if (!inviteeId.equals(userId)) {
                    LocationSharingMember member = new LocationSharingMember();
                    member.setSessionId(sessionId);
                    member.setUserId(inviteeId);
                    member.setMemberStatus(SharingMemberStatus.PENDING.getCode());
                    member.setCreateTime(LocalDateTime.now());
                    member.setUpdateTime(LocalDateTime.now());
                    memberMapper.insert(member);
                }
            }
        }
        
        return convertToSessionResponse(session);
    }
    
    @Override
    public LocationSharingSessionResponse getSessionDetail(String sessionId) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            return null;
        }
        return convertToSessionResponse(session);
    }
    
    @Override
    @Transactional
    public void joinSession(Long userId, JoinLocationSharingRequest request) {
        LocationSharingMember member = memberMapper.selectBySessionAndUser(request.getSessionId(), userId);
        
        if (member == null) {
            member = new LocationSharingMember();
            member.setSessionId(request.getSessionId());
            member.setUserId(userId);
            member.setMemberStatus(SharingMemberStatus.JOINED.getCode());
            member.setLongitude(request.getLongitude());
            member.setLatitude(request.getLatitude());
            member.setJoinTime(LocalDateTime.now());
            member.setCreateTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            memberMapper.insert(member);
            sessionMapper.incrementParticipantCount(request.getSessionId());
        } else {
            member.setMemberStatus(SharingMemberStatus.JOINED.getCode());
            member.setLongitude(request.getLongitude());
            member.setLatitude(request.getLatitude());
            member.setJoinTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            memberMapper.updateById(member);
        }
        
        // 启动会话
        LocationSharingSession session = sessionMapper.selectBySessionId(request.getSessionId());
        if (session != null && session.getStatus().equals(SharingSessionStatus.CREATED.getCode())) {
            sessionMapper.updateStatus(request.getSessionId(), SharingSessionStatus.ACTIVE.getCode());
        }
    }
    
    @Override
    @Transactional
    public void leaveSession(Long userId, String sessionId) {
        memberMapper.updateMemberStatus(sessionId, userId, SharingMemberStatus.LEFT.getCode());
        sessionMapper.decrementParticipantCount(sessionId);
    }
    
    @Override
    @Transactional
    public void updateLocation(Long userId, UpdateLocationRequest request) {
        memberMapper.updateLocation(request.getSessionId(), userId, 
            request.getLongitude(), request.getLatitude(),
            request.getAccuracy(), request.getAltitude(),
            request.getSpeed(), request.getBearing(),
            request.getBatteryLevel());
        
        // 检查围栏触发
        checkGeofenceTrigger(request.getSessionId(), userId, request.getLongitude(), request.getLatitude());
    }
    
    @Override
    public void updateSessionStatus(Long userId, String sessionId, Integer status) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null || !session.getCreatorId().equals(userId)) {
            return;
        }
        sessionMapper.updateStatus(sessionId, status);
    }
    
    @Override
    public List<LocationSharingSessionResponse> getUserActiveSessions(Long userId) {
        List<LocationSharingSession> sessions = sessionMapper.selectActiveSessionsByUserId(userId);
        return sessions.stream().map(this::convertToSessionResponse).collect(Collectors.toList());
    }
    
    @Override
    public List<LocationSharingMemberResponse> getSessionMembers(String sessionId) {
        List<LocationSharingMember> members = memberMapper.selectBySessionId(sessionId);
        return members.stream().map(this::convertToMemberResponse).collect(Collectors.toList());
    }
    
    @Override
    public Integer calculateETA(String sessionId, Double longitude, Double latitude) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null || session.getDestLongitude() == null || session.getDestLatitude() == null) {
            return null;
        }
        
        double distance = geofenceService.calculateDistance(
            longitude, latitude, session.getDestLongitude(), session.getDestLatitude());
        
        // 假设平均速度 30km/h = 8.33m/s
        int etaMinutes = (int) Math.ceil(distance / 500);
        return Math.max(1, Math.min(etaMinutes, 120));
    }
    
    @Override
    public void checkGeofenceTrigger(String sessionId, Long userId, Double longitude, Double latitude) {
        LocationSharingSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null || session.getDestLongitude() == null) {
            return;
        }
        
        // 检查目的地围栏
        boolean inDestination = geofenceService.isPointInCircle(
            longitude, latitude, 
            session.getDestLongitude(), session.getDestLatitude(),
            session.getGeofenceRadius()
        );
        
        LocationSharingMember member = memberMapper.selectBySessionAndUser(sessionId, userId);
        if (member == null) return;
        
        if (inDestination && member.getInGeofence() != null && member.getInGeofence() == 0) {
            memberMapper.updateEnterGeofence(sessionId, userId);
            memberMapper.updateArrivalStatus(sessionId, userId, ArrivalStatus.ARRIVED.getCode(), 0);
        } else if (!inDestination && member.getInGeofence() != null && member.getInGeofence() == 1) {
            memberMapper.updateExitGeofence(sessionId, userId);
        }
    }
    
    private LocationSharingSessionResponse convertToSessionResponse(LocationSharingSession session) {
        LocationSharingSessionResponse response = new LocationSharingSessionResponse();
        response.setSessionId(session.getSessionId());
        response.setSessionType(session.getSessionType());
        response.setSessionTypeDesc(SharingSessionType.fromCode(session.getSessionType()).getDesc());
        response.setTitle(session.getTitle());
        response.setDestinationName(session.getDestinationName());
        response.setDestLongitude(session.getDestLongitude());
        response.setDestLatitude(session.getDestLatitude());
        response.setGeofenceRadius(session.getGeofenceRadius());
        response.setPrecisionLevel(session.getPrecisionLevel());
        response.setPrecisionLevelDesc(LocationPrecisionLevel.fromCode(session.getPrecisionLevel()).getDesc());
        response.setStatus(session.getStatus());
        response.setStatusDesc(SharingSessionStatus.fromCode(session.getStatus()).getDesc());
        response.setExpireTime(session.getExpireTime());
        response.setParticipantCount(session.getParticipantCount());
        response.setUpdateInterval(session.getUpdateInterval());
        response.setCreateTime(session.getCreateTime());
        response.setMembers(getSessionMembers(session.getSessionId()));
        return response;
    }
    
    private LocationSharingMemberResponse convertToMemberResponse(LocationSharingMember member) {
        LocationSharingMemberResponse response = new LocationSharingMemberResponse();
        response.setMemberId(member.getId());
        response.setUserId(member.getUserId());
        response.setNickname(member.getNickname());
        response.setAvatar(member.getAvatar());
        response.setMemberStatus(member.getMemberStatus());
        response.setMemberStatusDesc(SharingMemberStatus.fromCode(member.getMemberStatus()).getDesc());
        response.setLongitude(member.getLongitude());
        response.setLatitude(member.getLatitude());
        response.setAccuracy(member.getAccuracy());
        response.setSpeed(member.getSpeed());
        response.setBearing(member.getBearing());
        response.setLocationUpdateTime(member.getLocationUpdateTime());
        response.setInGeofence(member.getInGeofence() != null && member.getInGeofence() == 1);
        response.setEnterGeofenceTime(member.getEnterGeofenceTime());
        response.setArrivedStatus(member.getArrivedStatus());
        response.setArrivedStatusDesc(ArrivalStatus.fromCode(member.getArrivedStatus()).getDesc());
        response.setEtaMinutes(member.getEtaMinutes());
        response.setBatteryLevel(member.getBatteryLevel());
        response.setJoinTime(member.getJoinTime());
        return response;
    }
}
