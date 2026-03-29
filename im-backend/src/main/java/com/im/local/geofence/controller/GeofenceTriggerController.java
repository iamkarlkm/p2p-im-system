package com.im.local.geofence.controller;

import com.im.common.response.ApiResponse;
import com.im.local.geofence.dto.*;
import com.im.local.geofence.entity.Geofence;
import com.im.local.geofence.entity.GroupLocationSharing;
import com.im.local.geofence.service.GeofenceTriggerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 地理围栏场景化触发控制器
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Tag(name = "地理围栏触发", description = "地理围栏驱动的即时通讯场景化触发")
@RestController
@RequestMapping("/api/v1/geofence")
@RequiredArgsConstructor
public class GeofenceTriggerController {

    private final GeofenceTriggerService triggerService;

    @Operation(summary = "创建地理围栏")
    @PostMapping("/geofences")
    public ApiResponse<Geofence> createGeofence(@Valid @RequestBody CreateGeofenceRequest request) {
        Geofence geofence = triggerService.createGeofence(request);
        return ApiResponse.success(geofence);
    }

    @Operation(summary = "更新用户位置")
    @PostMapping("/location/update")
    public ApiResponse<Void> updateLocation(
            @RequestParam Long userId,
            @Valid @RequestBody LocationUpdateRequest request) {
        triggerService.processUserLocationUpdate(userId, request.toLocationUpdate());
        return ApiResponse.success();
    }

    @Operation(summary = "创建群组位置共享")
    @PostMapping("/group-sharing")
    public ApiResponse<GroupLocationSharing> createGroupSharing(
            @Valid @RequestBody CreateGroupSharingRequest request) {
        GroupLocationSharing sharing = triggerService.createGroupSharing(request);
        return ApiResponse.success(sharing);
    }

    @Operation(summary = "更新群组成员位置")
    @PostMapping("/group-sharing/{groupId}/location")
    public ApiResponse<Void> updateGroupMemberLocation(
            @Parameter(description = "群组ID") @PathVariable String groupId,
            @RequestParam Long memberId,
            @Valid @RequestBody LocationUpdateRequest request) {
        triggerService.updateGroupMemberLocation(groupId, memberId, request.toLocationUpdate());
        return ApiResponse.success();
    }

    @Operation(summary = "获取群组位置快照")
    @GetMapping("/group-sharing/{groupId}/locations")
    public ApiResponse<GroupLocationSnapshot> getGroupLocations(
            @Parameter(description = "群组ID") @PathVariable String groupId,
            @RequestParam Long userId) {
        GroupLocationSnapshot snapshot = triggerService.getGroupLocations(groupId, userId);
        return ApiResponse.success(snapshot);
    }

    @Operation(summary = "结束群组位置共享")
    @PostMapping("/group-sharing/{groupId}/end")
    public ApiResponse<Void> endGroupSharing(
            @Parameter(description = "群组ID") @PathVariable String groupId,
            @RequestParam Long userId) {
        triggerService.endGroupSharing(groupId, userId);
        return ApiResponse.success();
    }
}
