package com.im.system.service;

import com.im.system.entity.ClientEncryptionEntity;
import com.im.system.repository.ClientEncryptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端加密本地存储服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientEncryptionService {
    
    private final ClientEncryptionRepository clientEncryptionRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // 用户加密配置缓存
    private final Map<Long, ClientEncryptionEntity> encryptionCache = new ConcurrentHashMap<>();
    
    /**
     * 初始化用户加密配置
     */
    @Transactional
    public ClientEncryptionEntity initializeEncryption(Long userId, String deviceId, String algorithm) {
        return clientEncryptionRepository.findByUserId(userId).orElseGet(() -> {
            ClientEncryptionEntity entity = new ClientEncryptionEntity();
            entity.setUserId(userId);
            entity.setDeviceId(deviceId);
            entity.setMasterKey(generateSecureKey());
            entity.setKeyDerivationSalt(generateSalt());
            entity.setEncryptionAlgorithm(algorithm != null ? algorithm : "AES-256-GCM");
            entity.setEncryptionEnabled(false);
            entity.setKeyVersion(1);
            
            ClientEncryptionEntity saved = clientEncryptionRepository.save(entity);
            encryptionCache.put(userId, saved);
            
            log.info("初始化用户加密配置：userId={}, algorithm={}", userId, entity.getEncryptionAlgorithm());
            return saved;
        });
    }
    
    /**
     * 获取用户加密配置
     */
    public Optional<ClientEncryptionEntity> getEncryptionConfig(Long userId) {
        // 先查缓存
        ClientEncryptionEntity cached = encryptionCache.get(userId);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // 查数据库
        Optional<ClientEncryptionEntity> entity = clientEncryptionRepository.findByUserId(userId);
        entity.ifPresent(e -> encryptionCache.put(userId, e));
        return entity;
    }
    
    /**
     * 启用加密
     */
    @Transactional
    public boolean enableEncryption(Long userId, String scope) {
        Optional<ClientEncryptionEntity> entityOpt = clientEncryptionRepository.findByUserId(userId);
        if (entityOpt.isEmpty()) {
            return false;
        }
        
        ClientEncryptionEntity entity = entityOpt.get();
        entity.setEncryptionEnabled(true);
        entity.setEncryptionScope(scope != null ? scope : "ALL");
        entity.setKeyLastUsedAt(LocalDateTime.now());
        
        clientEncryptionRepository.save(entity);
        encryptionCache.put(userId, entity);
        
        log.info("启用加密：userId={}, scope={}", userId, entity.getEncryptionScope());
        return true;
    }
    
    /**
     * 禁用加密
     */
    @Transactional
    public boolean disableEncryption(Long userId) {
        Optional<ClientEncryptionEntity> entityOpt = clientEncryptionRepository.findByUserId(userId);
        if (entityOpt.isEmpty()) {
            return false;
        }
        
        ClientEncryptionEntity entity = entityOpt.get();
        entity.setEncryptionEnabled(false);
        
        clientEncryptionRepository.save(entity);
        encryptionCache.put(userId, entity);
        
        log.info("禁用加密：userId={}", userId);
        return true;
    }
    
    /**
     * 轮换密钥
     */
    @Transactional
    public boolean rotateKey(Long userId) {
        Optional<ClientEncryptionEntity> entityOpt = clientEncryptionRepository.findByUserId(userId);
        if (entityOpt.isEmpty()) {
            return false;
        }
        
        ClientEncryptionEntity entity = entityOpt.get();
        entity.setMasterKey(generateSecureKey());
        entity.setKeyDerivationSalt(generateSalt());
        entity.setKeyVersion(entity.getKeyVersion() + 1);
        entity.setKeyCreatedAt(LocalDateTime.now());
        entity.setBackedUp(false);
        
        clientEncryptionRepository.save(entity);
        encryptionCache.put(userId, entity);
        
        log.info("轮换密钥：userId={}, newVersion={}", userId, entity.getKeyVersion());
        return true;
    }
    
    /**
     * 创建备份密钥
     */
    @Transactional
    public String createBackupKey(Long userId) {
        Optional<ClientEncryptionEntity> entityOpt = clientEncryptionRepository.findByUserId(userId);
        if (entityOpt.isEmpty()) {
            return null;
        }
        
        ClientEncryptionEntity entity = entityOpt.get();
        String backupKey = generateSecureKey();
        entity.setBackupKey(backupKey);
        entity.setBackedUp(true);
        entity.setBackupKeyCreatedAt(LocalDateTime.now());
        
        clientEncryptionRepository.save(entity);
        encryptionCache.put(userId, entity);
        
        log.info("创建备份密钥：userId={}", userId);
        return backupKey;
    }
    
    /**
     * 记录加密操作
     */
    @Transactional
    public void recordEncryption(Long userId, int count) {
        clientEncryptionRepository.incrementEncryptedCount(userId, (long) count, LocalDateTime.now());
    }
    
    /**
     * 记录解密操作
     */
    @Transactional
    public void recordDecryption(Long userId, int count) {
        clientEncryptionRepository.incrementDecryptedCount(userId, (long) count, LocalDateTime.now());
    }
    
    /**
     * 获取加密统计
     */
    public Map<String, Object> getEncryptionStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<ClientEncryptionEntity> entityOpt = clientEncryptionRepository.findByUserId(userId);
        if (entityOpt.isPresent()) {
            ClientEncryptionEntity entity = entityOpt.get();
            stats.put("enabled", entity.getEncryptionEnabled());
            stats.put("algorithm", entity.getEncryptionAlgorithm());
            stats.put("scope", entity.getEncryptionScope());
            stats.put("keyVersion", entity.getKeyVersion());
            stats.put("encryptedCount", entity.getEncryptedMessageCount());
            stats.put("decryptedCount", entity.getDecryptedMessageCount());
            stats.put("encryptionFailures", entity.getEncryptionFailureCount());
            stats.put("decryptionFailures", entity.getDecryptionFailureCount());
            stats.put("lastSyncAt", entity.getLastSyncAt());
            stats.put("backedUp", entity.getBackedUp());
        } else {
            stats.put("enabled", false);
            stats.put("message", "Encryption not configured");
        }
        
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }
    
    /**
     * 生成安全密钥
     */
    private String generateSecureKey() {
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    
    /**
     * 生成盐值
     */
    private String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}