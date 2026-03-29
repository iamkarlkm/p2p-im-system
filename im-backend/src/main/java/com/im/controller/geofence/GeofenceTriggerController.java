package com.im.controller.geofence;

import com.im.common.Result;
import com.im.entity.geofence.*;
import com.im.service.geofence.GeofenceTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 地理围栏场景化触发控制器
 * GeoFence Scenario Trigger Controller
 * 
 * REST API 接口：
 * - 围栏触发规则管理
 * - 用户位置更新处理
 * - 群组场景管理
 * - 触发日志查询
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/geofence")
public class GeofenceTriggerController {
    
    @Autowired
    private GeofenceTriggerService geofenceTriggerService;
    
    // ==================== 触发规则管理 ====================
    
    /**
     * 创建触发规则
     */
    @PostMapping("/rules")
    public Result<GeofenceTriggerRule> createTriggerRule(@RequestBody GeofenceTriggerRule rule) {
        GeofenceTriggerRule created = geofenceTriggerService.createTriggerRule(rule);
        return Result.success(created);
    }
    
    /**
     * 更新触发规则
     */
    @PutMapping("/rules/{ruleId}")
    public Result<GeofenceTriggerRule> updateTriggerRule(@PathVariable String ruleId,
            @RequestBody GeofenceTriggerRule rule) {
        rule.setRuleId(ruleId);
        GeofenceTriggerRule updated = geofenceTriggerService.updateTriggerRule(rule);
        return Result.success(updated);
    }
    
    /**
     * 删除触发规则
     */
    @DeleteMapping("/rules/{ruleId}")
    public Result<Boolean> deleteTriggerRule(@PathVariable String ruleId) {
        boolean success = geofenceTriggerService.deleteTriggerRule(ruleId);
        return Result.success(success);
    }
    
    /**
     * 获取规则详情
     */
    @GetMapping("/rules/{ruleId}")
    public Result<GeofenceTriggerRule> getTriggerRule(@PathVariable String ruleId) {
        GeofenceTriggerRule rule = geofenceTriggerService.getTriggerRuleById(ruleId);
        return Result.success(rule);
    }
    
    /**
     * 获取POI的所有规则
     */
    @GetMapping("/pois/{poiId}/rules")
    public Result<List<GeofenceTriggerRule>> getPoiTriggerRules(@PathVariable String poiId) {
        List<GeofenceTriggerRule> rules = geofenceTriggerService.getTriggerRulesByPoiId(poiId);
        return Result.success(rules);
    }
    
    /**
     * 获取指定类型的规则
     */
    @GetMapping("/rules")
    public Result<List<GeofenceTriggerRule>> getTriggerRulesByType(@RequestParam String type) {
        List<GeofenceTriggerRule> rules = geofenceTriggerService.getTriggerRulesByType(type);
        return Result.success(rules);
    }
    
    /**
     * 启用/禁用规则
     */
    @PutMapping("/rules/{ruleId}/status")
    public Result<Boolean> toggleRuleStatus(@PathVariable String ruleId,
            @RequestParam boolean enabled) {
        boolean success = geofenceTriggerService.toggleRuleStatus(ruleId, enabled);
        return Result.success(success);
    }
    
    // ==================== 位置更新处理 ====================
    
    /**
     * 处理用户位置更新
     */
    @PostMapping("/location/update")
    public Result<GeofenceTriggerService.GeofenceProcessResult> processLocationUpdate(
            @RequestBody LocationUpdateRequest request) {
        GeofenceTriggerService.GeofenceProcessResult result = 
                geofenceTriggerService.processLocationUpdate(
                        request.getUserId(),
                        request.getDeviceId(),
                        request.getLatitude(),
                        request.getLongitude(),
                        request.getAccuracy()
                );
        return Result.success(result);
    }
    
    /**
     * 批量处理位置更新
     */
    @PostMapping("/location/batch-update")
    public Result<List<GeofenceTriggerService.GeofenceProcessResult>> batchProcessLocationUpdates(
            @RequestBody List<GeofenceTriggerService.LocationUpdateRequest> requests) {
        List<GeofenceTriggerService.GeofenceProcessResult> results = 
                geofenceTriggerService.batchProcessLocationUpdates(requests);
        return Result.success(results);
    }
    
    /**
     * 获取用户当前所在的围栏
     */
    @GetMapping("/users/{userId}/active-fences")
    public Result<List<UserGeofenceState>> getUserActiveFences(@PathVariable Long userId) {
        List<UserGeofenceState> states = geofenceTriggerService.getUserActiveFences(userId);
        return Result.success(states);
    }
    
    /**
     * 检查用户是否在围栏内
     */
    @GetMapping("/users/{userId}/fences/{fenceId}/check")
    public Result<Boolean> isUserInFence(@PathVariable Long userId, @PathVariable String fenceId) {
        boolean inFence = geofenceTriggerService.isUserInFence(userId, fenceId);
        return Result.success(inFence);
    }
    
