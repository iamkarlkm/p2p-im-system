package com.im.service.message.controller;

import com.im.dto.PrivateChatRequest;
import com.im.dto.PrivateChatResponse;
import com.im.dto.MessageDTO;
import com.im.service.PrivateChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 单聊功能控制器
 * 提供一对一私信的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/chat/private")
@RequiredArgsConstructor
public class PrivateChatController {

    private final PrivateChatService privateChatService;

    /**
     * 发送单聊消息
     */
    @PostMapping("/send")
    public ResponseEntity<PrivateChatResponse> sendMessage(
            @RequestHeader("X-User-Id") Long senderId,
            @Valid @RequestBody PrivateChatRequest request) {
        log.info("用户 {} 发送单聊消息给 {}", senderId, request.getReceiverId());
        PrivateChatResponse response = privateChatService.sendMessage(senderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取单聊会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<PrivateChatResponse>> getSessionList(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("获取用户 {} 的单聊会话列表", userId);
        List<PrivateChatResponse> sessions = privateChatService.getSessionList(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @GetMapping("/history/{targetUserId}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户 {} 与 {} 的聊天记录", userId, targetUserId);
        List<MessageDTO> history = privateChatService.getChatHistory(userId, targetUserId, page, size);
        return ResponseEntity.ok(history);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 标记消息 {} 已读", userId, messageId);
        privateChatService.markMessageAsRead(userId, messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 撤回消息
     */
    @PostMapping("/recall/{messageId}")
    public ResponseEntity<Void> recallMessage(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long messageId) {
        log.info("用户 {} 撤回消息 {}", userId, messageId);
        privateChatService.recallMessage(userId, messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{targetUserId}")
    public ResponseEntity<Void> deleteSession(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long targetUserId) {
        log.info("用户 {} 删除与 {} 的会话", userId, targetUserId);
        privateChatService.deleteSession(userId, targetUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        Integer count = privateChatService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
}
