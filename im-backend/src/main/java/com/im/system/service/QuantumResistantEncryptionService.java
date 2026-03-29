package com.im.system.service;

import com.im.system.entity.QuantumResistantEncryptionEntity;
import com.im.system.entity.PostQuantumSignatureEntity;
import com.im.system.repository.QuantumResistantEncryptionRepository;
import com.im.system.repository.PostQuantumSignatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

/**
 * 量子抗性加密服务
 * 
 * 提供后量子密码学功能：
 * 1. 量子抗性加密密钥管理
 * 2. 后量子签名生成与验证
 * 3. 混合加密方案
 * 4. 量子密钥分发集成
 * 5. NIST标准化算法支持
 */
@Service
@Transactional
public class QuantumResistantEncryptionService {
    
    private final QuantumResistantEncryptionRepository encryptionRepository;
    private final PostQuantumSignatureRepository signatureRepository;
    
    public QuantumResistantEncryptionService(
            QuantumResistantEncryptionRepository encryptionRepository,
            PostQuantumSignatureRepository signatureRepository) {
        this.encryptionRepository = encryptionRepository;
        this.signatureRepository = signatureRepository;
    }
    
    // ========== 量子抗性加密管理 ==========
    
    /**
     * 创建量子抗性加密密钥对
     */
    public QuantumResistantEncryptionEntity createEncryptionKeyPair(
            String name, String algorithmType, String specificAlgorithm,
            Integer securityLevel, Integer keySize, UUID userId, String description) {
        
        QuantumResistantEncryptionEntity entity = new QuantumResistantEncryptionEntity(
            name, algorithmType, specificAlgorithm, securityLevel, keySize, userId
        );
        
        entity.setDescription(description);
        entity.setUpdatedAt(LocalDateTime.now());
        
        // 模拟生成密钥对（实际实现中应调用具体的后量子密码库）
        String publicKey = generateSimulatedPublicKey(algorithmType, keySize);
        String privateKey = generateSimulatedPrivateKey(algorithmType, keySize);
        
        entity.setPublicKey(publicKey);
        entity.setPrivateKey(privateKey);
        
        // 根据算法类型设置性能和安全评分
        entity.setPerformanceScore(calculatePerformanceScore(algorithmType, keySize));
        entity.setSecurityScore(calculateSecurityScore(algorithmType, securityLevel));
        
        // 设置元数据
        entity.setMetadata(buildMetadata(algorithmType, specificAlgorithm, keySize, securityLevel));
        
        return encryptionRepository.save(entity);
    }
    
    /**
     * 获取用户的所有量子抗性加密密钥
     */
    public List<QuantumResistantEncryptionEntity> getUserEncryptionKeys(UUID userId) {
        return encryptionRepository.findByUserId(userId);
    }
    
    /**
     * 获取活跃的量子抗性加密密钥
     */
    public List<QuantumResistantEncryptionEntity> getActiveEncryptionKeys(UUID userId) {
        return encryptionRepository.findByUserIdAndIsActive(userId, true);
    }
    
    /**
     * 根据ID获取加密密钥
     */
    public Optional<QuantumResistantEncryptionEntity> getEncryptionKeyById(UUID id) {
        return encryptionRepository.findById(id);
    }
    
    /**
     * 更新加密密钥
     */
    public QuantumResistantEncryptionEntity updateEncryptionKey(
            UUID id, String name, String description, Boolean isActive, LocalDateTime expiresAt) {
        
        return encryptionRepository.findById(id).map(entity -> {
            if (name != null) entity.setName(name);
            if (description != null) entity.setDescription(description);
            if (isActive != null) entity.setIsActive(isActive);
            if (expiresAt != null) entity.setExpiresAt(expiresAt);
            entity.setUpdatedAt(LocalDateTime.now());
            return encryptionRepository.save(entity);
        }).orElseThrow(() -> new RuntimeException("Encryption key not found: " + id));
    }
    
