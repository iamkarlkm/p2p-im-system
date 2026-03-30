package com.im.controller;

import com.im.dto.ApiResponse;
import com.im.dto.CreatePrivateChatRequest;
import com.im.dto.MessageDTO;
import com.im.dto.PrivateChatSessionDTO;
import com.im.entity.PrivateChatSession;
import com.im.repository.PrivateChatSessionRepository;
import com.im.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单聊控制器
 * 功能ID: #6
 * @author developer-agent
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/chat/private")
@CrossOrigin(origins = "*")
public class PrivateChatController {

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private PrivateChatSessionRepository sessionRepository;

    /**
     * 创建或获取单聊会话
     */
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> createOrGetSession(
            @RequestBody CreatePrivateChatRequest request) {
        PrivateChatSessionDTO session = privateChatService.createOrGetSession(
            request.getUserId1(), request.getUserId2());
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<PrivateChatSessionDTO>>> getUserSessions(
            @RequestParam String userId) {
        List<PrivateChatSessionDTO> sessions = privateChatService.getUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> getSession(
            @PathVariable String sessionId) {
        PrivateChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, "会话不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(privateChatService.toSessionDTO(session)));
    }

    /**
     * 获取会话消息历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<MessageDTO> messages = privateChatService.getSessionMessages(sessionId, page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    /**
     * 标记会话已读
     */
    @PostMapping("/sessions/{sessionId}/read")
    public ResponseEntity<ApiResponse<Void>> markSessionAsRead(
            @PathVariable String sessionId,
            @RequestParam String userId) {
        privateChatService.markSessionAsRead(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable String sessionId) {
        privateChatService.deleteSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 设置会话置顶
     */
    @PutMapping("/sessions/{sessionId}/pin")
    public ResponseEntity<ApiResponse<Void>> pinSession(
            @PathVariable String sessionId,
            @RequestParam boolean pinned) {
        privateChatService.pinSession(sessionId, pinned);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 设置会话免打扰
     */
    @PutMapping("/sessions/{sessionId}/mute")
    public ResponseEntity<ApiResponse<Void>> muteSession(
            @PathVariable String sessionId,
            @RequestParam boolean muted) {
        privateChatService.muteSession(sessionId, muted);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 根据对方用户ID获取或创建会话
     */
    @GetMapping("/sessions/with/{targetUserId}")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> getSessionWithUser(
            @RequestParam String userId,
            @PathVariable String targetUserId) {
        PrivateChatSessionDTO session = privateChatService.getOrCreateSessionWithUser(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }
}
