package com.im.presence.controller;

import com.im.common.dto.ApiResponse;
import com.im.presence.dto.BatchSubscribeRequest;
import com.im.presence.dto.PresenceResponse;
import com.im.presence.dto.PresenceUpdateRequest;
import com.im.presence.service.UserPresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户在线状态控制器
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/presence")
@RequiredArgsConstructor
@Validated
@Tag(name = "在线状态", description = "用户在线状态管理与订阅")
public class PresenceController {
    
    private final UserPresenceService presenceService;
    
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户状态", description = "获取指定用户的在线状态")
    public ApiResponse<PresenceResponse> getUserPresence(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        log.info("获取用户状态: userId={}", userId);
        PresenceResponse response = presenceService.getUserPresence(userId);
        return ApiResponse.success(response);
    }
    
    @PostMapping("/batch")
    @Operation(summary = "批量获取状态", description = "批量查询多个用户的在线状态")
    public ApiResponse<List<PresenceResponse>> batchGetPresence(
            @RequestBody List<Long> userIds) {
        log.info("批量获取状态: userIds count={}", userIds.size());
        List<PresenceResponse> responses = presenceService.batchGetPresence(userIds);
        return ApiResponse.success(responses);
    }
    
    @PostMapping("/update")
    @Operation(summary = "更新状态", description = "更新用户在线状态")
    public ApiResponse<PresenceResponse> updatePresence(
            @Valid @RequestBody PresenceUpdateRequest request) {
        log.info("更新状态: userId={}, status={}", request.getUserId(), request.getStatus());
        PresenceResponse response = presenceService.updatePresence(request);
        return ApiResponse.success(response);
    }
    
    @PostMapping("/subscribe/batch")
    @Operation(summary = "批量订阅", description = "批量订阅用户状态变更")
    public ApiResponse<Integer> batchSubscribe(
            @Valid @RequestBody BatchSubscribeRequest request) {
        log.info("批量订阅: subscriberId={}, targetCount={}", 
                request.getSubscriberId(), request.getTargetUserIds().size());
        int count = presenceService.batchSubscribe(request);
        return ApiResponse.success(count, "成功订阅 " + count + " 个用户");
    }
    
    @GetMapping("/stats/online-count")
    @Operation(summary = "在线用户数", description = "获取当前在线用户总数")
    public ApiResponse<Integer> getOnlineUserCount() {
        int count = presenceService.getOnlineUserCount();
        return ApiResponse.success(count);
    }
    
    @GetMapping("/stats/detail")
    @Operation(summary = "状态统计", description = "获取各状态用户数量统计")
    public ApiResponse<Map<String, Integer>> getPresenceStatistics() {
        Map<String, Integer> stats = presenceService.getPresenceStatistics();
        return ApiResponse.success(stats);
    }
}
