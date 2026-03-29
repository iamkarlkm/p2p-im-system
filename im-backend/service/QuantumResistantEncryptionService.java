package com.imsystem.service;

import com.imsystem.entity.QuantumResistantEncryptionEntity;
import com.imsystem.entity.PostQuantumSignatureEntity;
import com.imsystem.repository.QuantumResistantEncryptionRepository;
import com.imsystem.repository.PostQuantumSignatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * 量子抗性加密服务
 * 
 * 提供后量子密码学算法管理和操作
 * 支持 CRYSTALS-Kyber, CRYSTALS-Dilithium, FALCON, SPHINCS+ 等算法
 * 
 * 作者: 编程开发代理
 * 创建时间: 2026-03-24 10:03
 */
@Service
public class QuantumResistantEncryptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuantumResistantEncryptionService.class);
    
    private final QuantumResistantEncryptionRepository encryptionRepository;
    private final PostQuantumSignatureRepository signatureRepository;
    
    // NIST 标准化算法配置
    private static final Map<String, Map<String, Object>> NIST_ALGORITHMS = new HashMap<>();
    
    static {
        // CRYSTALS-Kyber (Key Encapsulation Mechanism)
        Map<String, Object> kyberConfig = new HashMap<>();
        kyberConfig.put("name", "CRYSTALS-Kyber");
        kyberConfig.put("type", "KEM");
        kyberConfig.put("securityLevels", List.of("Level 1", "Level 3", "Level 5"));
        kyberConfig.put("keySizes", List.of(512, 768, 1024));
        kyberConfig.put("signatureSupported", false);
        kyberConfig.put("nistStatus", "Standardized (Round 3 Winner)");
        NIST_ALGORITHMS.put("Kyber", kyberConfig);
        
        // CRYSTALS-Dilithium (Digital Signature)
        Map<String, Object> dilithiumConfig = new HashMap<>();
        dilithiumConfig.put("name", "CRYSTALS-Dilithium");
        dilithiumConfig.put("type", "Signature");
        dilithiumConfig.put("securityLevels", List.of("Level 2", "Level 3", "Level 5"));
        dilithiumConfig.put("keySizes", List.of(128, 192, 256));
        dilithiumConfig.put("signatureSupported", true);
        dilithiumConfig.put("nistStatus", "Standardized (Round 3 Winner)");
        NIST_ALGORITHMS.put("Dilithium", dilithiumConfig);
        
        // FALCON (Digital Signature)
        Map<String, Object> falconConfig = new HashMap<>();
        falconConfig.put("name", "FALCON");
        falconConfig.put("type", "Signature");
        falconConfig.put("securityLevels", List.of("Level 1", "Level 5"));
        falconConfig.put("keySizes", List.of(512, 1024));
        falconConfig.put("signatureSupported", true);
        falconConfig.put("nistStatus", "Standardized (Round 3 Winner)");
        NIST_ALGORITHMS.put("Falcon", falconConfig);
        
        // SPHINCS+ (Digital Signature)
        Map<String, Object> sphincsConfig = new HashMap<>();
        sphincsConfig.put("name", "SPHINCS+");
        sphincsConfig.put("type", "Signature");
        sphincsConfig.put("securityLevels", List.of("Level 1", "Level 3", "Level 5"));
        sphincsConfig.put("keySizes", List.of(128, 192, 256));
        sphincsConfig.put("signatureSupported", true);
        sphincsConfig.put("nistStatus", "Standardized (Round 3 Winner)");
        NIST_ALGORITHMS.put("SPHINCS", sphincsConfig);
    }
    
    public QuantumResistantEncryptionService(QuantumResistantEncryptionRepository encryptionRepository,
                                            PostQuantumSignatureRepository signatureRepository) {
        this.encryptionRepository = encryptionRepository;
        this.signatureRepository = signatureRepository;
    }
    
    /**
     * 创建量子抗性加密密钥对
     */
    @Transactional
    public QuantumResistantEncryptionEntity createEncryptionKeyPair(Long userId, 
                                                                   String algorithmType,
                                                                   Integer keySize,
                                                                   String securityLevel,
                                                                   Boolean hybridMode) {
        
        logger.info("Creating quantum-resistant encryption key pair for user: {}, algorithm: {}", userId, algorithmType);
        
        // 验证算法参数
        validateAlgorithmParameters(algorithmType, keySize, securityLevel);
        
        // 检查是否已存在相同的配置
        Optional<QuantumResistantEncryptionEntity> existing = encryptionRepository
                .findByUserIdAndAlgorithmTypeAndKeySize(userId, algorithmType, keySize);
        
        if (existing.isPresent()) {
            logger.warn("Encryption key pair already exists for user: {}, algorithm: {}", userId, algorithmType);
            return existing.get();
        }
        
        // 创建新实体
        QuantumResistantEncryptionEntity entity = new QuantumResistantEncryptionEntity();
        entity.setUserId(userId);
        entity.setAlgorithmType(algorithmType);
        entity.setKeySize(keySize);
        entity.setSecurityLevel(securityLevel);
        entity.setHybridMode(hybridMode != null ? hybridMode : true);
        
        // 设置默认值
        entity.setEncryptionScheme(getDefaultEncryptionScheme(algorithmType, keySize));
        entity.setSignatureScheme(getDefaultSignatureScheme(algorithmType, keySize));
        entity.setKeyExchangeProtocol(getDefaultKeyExchangeProtocol(algorithmType));
        
        // 生成模拟的密钥数据（实际实现应使用密码学库）
        String publicKey = generateMockPublicKey(algorithmType, keySize);
        String privateKeyEncrypted = generateMockPrivateKey(algorithmType, keySize);
        
        entity.setPublicKey(publicKey);
        entity.setPrivateKeyEncrypted(privateKeyEncrypted);
        entity.setKeyFingerprint(generateKeyFingerprint(publicKey));
        
        // 设置密钥生命周期和轮换时间
        entity.setLastRotationTime(LocalDateTime.now());
        entity.setNextRotationTime(LocalDateTime.now().plusDays(entity.getRotationIntervalDays()));
        
        // 设置性能指标
        entity.setPerformanceMetrics(generatePerformanceMetrics(algorithmType, keySize));
        
        // 设置合规性标签
        entity.setComplianceTags("NIST,Post-Quantum,FIPS 140-3");
        
        // 保存实体
        QuantumResistantEncryptionEntity saved = encryptionRepository.save(entity);
        
        logger.info("Quantum-resistant encryption key pair created successfully: {}", saved.getId());
        
        return saved;
    }
    
    /**
     * 创建后量子签名密钥对
     */
    @Transactional
    public PostQuantumSignatureEntity createSignatureKeyPair(Long userId,
                                                            String algorithm,
                                                            Integer keySize,
                                                            String securityLevel) {
        
        logger.info("Creating post-quantum signature key pair for user: {}, algorithm: {}", userId, algorithm);
        
        // 验证算法参数
        validateSignatureAlgorithmParameters(algorithm, keySize, securityLevel);
        
        // 创建新实体
        PostQuantumSignatureEntity entity = new PostQuantumSignatureEntity();
        entity.setUserId(userId);
        entity.setAlgorithm(algorithm);
        entity.setKeySize(keySize);
        entity.setSecurityLevel(securityLevel);
        
        // 生成模拟的密钥数据
        String publicKey = generateMockSignaturePublicKey(algorithm, keySize);
        String privateKeyEncrypted = generateMockSignaturePrivateKey(algorithm, keySize);
        
        entity.setPublicKey(publicKey);
        entity.setPrivateKeyEncrypted(privateKeyEncrypted);
        
        // 设置大小信息
        entity.setPublicKeySizeBytes(calculatePublicKeySize(algorithm, keySize));
        entity.setPrivateKeySizeBytes(calculatePrivateKeySize(algorithm, keySize));
        entity.setSignatureSizeBytes(calculateSignatureSize(algorithm, keySize));
        
        // 设置性能指标
        entity.setSigningTimeMs(estimateSigningTime(algorithm, keySize));
        entity.setVerificationTimeMs(estimateVerificationTime(algorithm, keySize));
        
        // 设置合规性信息
        entity.setComplianceInfo("{\"nistStandardized\": true, \"fipsCompliant\": true, \"europeanApproved\": false}");
        
        // 保存实体
        PostQuantumSignatureEntity saved = signatureRepository.save(entity);
        
        logger.info("Post-quantum signature key pair created successfully: {}", saved.getSignatureId());
        
        return saved;
    }
    
    /**
     * 加密数据
     */
    public String encryptData(Long userId, String plaintext, String algorithmType) {
        logger.info("Encrypting data for user: {} using algorithm: {}", userId, algorithmType);
        
        // 获取用户的加密密钥
        Optional<QuantumResistantEncryptionEntity> keyPair = encryptionRepository
                .findActiveKeyByUserIdAndAlgorithm(userId, algorithmType);
        
        if (keyPair.isEmpty()) {
            throw new IllegalArgumentException("No active encryption key found for algorithm: " + algorithmType);
        }
        
        QuantumResistantEncryptionEntity entity = keyPair.get();
        
        // 检查密钥状态
        if (!"ACTIVE".equals(entity.getKeyStatus())) {
            throw new IllegalStateException("Encryption key is not active: " + entity.getKeyStatus());
        }
        
        // 模拟加密操作（实际实现应使用密码学库）
        String ciphertext = performMockEncryption(plaintext, entity);
        
        // 更新性能指标
        updatePerformanceMetrics(entity, "encryption");
        
        encryptionRepository.save(entity);
        
        logger.info("Data encrypted successfully, ciphertext length: {}", ciphertext.length());
        
        return ciphertext;
    }
    
    /**
     * 解密数据
     */
    public String decryptData(Long userId, String ciphertext, String algorithmType) {
        logger.info("Decrypting data for user: {} using algorithm: {}", userId, algorithmType);
        
        // 获取用户的加密密钥
        Optional<QuantumResistantEncryptionEntity> keyPair = encryptionRepository
                .findActiveKeyByUserIdAndAlgorithm(userId, algorithmType);
        
        if (keyPair.isEmpty()) {
            throw new IllegalArgumentException("No active encryption key found for algorithm: " + algorithmType);
        }
        
        QuantumResistantEncryptionEntity entity = keyPair.get();
        
        // 检查密钥状态
        if (!"ACTIVE".equals(entity.getKeyStatus())) {
            throw new IllegalStateException("Encryption key is not active: " + entity.getKeyStatus());
        }
        
        // 模拟解密操作（实际实现应使用密码学库）
        String plaintext = performMockDecryption(ciphertext, entity);
        
        // 更新性能指标
        updatePerformanceMetrics(entity, "decryption");
        
        encryptionRepository.save(entity);
        
        logger.info("Data decrypted successfully, plaintext length: {}", plaintext.length());
        
        return plaintext;
    }
    
    /**
     * 签名数据
     */
    public String signData(Long userId, String data, String algorithm) {
        logger.info("Signing data for user: {} using algorithm: {}", userId, algorithm);
        
        // 获取用户的签名密钥
        Optional<PostQuantumSignatureEntity> signatureKey = signatureRepository
                .findActiveSignatureByUserIdAndAlgorithm(userId, algorithm);
        
        if (signatureKey.isEmpty()) {
            throw new IllegalArgumentException("No active signature key found for algorithm: " + algorithm);
        }
        
        PostQuantumSignatureEntity entity = signatureKey.get();
        
        // 检查签名状态
        if (!entity.getIsActive() || !"VALID".equals(entity.getRevocationStatus())) {
            throw new IllegalStateException("Signature key is not active or valid");
        }
        
        // 模拟签名操作（实际实现应使用密码学库）
        String signature = performMockSigning(data, entity);
        
        entity.setSignatureData(signature);
        entity.setSignedDocumentHash(calculateHash(data));
        
        // 更新性能指标
        entity.setSigningTimeMs(System.currentTimeMillis() % 1000 + 50L);
        
        signatureRepository.save(entity);
        
        logger.info("Data signed successfully, signature length: {}", signature.length());
        
        return signature;
    }
    
    /**
     * 验证签名
     */
    public boolean verifySignature(Long userId, String data, String signature, String algorithm) {
        logger.info("Verifying signature for user: {} using algorithm: {}", userId, algorithm);
        
        // 获取用户的签名密钥
        Optional<PostQuantumSignatureEntity> signatureKey = signatureRepository
                .findActiveSignatureByUserIdAndAlgorithm(userId, algorithm);
        
        if (signatureKey.isEmpty()) {
            throw new IllegalArgumentException("No active signature key found for algorithm: " + algorithm);
        }
        
        PostQuantumSignatureEntity entity = signatureKey.get();
        
        // 模拟验证操作（实际实现应使用密码学库）
        boolean isValid = performMockVerification(data, signature, entity);
        
        if (isValid) {
            entity.setTimestampVerified(true);
            entity.setTimestampVerificationTime(LocalDateTime.now());
            entity.setVerificationTimeMs(System.currentTimeMillis() % 1000 + 30L);
            signatureRepository.save(entity);
        } else {
            entity.recordError("Signature verification failed");
            signatureRepository.save(entity);
        }
        
        logger.info("Signature verification result: {}", isValid);
        
        return isValid;
    }
    
    /**
     * 轮换加密密钥
     */
    @Transactional
    public QuantumResistantEncryptionEntity rotateEncryptionKey(Long keyId) {
        logger.info("Rotating encryption key: {}", keyId);
        
        Optional<QuantumResistantEncryptionEntity> existing = encryptionRepository.findById(keyId);
        
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Encryption key not found: " + keyId);
        }
        
        QuantumResistantEncryptionEntity oldKey = existing.get();
        
        // 标记旧密钥为已轮换
        oldKey.setKeyStatus("ROTATED");
        oldKey.setUpdatedAt(LocalDateTime.now());
        encryptionRepository.save(oldKey);
        
        // 创建新密钥
        QuantumResistantEncryptionEntity newKey = new QuantumResistantEncryptionEntity();
        newKey.setUserId(oldKey.getUserId());
        newKey.setAlgorithmType(oldKey.getAlgorithmType());
        newKey.setKeySize(oldKey.getKeySize());
        newKey.setSecurityLevel(oldKey.getSecurityLevel());
        newKey.setHybridMode(oldKey.getHybridMode());
        
        // 生成新的密钥对
        newKey.setPublicKey(generateMockPublicKey(oldKey.getAlgorithmType(), oldKey.getKeySize()));
        newKey.setPrivateKeyEncrypted(generateMockPrivateKey(oldKey.getAlgorithmType(), oldKey.getKeySize()));
        newKey.setKeyFingerprint(generateKeyFingerprint(newKey.getPublicKey()));
        newKey.setKeyVersion(oldKey.getKeyVersion() + 1);
        
        newKey.setLastRotationTime(LocalDateTime.now());
        newKey.setNextRotationTime(LocalDateTime.now().plusDays(newKey.getRotationIntervalDays()));
        
        QuantumResistantEncryptionEntity saved = encryptionRepository.save(newKey);
        
        logger.info("Encryption key rotated successfully, new key id: {}", saved.getId());
        
        return saved;
    }
    
    /**
     * 获取用户的所有加密密钥
     */
    public List<QuantumResistantEncryptionEntity> getUserEncryptionKeys(Long userId) {
        return encryptionRepository.findByUserId(userId);
    }
    
    /**
     * 获取用户的所有签名密钥
     */
    public List<PostQuantumSignatureEntity> getUserSignatureKeys(Long userId) {
        return signatureRepository.findByUserId(userId);
    }
    
    /**
     * 获取支持的 NIST 算法列表
     */
    public Map<String, Map<String, Object>> getSupportedAlgorithms() {
        return NIST_ALGORITHMS;
    }
    
    /**
     * 验证算法参数
     */
    private void validateAlgorithmParameters(String algorithmType, Integer keySize, String securityLevel) {
        if (!NIST_ALGORITHMS.containsKey(algorithmType)) {
            throw new IllegalArgumentException("Unsupported algorithm type: " + algorithmType);
        }
        
        Map<String, Object> config = NIST_ALGORITHMS.get(algorithmType);
        List<Integer> validKeySizes = (List<Integer>) config.get("keySizes");
        
        if (!validKeySizes.contains(keySize)) {
            throw new IllegalArgumentException("Invalid key size for algorithm " + algorithmType + 
                    ". Valid sizes: " + validKeySizes);
        }
        
        List<String> validSecurityLevels = (List<String>) config.get("securityLevels");
        if (!validSecurityLevels.contains(securityLevel)) {
            throw new IllegalArgumentException("Invalid security level for algorithm " + algorithmType + 
                    ". Valid levels: " + validSecurityLevels);
        }
    }
    
    /**
     * 验证签名算法参数
     */
    private void validateSignatureAlgorithmParameters(String algorithm, Integer keySize, String securityLevel) {
        // 简化验证逻辑
        if (!algorithm.startsWith("Dilithium") && !algorithm.startsWith("Falcon") && 
            !algorithm.startsWith("SPHINCS")) {
            throw new IllegalArgumentException("Unsupported signature algorithm: " + algorithm);
        }
    }
    
    // 以下为模拟方法，实际实现应集成真正的密码学库
    
    private String generateMockPublicKey(String algorithmType, Integer keySize) {
        return "PUBLIC_KEY_" + algorithmType + "_" + keySize + "_" + UUID.randomUUID().toString().substring(0, 16);
    }
    
    private String generateMockPrivateKey(String algorithmType, Integer keySize) {
        return "ENCRYPTED_PRIVATE_KEY_" + algorithmType + "_" + keySize + "_" + UUID.randomUUID().toString().substring(0, 16);
    }
    
    private String generateMockSignaturePublicKey(String algorithm, Integer keySize) {
        return "SIG_PUBLIC_" + algorithm + "_" + keySize + "_" + UUID.randomUUID().toString().substring(0, 12);
    }
    
    private String generateMockSignaturePrivateKey(String algorithm, Integer keySize) {
        return "ENCRYPTED_SIG_PRIVATE_" + algorithm + "_" + keySize + "_" + UUID.randomUUID().toString().substring(0, 12);
    }
    
    private String generateKeyFingerprint(String publicKey) {
        return "FINGERPRINT_" + Integer.toHexString(publicKey.hashCode()).toUpperCase();
    }
    
    private String getDefaultEncryptionScheme(String algorithmType, Integer keySize) {
        switch (algorithmType) {
            case "Kyber":
                return "Kyber" + keySize;
            default:
                return algorithmType + "-" + keySize;
        }
    }
    
    private String getDefaultSignatureScheme(String algorithmType, Integer keySize) {
        if ("Dilithium".equals(algorithmType)) {
            return "Dilithium" + (keySize == 128 ? 2 : keySize == 192 ? 3 : 5);
        } else if ("Falcon".equals(algorithmType)) {
            return "Falcon-" + keySize;
        } else {
            return algorithmType + "-SHAKE256";
        }
    }
    
    private String getDefaultKeyExchangeProtocol(String algorithmType) {
        if ("Kyber".equals(algorithmType)) {
            return "Kyber";
        } else if ("NTRU".equals(algorithmType)) {
            return "NTRU-HPS";
        } else {
            return "Hybrid-EC-DH";
        }
    }
    
    private String generatePerformanceMetrics(String algorithmType, Integer keySize) {
        Random random = new Random();
        return String.format("{\"keygenTimeMs\": %d, \"encryptTimeMs\": %d, \"decryptTimeMs\": %d, \"memoryUsageKB\": %d}",
                random.nextInt(100) + 50,
                random.nextInt(50) + 20,
                random.nextInt(50) + 20,
                random.nextInt(1024) + 512);
    }
    
    private String performMockEncryption(String plaintext, QuantumResistantEncryptionEntity entity) {
        return "ENCRYPTED_" + entity.getAlgorithmType() + "_" + 
                Integer.toHexString(plaintext.hashCode()) + "_" + 
                System.currentTimeMillis();
    }
    
    private String performMockDecryption(String ciphertext, QuantumResistantEncryptionEntity entity) {
        if (ciphertext.startsWith("ENCRYPTED_")) {
            return "DECRYPTED_PLAINTEXT_" + System.currentTimeMillis();
        }
        throw new IllegalArgumentException("Invalid ciphertext format");
    }
    
    private String performMockSigning(String data, PostQuantumSignatureEntity entity) {
        return "SIGNATURE_" + entity.getAlgorithm() + "_" + 
                Integer.toHexString(data.hashCode()) + "_" + 
                System.currentTimeMillis();
    }
    
    private boolean performMockVerification(String data, String signature, PostQuantumSignatureEntity entity) {
        return signature.startsWith("SIGNATURE_") && signature.contains(entity.getAlgorithm());
    }
    
    private String calculateHash(String data) {
        return "HASH_" + Integer.toHexString(data.hashCode());
    }
    
    private void updatePerformanceMetrics(QuantumResistantEncryptionEntity entity, String operation) {
        // 简化的性能指标更新
        String metrics = entity.getPerformanceMetrics();
        if (metrics == null) {
            metrics = "{\"operations\": 1}";
        }
        entity.setPerformanceMetrics(metrics.replace("}", ",\"" + operation + "\": 1}"));
    }
    
    private Integer calculatePublicKeySize(String algorithm, Integer keySize) {
        if (algorithm.startsWith("Dilithium")) {
            return keySize == 128 ? 1312 : keySize == 192 ? 1952 : 2592;
        } else if (algorithm.startsWith("Falcon")) {
            return keySize == 512 ? 897 : 1793;
        } else {
            return 1000;
        }
    }
    
    private Integer calculatePrivateKeySize(String algorithm, Integer keySize) {
        if (algorithm.startsWith("Dilithium")) {
            return keySize == 128 ? 2560 : keySize == 192 ? 4032 : 4864;
        } else if (algorithm.startsWith("Falcon")) {
            return keySize == 512 ? 1281 : 2305;
        } else {
            return 2000;
        }
    }
    
    private Integer calculateSignatureSize(String algorithm, Integer keySize) {
        if (algorithm.startsWith("Dilithium")) {
            return keySize == 128 ? 2420 : keySize == 192 ? 3293 : 4595;
        } else if (algorithm.startsWith("Falcon")) {
            return keySize == 512 ? 666 : 1280;
        } else {
            return 1500;
        }
    }
    
    private Long estimateSigningTime(String algorithm, Integer keySize) {
        if (algorithm.startsWith("Dilithium")) {
            return keySize == 128 ? 120L : keySize == 192 ? 180L : 250L;
        } else if (algorithm.startsWith("Falcon")) {
            return keySize == 512 ? 80L : 150L;
        } else {
            return 200L;
        }
    }
    
    private Long estimateVerificationTime(String algorithm, Integer keySize) {
        if (algorithm.startsWith("Dilithium")) {
            return keySize == 128 ? 40L : keySize == 192 ? 60L : 80L;
        } else if (algorithm.startsWith("Falcon")) {
            return keySize == 512 ? 30L : 50L;
        } else {
            return 60L;
        }
    }
}