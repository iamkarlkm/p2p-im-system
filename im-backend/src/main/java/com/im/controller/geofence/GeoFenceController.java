package com.im.controller.geofence;

import com.im.common.Result;
import com.im.entity.geofence.GeoFence;
import com.im.entity.geofence.UserFenceStatus;
import com.im.entity.geofence.FenceTriggerMessage;
import com.im.service.geofence.GeoFenceService;
import com.im.service.geofence.FenceTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏控制器
 */
@RestController
@RequestMapping("/api/v1/geofence")
public class GeoFenceController {
    
    @Autowired
    private GeoFenceService geoFenceService;
    
    @Autowired
    private FenceTriggerService fenceTriggerService;
    
    /**
     * 创建围栏
     */
    @PostMapping("/fences")
    public Result<GeoFence> createFence(@RequestBody GeoFence fence) {
        GeoFence created = geoFenceService.createFence(fence);
        return Result.success(created);
    }
    
    /**
     * 更新围栏
     */
    @PutMapping("/fences/{fenceId}")
    public Result<GeoFence> updateFence(@PathVariable String fenceId, @RequestBody GeoFence fence) {
        GeoFence updated = geoFenceService.updateFence(fenceId, fence);
        return Result.success(updated);
    }
    
    /**
     * 删除围栏
     */
    @DeleteMapping("/fences/{fenceId}")
    public Result<Void> deleteFence(@PathVariable String fenceId) {
        geoFenceService.deleteFence(fenceId);
        return Result.success();
    }
    
    /**
     * 获取围栏详情
     */
    @GetMapping("/fences/{fenceId}")
    public Result<GeoFence> getFence(@PathVariable String fenceId) {
        GeoFence fence = geoFenceService.getFenceById(fenceId);
        return Result.success(fence);
    }
    
    /**
     * 获取POI的所有围栏
     */
    @GetMapping("/fences/poi/{poiId}")
    public Result<List<GeoFence>> getFencesByPoi(@PathVariable String poiId) {
        List<GeoFence> fences = geoFenceService.getFencesByPoiId(poiId);
        return Result.success(fences);
    }
    
    /**
     * 获取所有围栏
     */
    @GetMapping("/fences")
    public Result<List<GeoFence>> getAllFences() {
        List<GeoFence> fences = geoFenceService.getAllActiveFences();
        return Result.success(fences);
    }
    
    /**
     * 启用围栏
     */
    @PostMapping("/fences/{fenceId}/enable")
    public Result<Void> enableFence(@PathVariable String fenceId) {
        geoFenceService.enableFence(fenceId);
        return Result.success();
    }
    
    /**
     * 禁用围栏
     */
    @PostMapping("/fences/{fenceId}/disable")
    public Result<Void> disableFence(@PathVariable String fenceId) {
        geoFenceService.disableFence(fenceId);
        return Result.success();
    }
    
    /**
     * 上报位置并检查围栏
     */
    @PostMapping("/location/report")
    public Result<Map<String, Object>> reportLocation(
            @RequestParam String userId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        
        // 处理位置更新
        fenceTriggerService.processLocationUpdate(userId, longitude, latitude);
        
        // 获取命中的围栏
        List<GeoFence> hitFences = geoFenceService.checkPointInFences(longitude, latitude);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("hitFences", hitFences);
        result.put("hitCount", hitFences.size());
        
        return Result.success(result);
    }
    
    /**
     * 检查点是否在围栏内
     */
    @GetMapping("/fences/check")
    public Result<Map<String, Object>> checkPointInFence(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        
        List<GeoFence> hitFences = geoFenceService.checkPointInFences(longitude, latitude);
        
        Map<String, Object> result = new HashMap<>();
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("inFence", !hitFences.isEmpty());
        result.put("fences", hitFences);
        
        return Result.success(result);
    }
    
    /**
     * 获取用户的围栏状态
     */
    @GetMapping("/users/{userId}/status")
    public Result<List<UserFenceStatus>> getUserFenceStatus(@PathVariable String userId) {
        List<UserFenceStatus> statuses = fenceTriggerService.getUserFenceStatuses(userId);
        return Result.success(statuses);
    }
    
    /**
     * 获取围栏中的用户
     */
    @GetMapping("/fences/{fenceId}/users")
    public Result<List<UserFenceStatus>> getUsersInFence(@PathVariable String fenceId) {
        List<UserFenceStatus> users = fenceTriggerService.getUsersInFence(fenceId);
        return Result.success(users);
    }
    
    /**
     * 获取用户的消息历史
     */
    @GetMapping("/users/{userId}/messages")
    public Result<List<FenceTriggerMessage>> getUserMessages(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        List<FenceTriggerMessage> messages = fenceTriggerService.getUserMessageHistory(userId, limit);
        return Result.success(messages);
    }
}