    /**
     * 删除加密密钥
     */
    public void deleteEncryptionKey(UUID id) {
        encryptionRepository.deleteById(id);
    }
    
    /**
     * 轮换加密密钥（创建新密钥并停用旧密钥）
     */
    public QuantumResistantEncryptionEntity rotateEncryptionKey(
            UUID oldKeyId, String newKeyName, UUID userId) {
        
        QuantumResistantEncryptionEntity oldKey = encryptionRepository.findById(oldKeyId)
            .orElseThrow(() -> new RuntimeException("Old encryption key not found"));
        
        // 停用旧密钥
        oldKey.setIsActive(false);
        oldKey.setUpdatedAt(LocalDateTime.now());
        encryptionRepository.save(oldKey);
        
        // 创建新密钥（使用相同算法配置）
        return createEncryptionKeyPair(
            newKeyName,
            oldKey.getAlgorithmType(),
            oldKey.getSpecificAlgorithm(),
            oldKey.getSecurityLevel(),
            oldKey.getKeySize(),
            userId,
            "Rotated from key: " + oldKey.getName()
        );
    }
    
    // ========== 后量子签名管理 ==========
    
    /**
     * 创建后量子签名
     */
    public PostQuantumSignatureEntity createPostQuantumSignature(
            String signatureName, String signatureAlgorithm, String specificVariant,
            UUID messageId, UUID userId, String signaturePurpose) {
        
        PostQuantumSignatureEntity entity = new PostQuantumSignatureEntity(
            signatureName, signatureAlgorithm, specificVariant, messageId, userId
        );
        
        entity.setSignaturePurpose(signaturePurpose);
        
        // 模拟生成签名数据（实际实现中应调用具体的后量子签名库）
        String signatureData = generateSimulatedSignature(signatureAlgorithm, messageId.toString());
        String publicKeyData = generateSimulatedPublicKeyForSignature(signatureAlgorithm);
        
        entity.setSignatureData(signatureData);
        entity.setPublicKeyData(publicKeyData);
        entity.setSignatureSize(calculateSignatureSize(signatureAlgorithm));
        entity.setPublicKeySize(calculatePublicKeySize(signatureAlgorithm));
        entity.setPrivateKeySize(calculatePrivateKeySize(signatureAlgorithm));
        
        // 设置默认值
        entity.setSecurityLevel(determineSecurityLevel(signatureAlgorithm));
        entity.setSupportsBatchVerification(supportsBatchVerification(signatureAlgorithm));
        entity.setSignatureGenerationTimeMs(measureSignatureGenerationTime(signatureAlgorithm));
        
        return signatureRepository.save(entity);
    }
    
    /**
     * 验证后量子签名
     */
    public boolean verifyPostQuantumSignature(UUID signatureId) {
        return signatureRepository.findById(signatureId).map(signature -> {
            // 模拟验证过程（实际实现中应调用具体的验证库）
            boolean isValid = simulateSignatureVerification(
                signature.getSignatureAlgorithm(),
                signature.getSignatureData(),
                signature.getPublicKeyData(),
                signature.getMessageId().toString()
            );
            
            if (isValid) {
                signature.setVerificationStatus("VERIFIED");
                signature.setVerificationTimeMs(measureVerificationTime(signature.getSignatureAlgorithm()));
                signature.setVerifiedAt(LocalDateTime.now());
            } else {
                signature.setVerificationStatus("INVALID");
                signature.setVerificationNotes("Signature verification failed");
            }
            
            signatureRepository.save(signature);
            return isValid;
        }).orElse(false);
    }
    
    /**
     * 获取消息的所有签名
     */
    public List<PostQuantumSignatureEntity> getSignaturesForMessage(UUID messageId) {
        return signatureRepository.findByMessageId(messageId);
    }
    
    /**
     * 获取用户的签名历史
     */
    public List<PostQuantumSignatureEntity> getUserSignatures(UUID userId) {
        return signatureRepository.findByUserId(userId);
    }
    
