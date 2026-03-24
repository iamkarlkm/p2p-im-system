package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 区块链时间戳证明实体
 * 提供基于区块链的时间戳证明服务
 */
@Entity
@Table(name = "blockchain_timestamp", indexes = {
    @Index(name = "idx_content_hash", columnList = "contentHash"),
    @Index(name = "idx_timestamp_value", columnList = "timestampValue"),
    @Index(name = "idx_blockchain_tx_hash", columnList = "blockchainTransactionHash"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class BlockchainTimestampEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;
    
    @Column(name = "content_hash", nullable = false, length = 256)
    private String contentHash;
    
    @Column(name = "content_metadata", columnDefinition = "TEXT")
    private String contentMetadata;
    
    @Column(name = "timestamp_value", nullable = false)
    private LocalDateTime timestampValue;
    
    @Column(name = "timestamp_precision_ms")
    private Long timestampPrecisionMs;
    
    @Column(name = "blockchain_network", nullable = false, length = 50)
    private String blockchainNetwork;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain_type", nullable = false, length = 20)
    private BlockchainType blockchainType;
    
    @Column(name = "blockchain_transaction_hash", unique = true, length = 256)
    private String blockchainTransactionHash;
    
    @Column(name = "block_number")
    private Long blockNumber;
    
    @Column(name = "transaction_index")
    private Integer transactionIndex;
    
    @Column(name = "block_timestamp")
    private LocalDateTime blockTimestamp;
    
    @Column(name = "merkle_root", length = 256)
    private String merkleRoot;
    
    @Column(name = "merkle_proof", columnDefinition = "TEXT")
    private String merkleProof;
    
    @Column(name = "merkle_path", columnDefinition = "TEXT")
    private String merklePath;
    
    @Column(name = "timestamp_certificate", columnDefinition = "TEXT")
    private String timestampCertificate;
    
    @Column(name = "timestamp_authority", length = 200)
    private String timestampAuthority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "timestamp_standard", length = 30)
    private TimestampStandard timestampStandard;
    
    @Column(name = "timestamp_validity_start")
    private LocalDateTime timestampValidityStart;
    
    @Column(name = "timestamp_validity_end")
    private LocalDateTime timestampValidityEnd;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus;
    
    @Column(name = "verification_timestamp")
    private LocalDateTime verificationTimestamp;
    
    @Column(name = "verification_result", columnDefinition = "TEXT")
    private String verificationResult;
    
    @Column(name = "verification_confidence")
    private Double verificationConfidence;
    
    @Column(name = "timestamp_generation_time_ms")
    private Long timestampGenerationTimeMs;
    
    @Column(name = "timestamp_verification_time_ms")
    private Long timestampVerificationTimeMs;
    
    @Column(name = "blockchain_fee_paid")
    private Long blockchainFeePaid;
    
    @Column(name = "blockchain_fee_currency", length = 10)
    private String blockchainFeeCurrency;
    
    @Column(name = "additional_metadata", columnDefinition = "JSON")
    private String additionalMetadata;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (verificationStatus == null) {
            verificationStatus = VerificationStatus.PENDING;
        }
        if (timestampValue == null) {
            timestampValue = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public BlockchainTimestampEntity() {}
    
    public BlockchainTimestampEntity(String contentType, String contentHash, String blockchainNetwork, BlockchainType blockchainType) {
        this.contentType = contentType;
        this.contentHash = contentHash;
        this.blockchainNetwork = blockchainNetwork;
        this.blockchainType = blockchainType;
        this.timestampValue = LocalDateTime.now();
        this.verificationStatus = VerificationStatus.PENDING;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    
    public String getContentMetadata() { return contentMetadata; }
    public void setContentMetadata(String contentMetadata) { this.contentMetadata = contentMetadata; }
    
    public LocalDateTime getTimestampValue() { return timestampValue; }
    public void setTimestampValue(LocalDateTime timestampValue) { this.timestampValue = timestampValue; }
    
    public Long getTimestampPrecisionMs() { return timestampPrecisionMs; }
    public void setTimestampPrecisionMs(Long timestampPrecisionMs) { this.timestampPrecisionMs = timestampPrecisionMs; }
    
    public String getBlockchainNetwork() { return blockchainNetwork; }
    public void setBlockchainNetwork(String blockchainNetwork) { this.blockchainNetwork = blockchainNetwork; }
    
    public BlockchainType getBlockchainType() { return blockchainType; }
    public void setBlockchainType(BlockchainType blockchainType) { this.blockchainType = blockchainType; }
    
    public String getBlockchainTransactionHash() { return blockchainTransactionHash; }
    public void setBlockchainTransactionHash(String blockchainTransactionHash) { this.blockchainTransactionHash = blockchainTransactionHash; }
    
    public Long getBlockNumber() { return blockNumber; }
    public void setBlockNumber(Long blockNumber) { this.blockNumber = blockNumber; }
    
    public Integer getTransactionIndex() { return transactionIndex; }
    public void setTransactionIndex(Integer transactionIndex) { this.transactionIndex = transactionIndex; }
    
    public LocalDateTime getBlockTimestamp() { return blockTimestamp; }
    public void setBlockTimestamp(LocalDateTime blockTimestamp) { this.blockTimestamp = blockTimestamp; }
    
    public String getMerkleRoot() { return merkleRoot; }
    public void setMerkleRoot(String merkleRoot) { this.merkleRoot = merkleRoot; }
    
    public String getMerkleProof() { return merkleProof; }
    public void setMerkleProof(String merkleProof) { this.merkleProof = merkleProof; }
    
    public String getMerklePath() { return merklePath; }
    public void setMerklePath(String merklePath) { this.merklePath = merklePath; }
    
    public String getTimestampCertificate() { return timestampCertificate; }
    public void setTimestampCertificate(String timestampCertificate) { this.timestampCertificate = timestampCertificate; }
    
    public String getTimestampAuthority() { return timestampAuthority; }
    public void setTimestampAuthority(String timestampAuthority) { this.timestampAuthority = timestampAuthority; }
    
    public TimestampStandard getTimestampStandard() { return timestampStandard; }
    public void setTimestampStandard(TimestampStandard timestampStandard) { this.timestampStandard = timestampStandard; }
    
    public LocalDateTime getTimestampValidityStart() { return timestampValidityStart; }
    public void setTimestampValidityStart(LocalDateTime timestampValidityStart) { this.timestampValidityStart = timestampValidityStart; }
    
    public LocalDateTime getTimestampValidityEnd() { return timestampValidityEnd; }
    public void setTimestampValidityEnd(LocalDateTime timestampValidityEnd) { this.timestampValidityEnd = timestampValidityEnd; }
    
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    
    public LocalDateTime getVerificationTimestamp() { return verificationTimestamp; }
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) { this.verificationTimestamp = verificationTimestamp; }
    
    public String getVerificationResult() { return verificationResult; }
    public void setVerificationResult(String verificationResult) { this.verificationResult = verificationResult; }
    
    public Double getVerificationConfidence() { return verificationConfidence; }
    public void setVerificationConfidence(Double verificationConfidence) { this.verificationConfidence = verificationConfidence; }
    
    public Long getTimestampGenerationTimeMs() { return timestampGenerationTimeMs; }
    public void setTimestampGenerationTimeMs(Long timestampGenerationTimeMs) { this.timestampGenerationTimeMs = timestampGenerationTimeMs; }
    
    public Long getTimestampVerificationTimeMs() { return timestampVerificationTimeMs; }
    public void setTimestampVerificationTimeMs(Long timestampVerificationTimeMs) { this.timestampVerificationTimeMs = timestampVerificationTimeMs; }
    
    public Long getBlockchainFeePaid() { return blockchainFeePaid; }
    public void setBlockchainFeePaid(Long blockchainFeePaid) { this.blockchainFeePaid = blockchainFeePaid; }
    
    public String getBlockchainFeeCurrency() { return blockchainFeeCurrency; }
    public void setBlockchainFeeCurrency(String blockchainFeeCurrency) { this.blockchainFeeCurrency = blockchainFeeCurrency; }
    
    public String getAdditionalMetadata() { return additionalMetadata; }
    public void setAdditionalMetadata(String additionalMetadata) { this.additionalMetadata = additionalMetadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 枚举类型
    public enum BlockchainType {
        ETHEREUM,
        ETHEREUM_LAYER2,
        HYPERLEDGER_FABRIC,
        HYPERLEDGER_BESU,
        POLYGON,
        BINANCE_SMART_CHAIN,
        SOLANA,
        COSMOS,
        CUSTOM
    }
    
    public enum VerificationStatus {
        PENDING,
        SUBMITTED,
        CONFIRMED,
        VERIFIED,
        FAILED,
        EXPIRED,
        DISPUTED
    }
    
    public enum TimestampStandard {
        RFC3161,
        ISO_8601,
        UNIX_TIMESTAMP,
        BLOCKCHAIN_TIMESTAMP,
        CUSTOM
    }
    
    // 辅助方法
    public boolean isVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }
    
    public boolean isPending() {
        return verificationStatus == VerificationStatus.PENDING || verificationStatus == VerificationStatus.SUBMITTED;
    }
    
    public boolean hasTransactionHash() {
        return blockchainTransactionHash != null && !blockchainTransactionHash.trim().isEmpty();
    }
    
    public boolean hasMerkleProof() {
        return merkleProof != null && !merkleProof.trim().isEmpty();
    }
    
    public boolean hasTimestampCertificate() {
        return timestampCertificate != null && !timestampCertificate.trim().isEmpty();
    }
    
    public boolean isValid() {
        if (timestampValidityStart != null && LocalDateTime.now().isBefore(timestampValidityStart)) {
            return false;
        }
        if (timestampValidityEnd != null && LocalDateTime.now().isAfter(timestampValidityEnd)) {
            return false;
        }
        return isVerified();
    }
    
    public boolean isExpired() {
        return timestampValidityEnd != null && LocalDateTime.now().isAfter(timestampValidityEnd);
    }
    
    // 验证 Merkle 证明
    public boolean verifyMerkleProof(String rootHash) {
        if (!hasMerkleProof() || merkleRoot == null) {
            return false;
        }
        // 这里应该实现实际的 Merkle 证明验证逻辑
        // 简化实现：检查 Merkle 根是否匹配
        return merkleRoot.equals(rootHash);
    }
    
    // 生成时间戳证明报告
    public String generateTimestampProofReport() {
        StringBuilder report = new StringBuilder();
        report.append("区块链时间戳证明报告\n");
        report.append("========================\n");
        report.append("内容类型: ").append(contentType).append("\n");
        report.append("内容哈希: ").append(contentHash).append("\n");
        report.append("时间戳值: ").append(timestampValue).append("\n");
        report.append("区块链网络: ").append(blockchainNetwork).append("\n");
        report.append("区块链类型: ").append(blockchainType).append("\n");
        report.append("验证状态: ").append(verificationStatus).append("\n");
        
        if (hasTransactionHash()) {
            report.append("交易哈希: ").append(blockchainTransactionHash).append("\n");
            report.append("区块高度: ").append(blockNumber).append("\n");
            report.append("区块时间: ").append(blockTimestamp).append("\n");
        }
        
        if (hasMerkleProof()) {
            report.append("Merkle 根: ").append(merkleRoot).append("\n");
            report.append("Merkle 证明: ").append(merkleProof != null ? "已生成" : "未生成").append("\n");
        }
        
        if (hasTimestampCertificate()) {
            report.append("时间戳证书: ").append("已生成").append("\n");
            report.append("证书颁发机构: ").append(timestampAuthority).append("\n");
        }
        
        if (timestampStandard != null) {
            report.append("时间戳标准: ").append(timestampStandard).append("\n");
        }
        
        report.append("创建时间: ").append(createdAt).append("\n");
        report.append("最后更新: ").append(updatedAt).append("\n");
        
        if (timestampValidityStart != null) {
            report.append("有效期开始: ").append(timestampValidityStart).append("\n");
        }
        
        if (timestampValidityEnd != null) {
            report.append("有效期结束: ").append(timestampValidityEnd).append("\n");
            if (isExpired()) {
                report.append("⚠️ 注意: 此时间戳证明已过期\n");
            }
        }
        
        if (timestampPrecisionMs != null) {
            report.append("时间戳精度: ±").append(timestampPrecisionMs).append("ms\n");
        }
        
        if (verificationConfidence != null) {
            report.append("验证置信度: ").append(String.format("%.2f%%", verificationConfidence * 100)).append("\n");
        }
        
        if (timestampGenerationTimeMs != null) {
            report.append("时间戳生成时间: ").append(timestampGenerationTimeMs).append("ms\n");
        }
        
        if (timestampVerificationTimeMs != null) {
            report.append("时间戳验证时间: ").append(timestampVerificationTimeMs).append("ms\n");
        }
        
        // 验证有效性
        if (isValid()) {
            report.append("✅ 状态: 有效的时间戳证明\n");
        } else {
            report.append("❌ 状态: 无效或过期的证明\n");
        }
        
        return report.toString();
    }
    
    // 计算时间戳精度
    public void calculateTimestampPrecision() {
        if (blockchainType == BlockchainType.ETHEREUM) {
            // 以太坊区块时间约 13-15 秒
            timestampPrecisionMs = 15000L;
        } else if (blockchainType == BlockchainType.SOLANA) {
            // Solana 区块时间约 400 毫秒
            timestampPrecisionMs = 400L;
        } else if (blockchainType == BlockchainType.POLYGON) {
            // Polygon 区块时间约 2-3 秒
            timestampPrecisionMs = 3000L;
        } else {
            // 默认精度
            timestampPrecisionMs = 10000L;
        }
    }
}