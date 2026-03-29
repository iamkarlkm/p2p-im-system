package com.im.backend.modules.local.controller;

import com.im.backend.common.model.Result;
import com.im.backend.modules.local.dto.*;
import com.im.backend.modules.local.service.IGeofenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 地理围栏控制器
 */
@RestController
@RequestMapping("/api/v1/geofence")
@Tag(name = "地理围栏", description = "地理围栏智能调度与资源管理")
public class GeofenceController {
    
    @Autowired
    private IGeofenceService geofenceService;
    
    @PostMapping("/create")
    @Operation(summary = "创建地理围栏")
    public Result<GeofenceResponse> createGeofence(@RequestBody CreateGeofenceRequest request) {
        return Result.success(geofenceService.createGeofence(request));
    }
    
    @GetMapping("/{geofenceId}")
    @Operation(summary = "获取围栏详情")
    public Result<GeofenceResponse> getGeofence(@PathVariable String geofenceId) {
        return Result.success(geofenceService.getGeofenceById(geofenceId));
    }
    
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商户的所有围栏")
    public Result<List<GeofenceResponse>> getMerchantGeofences(@PathVariable String merchantId) {
        return Result.success(geofenceService.getGeofencesByMerchant(merchantId));
    }
    
    @PutMapping("/{geofenceId}")
    @Operation(summary = "更新围栏")
    public Result<GeofenceResponse> updateGeofence(
            @PathVariable String geofenceId,
            @RequestBody CreateGeofenceRequest request) {
        return Result.success(geofenceService.updateGeofence(geofenceId, request));
    }
    
    @PostMapping("/{geofenceId}/toggle")
    @Operation(summary = "启用/禁用围栏")
    public Result<GeofenceResponse> toggleGeofence(
            @PathVariable String geofenceId,
            @RequestParam Boolean enable) {
        return Result.success(geofenceService.toggleGeofence(geofenceId, enable));
    }
    
    @DeleteMapping("/{geofenceId}")
    @Operation(summary = "删除围栏")
    public Result<Void> deleteGeofence(@PathVariable String geofenceId) {
        geofenceService.deleteGeofence(geofenceId);
        return Result.success();
    }
    
    @GetMapping("/check-point")
    @Operation(summary = "检查点是否在围栏内")
    public Result<Boolean> checkPointInGeofence(
            @RequestParam String geofenceId,
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return Result.success(geofenceService.isPointInGeofence(geofenceId, longitude, latitude));
    }
    
    @GetMapping("/find-by-point")
    @Operation(summary = "查找包含点的所有围栏")
    public Result<List<GeofenceResponse>> findGeofencesByPoint(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return Result.success(geofenceService.findGeofencesByPoint(longitude, latitude));
    }
    
    @GetMapping("/{geofenceId}/capacity-load")
    @Operation(summary = "获取围栏运力负载")
    public Result<CapacityLoadResponse> getCapacityLoad(@PathVariable String geofenceId) {
        return Result.success(geofenceService.getCapacityLoad(geofenceId));
    }
    
    @PostMapping("/{geofenceId}/update-load")
    @Operation(summary = "更新围栏负载")
    public Result<Void> updateCapacityLoad(@PathVariable String geofenceId) {
        geofenceService.updateCapacityLoad(geofenceId);
        return Result.success();
    }
    
    @PostMapping("/dispatch")
    @Operation(summary = "资源调度")
    public Result<ResourceDispatchResponse> dispatchResource(@RequestBody ResourceDispatchRequest request) {
        return Result.success(geofenceService.dispatchResource(request));
    }
    
    @PostMapping("/cross-dispatch")
    @Operation(summary = "跨围栏借调运力")
    public Result<ResourceDispatchResponse> crossFenceDispatch(
            @RequestParam String fromGeofenceId,
            @RequestParam String toGeofenceId,
            @RequestParam Integer staffCount) {
        return Result.success(geofenceService.crossFenceDispatch(fromGeofenceId, toGeofenceId, staffCount));
    }
}
