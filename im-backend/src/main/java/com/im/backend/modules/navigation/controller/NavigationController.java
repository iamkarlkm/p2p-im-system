package com.im.backend.modules.navigation.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.navigation.dto.RouteRequestDTO;
import com.im.backend.modules.navigation.dto.RouteResponseDTO;
import com.im.backend.modules.navigation.service.NavigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导航控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/navigation")
@RequiredArgsConstructor
@Validated
public class NavigationController {

    private final NavigationService navigationService;

    /**
     * 规划路线
     */
    @PostMapping("/plan")
    public Result<RouteResponseDTO> planRoute(@RequestBody @Validated RouteRequestDTO request) {
        log.info("规划路线请求: {} -> {}", request.getStartName(), request.getEndName());
        RouteResponseDTO response = navigationService.planRoute(request);
        return Result.success(response);
    }

    /**
     * 批量规划多条路线方案
     */
    @PostMapping("/plan/multiple")
    public Result<List<RouteResponseDTO>> planMultipleRoutes(@RequestBody @Validated RouteRequestDTO request) {
        log.info("批量规划路线请求");
        List<RouteResponseDTO> routes = navigationService.planMultipleRoutes(request);
        return Result.success(routes);
    }

    /**
     * 重新规划路线
     */
    @PostMapping("/{routeId}/re-route")
    public Result<RouteResponseDTO> reRoute(@PathVariable Long routeId, 
                                             @RequestParam(required = false) String reason) {
        log.info("重新规划路线: routeId={}", routeId);
        RouteResponseDTO response = navigationService.reRoute(routeId, reason);
        return Result.success(response);
    }

    /**
     * 保存路线
     */
    @PostMapping("/routes")
    public Result<Long> saveRoute(@RequestBody @Validated RouteRequestDTO request) {
        log.info("保存路线请求");
        RouteResponseDTO response = navigationService.planRoute(request);
        Long routeId = navigationService.saveRoute(request, response);
        return Result.success(routeId);
    }

    /**
     * 获取路线详情
     */
    @GetMapping("/routes/{routeId}")
    public Result<RouteResponseDTO> getRouteDetail(@PathVariable Long routeId) {
        log.info("获取路线详情: routeId={}", routeId);
        RouteResponseDTO route = navigationService.getRouteDetail(routeId);
        return Result.success(route);
    }

    /**
     * 获取用户路线列表
     */
    @GetMapping("/routes/user/{userId}")
    public Result<List<RouteResponseDTO>> getUserRoutes(@PathVariable Long userId) {
        log.info("获取用户路线列表: userId={}", userId);
        List<RouteResponseDTO> routes = navigationService.getUserRoutes(userId);
        return Result.success(routes);
    }

    /**
     * 获取收藏路线
     */
    @GetMapping("/routes/favorites/{userId}")
    public Result<List<RouteResponseDTO>> getFavoriteRoutes(@PathVariable Long userId) {
        log.info("获取收藏路线: userId={}", userId);
        List<RouteResponseDTO> routes = navigationService.getFavoriteRoutes(userId);
        return Result.success(routes);
    }

    /**
     * 获取相似历史路线
     */
    @GetMapping("/routes/similar")
    public Result<List<RouteResponseDTO>> getSimilarRoutes(@RequestParam Long userId,
                                                           @RequestParam Double startLng,
                                                           @RequestParam Double startLat,
                                                           @RequestParam Double endLng,
                                                           @RequestParam Double endLat) {
        log.info("获取相似历史路线: userId={}", userId);
        List<RouteResponseDTO> routes = navigationService.getSimilarRoutes(userId, startLng, startLat, endLng, endLat);
        return Result.success(routes);
    }

    /**
     * 收藏/取消收藏路线
     */
    @PutMapping("/routes/{routeId}/favorite")
    public Result<Void> updateFavoriteStatus(@PathVariable Long routeId, 
                                              @RequestParam Boolean isFavorite) {
        log.info("更新路线收藏状态: routeId={}, isFavorite={}", routeId, isFavorite);
        navigationService.updateFavoriteStatus(routeId, isFavorite);
        return Result.success();
    }

    /**
     * 删除路线
     */
    @DeleteMapping("/routes/{routeId}")
    public Result<Void> deleteRoute(@PathVariable Long routeId) {
        log.info("删除路线: routeId={}", routeId);
        navigationService.deleteRoute(routeId);
        return Result.success();
    }

    /**
     * 使用路线
     */
    @PostMapping("/routes/{routeId}/use")
    public Result<Void> useRoute(@PathVariable Long routeId) {
        log.info("使用路线: routeId={}", routeId);
        navigationService.useRoute(routeId);
        return Result.success();
    }

    /**
     * 预估到达时间
     */
    @GetMapping("/estimate-arrival")
    public Result<String> estimateArrivalTime(@RequestParam Double startLng,
                                               @RequestParam Double startLat,
                                               @RequestParam Double endLng,
                                               @RequestParam Double endLat,
                                               @RequestParam String travelMode) {
        log.info("预估到达时间");
        String arrivalTime = navigationService.estimateArrivalTime(startLng, startLat, endLng, endLat, travelMode);
        return Result.success(arrivalTime);
    }

    /**
     * 获取实时路况
     */
    @GetMapping("/traffic")
    public Result<RouteResponseDTO.TrafficInfoDTO> getRealTimeTraffic(@RequestParam String polyline) {
        log.info("获取实时路况");
        RouteResponseDTO.TrafficInfoDTO traffic = navigationService.getRealTimeTraffic(polyline);
        return Result.success(traffic);
    }

    /**
     * 检查限行信息
     */
    @GetMapping("/restriction")
    public Result<RouteResponseDTO.RestrictionInfoDTO> checkRestriction(@RequestParam String polyline,
                                                                        @RequestParam(required = false) String plateNumber) {
        log.info("检查限行信息");
        RouteResponseDTO.RestrictionInfoDTO restriction = navigationService.checkRestriction(polyline, plateNumber);
        return Result.success(restriction);
    }
}
