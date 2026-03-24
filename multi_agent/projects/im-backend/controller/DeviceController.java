package com.im.backend.controller;

import com.im.backend.entity.DeviceEntity;
import com.im.backend.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理 REST API
 * 多设备管理与设备列表
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // ========== 设备注册与查询 ==========

    @PostMapping("/register")
    public ResponseEntity<DeviceEntity> registerDevice(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String deviceType = body.get("deviceType");
        String deviceName = body.get("deviceName");
        String deviceModel = body.get("deviceModel");
        String osVersion = body.get("osVersion");
        String appVersion = body.get("appVersion");
        String ipAddress = body.get("ipAddress");
        String pushToken = body.get("pushToken");
        String pushType = body.get("pushType");

        DeviceEntity device = deviceService.registerDevice(
            userId, deviceType, deviceName, deviceModel, osVersion, appVersion,
            ipAddress, pushToken, pushType
        );
        return ResponseEntity.ok(device);
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> getDevice(@PathVariable String deviceId) {
        return deviceService.getDevice(deviceId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceEntity>> getUserDevices(@PathVariable String userId) {
        List<DeviceEntity> devices = deviceService.getUserDevices(userId);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<DeviceEntity>> getUserAllDevices(@PathVariable String userId) {
        List<DeviceEntity> devices = deviceService.getUserAllDevices(userId);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/user/{userId}/online")
    public ResponseEntity<List<DeviceEntity>> getOnlineDevices(@PathVariable String userId) {
        List<DeviceEntity> devices = deviceService.getOnlineDevices(userId);
        return ResponseEntity.ok(devices);
    }

    // ========== 设备状态 ==========

    @PostMapping("/{deviceId}/online")
    public ResponseEntity<Map<String, Object>> markOnline(
            @PathVariable String deviceId,
            @RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        deviceService.markOnline(deviceId, sessionId);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("status", "ONLINE");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{deviceId}/offline")
    public ResponseEntity<Map<String, Object>> markOffline(@PathVariable String deviceId) {
        deviceService.markOffline(deviceId);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("status", "OFFLINE");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{deviceId}/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(@PathVariable String deviceId) {
        deviceService.updateLastActive(deviceId);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }

    // ========== 远程登出 ==========

    @PostMapping("/{deviceId}/logout")
    public ResponseEntity<Map<String, Object>> logoutDevice(@PathVariable String deviceId) {
        deviceService.logoutDevice(deviceId);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("action", "logged_out");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/{userId}/logout-others")
    public ResponseEntity<Map<String, Object>> logoutOtherDevices(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        String currentDeviceId = body.get("currentDeviceId");
        int count = deviceService.logoutOtherDevices(userId, currentDeviceId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("loggedOutCount", count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/{userId}/logout-all")
    public ResponseEntity<Map<String, Object>> logoutAllDevices(@PathVariable String userId) {
        deviceService.logoutAllDevices(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("action", "all_devices_logged_out");
        return ResponseEntity.ok(result);
    }

    // ========== 设备配置 ==========

    @PutMapping("/{deviceId}")
    public ResponseEntity<DeviceEntity> updateDevice(
            @PathVariable String deviceId,
            @RequestBody Map<String, String> body) {
        String name = body.get("deviceName");
        DeviceEntity device = deviceService.updateDeviceName(deviceId, name);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{deviceId}/push-token")
    public ResponseEntity<Map<String, Object>> updatePushToken(
            @PathVariable String deviceId,
            @RequestBody Map<String, String> body) {
        String pushToken = body.get("pushToken");
        String pushType = body.get("pushType");
        deviceService.updatePushToken(deviceId, pushToken, pushType);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("pushTokenUpdated", true);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{deviceId}/hide")
    public ResponseEntity<Map<String, Object>> hideDevice(
            @PathVariable String deviceId,
            @RequestBody Map<String, Boolean> body) {
        boolean hidden = body.getOrDefault("hidden", true);
        deviceService.hideDevice(deviceId, hidden);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("isHidden", hidden);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok().build();
    }

    // ========== 统计信息 ==========

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getDeviceStats(@PathVariable String userId) {
        Map<String, Long> stats = deviceService.getDeviceStats(userId);
        long online = deviceService.getOnlineDeviceCount(userId);
        long total = deviceService.getTotalDeviceCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("totalDevices", total);
        result.put("onlineDevices", online);
        result.put("statusBreakdown", stats);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getDeviceCount(@PathVariable String userId) {
        long total = deviceService.getTotalDeviceCount(userId);
        long online = deviceService.getOnlineDeviceCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("total", total);
        result.put("online", online);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{deviceId}/online-status")
    public ResponseEntity<Map<String, Object>> isOnline(@PathVariable String deviceId) {
        boolean online = deviceService.isDeviceOnline(deviceId);
        Map<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("isOnline", online);
        return ResponseEntity.ok(result);
    }
}
