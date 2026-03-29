package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.model.User;
import com.im.backend.service.PrivateChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单聊控制器
 * 处理单聊会话的创建、查询、管理等操作
 */
@RestController
@RequestMapping("/api/chat/private")
@Tag(name = "单聊管理", description = "单聊会话相关接口")
@SecurityRequirement(name = "bearerAuth")
public class PrivateChatController {

    @Autowired
    private PrivateChatService privateChatService;

    /**
     * 创建单聊会话
     * 如果会话已存在则返回已有会话
     */
    @PostMapping("/sessions")
    @Operation(summary = "创建单聊会话", description = "与指定用户创建单聊会话，如已存在则返回已有会话")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> createSession(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreatePrivateChatRequest request) {
        
        PrivateChatSessionDTO session = privateChatService.createOrGetSession(
                currentUser.getId(), 
                request.getTargetUserId()
        );
        
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    /**
     * 获取用户的所有单聊会话列表
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取会话列表", description = "获取当前用户的所有单聊会话列表，按最新消息时间倒序排列")
    public ResponseEntity<ApiResponse<List<PrivateChatSessionDTO>>> getSessionList(
            @AuthenticationPrincipal User currentUser) {
        
        List<PrivateChatSessionDTO> sessions = privateChatService.getUserSessions(currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    /**
     * 获取单个会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "获取会话详情", description = "获取指定单聊会话的详细信息")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> getSessionDetail(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId) {
        
        PrivateChatSessionDTO session = privateChatService.getSessionDetail(sessionId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    /**
     * 获取会话的消息历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "获取消息历史", description = "分页获取指定会话的消息历史")
    public ResponseEntity<ApiResponse<PagedResponse<MessageDTO>>> getSessionMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PagedResponse<MessageDTO> messages = privateChatService.getSessionMessages(
                sessionId, 
                currentUser.getId(),
                page, 
                size
        );
        
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    /**
     * 标记会话消息为已读
     */
    @PostMapping("/sessions/{sessionId}/read")
    @Operation(summary = "标记已读", description = "将指定会话的所有未读消息标记为已读")
    public ResponseEntity<ApiResponse<Void>> markSessionAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId) {
        
        privateChatService.markSessionAsRead(sessionId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 删除单聊会话
     * 软删除，仅对当前用户隐藏该会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "删除会话", description = "删除指定单聊会话（软删除，仅对当前用户隐藏）")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId) {
        
        privateChatService.deleteSession(sessionId, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 设置会话置顶状态
     */
    @PutMapping("/sessions/{sessionId}/pin")
    @Operation(summary = "设置置顶", description = "设置会话置顶或取消置顶")
    public ResponseEntity<ApiResponse<Void>> setSessionPinned(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId,
            @RequestParam boolean pinned) {
        
        privateChatService.setSessionPinned(sessionId, currentUser.getId(), pinned);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 设置会话免打扰状态
     */
    @PutMapping("/sessions/{sessionId}/mute")
    @Operation(summary = "设置免打扰", description = "设置会话免打扰或取消免打扰")
    public ResponseEntity<ApiResponse<Void>> setSessionMuted(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long sessionId,
            @RequestParam boolean muted) {
        
        privateChatService.setSessionMuted(sessionId, currentUser.getId(), muted);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 获取与指定用户的会话（如果不存在则创建）
     */
    @GetMapping("/sessions/with/{userId}")
    @Operation(summary = "获取或创建会话", description = "获取与指定用户的单聊会话，如不存在则自动创建")
    public ResponseEntity<ApiResponse<PrivateChatSessionDTO>> getOrCreateSessionWithUser(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId) {
        
        PrivateChatSessionDTO session = privateChatService.createOrGetSession(
                currentUser.getId(), 
                userId
        );
        
        return ResponseEntity.ok(ApiResponse.success(session));
    }
}
