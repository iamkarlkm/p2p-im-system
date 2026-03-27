package com.im.backend.controller;

import com.im.backend.dto.BatchMoveFriendRequest;
import com.im.backend.dto.CommonResponse;
import com.im.backend.service.FriendBatchService;
import com.im.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/friends/batch")
@RequiredArgsConstructor
@Tag(name = "好友批量操作", description = "批量移动好友、批量删除好友等操作")
public class FriendBatchController {

    private final FriendBatchService friendBatchService;
    private final JwtUtil jwtUtil;

    @PostMapping("/move-to-group")
    @Operation(summary = "批量移动好友到分组", description = "将多个好友批量移动到指定分组")
    public ResponseEntity<CommonResponse<Void>> batchMoveToGroup(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BatchMoveFriendRequest request) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        log.info("用户 {} 批量移动好友到分组 {}, 好友数量: {}", 
                userId, request.getTargetGroupId(), request.getFriendIds().size());
        
        friendBatchService.batchMoveToGroup(userId, request);
        
        return ResponseEntity.ok(CommonResponse.success("批量移动好友成功"));
    }

    @PostMapping("/remove-from-group")
    @Operation(summary = "批量从分组移除好友", description = "将多个好友从指定分组中移除")
    public ResponseEntity<CommonResponse<Void>> batchRemoveFromGroup(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "分组ID") @RequestParam Long groupId,
            @Parameter(description = "好友ID列表") @RequestBody List<Long> friendIds) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        log.info("用户 {} 批量从分组 {} 移除好友, 数量: {}", userId, groupId, friendIds.size());
        
        friendBatchService.batchRemoveFromGroup(userId, groupId, friendIds);
        
        return ResponseEntity.ok(CommonResponse.success("批量移除好友成功"));
    }

    @PostMapping("/set-star")
    @Operation(summary = "批量设置星标好友", description = "批量设置好友的星标状态")
    public ResponseEntity<CommonResponse<Void>> batchSetStar(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "好友ID列表") @RequestBody List<Long> friendIds,
            @Parameter(description = "是否星标") @RequestParam Boolean isStarred) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        log.info("用户 {} 批量设置星标, 数量: {}, 状态: {}", userId, friendIds.size(), isStarred);
        
        friendBatchService.batchSetStar(userId, friendIds, isStarred);
        
        return ResponseEntity.ok(CommonResponse.success("批量设置星标成功"));
    }

    @PostMapping("/set-mute")
    @Operation(summary = "批量设置消息免打扰", description = "批量设置好友的消息免打扰状态")
    public ResponseEntity<CommonResponse<Void>> batchSetMute(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "好友ID列表") @RequestBody List<Long> friendIds,
            @Parameter(description = "是否免打扰") @RequestParam Boolean isMuted) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        log.info("用户 {} 批量设置免打扰, 数量: {}, 状态: {}", userId, friendIds.size(), isMuted);
        
        friendBatchService.batchSetMute(userId, friendIds, isMuted);
        
        return ResponseEntity.ok(CommonResponse.success("批量设置免打扰成功"));
    }

    @GetMapping("/group/{groupId}/friends")
    @Operation(summary = "获取分组中的好友列表", description = "获取指定分组中的所有好友")
    public ResponseEntity<CommonResponse<List<Long>>> getFriendsInGroup(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long groupId) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        
        List<Long> friendIds = friendBatchService.getFriendsInGroup(userId, groupId);
        
        return ResponseEntity.ok(CommonResponse.success(friendIds));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取好友批量操作统计", description = "获取用户的好友分组统计信息")
    public ResponseEntity<CommonResponse<BatchOperationStats>> getBatchOperationStats(
            @RequestHeader("Authorization") String authHeader) {
        
        Long userId = jwtUtil.extractUserId(authHeader.substring(7));
        
        BatchOperationStats stats = friendBatchService.getBatchOperationStats(userId);
        
        return ResponseEntity.ok(CommonResponse.success(stats));
    }
}
