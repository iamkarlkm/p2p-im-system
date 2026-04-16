package com.im.service.message.controller;

import com.im.service.message.dto.ConversationResponse;
import com.im.service.message.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String userId) {
        String type = request.get("type");
        String name = request.get("name");
        ConversationResponse response = conversationService.createConversation(type, name, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversation(@PathVariable String conversationId) {
        return conversationService.getConversation(conversationId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ConversationResponse> conversations = conversationService.getUserConversations(userId, page, size);
        return ResponseEntity.ok(conversations);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Map<String, Object>> deleteConversation(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = conversationService.deleteConversation(conversationId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("conversationId", conversationId);
        return ResponseEntity.ok(result);
    }
}
