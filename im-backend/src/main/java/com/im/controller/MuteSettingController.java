package com.im.controller;

import com.im.dto.ApiResponse;
import com.im.dto.MuteSettingDTO;
import com.im.dto.MuteSettingRequest;
import com.im.service.MuteSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 免打扰设置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mute")
@RequiredArgsConstructor
public class MuteSettingController {
    
    private final MuteSettingService muteSettingService;
    
    /**
     * 静音会话
     */
    @PutMapping("/conversations/{conversationId}")
    public ApiResponse<MuteSettingDTO> muteConversation(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        log.info("用户 {} 请求静音会话 {}", userId, conversationId);
        MuteSettingDTO result = muteSettingService.muteConversation(userId, conversationId);
        return ApiResponse.success(result);
    }
    
    /**
     * 取消会话静音
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ApiResponse<MuteSettingDTO> unmuteConversation(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        log.info("用户 {} 请求取消静音会话 {}", userId, conversationId);
        MuteSettingDTO result = muteSettingService.unmuteConversation(userId, conversationId);
        return ApiResponse.success(result);
    }
    
    /**
     * 获取用户所有会话的静音设置
     */
    @GetMapping("/conversations")
    public ApiResponse<List<MuteSettingDTO>> getConversationMuteSettings(
            @RequestHeader("X-User-Id") Long userId) {
        List<MuteSettingDTO> settings = muteSettingService.getConversationMuteSettings(userId);
        return ApiResponse.success(settings);
    }
    
    /**
     * 获取用户所有已静音的会话ID列表
     */
    @GetMapping("/conversations/muted")
    public ApiResponse<List<Long>> getMutedConversationIds(
            @RequestHeader("X-User-Id") Long userId) {
        List<Long> mutedIds = muteSettingService.getMutedConversationIds(userId);
        return ApiResponse.success(mutedIds);
    }
    
    /**
     * 检查会话是否被静音
     */
    @GetMapping("/conversations/{conversationId}/check")
    public ApiResponse<Boolean> isConversationMuted(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        boolean isMuted = muteSettingService.isConversationMuted(userId, conversationId);
        return ApiResponse.success(isMuted);
    }
    
    /**
     * 批量检查会话是否被静音
     */
    @PostMapping("/conversations/batch-check")
    public ApiResponse<List<Long>> batchCheckMuted(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> conversationIds) {
        List<Long> mutedIds = muteSettingService.batchCheckMuted(userId, conversationIds);
        return ApiResponse.success(mutedIds);
    }
    
    /**
     * 设置全局免打扰时段
     */
    @PutMapping("/dnd")
    public ApiResponse<MuteSettingDTO> setGlobalDnd(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody MuteSettingRequest request) {
        log.info("用户 {} 设置全局免打扰: {}", userId, request);
        MuteSettingDTO result = muteSettingService.setGlobalDnd(userId, request);
        return ApiResponse.success(result);
    }
    
    /**
     * 获取全局免打扰设置
     */
    @GetMapping("/dnd")
    public ApiResponse<MuteSettingDTO> getGlobalDnd(
            @RequestHeader("X-User-Id") Long userId) {
        MuteSettingDTO result = muteSettingService.getGlobalDnd(userId);
        return ApiResponse.success(result);
    }
    
    /**
     * 删除全局免打扰设置
     */
    @DeleteMapping("/dnd")
    public ApiResponse<Void> deleteGlobalDnd(
            @RequestHeader("X-User-Id") Long userId) {
        log.info("用户 {} 删除全局免打扰设置", userId);
        muteSettingService.deleteGlobalDnd(userId);
        return ApiResponse.success(null);
    }
    
    /**
     * 检查用户是否可以接收消息 (考虑静音和免打扰)
     */
    @GetMapping("/can-receive/{conversationId}")
    public ApiResponse<Boolean> canReceiveNotification(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        boolean canReceive = muteSettingService.canReceiveNotification(userId, conversationId);
        return ApiResponse.success(canReceive);
    }
    
    /**
     * 批量检查用户是否可以接收消息
     */
    @PostMapping("/can-receive/batch")
    public ApiResponse<List<Boolean>> batchCanReceive(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> conversationIds) {
        List<Boolean> results = conversationIds.stream()
                .map(conversationId -> muteSettingService.canReceiveNotification(userId, conversationId))
                .toList();
        return ApiResponse.success(results);
    }
    
    /**
     * 删除会话的静音设置
     */
    @DeleteMapping("/conversations/{conversationId}/setting")
    public ApiResponse<Void> deleteConversationSetting(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long conversationId) {
        log.info("用户 {} 删除会话 {} 的静音设置", userId, conversationId);
        muteSettingService.deleteConversationSetting(userId, conversationId);
        return ApiResponse.success(null);
    }
}
