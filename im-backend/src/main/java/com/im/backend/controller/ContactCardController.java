package com.im.backend.controller;

import com.im.backend.dto.ContactCardRequest;
import com.im.backend.dto.ContactCardResponse;
import com.im.backend.service.ContactCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 名片分享控制器
 * 功能#27: 名片分享
 */
@RestController
@RequestMapping("/api/contact-card")
public class ContactCardController {
    
    @Autowired
    private ContactCardService contactCardService;
    
    @PostMapping("/send")
    public ResponseEntity<ContactCardResponse> sendContactCard(
            @RequestAttribute("userId") Long senderId,
            @RequestBody ContactCardRequest request) {
        ContactCardResponse response = contactCardService.sendContactCard(senderId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<ContactCardResponse> getContactCard(@PathVariable String messageId) {
        ContactCardResponse response = contactCardService.getContactCard(messageId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<ContactCardResponse>> getContactCardHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactCardResponse> response = contactCardService.getContactCardHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<ContactCardResponse>> getConversationCards(
            @RequestAttribute("userId") Long currentUserId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactCardResponse> response = contactCardService.getConversationCards(currentUserId, userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<ContactCardResponse>> getGroupCards(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactCardResponse> response = contactCardService.getGroupCards(groupId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String messageId) {
        contactCardService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestAttribute("userId") Long userId) {
        Long count = contactCardService.getUnreadCount(userId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }
}