    /**
     * 批量验证签名
     */
    public int batchVerifySignatures(List<UUID> signatureIds) {
        int verifiedCount = 0;
        
        for (UUID signatureId : signatureIds) {
            if (verifyPostQuantumSignature(signatureId)) {
                verifiedCount++;
            }
        }
        
        return verifiedCount;
    }
    
    /**
     * 撤销签名
     */
    public void revokeSignature(UUID signatureId, String reason) {
        signatureRepository.findById(signatureId).ifPresent(signature -> {
            signature.revokeSignature(reason);
            signatureRepository.save(signature);
        });
    }
    
    // ========== 混合加密方案 ==========
    
    /**
     * 创建混合加密方案（传统+后量子）
     */
    public String createHybridEncryptionScheme(
            UUID traditionalKeyId, UUID quantumKeyId, UUID userId) {
        
        // 获取传统加密密钥
        // 获取量子抗性加密密钥
        // 创建混合方案
        
        String schemeId = UUID.randomUUID().toString();
        
        // 模拟混合加密方案创建
        String metadata = String.format(
            "{\"schemeId\":\"%s\",\"traditionalKeyId\":\"%s\",\"quantumKeyId\":\"%s\"," +
            "\"userId\":\"%s\",\"createdAt\":\"%s\",\"type\":\"HYBRID_RSA_KYBER\"}",
            schemeId, traditionalKeyId, quantumKeyId, userId, LocalDateTime.now()
        );
        
        return schemeId;
    }
    
    // ========== 辅助方法（模拟实现） ==========
    
    private String generateSimulatedPublicKey(String algorithmType, Integer keySize) {
        return String.format("PUBLIC_KEY_%s_%d_%s", algorithmType, keySize, UUID.randomUUID());
    }
    
    private String generateSimulatedPrivateKey(String algorithmType, Integer keySize) {
        return String.format("PRIVATE_KEY_%s_%d_%s", algorithmType, keySize, UUID.randomUUID());
    }
    
    private String generateSimulatedSignature(String algorithm, String messageHash) {
        return String.format("SIGNATURE_%s_%s_%s", algorithm, messageHash.substring(0, 16), UUID.randomUUID());
    }
    
    private String generateSimulatedPublicKeyForSignature(String algorithm) {
        return String.format("SIGNATURE_PUBKEY_%s_%s", algorithm, UUID.randomUUID());
    }
    
    private Double calculatePerformanceScore(String algorithmType, Integer keySize) {
        // 简单性能评分逻辑
        double baseScore = 100.0;
        
        // 根据算法类型调整
        switch (algorithmType) {
            case "LATTICE":
                baseScore -= keySize / 100.0;
                break;
            case "HASH_BASED":
                baseScore -= keySize / 50.0;
                break;
            case "CODE_BASED":
                baseScore -= keySize / 25.0;
                break;
            case "MULTIVARIATE":
                baseScore -= keySize / 75.0;
                break;
            case "SIKE":
                baseScore -= keySize / 60.0;
                break;
            default:
                baseScore = 80.0;
        }
        
        return Math.max(10.0, Math.min(100.0, baseScore));
    }
    
    private Double calculateSecurityScore(String algorithmType, Integer securityLevel) {
        double baseScore = securityLevel * 20.0; // 1-5级对应20-100分
        
        // 根据算法类型调整
        switch (algorithmType) {
            case "LATTICE":
                baseScore += 10.0; // 格密码安全性较高
                break;
            case "HASH_BASED":
                baseScore += 5.0; // 基于哈希的签名安全性好
                break;
            case "CODE_BASED":
                baseScore += 8.0; // 基于编码的加密安全性好
                break;
            default:
                baseScore += 0.0;
        }
        
        return Math.max(20.0, Math.min(100.0, baseScore));
    }
    
