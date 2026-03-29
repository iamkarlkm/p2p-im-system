package com.im.backend.service;

import com.im.backend.entity.Device;
import com.im.backend.entity.DeviceLoginHistory;
import com.im.backend.repository.DeviceRepository;
import com.im.backend.repository.DeviceLoginHistoryRepository;
import com.im.backend.dto.DeviceRequest;
import com.im.backend.dto.DeviceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final DeviceLoginHistoryRepository loginHistoryRepository;

    private static final int MAX_ACTIVE_DEVICES = 5;
    private static final int INACTIVE_DAYS_THRESHOLD = 90;

    @Transactional
    public DeviceResponse registerDevice(String userId, DeviceRequest request) {
        Device device = deviceRepository.findByUserIdAndDeviceToken(userId, request.getDeviceToken())
            .orElse(null);

        if (device == null) {
            String deviceToken = request.getDeviceToken() != null
                ? request.getDeviceToken()
                : UUID.randomUUID().toString();

            Integer activeCount = deviceRepository.countActiveByUserId(userId);
            if (activeCount >= MAX_ACTIVE_DEVICES) {
                List<Device> oldestDevices = deviceRepository.findRecentDevices(
                    userId, PageRequest.of(activeCount - 1, 1));
                if (!oldestDevices.isEmpty()) {
                    Device oldest = oldestDevices.get(0);
                    oldest.setIsActive(false);
                    deviceRepository.save(oldest);
                    log.info("Auto-deactivated old device {} for user {}", oldest.getDeviceToken(), userId);
                }
            }

            device = Device.builder()
                .userId(userId)
                .deviceToken(deviceToken)
                .deviceType(request.getDeviceType())
                .deviceName(request.getDeviceName())
                .deviceModel(request.getDeviceModel())
                .osVersion(request.getOsVersion())
                .appVersion(request.getAppVersion())
                .browserInfo(request.getBrowserInfo())
                .ipAddress(request.getIpAddress())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isCurrent(false)
                .isActive(true)
                .isTrusted(request.getIsTrusted() != null ? request.getIsTrusted() : false)
                .build();
            device = deviceRepository.save(device);
        } else {
            device.setLastActiveAt(Instant.now());
            device.setIpAddress(request.getIpAddress() != null ? request.getIpAddress() : device.getIpAddress());
            device.setLocation(request.getLocation() != null ? request.getLocation() : device.getLocation());
            device = deviceRepository.save(device);
        }

        loginHistoryRepository.save(DeviceLoginHistory.builder()
            .userId(userId)
            .deviceId(device.getId())
            .deviceToken(device.getDeviceToken())
            .deviceType(device.getDeviceType())
            .deviceName(device.getDeviceName())
            .ipAddress(request.getIpAddress())
            .location(request.getLocation())
            .loginTime(Instant.now())
            .action("LOGIN")
            .loginStatus("SUCCESS")
            .build());

        return toDeviceResponse(device);
    }

    @Transactional
    public DeviceResponse updateDevice(String userId, DeviceRequest request) {
        Device device = deviceRepository.findByUserIdAndId(userId, request.getDeviceId())
            .orElseThrow(() -> new RuntimeException("Device not found"));

        if (request.getDeviceName() != null) device.setDeviceName(request.getDeviceName());
        if (request.getIsTrusted() != null) device.setIsTrusted(request.getIsTrusted());
        device.setLastActiveAt(Instant.now());

        device = deviceRepository.save(device);
        return toDeviceResponse(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getUserDevices(String userId) {
        return deviceRepository.findByUserIdOrderByLastActiveAtDesc(userId).stream()
            .map(this::toDeviceResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getActiveDevices(String userId) {
        return deviceRepository.findByUserIdAndIsActiveTrueOrderByLastActiveAtDesc(userId).stream()
            .map(this::toDeviceResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateDevice(String userId, Long deviceId) {
        Device device = deviceRepository.findByUserIdAndId(userId, deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setIsActive(false);
        deviceRepository.save(device);

        loginHistoryRepository.save(DeviceLoginHistory.builder()
            .userId(userId)
            .deviceId(device.getId())
            .deviceToken(device.getDeviceToken())
            .deviceType(device.getDeviceType())
            .deviceName(device.getDeviceName())
            .ipAddress(device.getIpAddress())
            .location(device.getLocation())
            .loginTime(Instant.now())
            .action("LOGOUT")
            .loginStatus("SUCCESS")
            .build());

        log.info("Device {} deactivated for user {}", deviceId, userId);
    }

    @Transactional
    public void removeDevice(String userId, Long deviceId) {
        Device device = deviceRepository.findByUserIdAndId(userId, deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));

        loginHistoryRepository.save(DeviceLoginHistory.builder()
            .userId(userId)
            .deviceId(device.getId())
            .deviceToken(device.getDeviceToken())
            .deviceType(device.getDeviceType())
            .deviceName(device.getDeviceName())
            .ipAddress(device.getIpAddress())
            .location(device.getLocation())
            .loginTime(Instant.now())
            .action("REMOVE")
            .loginStatus("SUCCESS")
            .build());

        deviceRepository.delete(device);
        log.info("Device {} removed for user {}", deviceId, userId);
    }

    @Transactional
    public void trustDevice(String userId, Long deviceId) {
        Device device = deviceRepository.findByUserIdAndId(userId, deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setIsTrusted(true);
        deviceRepository.save(device);
    }

    @Transactional
    public void untrustDevice(String userId, Long deviceId) {
        Device device = deviceRepository.findByUserIdAndId(userId, deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setIsTrusted(false);
        deviceRepository.save(device);
    }

    @Transactional
    public void setCurrentDevice(String userId, Long deviceId) {
        deviceRepository.findCurrentDevice(userId).ifPresent(current -> {
            current.setIsCurrent(false);
            deviceRepository.save(current);
        });

        Device device = deviceRepository.findByUserIdAndId(userId, deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setIsCurrent(true);
        deviceRepository.save(device);
    }

    @Transactional(readOnly = true)
    public DeviceResponse.LoginHistoryPage getLoginHistory(String userId, Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        List<DeviceLoginHistory> histories = loginHistoryRepository
            .findByUserIdOrderByLoginTimeDesc(userId, PageRequest.of(p, s));
        Long total = loginHistoryRepository.countByUserId(userId);

        List<DeviceResponse.LoginHistoryResponse> items = histories.stream()
            .map(this::toLoginHistoryResponse)
            .collect(Collectors.toList());

        return DeviceResponse.LoginHistoryPage.builder()
            .items(items).page(p).size(s).total(total)
            .totalPages((int) Math.ceil((double) total / s))
            .build();
    }

    @Transactional(readOnly = true)
    public DeviceResponse.DeviceStatsResponse getDeviceStats(String userId) {
        Integer totalDevices = deviceRepository.countActiveByUserId(userId);
        Integer trustedDevices = deviceRepository.countTrustedByUserId(userId);
        List<Object[]> mostUsed = deviceRepository.findMostUsedDeviceType(userId);
        String mostUsedType = mostUsed.isEmpty() ? "unknown" : (String) mostUsed.get(0)[0];

        return DeviceResponse.DeviceStatsResponse.builder()
            .totalDevices(totalDevices)
            .activeDevices(totalDevices)
            .trustedDevices(trustedDevices)
            .mostUsedDeviceType(mostUsedType)
            .activeSessions(totalDevices)
            .build();
    }

    @Transactional
    public void cleanupInactiveDevices() {
        Instant threshold = Instant.now().minusSeconds(INACTIVE_DAYS_THRESHOLD * 24L * 60 * 60);
        List<Device> inactive = deviceRepository.findByUserIdAndLastActiveAtBefore("", threshold);
        log.info("Found {} inactive devices to clean up", inactive.size());
    }

    private DeviceResponse toDeviceResponse(Device device) {
        return DeviceResponse.builder()
            .id(device.getId())
            .userId(device.getUserId())
            .deviceToken(device.getDeviceToken())
            .deviceType(device.getDeviceType())
            .deviceName(device.getDeviceName())
            .deviceModel(device.getDeviceModel())
            .osVersion(device.getOsVersion())
            .appVersion(device.getAppVersion())
            .browserInfo(device.getBrowserInfo())
            .ipAddress(device.getIpAddress())
            .location(device.getLocation())
            .createdAt(device.getCreatedAt())
            .lastActiveAt(device.getLastActiveAt())
            .isCurrent(device.getIsCurrent())
            .lastLoginAt(device.getLastLoginAt())
            .isActive(device.getIsActive())
            .isTrusted(device.getIsTrusted())
            .build();
    }

    private DeviceResponse.LoginHistoryResponse toLoginHistoryResponse(DeviceLoginHistory history) {
        return DeviceResponse.LoginHistoryResponse.builder()
            .id(history.getId())
            .userId(history.getUserId())
            .deviceId(history.getDeviceId())
            .deviceToken(history.getDeviceToken())
            .deviceType(history.getDeviceType())
            .deviceName(history.getDeviceName())
            .ipAddress(history.getIpAddress())
            .location(history.getLocation())
            .loginTime(history.getLoginTime())
            .logoutTime(history.getLogoutTime())
            .action(history.getAction())
            .loginStatus(history.getLoginStatus())
            .build();
    }
}
