package com.im.backend.service;

import com.im.backend.dto.LocationMessageRequest;
import com.im.backend.dto.LocationMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 位置消息服务接口
 * 功能#26: 位置消息
 */
public interface LocationMessageService {
    
    LocationMessageResponse sendLocationMessage(Long senderId, LocationMessageRequest request);
    
    LocationMessageResponse getLocationMessage(String messageId);
    
    Page<LocationMessageResponse> getLocationHistory(Long userId, Pageable pageable);
    
    Page<LocationMessageResponse> getConversationLocations(Long userId1, Long userId2, Pageable pageable);
    
    Page<LocationMessageResponse> getGroupLocations(Long groupId, Pageable pageable);
    
    List<LocationMessageResponse> getNearbyLocations(BigDecimal latitude, BigDecimal longitude, double radiusKm);
    
    void markAsRead(String messageId);
    
    Long getUnreadCount(Long userId);
}
