package com.im.backend.controller;

import com.im.backend.service.DeviceService;
import com.im.backend.dto.DeviceRequest;
import com.im.backend.dto.DeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> registerDevice(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getUserDevices(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(deviceService.getUserDevices(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DeviceResponse>> getActiveDevices(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(deviceService.getActiveDevices(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<DeviceResponse.DeviceStatsResponse> getDeviceStats(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(deviceService.getDeviceStats(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<DeviceResponse.LoginHistoryPage> getLoginHistory(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(deviceService.getLoginHistory(userId, page, size));
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<DeviceResponse> updateDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId,
            @RequestBody DeviceRequest request) {
        request.setDeviceId(deviceId);
        return ResponseEntity.ok(deviceService.updateDevice(userId, request));
    }

    @PostMapping("/{deviceId}/deactivate")
    public ResponseEntity<Void> deactivateDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId) {
        deviceService.deactivateDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> removeDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId) {
        deviceService.removeDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deviceId}/trust")
    public ResponseEntity<Void> trustDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId) {
        deviceService.trustDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deviceId}/untrust")
    public ResponseEntity<Void> untrustDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId) {
        deviceService.untrustDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deviceId}/set-current")
    public ResponseEntity<Void> setCurrentDevice(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long deviceId) {
        deviceService.setCurrentDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }
}
