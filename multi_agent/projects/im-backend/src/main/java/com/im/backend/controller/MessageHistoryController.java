package com.im.backend.controller;

import com.im.backend.service.MessageHistoryService;
import com.im.backend.dto.SyncRequest;
import com.im.backend.dto.SyncResponse;
import com.im.backend.dto.MessageSyncItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Slf4j
public class MessageHistoryController {

    private final MessageHistoryService messageHistoryService;

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<?> getHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "50") Integer limit) {
        SyncRequest request = SyncRequest.builder()
                .deviceId("default")
                .conversationId(conversationId)
                .lastMessageId(lastId)
                .limit(limit)
                .build();
        SyncResponse response = messageHistoryService.syncMessages(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pull")
    public ResponseEntity<?> pullSync(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody SyncRequest request) {
        SyncResponse response = messageHistoryService.syncMessages(userId, request);
        log.info("Sync pull for user {}: {} messages", userId, response.getTotalSynced());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkpoints")
    public ResponseEntity<?> getCheckpoints(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String deviceId) {
        var checkpoints = messageHistoryService.getAllCheckpoints(userId, deviceId);
        return ResponseEntity.ok(Map.of("checkpoints", checkpoints));
    }

    @DeleteMapping("/history/{messageId}")
    public ResponseEntity<?> deleteFromHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        messageHistoryService.markMessageDeleted(messageId, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
