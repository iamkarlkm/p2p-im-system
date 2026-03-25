package com.im.backend.service;

import com.im.backend.entity.LoginAnomalyAlert;
import com.im.backend.entity.LoginAnomalySettings;
import com.im.backend.repository.LoginAnomalyAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAnomalyAlertService {

    private final LoginAnomalyAlertRepository alertRepository;

    @Transactional
    public LoginAnomalyAlert createAlert(Long userId, String alertType, String deviceId,
            String deviceName, String deviceType, String ipAddress, String location,
            Integer riskScore, String riskFactors) {
        LoginAnomalyAlert alert = LoginAnomalyAlert.builder()
                .userId(userId)
                .alertType(alertType)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(deviceType)
                .ipAddress(ipAddress)
                .location(location)
                .loginTime(LocalDateTime.now())
                .riskScore(riskScore)
                .riskFactors(riskFactors)
                .isConfirmed(false)
                .isDismissed(false)
                .createdAt(LocalDateTime.now())
                .build();
        return alertRepository.save(alert);
    }

    public List<LoginAnomalyAlert> getUserAlerts(Long userId) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<LoginAnomalyAlert> getPendingAlerts(Long userId) {
        return alertRepository.findByUserIdAndIsDismissedFalseOrderByCreatedAtDesc(userId);
    }

    public List<LoginAnomalyAlert> getUnconfirmedAlerts(Long userId) {
        return alertRepository.findByUserIdAndIsConfirmedFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public LoginAnomalyAlert confirmAlert(Long alertId, Long userId) {
        LoginAnomalyAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if (!alert.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        alert.setIsConfirmed(true);
        alert.setConfirmedAt(LocalDateTime.now());
        alert.setActionTaken("CONFIRMED_BY_USER");
        return alertRepository.save(alert);
    }

    @Transactional
    public LoginAnomalyAlert dismissAlert(Long alertId, Long userId) {
        LoginAnomalyAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if (!alert.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        alert.setIsDismissed(true);
        alert.setDismissedAt(LocalDateTime.now());
        alert.setActionTaken("DISMISSED_BY_USER");
        return alertRepository.save(alert);
    }

    public int calculateRiskScore(String ipAddress, String location, String deviceId,
            List<String> knownIps, List<String> knownLocations) {
        int score = 0;
        if (knownIps != null && !knownIps.isEmpty() && !knownIps.contains(ipAddress)) {
            score += 30;
        }
        if (knownLocations != null && !knownLocations.isEmpty() && !knownLocations.contains(location)) {
            score += 40;
        }
        if (deviceId == null || deviceId.isEmpty()) {
            score += 20;
        }
        return Math.min(score, 100);
    }

    public String detectAnomalyType(Long userId, String deviceId, String ipAddress,
            String location, LoginAnomalySettings settings) {
        List<String> anomalies = new ArrayList<>();

        if (settings.getNewDeviceAlert() && deviceId != null) {
            // 新设备检测逻辑
            Optional<LoginAnomalyAlert> existing = alertRepository
                    .findByUserIdAndDeviceIdAndCreatedAtAfter(
                            userId, deviceId, LocalDateTime.now().minusDays(30));
            if (existing.isEmpty()) {
                anomalies.add("NEW_DEVICE");
            }
        }

        if (settings.getCrossRegionAlert() && location != null) {
            // 异地登录检测
            List<String> knownLocs = parseCommaSeparated(settings.getKnownLocations());
            if (!knownLocs.isEmpty() && !knownLocs.contains(location)) {
                anomalies.add("CROSS_REGION");
            }
        }

        if (!anomalies.isEmpty()) {
            return String.join(",", anomalies);
        }
        return "NORMAL";
    }

    private List<String> parseCommaSeparated(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }
}
