package com.im.backend.service;

import com.im.backend.entity.LoginDevice;
import com.im.backend.repository.LoginDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginDeviceService {

    private final LoginDeviceRepository deviceRepository;

    public List<LoginDevice> getUserDevices(Long userId) {
        return deviceRepository.findByUserIdAndIsRemoteTerminatedFalseOrderByLastActiveTimeDesc(userId);
    }

    public LoginDevice getDevice(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }

    @Transactional
    public LoginDevice registerDevice(Long userId, String deviceId, String deviceName,
            String deviceType, String deviceModel, String osVersion, String appVersion,
            String ipAddress, String location, String pushToken) {
        LoginDevice existing = deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElse(null);
        if (existing != null) {
            existing.setLastActiveTime(LocalDateTime.now());
            existing.setIpAddress(ipAddress);
            existing.setLocation(location);
            existing.setPushToken(pushToken);
            return deviceRepository.save(existing);
        }
        LoginDevice device = LoginDevice.builder()
                .userId(userId)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(deviceType)
                .deviceModel(deviceModel)
                .osVersion(osVersion)
                .appVersion(appVersion)
                .ipAddress(ipAddress)
                .location(location)
                .pushToken(pushToken)
                .isCurrent(true)
                .isTrusted(false)
                .firstLoginTime(LocalDateTime.now())
                .lastActiveTime(LocalDateTime.now())
                .build();
        return deviceRepository.save(device);
    }

    @Transactional
    public void setCurrentDevice(Long userId, String deviceId) {
        deviceRepository.findByUserIdAndIsRemoteTerminatedFalseOrderByLastActiveTimeDesc(userId)
                .forEach(d -> {
                    d.setIsCurrent(d.getDeviceId().equals(deviceId));
                    deviceRepository.save(d);
                });
    }

    @Transactional
    public void setDeviceTrusted(Long userId, String deviceId) {
        LoginDevice device = deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setIsTrusted(true);
        deviceRepository.save(device);
    }

    @Transactional
    public void terminateDevice(Long userId, String deviceId) {
        LoginDevice device = deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setIsRemoteTerminated(true);
        device.setTerminatedAt(LocalDateTime.now());
        deviceRepository.save(device);
        log.info("Device {} terminated for user {}", deviceId, userId);
    }

    @Transactional
    public void terminateAllOtherDevices(Long userId, String currentDeviceId) {
        deviceRepository.findByUserIdAndIsRemoteTerminatedFalseOrderByLastActiveTimeDesc(userId)
                .forEach(device -> {
                    if (!device.getDeviceId().equals(currentDeviceId)) {
                        device.setIsRemoteTerminated(true);
                        device.setTerminatedAt(LocalDateTime.now());
                        deviceRepository.save(device);
                    }
                });
    }

    public int getActiveDeviceCount(Long userId) {
        return deviceRepository.findByUserIdAndIsRemoteTerminatedFalseOrderByLastActiveTimeDesc(userId)
                .size();
    }
}