    private String buildMetadata(String algorithmType, String specificAlgorithm, 
                                Integer keySize, Integer securityLevel) {
        return String.format(
            "{\"algorithmType\":\"%s\",\"specificAlgorithm\":\"%s\"," +
            "\"keySize\":%d,\"securityLevel\":%d,\"nistStandardized\":%b," +
            "\"quantumResistant\":true,\"timestamp\":\"%s\"}",
            algorithmType, specificAlgorithm, keySize, securityLevel,
            isNISTStandardized(specificAlgorithm), LocalDateTime.now()
        );
    }
    
    private boolean isNISTStandardized(String specificAlgorithm) {
        return specificAlgorithm != null && 
               (specificAlgorithm.contains("Kyber") || 
                specificAlgorithm.contains("Dilithium") ||
                specificAlgorithm.contains("FALCON") ||
                specificAlgorithm.contains("SPHINCS+"));
    }
    
    private Integer calculateSignatureSize(String algorithm) {
        // 各种算法的典型签名大小（字节）
        switch (algorithm) {
            case "DILITHIUM": return 2420; // Dilithium2
            case "FALCON": return 1280;    // Falcon-512
            case "SPHINCS_PLUS": return 17088; // SPHINCS+-SHA256-128f-simple
            case "RAINBOW": return 156;    // Rainbow-Ia-Classic
            case "ED448": return 114;      // Ed448
            default: return 1024;
        }
    }
    
    private Integer calculatePublicKeySize(String algorithm) {
        // 各种算法的典型公钥大小（字节）
        switch (algorithm) {
            case "DILITHIUM": return 1312; // Dilithium2
            case "FALCON": return 897;     // Falcon-512
            case "SPHINCS_PLUS": return 32; // SPHINCS+公钥较小
            case "RAINBOW": return 161600; // Rainbow公钥较大
            case "ED448": return 57;       // Ed448
            default: return 512;
        }
    }
    
    private Integer calculatePrivateKeySize(String algorithm) {
        // 各种算法的典型私钥大小（字节）
        switch (algorithm) {
            case "DILITHIUM": return 2560; // Dilithium2
            case "FALCON": return 1281;    // Falcon-512
            case "SPHINCS_PLUS": return 64; // SPHINCS+私钥
            case "RAINBOW": return 103648; // Rainbow私钥
            case "ED448": return 57;       // Ed448
            default: return 1024;
        }
    }
    
    private Integer determineSecurityLevel(String algorithm) {
        // 根据算法确定安全级别
        switch (algorithm) {
            case "DILITHIUM": return 1; // NIST Level 1
            case "FALCON": return 2;    // NIST Level 2
            case "SPHINCS_PLUS": return 1; // NIST Level 1
            case "RAINBOW": return 3;   // 中等安全性
            case "ED448": return 4;     // 高安全性（传统）
            default: return 3;
        }
    }
    
    private boolean supportsBatchVerification(String algorithm) {
        return "DILITHIUM".equals(algorithm) || "FALCON".equals(algorithm);
    }
    
    private Double measureSignatureGenerationTime(String algorithm) {
        // 模拟签名生成时间（毫秒）
        switch (algorithm) {
            case "DILITHIUM": return 2.5;
            case "FALCON": return 1.8;
            case "SPHINCS_PLUS": return 15.2;
            case "RAINBOW": return 8.7;
            case "ED448": return 0.8;
            default: return 5.0;
        }
    }
    
    private Double measureVerificationTime(String algorithm) {
        // 模拟验证时间（毫秒）
        switch (algorithm) {
            case "DILITHIUM": return 0.8;
            case "FALCON": return 0.6;
            case "SPHINCS_PLUS": return 3.2;
            case "RAINBOW": return 2.1;
            case "ED448": return 0.3;
            default: return 1.5;
        }
    }
    
    private boolean simulateSignatureVerification(String algorithm, String signature, 
                                                 String publicKey, String messageHash) {
        // 模拟签名验证（实际应调用具体库）
        return signature != null && publicKey != null && 
               signature.startsWith("SIGNATURE_") && publicKey.startsWith("SIGNATURE_PUBKEY_");
    }
}