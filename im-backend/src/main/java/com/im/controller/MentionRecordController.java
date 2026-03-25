package com.im.controller;

import com.im.entity.MentionRecordEntity;
import com.im.service.MentionRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Mention 提醒 REST API
 * 
 * GET    /api/mentions/unread              - 获取未读@列表
 * GET    /api/mentions                     - 获取@历史 (分页)
 * GET    /api/mentions/conversation/{id}   - 获取会话中的@提及
 * GET    /api/mentions/stats              - @提及统计
 * POST   /api/mentions/read/{id}          - 标记已读
 * POST   /api/mentions/read-all/{convId}  - 标记会话全部已读
 * POST   /api/mentions/dismiss/{id}       - 忽略@提醒
 * DELETE /api/mentions/message/{msgId}    - 删除消息时清理@记录
 * GET    /api/mentions/count              - 未读总数
 * GET    /api/mentions/count/{convId}     - 会话未读数
 * GET    /api/mentions/parse              - 解析消息内容中的@提及
 */
@Slf4j
@RestController
@RequestMapping("/api/mentions")
@RequiredArgsConstructor
public class MentionRecordController {

    private final MentionRecordService mentionService;

    // ==================== 查询接口 ====================

    /**
     * GET /api/mentions/unread
     * 获取当前用户所有未读@提及
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadMentions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<MentionRecordEntity> mentions = mentionService.getUnreadMentions(userId, page, size);
        long total = mentionService.getTotalUnreadCount(userId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("mentions", mentions);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("size", size);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /api/mentions
     * 获取@提及历史 (分页)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMentions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<MentionRecordEntity> mentions = mentionService.getUserMentions(userId, page, size);

        Map<String, Object> resp = new HashMap<>();
        resp.put("mentions", mentions);
        resp.put("page", page);
        resp.put("size", size);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /api/mentions/conversation/{conversationId}
     * 获取某会话中的所有@提及
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MentionRecordEntity>> getConversationMentions(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(mentionService.getConversationMentions(conversationId, limit));
    }

    /**
     * GET /api/mentions/count
     * 获取当前用户未读@总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        long count = mentionService.getTotalUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * GET /api/mentions/count/{conversationId}
     * 获取某会话的未读@数
     */
    @GetMapping("/count/{conversationId}")
    public ResponseEntity<Map<String, Object>> getConversationUnreadCount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        long count = mentionService.getUnreadCount(userId, conversationId);
        return ResponseEntity.ok(Map.of(
            "conversationId", conversationId,
            "unreadCount", count
        ));
    }

    /**
     * GET /api/mentions/stats
     * @提及统计数据 (仪表盘用)
     */
    @GetMapping("/stats")
    public ResponseEntity<MentionRecordService.MentionStats> getStats(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(mentionService.getStats(userId, days));
    }

    /**
     * GET /api/mentions/parse
     * 解析消息内容中的@提及 (用于发送前预览)
     */
    @GetMapping("/parse")
    public ResponseEntity<List<MentionRecordService.MentionParseResult>> parseMentions(
            @RequestParam String content) {
        return ResponseEntity.ok(mentionService.parseMentions(content));
    }

    // ==================== 操作接口 ====================

    /**
     * POST /api/mentions/read/{mentionId}
     * 标记单条@提及为已读
     */
    @PostMapping("/read/{mentionId}")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mentionId) {

        boolean ok = mentionService.markAsRead(mentionId, userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("mentionId", mentionId);
        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/mentions/read-all/{conversationId}
     * 标记某会话中所有@为已读
     */
    @PostMapping("/read-all/{conversationId}")
    public ResponseEntity<Map<String, Object>> markAllRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {

        int count = mentionService.markAllRead(conversationId, userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("conversationId", conversationId);
        resp.put("markedRead", count);
        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/mentions/dismiss/{mentionId}
     * 忽略/关闭某条@提醒
     */
    @PostMapping("/dismiss/{mentionId}")
    public ResponseEntity<Map<String, Object>> dismissMention(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long mentionId) {

        boolean ok = mentionService.dismissMention(mentionId, userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("mentionId", mentionId);
        return ResponseEntity.ok(resp);
    }

    /**
     * DELETE /api/mentions/message/{messageId}
     * 删除消息时同步清理@记录
     */
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteByMessage(
            @PathVariable Long messageId) {
        mentionService.deleteByMessageId(messageId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("messageId", messageId);
        return ResponseEntity.ok(resp);
    }
}
