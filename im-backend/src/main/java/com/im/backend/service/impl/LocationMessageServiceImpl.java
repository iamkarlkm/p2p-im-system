package com.im.backend.service.impl;

import com.im.backend.dto.LocationMessageRequest;
import com.im.backend.dto.LocationMessageResponse;
import com.im.backend.entity.ConversationType;
import com.im.backend.entity.LocationMessage;
import com.im.backend.repository.LocationMessageRepository;
import com.im.backend.service.LocationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 位置消息服务实现
 * 功能#26: 位置消息
 */
@Service
public class LocationMessageServiceImpl implements LocationMessageService {
    
    @Autowired
    private LocationMessageRepository locationMessageRepository;
    
    @Override
    public LocationMessageResponse sendLocationMessage(Long senderId, LocationMessageRequest request) {
        LocationMessage message = new LocationMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setGroupId(request.getGroupId());
        message.setConversationType(ConversationType.valueOf(request.getConversationType()));
        message.setLatitude(request.getLatitude());
        message.setLongitude(request.getLongitude());
        message.setAddress(request.getAddress());
        message.setLocationName(request.getLocationName());
        message.setPoiId(request.getPoiId());
        message.setSnapshotUrl(request.getSnapshotUrl());
        message.setIsRead(false);
        
        LocationMessage saved = locationMessageRepository.save(message);
        return convertToResponse(saved);
    }
    
    @Override
    public LocationMessageResponse getLocationMessage(String messageId) {
        LocationMessage message = locationMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Location message not found"));
        return convertToResponse(message);
    }
    
    @Override
    public Page<LocationMessageResponse> getLocationHistory(Long userId, Pageable pageable) {
        return locationMessageRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<LocationMessageResponse> getConversationLocations(Long userId1, Long userId2, Pageable pageable) {
        return locationMessageRepository.findByConversation(userId1, userId2, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public Page<LocationMessageResponse> getGroupLocations(Long groupId, Pageable pageable) {
        return locationMessageRepository.findByGroupId(groupId, pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public List<LocationMessageResponse> getNearbyLocations(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        return locationMessageRepository.findNearby(latitude, longitude, radiusKm)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void markAsRead(String messageId) {
        LocationMessage message = locationMessageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Location message not found"));
        locationMessageRepository.markAsRead(message.getId(), LocalDateTime.now());
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return locationMessageRepository.countUnreadByUserId(userId);
    }
    
    private LocationMessageResponse convertToResponse(LocationMessage message) {
        LocationMessageResponse response = new LocationMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setGroupId(message.getGroupId());
        response.setConversationType(message.getConversationType().name());
        response.setLatitude(message.getLatitude());
        response.setLongitude(message.getLongitude());
        response.setAddress(message.getAddress());
        response.setLocationName(message.getLocationName());
        response.setPoiId(message.getPoiId());
        response.setSnapshotUrl(message.getSnapshotUrl());
        response.setIsRead(message.getIsRead());
        response.setReadTime(message.getReadTime());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}
