package com.im.backend.modules.local.life.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.local.life.dto.*;
import com.im.backend.modules.local.life.service.NavigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航控制器
 * Navigation Controller
 */
@RestController
@RequestMapping("/api/v1/navigation")
@RequiredArgsConstructor
@Tag(name = "导航服务", description = "智能导航与路线规划相关接口")
public class NavigationController {

    private final NavigationService navigationService;

    @PostMapping("/route-planning")
    @Operation(summary = "路线规划", description = "根据起点和终点规划导航路线")
    public ApiResponse<RoutePlanningResultDTO> planRoute(
            @RequestBody @Validated RoutePlanningRequestDTO request,
            @RequestAttribute("userId") Long userId) {
        RoutePlanningResultDTO result = navigationService.planRoute(request, userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/multi-stop-route")
    @Operation(summary = "多目的地路线规划", description = "规划包含多个途经点的路线")
    public ApiResponse<RoutePlanningResultDTO> planMultiStopRoute(
            @RequestBody @Validated RoutePlanningRequestDTO request,
            @RequestParam List<RoutePlanningRequestDTO.WaypointDTO> stops,
            @RequestAttribute("userId") Long userId) {
        RoutePlanningResultDTO result = navigationService.planMultiStopRoute(request, stops, userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/routes/{routeId}/start")
    @Operation(summary = "开始导航", description = "开始导航会话")
    public ApiResponse<NavigationStatusDTO> startNavigation(
            @PathVariable Long routeId,
            @RequestAttribute("userId") Long userId) {
        NavigationStatusDTO status = navigationService.startNavigation(routeId, userId);
        return ApiResponse.success(status);
    }

    @PostMapping("/sessions/{sessionId}/location-update")
    @Operation(summary = "更新位置", description = "更新当前导航位置")
    public ApiResponse<NavigationStatusDTO> updateLocation(
            @PathVariable Long sessionId,
            @RequestBody @Validated LocationUpdateRequestDTO request,
            @RequestAttribute("userId") Long userId) {
        request.setSessionId(sessionId);
        NavigationStatusDTO status = navigationService.updateLocation(request, userId);
        return ApiResponse.success(status);
    }

    @GetMapping("/sessions/{sessionId}/status")
    @Operation(summary = "获取导航状态", description = "获取当前导航会话状态")
    public ApiResponse<NavigationStatusDTO> getNavigationStatus(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        NavigationStatusDTO status = navigationService.getNavigationStatus(sessionId, userId);
        return ApiResponse.success(status);
    }

    @PostMapping("/sessions/{sessionId}/pause")
    @Operation(summary = "暂停导航", description = "暂停当前导航")
    public ApiResponse<NavigationStatusDTO> pauseNavigation(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        NavigationStatusDTO status = navigationService.pauseNavigation(sessionId, userId);
        return ApiResponse.success(status);
    }

    @PostMapping("/sessions/{sessionId}/resume")
    @Operation(summary = "恢复导航", description = "恢复暂停的导航")
    public ApiResponse<NavigationStatusDTO> resumeNavigation(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        NavigationStatusDTO status = navigationService.resumeNavigation(sessionId, userId);
        return ApiResponse.success(status);
    }

    @PostMapping("/sessions/{sessionId}/end")
    @Operation(summary = "结束导航", description = "结束导航会话")
    public ApiResponse<Void> endNavigation(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        navigationService.endNavigation(sessionId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/sessions/{sessionId}/cancel")
    @Operation(summary = "取消导航", description = "取消导航会话")
    public ApiResponse<Void> cancelNavigation(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        navigationService.cancelNavigation(sessionId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/sessions/{sessionId}/reroute")
    @Operation(summary = "重新规划路线", description = "偏航后重新规划路线")
    public ApiResponse<RoutePlanningResultDTO> reroute(
            @PathVariable Long sessionId,
            @RequestAttribute("userId") Long userId) {
        RoutePlanningResultDTO result = navigationService.reroute(sessionId, userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/routes/{routeId}/favorite")
    @Operation(summary = "收藏路线", description = "收藏导航路线")
    public ApiResponse<Void> favoriteRoute(
            @PathVariable Long routeId,
            @RequestAttribute("userId") Long userId) {
        navigationService.favoriteRoute(routeId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/routes/{routeId}/unfavorite")
    @Operation(summary = "取消收藏路线", description = "取消收藏导航路线")
    public ApiResponse<Void> unfavoriteRoute(
            @PathVariable Long routeId,
            @RequestAttribute("userId") Long userId) {
        navigationService.unfavoriteRoute(routeId, userId);
        return ApiResponse.success();
    }

    @GetMapping("/routes/favorites")
    @Operation(summary = "获取收藏路线", description = "获取用户收藏的路线列表")
    public ApiResponse<List<RoutePlanningResultDTO>> getFavoriteRoutes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestAttribute("userId") Long userId) {
        List<RoutePlanningResultDTO> routes = navigationService.getFavoriteRoutes(userId, page, size);
        return ApiResponse.success(routes);
    }

    @DeleteMapping("/routes/{routeId}")
    @Operation(summary = "删除路线", description = "删除保存的导航路线")
    public ApiResponse<Void> deleteRoute(
            @PathVariable Long routeId,
            @RequestAttribute("userId") Long userId) {
        navigationService.deleteRoute(routeId, userId);
        return ApiResponse.success();
    }

    @GetMapping("/history")
    @Operation(summary = "导航历史", description = "获取导航历史记录")
    public ApiResponse<List<RoutePlanningResultDTO>> getNavigationHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestAttribute("userId") Long userId) {
        List<RoutePlanningResultDTO> history = navigationService.getNavigationHistory(userId, page, size);
        return ApiResponse.success(history);
    }
}
