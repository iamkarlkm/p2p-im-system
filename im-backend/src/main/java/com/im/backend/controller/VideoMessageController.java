package com.im.backend.controller;

import com.im.backend.dto.VideoMessageRequest;
import com.im.backend.dto.VideoMessageResponse;
import com.im.backend.service.VideoMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频消息控制器
 * 功能#25: 视频消息
 */
@RestController
@RequestMapping("/api/video-message")
public class VideoMessageController {
    
    @Autowired
    private VideoMessageService videoMessageService;
    
    @PostMapping("/send")
    public ResponseEntity<VideoMessageResponse> sendVideoMessage(
            @RequestAttribute("userId") Long senderId,
            @RequestBody VideoMessageRequest request) {
        VideoMessageResponse response = videoMessageService.sendVideoMessage(senderId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<VideoMessageResponse> getVideoMessage(@PathVariable String messageId) {
        VideoMessageResponse response = videoMessageService.getVideoMessage(messageId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<VideoMessageResponse>> getVideoHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VideoMessageResponse> response = videoMessageService.getVideoHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<VideoMessageResponse>> getConversationVideos(
            @RequestAttribute("userId") Long currentUserId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VideoMessageResponse> response = videoMessageService.getConversationVideos(currentUserId, userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<VideoMessageResponse>> getGroupVideos(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VideoMessageResponse> response = videoMessageService.getGroupVideos(groupId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        videoMessageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Long count = videoMessageService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<VideoMessageResponse>> getRecentVideos(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "7") int days) {
        List<VideoMessageResponse> response = videoMessageService.getRecentVideos(userId, days);
        return ResponseEntity.ok(response);
    }
}
