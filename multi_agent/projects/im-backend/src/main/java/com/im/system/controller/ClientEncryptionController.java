package com.im.system.controller;

import com.im.system.entity.ClientEncryptionEntity;
import com.im.system.service.ClientEncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 客户端加密本地存储 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/encryption")
@RequiredArgsConstructor
@Tag(name = "客户端加密", description = "客户端加密本地存储管理")
public class ClientEncryptionController {
    
    private final ClientEncryptionService clientEncryptionService;
    
    /**
     * 初始化加密配置
     */
    @PostMapping("/initialize")
    @Operation(summary = "初始化加密配置", description = "为用户创建加密配置和密钥")
    public ResponseEntity<ClientEncryptionEntity> initializeEncryption(
            @RequestParam @Parameter(description = "用户 ID") Long userId,
            @RequestParam @Parameter(description = "设备 ID") String deviceId,
            @RequestParam(required = false, defaultValue = "AES-256-GCM") @Parameter(description = "加密算法") String algorithm) {
        
        ClientEncryptionEntity entity = clientEncryptionService.initializeEncryption(userId, deviceId, algorithm);
        return ResponseEntity.ok(entity);
    }
    
    /**
     * 获取加密配置
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取加密配置", description = "获取用户的加密配置信息")
    public ResponseEntity<ClientEncryptionEntity> getEncryptionConfig(
            @PathVariable @Parameter(description = "用户 ID") Long userId) {
        
        return clientEncryptionService.getEncryptionConfig(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 启用加密
     */
    @PostMapping("/{userId}/enable")
    @Operation(summary = "启用加密", description = "启用用户的本地加密存储")
    public ResponseEntity<Map<String, Object>> enableEncryption(
            @PathVariable @Parameter(description = "用户 ID") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") @Parameter(description = "加密范围") String scope) {
        
        boolean success = clientEncryptionService.enableEncryption(userId, scope);
        
        Map<String, Object> response = Map.of(
                "success", success,
                "userId", userId,
                "enabled", success,
                "scope", scope,
                "timestamp", LocalDateTime.now()
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    /**
     * 禁用加密
     */
    @PostMapping("/{userId}/disable")
    @Operation(summary = "禁用加密", description = "禁用用户的本地加密存储")
    public ResponseEntity<Map<String, Object>> disableEncryption(
            @PathVariable @Parameter(description = "用户 ID") Long userId) {
        
        boolean success = clientEncryptionService.disableEncryption(userId);
        
        Map<String, Object> response = Map.of(
                "success", success,
                "userId", userId,
                "enabled", !success,
                "timestamp", LocalDateTime.now()
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    /**
     * 轮换密钥
     */
    @PostMapping("/{userId}/rotate-key")
    @Operation(summary = "轮换密钥", description = "生成新的加密密钥")
    public ResponseEntity<Map<String, Object>> rotateKey(
            @PathVariable @Parameter(description = "用户 ID") Long userId) {
        
        boolean success = clientEncryptionService.rotateKey(userId);
        
        Map<String, Object> response = Map.of(
                "success", success,
                "userId", userId,
                "keyRotated", success,
                "timestamp", LocalDateTime.now()
        );
        
        return success ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    /**
     * 创建备份密钥
     */
    @PostMapping("/{userId}/create-backup")
    @Operation(summary = "创建备份密钥", description = "生成备份密钥用于恢复")
    public ResponseEntity<Map<String, Object>> createBackupKey(
            @PathVariable @Parameter(description = "用户 ID") Long userId) {
        
        String backupKey = clientEncryptionService.createBackupKey(userId);
        
        if (backupKey != null) {
            Map<String, Object> response = Map.of(
                    "success", true,
                    "userId", userId,
                    "backupKey", backupKey,
                    "warning", "Please save this backup key securely. It cannot be retrieved later.",
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 记录加密操作
     */
    @PostMapping("/{userId}/record-encryption")
    @Operation(summary = "记录加密操作", description = "记录加密消息数量")
    public ResponseEntity<Map<String, Object>> recordEncryption(
            @PathVariable @Parameter(description = "用户 ID") Long userId,
            @RequestParam @Parameter(description = "加密消息数") int count) {
        
        clientEncryptionService.recordEncryption(userId, count);
        
        Map<String, Object> response = Map.of(
                "success", true,
                "userId", userId,
                "recordedCount", count,
                "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 记录解密操作
     */
    @PostMapping("/{userId}/record-decryption")
    @Operation(summary = "记录解密操作", description = "记录解密消息数量")
    public ResponseEntity<Map<String, Object>> recordDecryption(
            @PathVariable @Parameter(description = "用户 ID") Long userId,
            @RequestParam @Parameter(description = "解密消息数") int count) {
        
        clientEncryptionService.recordDecryption(userId, count);
        
        Map<String, Object> response = Map.of(
                "success", true,
                "userId", userId,
                "recordedCount", count,
                "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取加密统计
     */
    @GetMapping("/{userId}/statistics")
    @Operation(summary = "获取加密统计", description = "获取用户的加密操作统计信息")
    public ResponseEntity<Map<String, Object>> getEncryptionStatistics(
            @PathVariable @Parameter(description = "用户 ID") Long userId) {
        
        Map<String, Object> stats = clientEncryptionService.getEncryptionStatistics(userId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查加密服务健康状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "service", "client-encryption-service",
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"
        );
        return ResponseEntity.ok(health);
    }
}