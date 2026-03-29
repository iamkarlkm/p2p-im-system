package com.im.location.controller;

import com.im.common.response.Result;
import com.im.common.utils.UserContext;
import com.im.location.dto.CreateGeofenceRequest;
import com.im.location.dto.GeofenceResponse;
import com.im.location.entity.GeofenceTriggerRecord;
import com.im.location.service.IGeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地理围栏控制器
 */
@RestController
@RequestMapping("/api/v1/geofence")
@RequiredArgsConstructor
public class GeofenceController {
    
    private final IGeofenceService geofenceService;
    
    /**
     * 创建地理围栏
     */
    @PostMapping("/create")
    public Result<GeofenceResponse> createGeofence(@RequestBody CreateGeofenceRequest request) {
        Long userId = UserContext.getUserId();
        GeofenceResponse response = geofenceService.createGeofence(userId, request);
        return Result.success(response);
    }
    
    /**
     * 获取围栏详情
     */
    @GetMapping("/{geofenceId}")
    public Result<GeofenceResponse> getGeofenceDetail(@PathVariable String geofenceId) {
        GeofenceResponse response = geofenceService.getGeofenceDetail(geofenceId);
        return Result.success(response);
    }
    
    /**
     * 获取会话的所有围栏
     */
    @GetMapping("/session/{sessionId}")
    public Result<List<GeofenceResponse>> getSessionGeofences(@PathVariable String sessionId) {
        List<GeofenceResponse> geofences = geofenceService.getSessionGeofences(sessionId);
        return Result.success(geofences);
    }
    
    /**
     * 删除围栏
     */
    @DeleteMapping("/{geofenceId}")
    public Result<Void> deleteGeofence(@PathVariable String geofenceId) {
        geofenceService.deleteGeofence(geofenceId);
        return Result.success();
    }
    
    /**
     * 获取围栏触发记录
     */
    @GetMapping("/{geofenceId}/triggers")
    public Result<List<GeofenceTriggerRecord>> getTriggerRecords(@PathVariable String geofenceId) {
        List<GeofenceTriggerRecord> records = geofenceService.getTriggerRecords(geofenceId);
        return Result.success(records);
    }
    
    /**
     * 检查点是否在圆形围栏内
     */
    @GetMapping("/check/circle")
    public Result<Boolean> checkPointInCircle(@RequestParam Double longitude,
                                               @RequestParam Double latitude,
                                               @RequestParam Double centerLng,
                                               @RequestParam Double centerLat,
                                               @RequestParam Integer radius) {
        boolean inCircle = geofenceService.isPointInCircle(longitude, latitude, centerLng, centerLat, radius);
        return Result.success(inCircle);
    }
    
    /**
     * 计算两点间距离
     */
    @GetMapping("/distance")
    public Result<Double> calculateDistance(@RequestParam Double lng1,
                                            @RequestParam Double lat1,
                                            @RequestParam Double lng2,
                                            @RequestParam Double lat2) {
        double distance = geofenceService.calculateDistance(lng1, lat1, lng2, lat2);
        return Result.success(distance);
    }
}
