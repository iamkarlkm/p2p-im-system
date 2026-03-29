package com.im.backend.controller;

import com.im.backend.dto.PinConversationRequest;
import com.im.backend.dto.PinConversationResponse;
import com.im.backend.service.ConversationPinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations/pin")
@RequiredArgsConstructor
public class ConversationPinController {
    private final ConversationPinService pinService;

    @PostMapping
    public ResponseEntity<PinConversationResponse> pinConversation(
            @Valid @RequestBody PinConversationRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        PinConversationResponse response = pinService.pinConversation(request, userId);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<PinConversationResponse> unpinConversation(
            @PathVariable Long conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(pinService.unpinConversation(conversationId, userId));
    }

    @GetMapping
    public ResponseEntity<List<PinConversationResponse.PinnedConversationDTO>> getPinned(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(pinService.getPinnedConversations(userId));
    }

    @PutMapping("/reorder")
    public ResponseEntity<PinConversationResponse> reorder(
            @RequestBody Map<String, List<Long>> body,
            @RequestHeader("X-User-Id") Long userId) {
        List<Long> ids = body.get("conversationIds");
        return ResponseEntity.ok(pinService.reorderPinned(ids, userId));
    }

    @GetMapping("/{conversationId}/status")
    public ResponseEntity<Map<String, Boolean>> isPinned(
            @PathVariable Long conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("pinned", pinService.isPinned(userId, conversationId)));
    }
}