    /**
     * 获取附近的围栏
     */
    @GetMapping("/nearby-fences")
    public Result<List<GeofenceTriggerService.GeofenceInfo>> getNearbyFences(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1000") Double radius) {
        List<GeofenceTriggerService.GeofenceInfo> fences = 
                geofenceTriggerService.getNearbyFences(latitude, longitude, radius);
        return Result.success(fences);
    }
    
    // ==================== 群组场景管理 ====================
    
    /**
     * 创建群组围栏场景
     */
    @PostMapping("/groups")
    public Result<GeofenceGroupScenario> createGroupScenario(@RequestBody GeofenceGroupScenario scenario) {
        GeofenceGroupScenario created = geofenceTriggerService.createGroupScenario(scenario);
        return Result.success(created);
    }
    
    /**
     * 获取群组场景详情
     */
    @GetMapping("/groups/{groupId}")
    public Result<GeofenceGroupScenario> getGroupScenario(@PathVariable String groupId) {
        GeofenceGroupScenario scenario = geofenceTriggerService.getGroupScenario(groupId);
        return Result.success(scenario);
    }
    
    /**
     * 更新群组场景
     */
    @PutMapping("/groups/{groupId}")
    public Result<GeofenceGroupScenario> updateGroupScenario(@PathVariable String groupId,
            @RequestBody GeofenceGroupScenario scenario) {
        scenario.setGroupId(groupId);
        GeofenceGroupScenario updated = geofenceTriggerService.updateGroupScenario(scenario);
        return Result.success(updated);
    }
    
    /**
     * 删除群组场景
     */
    @DeleteMapping("/groups/{groupId}")
    public Result<Boolean> deleteGroupScenario(@PathVariable String groupId) {
        boolean success = geofenceTriggerService.deleteGroupScenario(groupId);
        return Result.success(success);
    }
    
    /**
     * 添加成员到群组
     */
    @PostMapping("/groups/{groupId}/members")
    public Result<Boolean> addMemberToGroup(@PathVariable String groupId,
            @RequestParam String userId,
            @RequestParam String userName) {
        boolean success = geofenceTriggerService.addMemberToGroup(groupId, userId, userName);
        return Result.success(success);
    }
    
    /**
     * 从群组移除成员
     */
    @DeleteMapping("/groups/{groupId}/members/{userId}")
    public Result<Boolean> removeMemberFromGroup(@PathVariable String groupId,
            @PathVariable String userId) {
        boolean success = geofenceTriggerService.removeMemberFromGroup(groupId, userId);
        return Result.success(success);
    }
    
    /**
     * 获取用户的群组场景列表
     */
    @GetMapping("/users/{userId}/groups")
    public Result<List<GeofenceGroupScenario>> getUserGroupScenarios(@PathVariable Long userId) {
        List<GeofenceGroupScenario> scenarios = geofenceTriggerService.getUserGroupScenarios(userId);
        return Result.success(scenarios);
    }
    
    /**
     * 获取群组到达统计
     */
    @GetMapping("/groups/{groupId}/arrival-statistics")
    public Result<GeofenceTriggerService.GroupArrivalStatistics> getGroupArrivalStatistics(
            @PathVariable String groupId) {
        GeofenceTriggerService.GroupArrivalStatistics stats = 
                geofenceTriggerService.getGroupArrivalStatistics(groupId);
        return Result.success(stats);
    }
    
    // ==================== 日志与统计 ====================
    
    /**
     * 查询触发日志
     */
    @PostMapping("/logs/query")
    public Result<List<GeofenceTriggerLog>> queryTriggerLogs(
            @RequestBody GeofenceTriggerService.TriggerLogQueryRequest request) {
        List<GeofenceTriggerLog> logs = geofenceTriggerService.queryTriggerLogs(request);
        return Result.success(logs);
    }
    
    /**
     * 获取规则触发统计
     */
    @GetMapping("/rules/{ruleId}/statistics")
    public Result<GeofenceTriggerService.RuleTriggerStatistics> getRuleTriggerStatistics(
            @PathVariable String ruleId) {
        GeofenceTriggerService.RuleTriggerStatistics stats = 
                geofenceTriggerService.getRuleTriggerStatistics(ruleId);
        return Result.success(stats);
    }
    
    /**
     * 获取POI触发统计
     */
    @GetMapping("/pois/{poiId}/statistics")
    public Result<GeofenceTriggerService.PoiTriggerStatistics> getPoiTriggerStatistics(
            @PathVariable String poiId) {
        GeofenceTriggerService.PoiTriggerStatistics stats = 
                geofenceTriggerService.getPoiTriggerStatistics(poiId);
        return Result.success(stats);
    }
    
    /**
     * 获取场景整体统计
     */
    @GetMapping("/statistics")
    public Result<GeofenceTriggerService.GeofenceSceneStatistics> getSceneStatistics() {
        GeofenceTriggerService.GeofenceSceneStatistics stats = 
                geofenceTriggerService.getSceneStatistics();
        return Result.success(stats);
    }
    
    // ==================== 请求DTO ====================
    
    /**
     * 位置更新请求DTO
     */
    public static class LocationUpdateRequest {
        private Long userId;
        private String deviceId;
        private Double latitude;
        private Double longitude;
        private String accuracy;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getAccuracy() { return accuracy; }
        public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
    }
}
