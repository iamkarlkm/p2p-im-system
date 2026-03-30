package com.im.backend.controller;

import com.im.backend.dto.LocationMessageRequest;
import com.im.backend.dto.LocationMessageResponse;
import com.im.backend.service.LocationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 位置消息控制器
 * 功能#26: 位置消息
 */
@RestController
@RequestMapping("/api/location-message")
public class LocationMessageController {
    
    @Autowired
    private LocationMessageService locationMessageService;
    
    @PostMapping("/send")
    public ResponseEntity<LocationMessageResponse> sendLocationMessage(
            @RequestAttribute("userId") Long senderId,
            @RequestBody LocationMessageRequest request) {
        LocationMessageResponse response = locationMessageService.sendLocationMessage(senderId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<LocationMessageResponse> getLocationMessage(@PathVariable String messageId) {
        LocationMessageResponse response = locationMessageService.getLocationMessage(messageId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<LocationMessageResponse>> getLocationHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LocationMessageResponse> response = locationMessageService.getLocationHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<LocationMessageResponse>> getConversationLocations(
            @RequestAttribute("userId") Long currentUserId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LocationMessageResponse> response = locationMessageService.getConversationLocations(currentUserId, userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<LocationMessageResponse>> getGroupLocations(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LocationMessageResponse> response = locationMessageService.getGroupLocations(groupId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<LocationMessageResponse>> getNearbyLocations(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        List<LocationMessageResponse> response = locationMessageService.getNearbyLocations(latitude, longitude, radiusKm);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        locationMessageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Long count = locationMessageService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
}
