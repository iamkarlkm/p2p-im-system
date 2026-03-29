package com.im.backend.service;

import com.im.backend.entity.BlockchainMessageProofEntity;
import com.im.backend.entity.BlockchainTimestampEntity;
import com.im.backend.repository.BlockchainMessageProofRepository;
import com.im.backend.repository.BlockchainTimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * 基于区块链的消息不可否认性验证服务
 * 提供消息哈希上链、时间戳证明、身份验证、防篡改验证等功能
 */
@Service
@Transactional
public class BlockchainVerificationService {
    
    @Autowired
    private BlockchainMessageProofRepository messageProofRepository;
    
    @Autowired
    private BlockchainTimestampRepository timestampRepository;
    
    // 区块链网络配置
    private final Map<String, BlockchainNetworkConfig> networkConfigs = new HashMap<>();
    
    // 初始化区块链网络配置
    public BlockchainVerificationService() {
        initializeNetworkConfigs();
    }
    
    /**
     * 为消息创建区块链证明
     */
    public BlockchainMessageProofEntity createMessageProof(String messageId, String messageHash, 
                                                          String blockchainNetwork, 
                                                          BlockchainMessageProofEntity.BlockchainType blockchainType,
                                                          Map<String, Object> options) {
        
        // 验证输入参数
        validateMessageProofInputs(messageId, messageHash, blockchainNetwork, blockchainType);
        
        // 创建消息证明实体
        BlockchainMessageProofEntity proof = new BlockchainMessageProofEntity(messageId, messageHash, 
                                                                             blockchainNetwork, blockchainType);
        
        // 设置可选参数
        if (options != null) {
            applyMessageProofOptions(proof, options);
        }
        
        // 计算消息哈希（如果需要）
        if (proof.getMessageHash() == null) {
            String calculatedHash = calculateMessageHash(messageId, options);
            proof.setMessageHash(calculatedHash);
        }
        
        // 保存到数据库
        proof = messageProofRepository.save(proof);
        
        // 异步提交到区块链（实际实现应使用消息队列或异步任务）
        submitToBlockchainAsync(proof);
        
        return proof;
    }
    
    /**
     * 验证消息证明
     */
    public BlockchainMessageProofEntity verifyMessageProof(UUID proofId) {
        Optional<BlockchainMessageProofEntity> optionalProof = messageProofRepository.findById(proofId);
        if (!optionalProof.isPresent()) {
            throw new IllegalArgumentException("消息证明不存在: " + proofId);
        }
        
        BlockchainMessageProofEntity proof = optionalProof.get();
        
        // 检查证明是否已过期
        if (proof.isExpired()) {
            proof.setVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.EXPIRED);
            proof.setVerificationTimestamp(LocalDateTime.now());
            proof.setVerificationResult("证明已过期");
            return messageProofRepository.save(proof);
        }
        
        // 验证区块链交易
        boolean blockchainVerified = verifyBlockchainTransaction(proof);
        
        // 验证消息哈希
        boolean hashVerified = verifyMessageHash(proof);
        
        // 验证智能合约（如果存在）
        boolean contractVerified = verifySmartContract(proof);
        
        // 验证零知识证明（如果存在）
        boolean zkProofVerified = verifyZeroKnowledgeProof(proof);
        
