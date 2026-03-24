package com.im.controller;

import com.im.entity.PinRecordEntity;
import com.im.service.PinRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 置顶记录 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/pins")
@RequiredArgsConstructor
public class PinRecordController {

    private final PinRecordService pinRecordService;

    /** 置顶会话 */
    @PostMapping("/conversation/{conversationId}")
    public ResponseEntity<PinRecordEntity> pinConversation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String conversationId) {
        return ResponseEntity.ok(pinRecordService.pinConversation(userId, conversationId));
    }

    /** 取消会话置顶 */
    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> unpinConversation(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String conversationId) {
        pinRecordService.unpinConversation(userId, conversationId);
        return ResponseEntity.noContent().build();
    }

    /** 获取置顶会话列表 */
    @GetMapping("/conversations")
    public ResponseEntity<List<PinRecordEntity>> getPinnedConversations(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(pinRecordService.getPinnedConversations(userId));
    }

    /** 检查会话是否置顶 */
    @GetMapping("/conversation/{conversationId}/status")
    public ResponseEntity<Map<String, Boolean>> isConversationPinned(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String conversationId) {
        return ResponseEntity.ok(Map.of("pinned", pinRecordService.isConversationPinned(userId, conversationId)));
    }

    /** 置顶消息 */
    @PostMapping("/message/{messageId}")
    public ResponseEntity<PinRecordEntity> pinMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId,
            @RequestBody Map<String, String> body) {
        String conversationId = body.get("conversationId");
        String note = body.get("note");
        return ResponseEntity.ok(pinRecordService.pinMessage(userId, conversationId, messageId, note));
    }

    /** 取消消息置顶 */
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> unpinMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId) {
        pinRecordService.unpinMessage(userId, messageId);
        return ResponseEntity.noContent().build();
    }

    /** 获取会话内的置顶消息 */
    @GetMapping("/messages/conversation/{conversationId}")
    public ResponseEntity<List<PinRecordEntity>> getPinnedMessages(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String conversationId) {
        return ResponseEntity.ok(pinRecordService.getPinnedMessages(userId, conversationId));
    }

    /** 重新排序置顶会话 */
    @PutMapping("/conversations/reorder")
    public ResponseEntity<Void> reorderPinnedConversations(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, List<String>> body) {
        List<String> orderedIds = body.get("conversationIds");
        pinRecordService.reorderPinnedConversations(userId, orderedIds);
        return ResponseEntity.ok().build();
    }
}
