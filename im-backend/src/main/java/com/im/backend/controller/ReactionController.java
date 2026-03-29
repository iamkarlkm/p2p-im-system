package com.im.backend.controller;

import com.im.backend.dto.ReactionDTO;
import com.im.backend.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    /** 添加/切换表情反应 (toggle) */
    @PostMapping("/message/{messageId}")
    public ResponseEntity<ReactionDTO> toggleReaction(
            @PathVariable Long messageId,
            @RequestParam String emoji,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Boolean isCustom) {
        return ResponseEntity.ok(reactionService.toggleReaction(messageId, userId, emoji, isCustom));
    }

    /** 获取消息反应统计 */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<ReactionDTO> getReactions(
            @PathVariable Long messageId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(reactionService.getReactionStats(messageId, userId));
    }

    /** 批量获取多个消息的反应统计 */
    @PostMapping("/batch")
    public ResponseEntity<Map<Long, ReactionDTO>> batchGetReactions(
            @RequestBody List<Long> messageIds,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(reactionService.batchGetReactionStats(messageIds, userId));
    }
}
