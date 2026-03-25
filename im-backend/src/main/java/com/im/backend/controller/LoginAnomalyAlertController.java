package com.im.backend.controller;

import com.im.backend.dto.LoginAnomalyAlertResponse;
import com.im.backend.entity.LoginAnomalyAlert;
import com.im.backend.service.LoginAnomalyAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/security/login-alerts")
@RequiredArgsConstructor
public class LoginAnomalyAlertController {

    private final LoginAnomalyAlertService alertService;

    @GetMapping
    public ResponseEntity<List<LoginAnomalyAlertResponse>> getUserAlerts(
            @RequestHeader("X-User-Id") Long userId) {
        List<LoginAnomalyAlert> alerts = alertService.getUserAlerts(userId);
        List<LoginAnomalyAlertResponse> response = alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LoginAnomalyAlertResponse>> getPendingAlerts(
            @RequestHeader("X-User-Id") Long userId) {
        List<LoginAnomalyAlert> alerts = alertService.getPendingAlerts(userId);
        List<LoginAnomalyAlertResponse> response = alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{alertId}/confirm")
    public ResponseEntity<LoginAnomalyAlertResponse> confirmAlert(
            @PathVariable Long alertId,
            @RequestHeader("X-User-Id") Long userId) {
        LoginAnomalyAlert alert = alertService.confirmAlert(alertId, userId);
        return ResponseEntity.ok(toResponse(alert));
    }

    @PostMapping("/{alertId}/dismiss")
    public ResponseEntity<LoginAnomalyAlertResponse> dismissAlert(
            @PathVariable Long alertId,
            @RequestHeader("X-User-Id") Long userId) {
        LoginAnomalyAlert alert = alertService.dismissAlert(alertId, userId);
        return ResponseEntity.ok(toResponse(alert));
    }

    private LoginAnomalyAlertResponse toResponse(LoginAnomalyAlert alert) {
        return LoginAnomalyAlertResponse.builder()
                .id(alert.getId())
                .userId(alert.getUserId())
                .alertType(alert.getAlertType())
                .deviceId(alert.getDeviceId())
                .deviceName(alert.getDeviceName())
                .deviceType(alert.getDeviceType())
                .ipAddress(alert.getIpAddress())
                .location(alert.getLocation())
                .loginTime(alert.getLoginTime())
                .isConfirmed(alert.getIsConfirmed())
                .confirmedAt(alert.getConfirmedAt())
                .isDismissed(alert.getIsDismissed())
                .dismissedAt(alert.getDismissedAt())
                .riskScore(alert.getRiskScore())
                .riskFactors(alert.getRiskFactors())
                .actionTaken(alert.getActionTaken())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
