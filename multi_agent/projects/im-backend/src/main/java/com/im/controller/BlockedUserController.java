package com.im.controller;

import com.im.dto.ApiResponse;
import com.im.dto.BlockedUserDTO;
import com.im.service.BlockedUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户黑名单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/blocked-users")
@RequiredArgsConstructor
public class BlockedUserController {
    
    private final BlockedUserService blockedUserService;
    
    /**
     * 获取黑名单列表
     */
    @GetMapping
    public ApiResponse<List<BlockedUserDTO>> getBlockedUsers(
            @RequestHeader("X-User-Id") Long userId) {
        List<BlockedUserDTO> blockedUsers = blockedUserService.getBlockedUsers(userId);
        return ApiResponse.success(blockedUsers);
    }
    
    /**
     * 获取黑名单ID列表
     */
    @GetMapping("/ids")
    public ApiResponse<List<Long>> getBlockedUserIds(
            @RequestHeader("X-User-Id") Long userId) {
        List<Long> blockedIds = blockedUserService.getBlockedUserIds(userId);
        return ApiResponse.success(blockedIds);
    }
    
    /**
     * 获取黑名单数量
     */
    @GetMapping("/count")
    public ApiResponse<Long> getBlockedCount(
            @RequestHeader("X-User-Id") Long userId) {
        long count = blockedUserService.getBlockedCount(userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 拉黑用户
     */
    @PostMapping("/{blockedId}")
    public ApiResponse<BlockedUserDTO> blockUser(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long blockedId,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        log.info("用户 {} 拉黑用户 {}, 原因: {}", userId, blockedId, reason);
        BlockedUserDTO result = blockedUserService.blockUser(userId, blockedId, reason);
        if (result == null) {
            return ApiResponse.error("该用户已在黑名单中或无法拉黑");
        }
        return ApiResponse.success(result);
    }
    
    /**
     * 解除拉黑
     */
    @DeleteMapping("/{blockedId}")
    public ApiResponse<Void> unblockUser(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long blockedId) {
        log.info("用户 {} 解除拉黑用户 {}", userId, blockedId);
        boolean success = blockedUserService.unblockUser(userId, blockedId);
        if (!success) {
            return ApiResponse.error("该用户不在黑名单中");
        }
        return ApiResponse.success(null);
    }
    
    /**
     * 检查是否拉黑了指定用户
     */
    @GetMapping("/check/{blockedId}")
    public ApiResponse<Boolean> checkBlocked(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long blockedId) {
        boolean isBlocked = blockedUserService.isUserBlocked(userId, blockedId);
        return ApiResponse.success(isBlocked);
    }
    
    /**
     * 批量检查是否在黑名单中
     */
    @PostMapping("/check-batch")
    public ApiResponse<List<Long>> checkBlockedBatch(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> userIds) {
        List<Long> blockedIds = blockedUserService.checkBlockedUsers(userId, userIds);
        return ApiResponse.success(blockedIds);
    }
    
    /**
     * 检查双向拉黑
     */
    @GetMapping("/mutual/{userId}")
    public ApiResponse<Boolean> checkMutualBlock(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long userId2) {
        boolean mutual = blockedUserService.isMutualBlock(userId, userId2);
        return ApiResponse.success(mutual);
    }
}
