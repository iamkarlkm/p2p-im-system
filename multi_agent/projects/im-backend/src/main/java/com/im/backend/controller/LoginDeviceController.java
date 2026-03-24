package com.im.backend.controller;

import com.im.backend.dto.LoginDeviceResponse;
import com.im.backend.entity.LoginDevice;
import com.im.backend.service.LoginDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/security/devices")
@RequiredArgsConstructor
public class LoginDeviceController {

    private final LoginDeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<LoginDeviceResponse>> getUserDevices(
            @RequestHeader("X-User-Id") Long userId) {
        List<LoginDevice> devices = deviceService.getUserDevices(userId);
        List<LoginDeviceResponse> response = devices.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginDeviceResponse> registerDevice(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody RegisterDeviceRequest request) {
        LoginDevice device = deviceService.registerDevice(
                userId, request.deviceId, request.deviceName,
                request.deviceType, request.deviceModel, request.osVersion,
                request.appVersion, request.ipAddress, request.location,
                request.pushToken);
        return ResponseEntity.ok(toResponse(device));
    }

    @PostMapping("/{deviceId}/trust")
    public ResponseEntity<Void> trustDevice(
            @PathVariable String deviceId,
            @RequestHeader("X-User-Id") Long userId) {
        deviceService.setDeviceTrusted(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deviceId}/terminate")
    public ResponseEntity<Void> terminateDevice(
            @PathVariable String deviceId,
            @RequestHeader("X-User-Id") Long userId) {
        deviceService.terminateDevice(userId, deviceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/terminate-others")
    public ResponseEntity<Void> terminateAllOtherDevices(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String currentDeviceId) {
        deviceService.terminateAllOtherDevices(userId, currentDeviceId);
        return ResponseEntity.ok().build();
    }

    private LoginDeviceResponse toResponse(LoginDevice d) {
        return LoginDeviceResponse.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .deviceId(d.getDeviceId())
                .deviceName(d.getDeviceName())
                .deviceType(d.getDeviceType())
                .deviceModel(d.getDeviceModel())
                .osVersion(d.getOsVersion())
                .appVersion(d.getAppVersion())
                .ipAddress(d.getIpAddress())
                .location(d.getLocation())
                .lastActiveTime(d.getLastActiveTime())
                .firstLoginTime(d.getFirstLoginTime())
                .isCurrent(d.getIsCurrent())
                .isTrusted(d.getIsTrusted())
                .isRemoteTerminated(d.getIsRemoteTerminated())
                .terminatedAt(d.getTerminatedAt())
                .build();
    }

    @lombok.Data
    public static class RegisterDeviceRequest {
        private String deviceId;
        private String deviceName;
        private String deviceType;
        private String deviceModel;
        private String osVersion;
        private String appVersion;
        private String ipAddress;
        private String location;
        private String pushToken;
    }
}
