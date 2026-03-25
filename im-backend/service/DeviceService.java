package com.im.backend.service;

import com.im.backend.entity.DeviceEntity;
import com.im.backend.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceEntity registerDevice(String userId, String deviceType, String deviceName,
                                       String deviceModel, String osVersion, String appVersion,
                                       String ipAddress, String pushToken, String pushType) {
        String deviceToken = UUID.randomUUID().toString();

        DeviceEntity device = new DeviceEntity();
        device.setDeviceId(UUID.randomUUID().toString());
        device.setUserId(userId);
        device.setDeviceToken(deviceToken);
        device.setDeviceName(deviceName != null ? deviceName : deviceType);
        device.setDeviceType(deviceType != null ? deviceType : "OTHER");
        device.setDeviceModel(deviceModel);
        device.setOsVersion(osVersion);
        device.setAppVersion(appVersion);
        device.setIpAddress(ipAddress);
        device.setPushToken(pushToken);
        device.setPushType(pushType);
        device.setStatus("OFFLINE");
        device.setIsCurrent(false);
        device.setPushEnabled(pushToken != null);
        device.setIsHidden(false);
        device.setFirstLoginAt(LocalDateTime.now());
        device.setCreatedAt(LocalDateTime.now());
        device.setCapabilities("[\"PUSH\",\"VOICE\",\"VIDEO\",\"FILE\"]");

        return deviceRepository.save(device);
    }

    public Optional<DeviceEntity> getDevice(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId);
    }

    public List<DeviceEntity> getUserDevices(String userId) {
        return deviceRepository.findByUserIdAndIsHiddenFalseOrderByLastActiveAtDesc(userId);
    }

    public List<DeviceEntity> getUserAllDevices(String userId) {
        return deviceRepository.findByUserIdOrderByLastActiveAtDesc(userId);
    }

    public List<DeviceEntity> getOnlineDevices(String userId) {
        return deviceRepository.findOnlineDevices(userId);
    }

    public long getOnlineDeviceCount(String userId) {
        return deviceRepository.countByUserIdAndStatus(userId, "ONLINE");
    }

    public long getTotalDeviceCount(String userId) {
        return deviceRepository.countByUserId(userId);
    }

    @Transactional
    public DeviceEntity updateDeviceName(String deviceId, String name) {
        DeviceEntity device = deviceRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
        device.setDeviceName(name);
        return deviceRepository.save(device);
    }

    @Transactional
    public void setCurrentDevice(String deviceId, String sessionId) {
        deviceRepository.findByDeviceId(deviceId).ifPresent(device -> {
            device.setIsCurrent(true);
            device.setSessionId(sessionId);
            device.setLastActiveAt(LocalDateTime.now());
            device.setLastOnlineAt(LocalDateTime.now());
            device.setStatus("ONLINE");
            deviceRepository.save(device);
        });
    }

    @Transactional
    public void updateLastActive(String deviceId) {
        deviceRepository.updateLastActive(deviceId, LocalDateTime.now());
    }

    @Transactional
    public void markOnline(String deviceId, String sessionId) {
        deviceRepository.markOnline(deviceId, LocalDateTime.now(), sessionId);
    }

    @Transactional
    public void markOffline(String deviceId) {
        deviceRepository.markOffline(deviceId, LocalDateTime.now());
    }

    @Transactional
    public int logoutOtherDevices(String userId, String currentDeviceId) {
        return deviceRepository.logoutOtherDevices(userId, currentDeviceId);
    }

    @Transactional
    public void logoutDevice(String deviceId) {
        deviceRepository.logoutDevice(deviceId);
    }

    @Transactional
    public void logoutAllDevices(String userId) {
        List<DeviceEntity> devices = deviceRepository.findByUserIdAndStatusOrderByLastActiveAtDesc(userId, "ONLINE");
        for (DeviceEntity device : devices) {
            device.setStatus("OFFLINE");
            device.setSessionId(null);
            deviceRepository.save(device);
        }
    }

    @Transactional
    public void updatePushToken(String deviceId, String pushToken, String pushType) {
        deviceRepository.updatePushToken(deviceId, pushToken, pushType);
    }

    @Transactional
    public void hideDevice(String deviceId, boolean hidden) {
        deviceRepository.findByDeviceId(deviceId).ifPresent(device -> {
            device.setIsHidden(hidden);
            deviceRepository.save(device);
        });
    }

    @Transactional
    public void deleteDevice(String deviceId) {
        deviceRepository.deleteByDeviceId(deviceId);
    }

    public List<DeviceEntity> getDevicesWithPush(String userId) {
        return deviceRepository.findDevicesWithPush(userId);
    }

    @Transactional
    public void cleanupInactiveDevices(String userId, int daysInactive) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysInactive);
        List<DeviceEntity> inactive = deviceRepository.findInactiveDevices(userId, threshold);
        for (DeviceEntity device : inactive) {
            if (!device.getIsCurrent()) {
                deviceRepository.delete(device);
            }
        }
    }

    public Map<String, Long> getDeviceStats(String userId) {
        List<Object[]> stats = deviceRepository.countByStatusGroup(userId);
        Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : stats) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    public boolean isDeviceOnline(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId)
            .map(d -> "ONLINE".equals(d.getStatus()))
            .orElse(false);
    }
}
