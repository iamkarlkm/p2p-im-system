package com.im.security.controller;

import com.im.security.entity.IdentityFingerprintEntity;
import com.im.security.service.IdentityFingerprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份指纹验证 REST API 控制器
 */
@RestController
@RequestMapping("/api/security/fingerprint")
@RequiredArgsConstructor
@Tag(name = "身份指纹验证", description = "安全码验证、二维码扫描、密钥变更通知等身份验证功能")
public class IdentityFingerprintController {

    private final IdentityFingerprintService fingerprintService;
    
    /**
     * 创建安全码验证请求
     */
    @PostMapping("/safety-code/request")
    @Operation(summary = "请求安全码验证", description = "为当前用户创建安全码验证请求")
    public ResponseEntity<Map<String, Object>> requestSafetyCodeVerification(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String userAgent) {
        
        IdentityFingerprintEntity fingerprint = fingerprintService.createSafetyCodeVerification(
            userId, deviceId, deviceName, ipAddress, userAgent);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fingerprintId", fingerprint.getId());
        response.put("verificationCode", fingerprint.getVerificationCode());
        response.put("expiresAt", fingerprint.getExpiresAt());
        
        // 注意：实际生产环境中，验证码应该通过其他安全渠道发送（如短信、邮件）
        // 这里仅用于演示，返回给客户端
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证安全码
     */
    @PostMapping("/safety-code/verify")
    @Operation(summary = "验证安全码", description = "使用收到的安全码进行验证")
    public ResponseEntity<Map<String, Object>> verifySafetyCode(
            @AuthenticationPrincipal Long userId,
            @RequestParam String verificationCode,
            @RequestParam String deviceId) {
        
        try {
            IdentityFingerprintEntity fingerprint = fingerprintService.verifySafetyCode(
                userId, verificationCode, deviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fingerprintId", fingerprint.getId());
            response.put("verifiedAt", fingerprint.getVerifiedAt());
            response.put("status", fingerprint.getStatus().name());
            
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * 创建二维码验证请求
     */
    @PostMapping("/qr-code/request")
    @Operation(summary = "请求二维码验证", description = "为当前用户创建二维码验证请求")
    public ResponseEntity<Map<String, Object>> requestQrCodeVerification(
            @AuthenticationPrincipal Long userId,
            @RequestParam String deviceId,
            @RequestParam String deviceName) {
        
        IdentityFingerprintEntity fingerprint = fingerprintService.createQrCodeVerification(
            userId, deviceId, deviceName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fingerprintId", fingerprint.getId());
        response.put("qrImageUrl", fingerprint.getQrImageUrl());
        response.put("expiresAt", fingerprint.getExpiresAt());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证二维码扫描
     */
    @PostMapping("/qr-code/verify")
    @Operation(summary = "验证二维码扫描", description = "验证扫描到的二维码数据")
    public ResponseEntity<Map<String, Object>> verifyQrCodeScan(
            @AuthenticationPrincipal Long userId,
            @RequestParam String qrData,
            @RequestParam String scanningDeviceId) {
        
        try {
            IdentityFingerprintEntity fingerprint = fingerprintService.verifyQrCodeScan(
                userId, qrData, scanningDeviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fingerprintId", fingerprint.getId());
            response.put("verifiedAt", fingerprint.getVerifiedAt());
            response.put("status", fingerprint.getStatus().name());
            
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * 创建密钥变更通知
     */
    @PostMapping("/key-change/notify")
    @Operation(summary = "发送密钥变更通知", description = "当用户密钥变更时发送通知")
    public ResponseEntity<Map<String, Object>> notifyKeyChange(
            @AuthenticationPrincipal Long userId,
            @RequestParam String deviceId,
            @RequestParam String keyType,
            @RequestParam String changeReason) {
        
        IdentityFingerprintEntity fingerprint = fingerprintService.createKeyChangeNotification(
            userId, deviceId, keyType, changeReason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fingerprintId", fingerprint.getId());
        response.put("createdAt", fingerprint.getCreatedAt());
        response.put("requiresConfirmation", true);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取验证历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取验证历史", description = "获取用户的身份验证历史记录")
    public ResponseEntity<Map<String, Object>> getVerificationHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<IdentityFingerprintEntity> history = fingerprintService.getVerificationHistory(userId, limit);
        
        List<Map<String, Object>> historyList = history.stream()
            .map(fingerprint -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", fingerprint.getId());
                item.put("type", fingerprint.getFingerprintType());
                item.put("status", fingerprint.getStatus().name());
                item.put("verifiedAt", fingerprint.getVerifiedAt());
                item.put("deviceName", fingerprint.getDeviceName());
                item.put("deviceType", fingerprint.getDeviceType());
                return item;
            })
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("total", historyList.size());
        response.put("history", historyList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取待处理的验证请求
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待处理验证", description = "获取当前用户待处理的验证请求")
    public ResponseEntity<Map<String, Object>> getPendingVerifications(
            @AuthenticationPrincipal Long userId) {
        
        List<IdentityFingerprintEntity> pending = fingerprintService.getPendingVerifications(userId);
        
        List<Map<String, Object>> pendingList = pending.stream()
            .map(fingerprint -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", fingerprint.getId());
                item.put("type", fingerprint.getFingerprintType());
                item.put("createdAt", fingerprint.getCreatedAt());
                item.put("expiresAt", fingerprint.getExpiresAt());
                item.put("deviceName", fingerprint.getDeviceName());
                
                if (fingerprint.getFingerprintType().equals("QR_SCAN")) {
                    item.put("qrImageUrl", fingerprint.getQrImageUrl());
                } else if (fingerprint.getFingerprintType().equals("SAFETY_CODE")) {
                    item.put("hasVerificationCode", fingerprint.getVerificationCode() != null);
                }
                
                return item;
            })
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("count", pendingList.size());
        response.put("pendingVerifications", pendingList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 撤销指纹验证
     */
    @PostMapping("/{fingerprintId}/revoke")
    @Operation(summary = "撤销指纹验证", description = "撤销指定的指纹验证记录")
    public ResponseEntity<Map<String, Object>> revokeFingerprint(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long fingerprintId,
            @RequestParam String reason) {
        
        fingerprintService.revokeFingerprint(fingerprintId, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fingerprintId", fingerprintId);
        response.put("revokedAt", LocalDateTime.now().toString());
        response.put("reason", reason);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取验证统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取验证统计", description = "获取用户的验证统计信息")
    public ResponseEntity<Map<String, Object>> getVerificationStats(
            @AuthenticationPrincipal Long userId) {
        
        // 获取最近30天的验证记录
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 这里应该调用服务层获取统计信息
        // 简化实现，返回示例数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalVerifications", 15);
        stats.put("successfulVerifications", 12);
        stats.put("failedVerifications", 3);
        stats.put("lastVerification", LocalDateTime.now().minusDays(1).toString());
        stats.put("preferredMethod", "SAFETY_CODE");
        stats.put("devicesUsed", 3);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", stats);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查验证状态
     */
    @GetMapping("/{fingerprintId}/status")
    @Operation(summary = "检查验证状态", description = "检查指定指纹验证的当前状态")
    public ResponseEntity<Map<String, Object>> checkVerificationStatus(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long fingerprintId) {
        
        // 这里应该调用服务层获取指纹状态
        // 简化实现
        Map<String, Object> status = new HashMap<>();
        status.put("fingerprintId", fingerprintId);
        status.put("status", "VERIFIED");
        status.put("verifiedAt", LocalDateTime.now().minusMinutes(5).toString());
        status.put("method", "SAFETY_CODE");
        status.put("device", "Desktop Chrome");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重新发送验证码
     */
    @PostMapping("/{fingerprintId}/resend")
    @Operation(summary = "重新发送验证码", description = "为待验证的请求重新发送验证码")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long fingerprintId) {
        
        // 这里应该调用服务层重新发送验证码
        // 简化实现
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fingerprintId", fingerprintId);
        response.put("resentAt", LocalDateTime.now().toString());
        response.put("message", "验证码已重新发送");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查身份指纹验证服务状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "identity-fingerprint");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取支持的验证方法
     */
    @GetMapping("/methods")
    @Operation(summary = "获取支持的验证方法", description = "获取系统支持的验证方法列表")
    public ResponseEntity<Map<String, Object>> getSupportedMethods() {
        List<Map<String, Object>> methods = List.of(
            Map.of(
                "method", "SAFETY_CODE",
                "name", "安全码验证",
                "description", "通过6位数字安全码进行验证",
                "supportedPlatforms", List.of("DESKTOP", "MOBILE", "WEB"),
                "expirationMinutes", 10,
                "requiresNetwork", false
            ),
            Map.of(
                "method", "QR_SCAN",
                "name", "二维码扫描",
                "description", "通过扫描二维码进行设备间验证",
                "supportedPlatforms", List.of("MOBILE"),
                "expirationMinutes", 5,
                "requiresNetwork", true
            ),
            Map.of(
                "method", "KEY_CHANGE",
                "name", "密钥变更通知",
                "description", "密钥变更时发送安全通知",
                "supportedPlatforms", List.of("DESKTOP", "MOBILE", "WEB"),
                "requiresConfirmation", true
            ),
            Map.of(
                "method", "DEVICE_FINGERPRINT",
                "name", "设备指纹识别",
                "description", "基于设备特征进行自动识别",
                "supportedPlatforms", List.of("DESKTOP", "MOBILE"),
                "automatic", true
            )
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("methods", methods);
        
        return ResponseEntity.ok(response);
    }
    
    // 需要在服务层添加这个方法
    private List<IdentityFingerprintEntity> getPendingVerifications(Long userId) {
        // 简化实现 - 实际应该调用服务层
        return List.of();
    }
}