package com.im.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.security.entity.IdentityFingerprintEntity;
import com.im.security.repository.IdentityFingerprintRepository;
import com.im.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 身份指纹验证服务层
 * 处理安全码验证、二维码扫描、密钥变更通知等业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityFingerprintService {

    private final IdentityFingerprintRepository fingerprintRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;
    
    @Value("${security.fingerprint.safety-code.expiration-minutes:10}")
    private int safetyCodeExpirationMinutes;
    
    @Value("${security.fingerprint.max-daily-attempts:10}")
    private int maxDailyAttempts;
    
    @Value("${security.fingerprint.lock-minutes:30}")
    private int lockMinutes;
    
    /**
     * 创建安全码验证请求
     */
    @Transactional
    public IdentityFingerprintEntity createSafetyCodeVerification(Long userId, String deviceId, 
                                                                  String deviceName, String ipAddress, 
                                                                  String userAgent) {
        // 检查每日尝试次数
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        Long failedAttempts = fingerprintRepository.countFailedAttemptsSince(userId, startOfDay);
        if (failedAttempts >= maxDailyAttempts) {
            throw new SecurityException("今日验证尝试次数已达上限，请稍后再试");
        }
        
        // 生成6位验证码
        String verificationCode = generateVerificationCode();
        
        // 创建指纹记录
        IdentityFingerprintEntity fingerprint = new IdentityFingerprintEntity();
        fingerprint.setUserId(userId);
        fingerprint.setFingerprintType(IdentityFingerprintEntity.FingerprintType.SAFETY_CODE.name());
        fingerprint.setFingerprintValue(passwordEncoder.encode(verificationCode));
        fingerprint.setVerificationCode(verificationCode);
        fingerprint.setExpiresAt(LocalDateTime.now().plusMinutes(safetyCodeExpirationMinutes));
        fingerprint.setDeviceId(deviceId);
        fingerprint.setDeviceName(deviceName);
        fingerprint.setDeviceType("DESKTOP");
        fingerprint.setIpAddress(ipAddress);
        fingerprint.setUserAgent(userAgent);
        fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.PENDING);
        fingerprint.setMaxAttempts(5);
        
        // 设置元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createdFrom", "safety_code_verification");
        metadata.put("deviceInfo", deviceName);
        metadata.put("ipLocation", "pending"); // 可集成IP定位服务
        
        try {
            fingerprint.setMetadataJson(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata", e);
        }
        
        return fingerprintRepository.save(fingerprint);
    }
    
    /**
     * 验证安全码
     */
    @Transactional
    public IdentityFingerprintEntity verifySafetyCode(Long userId, String verificationCode, 
                                                      String deviceId) {
        Optional<IdentityFingerprintEntity> optionalFingerprint = 
            fingerprintRepository.findByVerificationCodeAndUserId(verificationCode, userId);
        
        if (optionalFingerprint.isEmpty()) {
            throw new SecurityException("验证码无效");
        }
        
        IdentityFingerprintEntity fingerprint = optionalFingerprint.get();
        
        // 检查状态
        if (fingerprint.getStatus() != IdentityFingerprintEntity.FingerprintStatus.PENDING) {
            throw new SecurityException("验证码已使用或已过期");
        }
        
        // 检查过期时间
        if (fingerprint.getExpiresAt() != null && fingerprint.getExpiresAt().isBefore(LocalDateTime.now())) {
            fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.EXPIRED);
            fingerprintRepository.save(fingerprint);
            throw new SecurityException("验证码已过期");
        }
        
        // 检查尝试次数
        if (fingerprint.getVerificationAttempts() >= fingerprint.getMaxAttempts()) {
            fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.LOCKED);
            fingerprintRepository.save(fingerprint);
            throw new SecurityException("验证码尝试次数过多，已锁定");
        }
        
        // 验证成功
        fingerprint.setVerificationAttempts(fingerprint.getVerificationAttempts() + 1);
        
        // 检查验证码是否匹配（已经通过查询找到了）
        fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.VERIFIED);
        fingerprint.setVerifiedAt(LocalDateTime.now());
        fingerprint.setDeviceId(deviceId);
        
        // 记录审计日志
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("verificationTime", LocalDateTime.now().toString());
        auditLog.put("method", "SAFETY_CODE");
        auditLog.put("deviceId", deviceId);
        auditLog.put("success", true);
        
        try {
            fingerprint.setAuditLog(objectMapper.writeValueAsString(auditLog));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit log", e);
        }
        
        // 发送WebSocket通知
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "FINGERPRINT_VERIFIED");
        notification.put("fingerprintId", fingerprint.getId());
        notification.put("userId", userId);
        notification.put("method", "SAFETY_CODE");
        
        webSocketService.sendToUser(userId.toString(), "security", notification);
        
        return fingerprintRepository.save(fingerprint);
    }
    
    /**
     * 创建二维码验证请求
     */
    @Transactional
    public IdentityFingerprintEntity createQrCodeVerification(Long userId, String deviceId, 
                                                              String deviceName) {
        // 生成二维码数据（UUID + 时间戳 + 用户ID）
        String qrData = String.format("IM_VERIFY_%s_%d_%s", 
            UUID.randomUUID().toString().substring(0, 8),
            System.currentTimeMillis(),
            userId);
        
        // 创建指纹记录
        IdentityFingerprintEntity fingerprint = new IdentityFingerprintEntity();
        fingerprint.setUserId(userId);
        fingerprint.setFingerprintType(IdentityFingerprintEntity.FingerprintType.QR_SCAN.name());
        fingerprint.setFingerprintValue(passwordEncoder.encode(qrData));
        fingerprint.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // 二维码5分钟有效
        fingerprint.setDeviceId(deviceId);
        fingerprint.setDeviceName(deviceName);
        fingerprint.setDeviceType("MOBILE");
        fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.PENDING);
        fingerprint.setQrImageUrl(generateQrImageUrl(qrData));
        
        // 设置元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("qrData", qrData);
        metadata.put("expiresAt", fingerprint.getExpiresAt().toString());
        metadata.put("purpose", "device_verification");
        
        try {
            fingerprint.setMetadataJson(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata", e);
        }
        
        return fingerprintRepository.save(fingerprint);
    }
    
    /**
     * 验证二维码扫描
     */
    @Transactional
    public IdentityFingerprintEntity verifyQrCodeScan(Long userId, String qrData, 
                                                      String scanningDeviceId) {
        // 查找匹配的二维码记录
        List<IdentityFingerprintEntity> fingerprints = fingerprintRepository.findByFingerprintTypeAndStatus(
            userId, IdentityFingerprintEntity.FingerprintType.QR_SCAN.name(), 
            IdentityFingerprintEntity.FingerprintStatus.PENDING);
        
        IdentityFingerprintEntity matchedFingerprint = null;
        for (IdentityFingerprintEntity fingerprint : fingerprints) {
            if (passwordEncoder.matches(qrData, fingerprint.getFingerprintValue())) {
                matchedFingerprint = fingerprint;
                break;
            }
        }
        
        if (matchedFingerprint == null) {
            throw new SecurityException("二维码无效或已过期");
        }
        
        // 检查过期时间
        if (matchedFingerprint.getExpiresAt() != null && 
            matchedFingerprint.getExpiresAt().isBefore(LocalDateTime.now())) {
            matchedFingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.EXPIRED);
            fingerprintRepository.save(matchedFingerprint);
            throw new SecurityException("二维码已过期");
        }
        
        // 验证成功
        matchedFingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.VERIFIED);
        matchedFingerprint.setVerifiedAt(LocalDateTime.now());
        matchedFingerprint.setDeviceId(scanningDeviceId);
        
        // 记录审计日志
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("verificationTime", LocalDateTime.now().toString());
        auditLog.put("method", "QR_SCAN");
        auditLog.put("scanningDeviceId", scanningDeviceId);
        auditLog.put("success", true);
        
        try {
            matchedFingerprint.setAuditLog(objectMapper.writeValueAsString(auditLog));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit log", e);
        }
        
        // 发送WebSocket通知到原始设备
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "QR_CODE_SCANNED");
        notification.put("fingerprintId", matchedFingerprint.getId());
        notification.put("userId", userId);
        notification.put("scanningDeviceId", scanningDeviceId);
        
        webSocketService.sendToUser(userId.toString(), "security", notification);
        
        return fingerprintRepository.save(matchedFingerprint);
    }
    
    /**
     * 创建密钥变更通知
     */
    @Transactional
    public IdentityFingerprintEntity createKeyChangeNotification(Long userId, String deviceId, 
                                                                 String keyType, String changeReason) {
        IdentityFingerprintEntity fingerprint = new IdentityFingerprintEntity();
        fingerprint.setUserId(userId);
        fingerprint.setFingerprintType(IdentityFingerprintEntity.FingerprintType.KEY_CHANGE.name());
        fingerprint.setFingerprintValue("KEY_CHANGE_" + UUID.randomUUID().toString());
        fingerprint.setDeviceId(deviceId);
        fingerprint.setDeviceType("ANY");
        fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.PENDING);
        fingerprint.setNotificationSent(false);
        
        // 设置元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("keyType", keyType);
        metadata.put("changeReason", changeReason);
        metadata.put("timestamp", LocalDateTime.now().toString());
        metadata.put("requiresConfirmation", true);
        
        try {
            fingerprint.setMetadataJson(objectMapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata", e);
        }
        
        return fingerprintRepository.save(fingerprint);
    }
    
    /**
     * 获取用户的验证历史
     */
    public List<IdentityFingerprintEntity> getVerificationHistory(Long userId, int limit) {
        return fingerprintRepository.findByUserIdAndStatus(userId, IdentityFingerprintEntity.FingerprintStatus.VERIFIED)
            .stream()
            .sorted((a, b) -> b.getVerifiedAt().compareTo(a.getVerifiedAt()))
            .limit(limit)
            .toList();
    }
    
    /**
     * 撤销指纹验证
     */
    @Transactional
    public void revokeFingerprint(Long fingerprintId, String reason) {
        fingerprintRepository.findById(fingerprintId).ifPresent(fingerprint -> {
            fingerprint.setStatus(IdentityFingerprintEntity.FingerprintStatus.REVOKED);
            
            // 更新审计日志
            Map<String, Object> auditUpdate = new HashMap<>();
            try {
                if (fingerprint.getAuditLog() != null) {
                    auditUpdate = objectMapper.readValue(fingerprint.getAuditLog(), Map.class);
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
            
            auditUpdate.put("revokedAt", LocalDateTime.now().toString());
            auditUpdate.put("revokeReason", reason);
            
            try {
                fingerprint.setAuditLog(objectMapper.writeValueAsString(auditUpdate));
            } catch (JsonProcessingException e) {
                log.warn("Failed to update audit log", e);
            }
            
            fingerprintRepository.save(fingerprint);
        });
    }
    
    /**
     * 生成验证码
     */
    private String generateVerificationCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
    
    /**
     * 生成二维码图片URL
     */
    private String generateQrImageUrl(String qrData) {
        // 这里可以集成二维码生成服务
        // 实际项目中应该调用二维码生成API
        return String.format("/api/security/qr-code/%s.png", Base64.getUrlEncoder().encodeToString(qrData.getBytes()));
    }
    
    /**
     * 定时任务：清理过期指纹
     */
    @Scheduled(cron = "0 0 */6 * * *") // 每6小时执行一次
    @Transactional
    public void cleanupExpiredFingerprints() {
        LocalDateTime now = LocalDateTime.now();
        List<IdentityFingerprintEntity> expired = fingerprintRepository.findExpiredFingerprints(now);
        
        if (!expired.isEmpty()) {
            List<Long> ids = expired.stream().map(IdentityFingerprintEntity::getId).toList();
            int updated = fingerprintRepository.markAsExpired(ids, now);
            log.info("标记了 {} 个过期的指纹记录为EXPIRED状态", updated);
        }
    }
    
    /**
     * 定时任务：清理旧记录
     */
    @Scheduled(cron = "0 0 2 * * *") // 每天凌晨2点执行
    @Transactional
    public void cleanupOldRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
        List<IdentityFingerprintEntity> oldRecords = fingerprintRepository.findOldRecordsForCleanup(cutoff);
        
        if (!oldRecords.isEmpty()) {
            fingerprintRepository.deleteAll(oldRecords);
            log.info("清理了 {} 条旧的指纹记录", oldRecords.size());
        }
    }
}