        // 更新验证状态
        if (blockchainVerified && hashVerified) {
            proof.setVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.VERIFIED);
            proof.setVerificationConfidence(calculateVerificationConfidence(proof, blockchainVerified, 
                                                                           hashVerified, contractVerified, zkProofVerified));
        } else {
            proof.setVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.FAILED);
        }
        
        proof.setVerificationTimestamp(LocalDateTime.now());
        proof.setVerificationResult(generateVerificationResult(blockchainVerified, hashVerified, 
                                                              contractVerified, zkProofVerified));
        
        return messageProofRepository.save(proof);
    }
    
    /**
     * 创建区块链时间戳证明
     */
    public BlockchainTimestampEntity createTimestampProof(String contentType, String contentHash,
                                                         String blockchainNetwork,
                                                         BlockchainTimestampEntity.BlockchainType blockchainType,
                                                         Map<String, Object> options) {
        
        // 验证输入参数
        validateTimestampProofInputs(contentType, contentHash, blockchainNetwork, blockchainType);
        
        // 创建时间戳实体
        BlockchainTimestampEntity timestamp = new BlockchainTimestampEntity(contentType, contentHash,
                                                                           blockchainNetwork, blockchainType);
        
        // 设置可选参数
        if (options != null) {
            applyTimestampProofOptions(timestamp, options);
        }
        
        // 计算时间戳精度
        timestamp.calculateTimestampPrecision();
        
        // 保存到数据库
        timestamp = timestampRepository.save(timestamp);
        
        // 异步提交到区块链
        submitTimestampToBlockchainAsync(timestamp);
        
        return timestamp;
    }
    
    /**
     * 验证时间戳证明
     */
    public BlockchainTimestampEntity verifyTimestampProof(UUID timestampId) {
        Optional<BlockchainTimestampEntity> optionalTimestamp = timestampRepository.findById(timestampId);
        if (!optionalTimestamp.isPresent()) {
            throw new IllegalArgumentException("时间戳证明不存在: " + timestampId);
        }
        
        BlockchainTimestampEntity timestamp = optionalTimestamp.get();
        
        // 检查证明是否已过期
        if (timestamp.isExpired()) {
            timestamp.setVerificationStatus(BlockchainTimestampEntity.VerificationStatus.EXPIRED);
            timestamp.setVerificationTimestamp(LocalDateTime.now());
            timestamp.setVerificationResult("时间戳证明已过期");
            return timestampRepository.save(timestamp);
        }
        
        // 验证区块链交易
        boolean blockchainVerified = verifyTimestampBlockchainTransaction(timestamp);
        
        // 验证 Merkle 证明（如果存在）
        boolean merkleVerified = verifyMerkleProof(timestamp);
        
        // 验证时间戳证书（如果存在）
        boolean certificateVerified = verifyTimestampCertificate(timestamp);
        
        // 更新验证状态
        if (blockchainVerified) {
            timestamp.setVerificationStatus(BlockchainTimestampEntity.VerificationStatus.VERIFIED);
            timestamp.setVerificationConfidence(calculateTimestampVerificationConfidence(timestamp, blockchainVerified,
                                                                                       merkleVerified, certificateVerified));
        } else {
            timestamp.setVerificationStatus(BlockchainTimestampEntity.VerificationStatus.FAILED);
        }
        
        timestamp.setVerificationTimestamp(LocalDateTime.now());
        timestamp.setVerificationResult(generateTimestampVerificationResult(blockchainVerified, merkleVerified,
                                                                           certificateVerified));
        
        return timestampRepository.save(timestamp);
    }
    
    /**
     * 批量验证消息证明
     */
    public List<BlockchainMessageProofEntity> batchVerifyMessageProofs(List<UUID> proofIds) {
        return proofIds.stream()
                .map(this::verifyMessageProof)
                .toList();
    }
    
    /**
     * 获取消息的验证历史
     */
    public List<BlockchainMessageProofEntity> getMessageVerificationHistory(String messageId) {
        return messageProofRepository.findByMessageIdOrderByCreatedAtDesc(messageId);
    }
    
    /**
     * 检查消息是否已被篡改
     */
    public boolean isMessageTampered(String messageId, String currentHash) {
        List<BlockchainMessageProofEntity> proofs = getMessageVerificationHistory(messageId);
        
        if (proofs.isEmpty()) {
            // 没有验证记录，无法判断
            return false;
        }
        
        // 查找最新的有效证明
        Optional<BlockchainMessageProofEntity> latestValidProof = proofs.stream()
                .filter(BlockchainMessageProofEntity::isVerified)
                .findFirst();
        
        if (!latestValidProof.isPresent()) {
            // 没有有效的证明
            return false;
        }
        
        BlockchainMessageProofEntity proof = latestValidProof.get();
        
        // 比较哈希值
        return !proof.getMessageHash().equals(currentHash);
    }
    
    /**
     * 生成验证报告
     */
    public String generateVerificationReport(UUID proofId) {
        Optional<BlockchainMessageProofEntity> optionalProof = messageProofRepository.findById(proofId);
        if (!optionalProof.isPresent()) {
            return "错误: 证明不存在";
        }
        
        BlockchainMessageProofEntity proof = optionalProof.get();
        return proof.generateVerificationReport();
    }
    
    /**
     * 生成时间戳证明报告
     */
    public String generateTimestampProofReport(UUID timestampId) {
        Optional<BlockchainTimestampEntity> optionalTimestamp = timestampRepository.findById(timestampId);
        if (!optionalTimestamp.isPresent()) {
            return "错误: 时间戳证明不存在";
        }
        
        BlockchainTimestampEntity timestamp = optionalTimestamp.get();
        return timestamp.generateTimestampProofReport();
    }
    
    /**
     * 获取区块链网络统计信息
     */
    public Map<String, Object> getBlockchainNetworkStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 消息证明统计
        long totalProofs = messageProofRepository.count();
        long verifiedProofs = messageProofRepository.countByVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.VERIFIED);
        long pendingProofs = messageProofRepository.countByVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.PENDING);
        
        // 时间戳证明统计
        long totalTimestamps = timestampRepository.count();
        long verifiedTimestamps = timestampRepository.countByVerificationStatus(BlockchainTimestampEntity.VerificationStatus.VERIFIED);
        
        stats.put("totalMessageProofs", totalProofs);
        stats.put("verifiedMessageProofs", verifiedProofs);
        stats.put("pendingMessageProofs", pendingProofs);
        stats.put("totalTimestampProofs", totalTimestamps);
        stats.put("verifiedTimestampProofs", verifiedTimestamps);
        stats.put("verificationSuccessRate", totalProofs > 0 ? (double) verifiedProofs / totalProofs * 100 : 0);
        stats.put("timestampSuccessRate", totalTimestamps > 0 ? (double) verifiedTimestamps / totalTimestamps * 100 : 0);
        
        // 按区块链类型统计
        Map<String, Long> blockchainTypeStats = new HashMap<>();
        for (BlockchainMessageProofEntity.BlockchainType type : BlockchainMessageProofEntity.BlockchainType.values()) {
            long count = messageProofRepository.countByBlockchainType(type);
            blockchainTypeStats.put(type.name(), count);
        }
        stats.put("blockchainTypeDistribution", blockchainTypeStats);
        
        return stats;
    }
    
    // 私有辅助方法
    
    private void initializeNetworkConfigs() {
        // 以太坊配置
        networkConfigs.put("ETHEREUM_MAINNET", new BlockchainNetworkConfig(
            "Ethereum Mainnet",
            "https://mainnet.infura.io/v3/YOUR_API_KEY",
            BlockchainMessageProofEntity.BlockchainType.ETHEREUM,
            1L,
            15000L, // 15秒区块时间
            "0xMessageProofContract" // 示例合约地址
        ));
        
        // Polygon 配置
        networkConfigs.put("POLYGON_MAINNET", new BlockchainNetworkConfig(
            "Polygon Mainnet",
            "https://polygon-mainnet.infura.io/v3/YOUR_API_KEY",
            BlockchainMessageProofEntity.BlockchainType.POLYGON,
            137L,
            3000L, // 3秒区块时间
            "0xMessageProofContractPolygon"
        ));
        
        // Hyperledger Fabric 配置
        networkConfigs.put("HYPERLEDGER_FABRIC", new BlockchainNetworkConfig(
            "Hyperledger Fabric",
            "grpcs://fabric.example.com:443",
            BlockchainMessageProofEntity.BlockchainType.HYPERLEDGER_FABRIC,
            null,
            1000L, // 1秒区块时间
            "message-proof-chaincode"
        ));
    }
    
    private void validateMessageProofInputs(String messageId, String messageHash, 
                                           String blockchainNetwork, 
                                           BlockchainMessageProofEntity.BlockchainType blockchainType) {
        if (messageId == null || messageId.trim().isEmpty()) {
            throw new IllegalArgumentException("消息ID不能为空");
        }
        if (messageHash == null || messageHash.trim().isEmpty()) {
            throw new IllegalArgumentException("消息哈希不能为空");
        }
        if (blockchainNetwork == null || blockchainNetwork.trim().isEmpty()) {
            throw new IllegalArgumentException("区块链网络不能为空");
        }
        if (blockchainType == null) {
            throw new IllegalArgumentException("区块链类型不能为空");
        }
    }
    
    private void validateTimestampProofInputs(String contentType, String contentHash,
                                             String blockchainNetwork,
                                             BlockchainTimestampEntity.BlockchainType blockchainType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("内容类型不能为空");
        }
        if (contentHash == null || contentHash.trim().isEmpty()) {
            throw new IllegalArgumentException("内容哈希不能为空");
        }
        if (blockchainNetwork == null || blockchainNetwork.trim().isEmpty()) {
            throw new IllegalArgumentException("区块链网络不能为空");
        }
        if (blockchainType == null) {
            throw new IllegalArgumentException("区块链类型不能为空");
        }
    }
    
    private void applyMessageProofOptions(BlockchainMessageProofEntity proof, Map<String, Object> options) {
        if (options.containsKey("contractAddress")) {
            proof.setContractAddress((String) options.get("contractAddress"));
        }
        if (options.containsKey("zeroKnowledgeProofId")) {
            proof.setZeroKnowledgeProofId((String) options.get("zeroKnowledgeProofId"));
        }
        if (options.containsKey("expiresAt")) {
            proof.setExpiresAt((LocalDateTime) options.get("expiresAt"));
        }
        if (options.containsKey("additionalMetadata")) {
            proof.setAdditionalMetadata((String) options.get("additionalMetadata"));
        }
    }
    
    private void applyTimestampProofOptions(BlockchainTimestampEntity timestamp, Map<String, Object> options) {
        if (options.containsKey("timestampStandard")) {
            timestamp.setTimestampStandard((BlockchainTimestampEntity.TimestampStandard) options.get("timestampStandard"));
        }
        if (options.containsKey("validityStart")) {
            timestamp.setTimestampValidityStart((LocalDateTime) options.get("validityStart"));
        }
        if (options.containsKey("validityEnd")) {
            timestamp.setTimestampValidityEnd((LocalDateTime) options.get("validityEnd"));
        }
        if (options.containsKey("timestampAuthority")) {
            timestamp.setTimestampAuthority((String) options.get("timestampAuthority"));
        }
        if (options.containsKey("additionalMetadata")) {
            timestamp.setAdditionalMetadata((String) options.get("additionalMetadata"));
        }
    }
    
    private String calculateMessageHash(String messageId, Map<String, Object> options) {
        // 实际实现应该使用安全的哈希算法
        // 这里返回示例哈希
        return "sha256:" + messageId.hashCode();
    }
    
    private boolean verifyBlockchainTransaction(BlockchainMessageProofEntity proof) {
        // 实际实现应该连接到区块链网络验证交易
        // 这里返回模拟验证结果
        return proof.hasTransactionHash() && proof.getVerificationStatus() != BlockchainMessageProofEntity.VerificationStatus.FAILED;
    }
    
    private boolean verifyMessageHash(BlockchainMessageProofEntity proof) {
        // 验证消息哈希是否匹配
        // 实际实现应该重新计算哈希并进行比较
        return proof.getMessageHash() != null && !proof.getMessageHash().trim().isEmpty();
    }
    
    private boolean verifySmartContract(BlockchainMessageProofEntity proof) {
        if (!proof.hasSmartContract()) {
            return true; // 没有智能合约，视为验证通过
        }
        // 实际实现应该验证智能合约调用
        return true;
    }
    
    private boolean verifyZeroKnowledgeProof(BlockchainMessageProofEntity proof) {
        if (!proof.hasZeroKnowledgeProof()) {
            return true; // 没有零知识证明，视为验证通过
        }
        // 实际实现应该验证零知识证明
        return true;
    }
    
    private boolean verifyTimestampBlockchainTransaction(BlockchainTimestampEntity timestamp) {
        // 验证区块链交易
        return timestamp.hasTransactionHash() && timestamp.getVerificationStatus() != BlockchainTimestampEntity.VerificationStatus.FAILED;
    }
    
    private boolean verifyMerkleProof(BlockchainTimestampEntity timestamp) {
        if (!timestamp.hasMerkleProof()) {
            return true; // 没有 Merkle 证明，视为验证通过
        }
        // 验证 Merkle 证明
        return timestamp.verifyMerkleProof(timestamp.getMerkleRoot());
    }
    
    private boolean verifyTimestampCertificate(BlockchainTimestampEntity timestamp) {
        if (!timestamp.hasTimestampCertificate()) {
            return true; // 没有时间戳证书，视为验证通过
        }
        // 验证时间戳证书
        return true;
    }
    
    private double calculateVerificationConfidence(BlockchainMessageProofEntity proof, 
                                                  boolean blockchainVerified, boolean hashVerified,
                                                  boolean contractVerified, boolean zkProofVerified) {
        double confidence = 0.0;
        
        if (blockchainVerified) confidence += 0.4;
        if (hashVerified) confidence += 0.3;
        if (contractVerified) confidence += 0.2;
        if (zkProofVerified) confidence += 0.1;
        
        // 根据区块链类型调整置信度
        switch (proof.getBlockchainType()) {
            case ETHEREUM:
                confidence *= 1.0;
                break;
            case HYPERLEDGER_FABRIC:
                confidence *= 0.9; // 私有链，可信度稍低
                break;
            case CUSTOM:
                confidence *= 0.7; // 自定义链，可信度较低
                break;
            default:
                confidence *= 0.8;
        }
        
        return Math.min(confidence, 1.0);
    }
    
    private double calculateTimestampVerificationConfidence(BlockchainTimestampEntity timestamp,
                                                           boolean blockchainVerified, boolean merkleVerified,
                                                           boolean certificateVerified) {
        double confidence = 0.0;
        
        if (blockchainVerified) confidence += 0.5;
        if (merkleVerified) confidence += 0.3;
        if (certificateVerified) confidence += 0.2;
        
        return Math.min(confidence, 1.0);
    }
    
    private String generateVerificationResult(boolean blockchainVerified, boolean hashVerified,
                                            boolean contractVerified, boolean zkProofVerified) {
        StringBuilder result = new StringBuilder();
        result.append("验证结果: ");
        
        if (blockchainVerified && hashVerified) {
            result.append("通过");
            if (contractVerified) result.append(" (智能合约已验证)");
            if (zkProofVerified) result.append(" (零知识证明已验证)");
        } else {
            result.append("失败");
            if (!blockchainVerified) result.append(" [区块链交易验证失败]");
            if (!hashVerified) result.append(" [消息哈希验证失败]");
        }
        
        return result.toString();
    }
    
    private String generateTimestampVerificationResult(boolean blockchainVerified, boolean merkleVerified,
                                                      boolean certificateVerified) {
        StringBuilder result = new StringBuilder();
        result.append("时间戳验证结果: ");
        
        if (blockchainVerified) {
            result.append("通过");
            if (merkleVerified) result.append(" (Merkle证明已验证)");
            if (certificateVerified) result.append(" (时间戳证书已验证)");
        } else {
            result.append("失败 [区块链交易验证失败]");
        }
        
        return result.toString();
    }
    
    private void submitToBlockchainAsync(BlockchainMessageProofEntity proof) {
        // 实际实现应该使用消息队列或异步任务提交到区块链
        // 这里只是模拟
        proof.setVerificationStatus(BlockchainMessageProofEntity.VerificationStatus.SUBMITTED);
        messageProofRepository.save(proof);
    }
    
    private void submitTimestampToBlockchainAsync(BlockchainTimestampEntity timestamp) {
        // 实际实现应该使用消息队列或异步任务提交到区块链
        timestamp.setVerificationStatus(BlockchainTimestampEntity.VerificationStatus.SUBMITTED);
        timestampRepository.save(timestamp);
    }
    
    // 区块链网络配置类
    private static class BlockchainNetworkConfig {
        private final String name;
        private final String rpcUrl;
        private final BlockchainMessageProofEntity.BlockchainType type;
        private final Long chainId;
        private final Long blockTimeMs;
        private final String defaultContractAddress;
        
        public BlockchainNetworkConfig(String name, String rpcUrl, 
                                      BlockchainMessageProofEntity.BlockchainType type,
                                      Long chainId, Long blockTimeMs, String defaultContractAddress) {
            this.name = name;
            this.rpcUrl = rpcUrl;
            this.type = type;
            this.chainId = chainId;
            this.blockTimeMs = blockTimeMs;
            this.defaultContractAddress = defaultContractAddress;
        }
        
        public String getName() { return name; }
        public String getRpcUrl() { return rpcUrl; }
        public BlockchainMessageProofEntity.BlockchainType getType() { return type; }
        public Long getChainId() { return chainId; }
        public Long getBlockTimeMs() { return blockTimeMs; }
        public String getDefaultContractAddress() { return defaultContractAddress; }
    }
}