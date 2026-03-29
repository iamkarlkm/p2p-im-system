package com.im.backend.modules.geofencing.controller;

import com.im.backend.common.result.PageResult;
import com.im.backend.common.result.Result;
import com.im.backend.modules.geofencing.dto.*;
import com.im.backend.modules.geofencing.service.GeofenceService;
import com.im.backend.modules.geofencing.service.UserGeofenceStateService;
import com.im.backend.modules.geofencing.service.SmartArrivalMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 地理围栏智能到店服务控制器
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Tag(name = "地理围栏智能到店服务", description = "智能围栏管理、到店检测、个性化消息推送")
@RestController
@RequestMapping("/api/v1/geofencing")
@RequiredArgsConstructor
public class GeofencingController {

    private final GeofenceService geofenceService;
    private final UserGeofenceStateService userGeofenceStateService;
    private final SmartArrivalMessageService smartArrivalMessageService;

    // ==================== 围栏管理接口 ====================
    
    @Operation(summary = "创建地理围栏", description = "创建圆形或多边形围栏，支持层级嵌套")
    @PostMapping("/geofences")
    public Result<Long> createGeofence(@Valid @RequestBody GeofenceCreateDTO dto) {
        Long id = geofenceService.createGeofence(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新地理围栏")
    @PutMapping("/geofences/{id}")
    public Result<Void> updateGeofence(
            @Parameter(description = "围栏ID") @PathVariable Long id,
            @Valid @RequestBody GeofenceUpdateDTO dto) {
        geofenceService.updateGeofence(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除地理围栏")
    @DeleteMapping("/geofences/{id}")
    public Result<Void> deleteGeofence(
            @Parameter(description = "围栏ID") @PathVariable Long id) {
        geofenceService.deleteGeofence(id);
        return Result.success();
    }

    @Operation(summary = "获取围栏详情")
    @GetMapping("/geofences/{id}")
    public Result<GeofenceDetailVO> getGeofenceDetail(
            @Parameter(description = "围栏ID") @PathVariable Long id) {
        return Result.success(geofenceService.getGeofenceDetail(id));
    }

    @Operation(summary = "分页查询围栏列表")
    @GetMapping("/geofences")
    public Result<PageResult<GeofenceListVO>> queryGeofencePage(GeofenceQueryDTO query) {
        return Result.success(geofenceService.queryGeofencePage(query));
    }

    @Operation(summary = "获取商户所有围栏")
    @GetMapping("/merchants/{merchantId}/geofences")
    public Result<List<GeofenceListVO>> getMerchantGeofences(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(geofenceService.getMerchantGeofences(merchantId));
    }

    @Operation(summary = "获取POI关联围栏")
    @GetMapping("/pois/{poiId}/geofences")
    public Result<List<GeofenceListVO>> getPoiGeofences(
            @Parameter(description = "POI ID") @PathVariable Long poiId) {
        return Result.success(geofenceService.getPoiGeofences(poiId));
    }

    @Operation(summary = "启用围栏")
    @PostMapping("/geofences/{id}/enable")
    public Result<Void> enableGeofence(
            @Parameter(description = "围栏ID") @PathVariable Long id) {
        geofenceService.enableGeofence(id);
        return Result.success();
    }

    @Operation(summary = "禁用围栏")
    @PostMapping("/geofences/{id}/disable")
    public Result<Void> disableGeofence(
            @Parameter(description = "围栏ID") @PathVariable Long id) {
        geofenceService.disableGeofence(id);
        return Result.success();
    }

    @Operation(summary = "复制围栏")
    @PostMapping("/geofences/{id}/copy")
    public Result<Long> copyGeofence(
            @Parameter(description = "源围栏ID") @PathVariable Long id,
            @RequestParam Long targetPoiId) {
        return Result.success(geofenceService.copyGeofence(id, targetPoiId));
    }

    @Operation(summary = "获取围栏层级树")
    @GetMapping("/merchants/{merchantId}/geofence-tree")
    public Result<List<GeofenceTreeVO>> getGeofenceTree(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(geofenceService.getGeofenceTree(merchantId));
    }

    // ==================== 位置检测接口 ====================

    @Operation(summary = "位置上报", description = "用户位置上报，检测围栏触发事件")
    @PostMapping("/location/report")
    public Result<List<GeofenceEventVO>> reportLocation(@Valid @RequestBody LocationReportDTO dto) {
        List<GeofenceEventVO> events = userGeofenceStateService.processLocationReport(
                dto.getUserId(),
                dto.getLongitude(),
                dto.getLatitude(),
                dto.getAccuracy(),
                dto.getSource()
        );
        return Result.success(events);
    }

    @Operation(summary = "批量位置上报")
    @PostMapping("/location/batch-report")
    public Result<List<GeofenceEventVO>> batchReportLocation(
            @Valid @RequestBody List<LocationReportDTO> reports) {
        return Result.success(userGeofenceStateService.batchProcessLocationReports(reports));
    }

    @Operation(summary = "查询用户当前所在围栏")
    @GetMapping("/users/{userId}/current-geofences")
    public Result<List<UserGeofenceStateVO>> getUserCurrentGeofences(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(userGeofenceStateService.getUserCurrentGeofences(userId));
    }

    @Operation(summary = "判断点是否在围栏内")
    @GetMapping("/geofences/{id}/contains")
    public Result<Boolean> isPointInGeofence(
            @Parameter(description = "围栏ID") @PathVariable Long id,
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return Result.success(geofenceService.isPointInGeofence(id, longitude, latitude));
    }

    @Operation(summary = "查询点所在的围栏列表")
    @GetMapping("/geofences/contains-point")
    public Result<List<Long>> findGeofencesByPoint(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return Result.success(geofenceService.findGeofencesByPoint(longitude, latitude));
    }

    @Operation(summary = "查询附近围栏")
    @GetMapping("/geofences/nearby")
    public Result<List<GeofenceListVO>> findNearbyGeofences(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude,
            @RequestParam(defaultValue = "1000") Integer radius) {
        return Result.success(geofenceService.findNearbyGeofences(longitude, latitude, radius));
    }

    // ==================== 围栏订阅接口 ====================

    @Operation(summary = "订阅围栏")
    @PostMapping("/users/{userId}/subscribe/{geofenceId}")
    public Result<Void> subscribeGeofence(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "围栏ID") @PathVariable Long geofenceId) {
        userGeofenceStateService.subscribeGeofence(userId, geofenceId);
        return Result.success();
    }

    @Operation(summary = "取消订阅围栏")
    @PostMapping("/users/{userId}/unsubscribe/{geofenceId}")
    public Result<Void> unsubscribeGeofence(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "围栏ID") @PathVariable Long geofenceId) {
        userGeofenceStateService.unsubscribeGeofence(userId, geofenceId);
        return Result.success();
    }

    @Operation(summary = "获取用户订阅的围栏")
    @GetMapping("/users/{userId}/subscribed-geofences")
    public Result<List<GeofenceListVO>> getUserSubscribedGeofences(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(userGeofenceStateService.getUserSubscribedGeofences(userId));
    }

    // ==================== 智能消息接口 ====================

    @Operation(summary = "获取用户到店消息列表")
    @GetMapping("/users/{userId}/messages")
    public Result<List<SmartArrivalMessageVO>> getUserMessages(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(smartArrivalMessageService.getUserMessages(userId, page, size));
    }

    @Operation(summary = "标记消息已读")
    @PostMapping("/messages/{messageId}/read")
    public Result<Void> markMessageAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestParam Long userId) {
        smartArrivalMessageService.markMessageAsRead(messageId, userId);
        return Result.success();
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/users/{userId}/messages/unread-count")
    public Result<Integer> getUnreadMessageCount(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(smartArrivalMessageService.getUnreadMessageCount(userId));
    }

    @Operation(summary = "生成个性化推荐")
    @GetMapping("/users/{userId}/merchants/{merchantId}/recommendations")
    public Result<PersonalizedRecommendationVO> generateRecommendations(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(smartArrivalMessageService.generateRecommendations(userId, merchantId));
    }

    @Operation(summary = "匹配最佳优惠券")
    @GetMapping("/users/{userId}/merchants/{merchantId}/best-coupon")
    public Result<MatchedCouponVO> matchBestCoupon(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(smartArrivalMessageService.matchBestCoupon(userId, merchantId));
    }

    // ==================== 统计接口 ====================

    @Operation(summary = "获取围栏统计信息")
    @GetMapping("/merchants/{merchantId}/statistics")
    public Result<GeofenceStatisticsVO> getGeofenceStatistics(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(geofenceService.getGeofenceStatistics(merchantId));
    }

    @Operation(summary = "获取用户到店统计")
    @GetMapping("/users/{userId}/arrival-statistics")
    public Result<UserArrivalStatisticsVO> getUserArrivalStatistics(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(userGeofenceStateService.getUserArrivalStatistics(userId, days));
    }
}
