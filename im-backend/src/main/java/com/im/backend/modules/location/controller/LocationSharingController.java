package com.im.backend.modules.location.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.location.model.dto.*;
import com.im.backend.modules.location.service.ILocationSharingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 位置共享控制器
 * 提供实时位置共享与群组追踪API
 */
@RestController
@RequestMapping("/api/v1/location-sharing")
@RequiredArgsConstructor
@Validated
@Tag(name = "位置共享", description = "实时位置共享与群组追踪相关接口")
public class LocationSharingController {

    private final ILocationSharingService locationSharingService;

    /**
     * 创建位置共享会话
     */
    @PostMapping("/sessions")
    @Operation(summary = "创建位置共享会话", description = "创建新的位置共享会话")
    public ApiResponse<LocationShareResponse> createSession(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CreateLocationShareRequest request) {
        LocationShareResponse response = locationSharingService.createSession(userId, request);
        return ApiResponse.success(response);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "获取会话详情", description = "获取位置共享会话详细信息")
    public ApiResponse<LocationShareResponse> getSessionDetail(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        LocationShareResponse response = locationSharingService.getSessionDetail(sessionId, userId);
        return ApiResponse.success(response);
    }

    /**
     * 加入位置共享
     */
    @PostMapping("/sessions/join")
    @Operation(summary = "加入位置共享", description = "加入已有的位置共享会话")
    public ApiResponse<Void> joinSession(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody JoinLocationShareRequest request) {
        locationSharingService.joinSession(userId, request);
        return ApiResponse.success();
    }

    /**
     * 离开位置共享
     */
    @PostMapping("/sessions/{sessionId}/leave")
    @Operation(summary = "离开位置共享", description = "离开当前位置共享会话")
    public ApiResponse<Void> leaveSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        locationSharingService.leaveSession(sessionId, userId);
        return ApiResponse.success();
    }

    /**
     * 暂停位置共享
     */
    @PostMapping("/sessions/{sessionId}/pause")
    @Operation(summary = "暂停位置共享", description = "暂停共享自己的位置")
    public ApiResponse<Void> pauseSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        locationSharingService.pauseSession(sessionId, userId);
        return ApiResponse.success();
    }

    /**
     * 恢复位置共享
     */
    @PostMapping("/sessions/{sessionId}/resume")
    @Operation(summary = "恢复位置共享", description = "恢复共享自己的位置")
    public ApiResponse<Void> resumeSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        locationSharingService.resumeSession(sessionId, userId);
        return ApiResponse.success();
    }

    /**
     * 结束位置共享
     */
    @PostMapping("/sessions/{sessionId}/end")
    @Operation(summary = "结束位置共享", description = "结束位置共享会话(仅创建者可操作)")
    public ApiResponse<Void> endSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        locationSharingService.endSession(sessionId, userId);
        return ApiResponse.success();
    }

    /**
     * 更新位置
     */
    @PostMapping("/location/update")
    @Operation(summary = "更新位置", description = "上报当前位置")
    public ApiResponse<Void> updateLocation(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody LocationUpdateRequest request) {
        locationSharingService.updateLocation(userId, request);
        return ApiResponse.success();
    }

    /**
     * 获取成员位置
     */
    @GetMapping("/sessions/{sessionId}/locations")
    @Operation(summary = "获取成员位置", description = "获取会话中所有成员的最新位置")
    public ApiResponse<List<SharedLocationDTO>> getMemberLocations(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        List<SharedLocationDTO> locations = locationSharingService.getMemberLocations(sessionId, userId);
        return ApiResponse.success(locations);
    }

    /**
     * 获取用户的活跃会话
     */
    @GetMapping("/sessions/active")
    @Operation(summary = "获取活跃会话", description = "获取用户参与的所有活跃位置共享会话")
    public ApiResponse<List<LocationShareResponse>> getUserActiveSessions(
            @RequestAttribute("userId") Long userId) {
        List<LocationShareResponse> sessions = locationSharingService.getUserActiveSessions(userId);
        return ApiResponse.success(sessions);
    }

    /**
     * 获取会话围栏事件
     */
    @GetMapping("/sessions/{sessionId}/events")
    @Operation(summary = "获取围栏事件", description = "获取会话中的地理围栏触发事件")
    public ApiResponse<List<GeofenceEventDTO>> getSessionEvents(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sessionId) {
        List<GeofenceEventDTO> events = locationSharingService.getSessionEvents(sessionId, userId);
        return ApiResponse.success(events);
    }
}
