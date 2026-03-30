package com.im.backend.controller;

import com.im.backend.dto.MentionRequest;
import com.im.backend.dto.MentionResponse;
import com.im.backend.service.MentionService;
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
 * @提醒控制器
 * 功能#28: 消息@提醒
 */
@RestController
@RequestMapping("/api/mention")
public class MentionController {
    
    @Autowired
    private MentionService mentionService;
    
    @PostMapping("/create")
    public ResponseEntity<MentionResponse> createMention(
            @RequestAttribute("userId") Long senderId,
            @RequestBody MentionRequest request) {
        MentionResponse response = mentionService.createMention(senderId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<MentionResponse> getMention(@PathVariable String messageId) {
        MentionResponse response = mentionService.getMention(messageId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-mentions")
    public ResponseEntity<Page<MentionResponse>> getUserMentions(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MentionResponse> response = mentionService.getUserMentions(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<MentionResponse>> getUnreadMentions(@RequestAttribute("userId") Long userId) {
        List<MentionResponse> response = mentionService.getUnreadMentions(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<MentionResponse>> getGroupMentions(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MentionResponse> response = mentionService.getGroupMentions(groupId, userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        mentionService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestAttribute("userId") Long userId) {
        mentionService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Long count = mentionService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/original/{originalMessageId}")
    public ResponseEntity<List<MentionResponse>> getMentionsByOriginalMessage(@PathVariable String originalMessageId) {
        List<MentionResponse> response = mentionService.getMentionsByOriginalMessage(originalMessageId);
        return ResponseEntity.ok(response);
    }
}
