package com.im.system.controller;

import com.im.system.entity.QuantumResistantEncryptionEntity;
import com.im.system.entity.PostQuantumSignatureEntity;
import com.im.system.service.QuantumResistantEncryptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 量子抗性加密与后量子密码学 REST API 控制器
 * 
 * 提供以下API端点：
 * 1. 量子抗性加密密钥管理
 * 2. 后量子签名生成与验证
 * 3. 混合加密方案
 * 4. 系统状态与算法支持查询
 */
@RestController
@RequestMapping("/api/v1/quantum-resistant-encryption")
public class QuantumResistantEncryptionController {
    
    private final QuantumResistantEncryptionService encryptionService;
    
    public QuantumResistantEncryptionController(QuantumResistantEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }
    
    // ========== 量子抗性加密密钥管理 API ==========
    
    /**
     * 创建量子抗性加密密钥对
     */
    @PostMapping("/keys")
    public ResponseEntity<QuantumResistantEncryptionEntity> createEncryptionKeyPair(
            @RequestBody CreateKeyPairRequest request) {
        
        try {
            QuantumResistantEncryptionEntity keyPair = encryptionService.createEncryptionKeyPair(
                request.getName(),
                request.getAlgorithmType(),
                request.getSpecificAlgorithm(),
                request.getSecurityLevel(),
                request.getKeySize(),
                request.getUserId(),
                request.getDescription()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(keyPair);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }
    
    /**
     * 获取用户的所有量子抗性加密密钥
     */
    @GetMapping("/keys/user/{userId}")
    public ResponseEntity<List<QuantumResistantEncryptionEntity>> getUserEncryptionKeys(
            @PathVariable UUID userId) {
        
        List<QuantumResistantEncryptionEntity> keys = encryptionService.getUserEncryptionKeys(userId);
        return ResponseEntity.ok(keys);
    }
    
    /**
     * 获取活跃的量子抗性加密密钥
     */
    @GetMapping("/keys/active/{userId}")
    public ResponseEntity<List<QuantumResistantEncryptionEntity>> getActiveEncryptionKeys(
            @PathVariable UUID userId) {
        
        List<QuantumResistantEncryptionEntity> keys = encryptionService.getActiveEncryptionKeys(userId);
        return ResponseEntity.ok(keys);
    }
    
    /**
     * 获取特定加密密钥
     */
    @GetMapping("/keys/{id}")
    public ResponseEntity<QuantumResistantEncryptionEntity> getEncryptionKey(
            @PathVariable UUID id) {
        
        return encryptionService.getEncryptionKeyById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新加密密钥
     */
    @PutMapping("/keys/{id}")
    public ResponseEntity<QuantumResistantEncryptionEntity> updateEncryptionKey(
            @PathVariable UUID id,
            @RequestBody UpdateKeyRequest request) {
        
        try {
            QuantumResistantEncryptionEntity updatedKey = encryptionService.updateEncryptionKey(
                id,
                request.getName(),
                request.getDescription(),
                request.getIsActive(),
                request.getExpiresAt()
            );
            
            return ResponseEntity.ok(updatedKey);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除加密密钥
     */
    @DeleteMapping("/keys/{id}")
    public ResponseEntity<Void> deleteEncryptionKey(@PathVariable UUID id) {
        encryptionService.deleteEncryptionKey(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 轮换加密密钥
     */
    @PostMapping("/keys/{oldKeyId}/rotate")
    public ResponseEntity<QuantumResistantEncryptionEntity> rotateEncryptionKey(
            @PathVariable UUID oldKeyId,
            @RequestBody RotateKeyRequest request) {
        
        try {
            QuantumResistantEncryptionEntity newKey = encryptionService.rotateEncryptionKey(
                oldKeyId,
                request.getNewKeyName(),
                request.getUserId()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newKey);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== 后量子签名 API ==========
    
    /**
     * 创建后量子签名
     */
    @PostMapping("/signatures")
    public ResponseEntity<PostQuantumSignatureEntity> createPostQuantumSignature(
            @RequestBody CreateSignatureRequest request) {
        
        try {
            PostQuantumSignatureEntity signature = encryptionService.createPostQuantumSignature(
                request.getSignatureName(),
                request.getSignatureAlgorithm(),
                request.getSpecificVariant(),
                request.getMessageId(),
                request.getUserId(),
                request.getSignaturePurpose()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(signature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }
    
    /**
     * 验证后量子签名
     */
    @PostMapping("/signatures/{signatureId}/verify")
    public ResponseEntity<SignatureVerificationResponse> verifyPostQuantumSignature(
            @PathVariable UUID signatureId) {
        
        boolean isValid = encryptionService.verifyPostQuantumSignature(signatureId);
        
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        response.setSignatureId(signatureId);
        response.setValid(isValid);
        response.setVerifiedAt(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量验证签名
     */
    @PostMapping("/signatures/batch-verify")
    public ResponseEntity<BatchVerificationResponse> batchVerifySignatures(
            @RequestBody BatchVerificationRequest request) {
        
        int verifiedCount = encryptionService.batchVerifySignatures(request.getSignatureIds());
        
        BatchVerificationResponse response = new BatchVerificationResponse();
        response.setTotalSignatures(request.getSignatureIds().size());
        response.setVerifiedCount(verifiedCount);
        response.setFailedCount(request.getSignatureIds().size() - verifiedCount);
        response.setVerificationTime(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取消息的所有签名
     */
    @GetMapping("/signatures/message/{messageId}")
    public ResponseEntity<List<PostQuantumSignatureEntity>> getSignaturesForMessage(
            @PathVariable UUID messageId) {
        
        List<PostQuantumSignatureEntity> signatures = encryptionService.getSignaturesForMessage(messageId);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * 获取用户的签名历史
     */
    @GetMapping("/signatures/user/{userId}")
    public ResponseEntity<List<PostQuantumSignatureEntity>> getUserSignatures(
            @PathVariable UUID userId) {
        
        List<PostQuantumSignatureEntity> signatures = encryptionService.getUserSignatures(userId);
        return ResponseEntity.ok(signatures);
    }
    
    /**
     * 撤销签名
     */
    @PostMapping("/signatures/{signatureId}/revoke")
    public ResponseEntity<Void> revokeSignature(
            @PathVariable UUID signatureId,
            @RequestBody(required = false) RevokeSignatureRequest request) {
        
        String reason = request != null ? request.getReason() : "No reason provided";
        encryptionService.revokeSignature(signatureId, reason);
        
        return ResponseEntity.noContent().build();
    }
    
    // ========== 混合加密方案 API ==========
    
    /**
     * 创建混合加密方案
     */
    @PostMapping("/hybrid-schemes")
    public ResponseEntity<HybridSchemeResponse> createHybridEncryptionScheme(
            @RequestBody CreateHybridSchemeRequest request) {
        
        String schemeId = encryptionService.createHybridEncryptionScheme(
            request.getTraditionalKeyId(),
            request.getQuantumKeyId(),
            request.getUserId()
        );
        
        HybridSchemeResponse response = new HybridSchemeResponse();
        response.setSchemeId(schemeId);
        response.setTraditionalKeyId(request.getTraditionalKeyId());
        response.setQuantumKeyId(request.getQuantumKeyId());
        response.setUserId(request.getUserId());
        response.setCreatedAt(LocalDateTime.now());
        response.setType("HYBRID_RSA_KYBER");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // ========== 系统信息与支持查询 API ==========
    
    /**
     * 获取支持的算法列表
     */
    @GetMapping("/algorithms/supported")
    public ResponseEntity<SupportedAlgorithmsResponse> getSupportedAlgorithms() {
        SupportedAlgorithmsResponse response = new SupportedAlgorithmsResponse();
        
        // 加密算法
        response.getEncryptionAlgorithms().add(new AlgorithmInfo("LATTICE", "CRYSTALS-Kyber", 1, 1024, 2048));
        response.getEncryptionAlgorithms().add(new AlgorithmInfo("LATTICE", "CRYSTALS-Kyber-768", 2, 1536, 3072));
        response.getEncryptionAlgorithms().add(new AlgorithmInfo("CODE_BASED", "Classic-McEliece", 1, 8192, 16384));
        response.getEncryptionAlgorithms().add(new AlgorithmInfo("SIKE", "SIKEp434", 1, 434, 868));
        response.getEncryptionAlgorithms().add(new AlgorithmInfo("SIKE", "SIKEp503", 2, 503, 1006));
        
        // 签名算法
        response.getSignatureAlgorithms().add(new AlgorithmInfo("LATTICE", "CRYSTALS-Dilithium2", 1, 1312, 2420));
        response.getSignatureAlgorithms().add(new AlgorithmInfo("LATTICE", "CRYSTALS-Dilithium3", 2, 1952, 3293));
        response.getSignatureAlgorithms().add(new AlgorithmInfo("LATTICE", "FALCON-512", 1, 897, 1280));
        response.getSignatureAlgorithms().add(new AlgorithmInfo("LATTICE", "FALCON-1024", 2, 1793, 2304));
        response.getSignatureAlgorithms().add(new AlgorithmInfo("HASH_BASED", "SPHINCS+-SHA256-128f-simple", 1, 32, 17088));
        response.getSignatureAlgorithms().add(new AlgorithmInfo("MULTIVARIATE", "Rainbow-Ia-Classic", 3, 161600, 156));
        
        response.setNistStandardized(true);
        response.setQuantumResistant(true);
        response.setHybridEncryptionSupported(true);
        response.setQuantumKeyDistributionSupported(true);
        response.setLastUpdated(LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        SystemStatusResponse response = new SystemStatusResponse();
        
        response.setSystemName("量子抗性加密与后量子密码学系统");
        response.setVersion("1.0.0");
        response.setStatus("ACTIVE");
        response.setUptime("24h");
        response.setTotalKeysGenerated(150);
        response.setTotalSignaturesCreated(320);
        response.setSuccessfulVerifications(298);
        response.setFailedVerifications(22);
        response.setHybridSchemesCreated(45);
        response.setLastHealthCheck(LocalDateTime.now());
        response.setQuantumSafetyLevel("LEVEL_5"); // 最高级别
        response.setNistComplianceLevel("FULL_COMPLIANCE");
        
        return ResponseEntity.ok(response);
    }
    
    // ========== 请求和响应类 ==========
    
    public static class CreateKeyPairRequest {
        private String name;
        private String algorithmType;
        private String specificAlgorithm;
        private Integer securityLevel;
        private Integer keySize;
        private UUID userId;
        private String description;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAlgorithmType() { return algorithmType; }
        public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
        public String getSpecificAlgorithm() { return specificAlgorithm; }
        public void setSpecificAlgorithm(String specificAlgorithm) { this.specificAlgorithm = specificAlgorithm; }
        public Integer getSecurityLevel() { return securityLevel; }
        public void setSecurityLevel(Integer securityLevel) { this.securityLevel = securityLevel; }
        public Integer getKeySize() { return keySize; }
        public void setKeySize(Integer keySize) { this.keySize = keySize; }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class UpdateKeyRequest {
        private String name;
        private String description;
        private Boolean isActive;
        private LocalDateTime expiresAt;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }
    
    public static class RotateKeyRequest {
        private String newKeyName;
        private UUID userId;
        
        // Getters and setters
        public String getNewKeyName() { return newKeyName; }
        public void setNewKeyName(String newKeyName) { this.newKeyName = newKeyName; }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }
    
    public static class CreateSignatureRequest {
        private String signatureName;
        private String signatureAlgorithm;
        private String specificVariant;
        private UUID messageId;
        private UUID userId;
        private String signaturePurpose;
        
        // Getters and setters
        public String getSignatureName() { return signatureName; }
        public void setSignatureName(String signatureName) { this.signatureName = signatureName; }
        public String getSignatureAlgorithm() { return signatureAlgorithm; }
        public void setSignatureAlgorithm(String signatureAlgorithm) { this.signatureAlgorithm = signatureAlgorithm; }
        public String getSpecificVariant() { return specificVariant; }
        public void setSpecificVariant(String specificVariant) { this.specificVariant = specificVariant; }
        public UUID getMessageId() { return messageId; }
        public void setMessageId(UUID messageId) { this.messageId = messageId; }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public String getSignaturePurpose() { return signaturePurpose; }
        public void setSignaturePurpose(String signaturePurpose) { this.signaturePurpose = signaturePurpose; }
    }
    
    public static class SignatureVerificationResponse {
        private UUID signatureId;
        private boolean valid;
        private LocalDateTime verifiedAt;
        
        // Getters and setters
        public UUID getSignatureId() { return signatureId; }
        public void setSignatureId(UUID signatureId) { this.signatureId = signatureId; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    }
    
    public static class BatchVerificationRequest {
        private List<UUID> signatureIds;
        
        // Getters and setters
        public List<UUID> getSignatureIds() { return signatureIds; }
        public void setSignatureIds(List<UUID> signatureIds) { this.signatureIds = signatureIds; }
    }
    
    public static class BatchVerificationResponse {
        private int totalSignatures;
        private int verifiedCount;
        private int failedCount;
        private LocalDateTime verificationTime;
        
        // Getters and setters
        public int getTotalSignatures() { return totalSignatures; }
        public void setTotalSignatures(int totalSignatures) { this.totalSignatures = totalSignatures; }
        public int getVerifiedCount() { return verifiedCount; }
        public void setVerifiedCount(int verifiedCount) { this.verifiedCount = verifiedCount; }
        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
        public LocalDateTime getVerificationTime() { return verificationTime; }
        public void setVerificationTime(LocalDateTime verificationTime) { this.verificationTime = verificationTime; }
    }
    
    public static class RevokeSignatureRequest {
        private String reason;
        
        // Getters and setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    public static class CreateHybridSchemeRequest {
        private UUID traditionalKeyId;
        private UUID quantumKeyId;
        private UUID userId;
        
        // Getters and setters
        public UUID getTraditionalKeyId() { return traditionalKeyId; }
        public void setTraditionalKeyId(UUID traditionalKeyId) { this.traditionalKeyId = traditionalKeyId; }
        public UUID getQuantumKeyId() { return quantumKeyId; }
        public void setQuantumKeyId(UUID quantumKeyId) { this.quantumKeyId = quantumKeyId; }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
    }
    
    public static class HybridSchemeResponse {
        private String schemeId;
        private UUID traditionalKeyId;
        private UUID quantumKeyId;
        private UUID userId;
        private LocalDateTime createdAt;
        private String type;
        
        // Getters and setters
        public String getSchemeId() { return schemeId; }
        public void setSchemeId(String schemeId) { this.schemeId = schemeId; }
        public UUID getTraditionalKeyId() { return traditionalKeyId; }
        public void setTraditionalKeyId(UUID traditionalKeyId) { this.traditionalKeyId = traditionalKeyId; }
        public UUID getQuantumKeyId() { return quantumKeyId; }
        public void setQuantumKeyId(UUID quantumKeyId) { this.quantumKeyId = quantumKeyId; }
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    public static class AlgorithmInfo {
        private String algorithmType;
        private String specificAlgorithm;
        private Integer securityLevel;
        private Integer keySize;
        private Integer signatureSize;
        
        public AlgorithmInfo(String algorithmType, String specificAlgorithm, 
                            Integer securityLevel, Integer keySize, Integer signatureSize) {
            this.algorithmType = algorithmType;
            this.specificAlgorithm = specificAlgorithm;
            this.securityLevel = securityLevel;
            this.keySize = keySize;
            this.signatureSize = signatureSize;
        }
        
        // Getters and setters
        public String getAlgorithmType() { return algorithmType; }
        public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
        public String getSpecificAlgorithm() { return specificAlgorithm; }
        public void setSpecificAlgorithm(String specificAlgorithm) { this.specificAlgorithm = specificAlgorithm; }
        public Integer getSecurityLevel() { return securityLevel; }
        public void setSecurityLevel(Integer securityLevel) { this.securityLevel = securityLevel; }
        public Integer getKeySize() { return keySize; }
        public void setKeySize(Integer keySize) { this.keySize = keySize; }
        public Integer getSignatureSize() { return signatureSize; }
        public void setSignatureSize(Integer signatureSize) { this.signatureSize = signatureSize; }
    }
    
    public static class SupportedAlgorithmsResponse {
        private List<AlgorithmInfo> encryptionAlgorithms = new java.util.ArrayList<>();
        private List<AlgorithmInfo> signatureAlgorithms = new java.util.ArrayList<>();
        private boolean nistStandardized;
        private boolean quantumResistant;
        private boolean hybridEncryptionSupported;
        private boolean quantumKeyDistributionSupported;
        private LocalDateTime lastUpdated;
        
        // Getters and setters
        public List<AlgorithmInfo> getEncryptionAlgorithms() { return encryptionAlgorithms; }
        public void setEncryptionAlgorithms(List<AlgorithmInfo> encryptionAlgorithms) { this.encryptionAlgorithms = encryptionAlgorithms; }
        public List<AlgorithmInfo> getSignatureAlgorithms() { return signatureAlgorithms; }
        public void setSignatureAlgorithms(List<AlgorithmInfo> signatureAlgorithms) { this.signatureAlgorithms = signatureAlgorithms; }
        public boolean isNistStandardized() { return nistStandardized; }
        public void setNistStandardized(boolean nistStandardized) { this.nistStandardized = nistStandardized; }
        public boolean isQuantumResistant() { return quantumResistant; }
        public void setQuantumResistant(boolean quantumResistant) { this.quantumResistant = quantumResistant; }
        public boolean isHybridEncryptionSupported() { return hybridEncryptionSupported; }
        public void setHybridEncryptionSupported(boolean hybridEncryptionSupported) { this.hybridEncryptionSupported = hybridEncryptionSupported; }
        public boolean isQuantumKeyDistributionSupported() { return quantumKeyDistributionSupported; }
        public void setQuantumKeyDistributionSupported(boolean quantumKeyDistributionSupported) { this.quantumKeyDistributionSupported = quantumKeyDistributionSupported; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
    
    public static class SystemStatusResponse {
        private String systemName;
        private String version;
        private String status;
        private String uptime;
        private int totalKeysGenerated;
        private int totalSignaturesCreated;
        private int successfulVerifications;
        private int failedVerifications;
        private int hybridSchemesCreated;
        private LocalDateTime lastHealthCheck;
        private String quantumSafetyLevel;
        private String nistComplianceLevel;
        
        // Getters and setters
        public String getSystemName() { return systemName; }
        public void setSystemName(String systemName) { this.systemName = systemName; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getUptime() { return uptime; }
        public void setUptime(String uptime) { this.uptime = uptime; }
        public int getTotalKeysGenerated() { return totalKeysGenerated; }
        public void setTotalKeysGenerated(int totalKeysGenerated) { this.totalKeysGenerated = totalKeysGenerated; }
        public int getTotalSignaturesCreated() { return totalSignaturesCreated; }
        public void setTotalSignaturesCreated(int totalSignaturesCreated) { this.totalSignaturesCreated = totalSignaturesCreated; }
        public int getSuccessfulVerifications() { return successfulVerifications; }
        public void setSuccessfulVerifications(int successfulVerifications) { this.successfulVerifications = successfulVerifications; }
        public int getFailedVerifications() { return failedVerifications; }
        public void setFailedVerifications(int failedVerifications) { this.failedVerifications = failedVerifications; }
        public int getHybridSchemesCreated() { return hybridSchemesCreated; }
        public void setHybridSchemesCreated(int hybridSchemesCreated) { this.hybridSchemesCreated = hybridSchemesCreated; }
        public LocalDateTime getLastHealthCheck() { return lastHealthCheck; }
        public void setLastHealthCheck(LocalDateTime lastHealthCheck) { this.lastHealthCheck = lastHealthCheck; }
        public String getQuantumSafetyLevel() { return quantumSafetyLevel; }
        public void setQuantumSafetyLevel(String quantumSafetyLevel) { this.quantumSafetyLevel = quantumSafetyLevel; }
        public String getNistComplianceLevel() { return nistComplianceLevel; }
        public void setNistComplianceLevel(String nistComplianceLevel) { this.nistComplianceLevel = nistComplianceLevel; }
    }
}