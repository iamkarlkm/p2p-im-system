package com.im.backend.modules.geofence.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.geofence.dto.*;
import com.im.backend.modules.geofence.service.IArrivalRecordService;
import com.im.backend.modules.geofence.service.IGeofenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地理围栏与到店服务Controller
 */
@Api(tags = "地理围栏与到店服务")
@RestController
@RequestMapping("/api/v1/geofence")
@RequiredArgsConstructor
public class GeofenceController {

    private final IGeofenceService geofenceService;
    private final IArrivalRecordService arrivalRecordService;

    // ==================== 地理围栏管理 ====================

    @ApiOperation("创建地理围栏")
    @PostMapping
    public Result<GeofenceResponse> createGeofence(@RequestBody @Validated CreateGeofenceRequest request) {
        return Result.success(geofenceService.createGeofence(request));
    }

    @ApiOperation("更新地理围栏")
    @PutMapping("/{id}")
    public Result<GeofenceResponse> updateGeofence(@PathVariable Long id, 
                                                    @RequestBody @Validated CreateGeofenceRequest request) {
        return Result.success(geofenceService.updateGeofence(id, request));
    }

    @ApiOperation("删除地理围栏")
    @DeleteMapping("/{id}")
    public Result<Void> deleteGeofence(@PathVariable Long id) {
        geofenceService.deleteGeofence(id);
        return Result.success();
    }

    @ApiOperation("获取围栏详情")
    @GetMapping("/{id}")
    public Result<GeofenceResponse> getGeofence(@PathVariable Long id) {
        return Result.success(geofenceService.getGeofenceById(id));
    }

    @ApiOperation("获取商户围栏列表")
    @GetMapping("/merchant/{merchantId}")
    public Result<List<GeofenceResponse>> getMerchantGeofences(@PathVariable Long merchantId) {
        return Result.success(geofenceService.getGeofencesByMerchantId(merchantId));
    }

    @ApiOperation("获取门店围栏列表")
    @GetMapping("/store/{storeId}")
    public Result<List<GeofenceResponse>> getStoreGeofences(@PathVariable Long storeId) {
        return Result.success(geofenceService.getGeofencesByStoreId(storeId));
    }

    @ApiOperation("激活围栏")
    @PostMapping("/{id}/activate")
    public Result<Void> activateGeofence(@PathVariable Long id) {
        geofenceService.activateGeofence(id);
        return Result.success();
    }

    @ApiOperation("停用围栏")
    @PostMapping("/{id}/deactivate")
    public Result<Void> deactivateGeofence(@PathVariable Long id) {
        geofenceService.deactivateGeofence(id);
        return Result.success();
    }

    // ==================== 位置上报与触发 ====================

    @ApiOperation("用户上报位置")
    @PostMapping("/location/report")
    public Result<List<GeofenceTriggerEvent>> reportLocation(@RequestAttribute("userId") Long userId,
                                                              @RequestBody @Validated LocationReportRequest request) {
        return Result.success(geofenceService.reportLocation(userId, request));
    }

    @ApiOperation("检测围栏触发(测试用)")
    @PostMapping("/check/{userId}")
    public Result<Void> checkGeofenceTriggers(@PathVariable Long userId,
                                               @RequestParam Double longitude,
                                               @RequestParam Double latitude) {
        geofenceService.checkGeofenceTriggers(userId, longitude, latitude);
        return Result.success();
    }

    // ==================== 到店记录查询 ====================

    @ApiOperation("获取用户到店记录")
    @GetMapping("/arrivals/user")
    public Result<List<ArrivalRecordResponse>> getUserArrivals(@RequestAttribute("userId") Long userId,
                                                                @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(arrivalRecordService.getUserArrivalRecords(userId, limit));
    }

    @ApiOperation("获取门店到店记录")
    @GetMapping("/arrivals/store/{storeId}")
    public Result<List<ArrivalRecordResponse>> getStoreArrivals(@PathVariable Long storeId,
                                                                 @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(arrivalRecordService.getStoreArrivalRecords(storeId, limit));
    }

    @ApiOperation("获取用户当前在店状态")
    @GetMapping("/arrivals/current")
    public Result<ArrivalRecordResponse> getCurrentInStore(@RequestAttribute("userId") Long userId) {
        return Result.success(arrivalRecordService.getCurrentInStoreStatus(userId));
    }

    @ApiOperation("获取门店今日到店统计")
    @GetMapping("/arrivals/store/{storeId}/statistics")
    public Result<IArrivalRecordService.StoreArrivalStatistics> getStoreStatistics(@PathVariable Long storeId) {
        return Result.success(arrivalRecordService.getStoreTodayStatistics(storeId));
    }
}
