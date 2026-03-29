package com.im.location.controller;

import com.im.common.response.Result;
import com.im.common.utils.UserContext;
import com.im.location.dto.*;
import com.im.location.service.ILocationSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 位置共享控制器
 */
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationSharingController {
    
    private final ILocationSharingService locationSharingService;
    
    /**
     * 创建位置共享会话
     */
    @PostMapping("/sharing/create")
    public Result<LocationSharingSessionResponse> createSession(
            @RequestBody @Validated CreateLocationSharingRequest request) {
        Long userId = UserContext.getUserId();
        LocationSharingSessionResponse response = locationSharingService.createSession(userId, request);
        return Result.success(response);
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/sharing/{sessionId}")
    public Result<LocationSharingSessionResponse> getSessionDetail(@PathVariable String sessionId) {
        LocationSharingSessionResponse response = locationSharingService.getSessionDetail(sessionId);
        return Result.success(response);
    }
    
    /**
     * 加入位置共享
     */
    @PostMapping("/sharing/join")
    public Result<Void> joinSession(@RequestBody @Validated JoinLocationSharingRequest request) {
        Long userId = UserContext.getUserId();
        locationSharingService.joinSession(userId, request);
        return Result.success();
    }
    
    /**
     * 离开位置共享
     */
    @PostMapping("/sharing/{sessionId}/leave")
    public Result<Void> leaveSession(@PathVariable String sessionId) {
        Long userId = UserContext.getUserId();
        locationSharingService.leaveSession(userId, sessionId);
        return Result.success();
    }
    
    /**
     * 更新位置
     */
    @PostMapping("/sharing/update")
    public Result<Void> updateLocation(@RequestBody @Validated UpdateLocationRequest request) {
        Long userId = UserContext.getUserId();
        locationSharingService.updateLocation(userId, request);
        return Result.success();
    }
    
    /**
     * 更新会话状态
     */
    @PostMapping("/sharing/{sessionId}/status/{status}")
    public Result<Void> updateSessionStatus(@PathVariable String sessionId, @PathVariable Integer status) {
        Long userId = UserContext.getUserId();
        locationSharingService.updateSessionStatus(userId, sessionId, status);
        return Result.success();
    }
    
    /**
     * 获取用户的活跃会话列表
     */
    @GetMapping("/sharing/my/active")
    public Result<List<LocationSharingSessionResponse>> getMyActiveSessions() {
        Long userId = UserContext.getUserId();
        List<LocationSharingSessionResponse> sessions = locationSharingService.getUserActiveSessions(userId);
        return Result.success(sessions);
    }
    
    /**
     * 获取会话成员列表
     */
    @GetMapping("/sharing/{sessionId}/members")
    public Result<List<LocationSharingMemberResponse>> getSessionMembers(@PathVariable String sessionId) {
        List<LocationSharingMemberResponse> members = locationSharingService.getSessionMembers(sessionId);
        return Result.success(members);
    }
    
    /**
     * 计算ETA
     */
    @GetMapping("/sharing/{sessionId}/eta")
    public Result<Integer> calculateETA(@PathVariable String sessionId,
                                        @RequestParam Double longitude,
                                        @RequestParam Double latitude) {
        Integer eta = locationSharingService.calculateETA(sessionId, longitude, latitude);
        return Result.success(eta);
    }
}
