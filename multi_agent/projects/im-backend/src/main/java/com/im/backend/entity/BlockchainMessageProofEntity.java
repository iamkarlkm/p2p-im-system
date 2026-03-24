package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 基于区块链的消息不可否认性验证实体
 * 存储消息哈希的区块链证明信息
 */
@Entity
@Table(name = "blockchain_message_proof", indexes = {
    @Index(name = "idx_message_id", columnList = "messageId"),
    @Index(name = "idx_blockchain_tx_hash", columnList = "blockchainTransactionHash"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_verification_status", columnList = "verificationStatus")
})
public class BlockchainMessageProofEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "message_id", nullable = false, length = 128)
    private String messageId;
    
    @Column(name = "message_hash", nullable = false, length = 256)
    private String messageHash;
    
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
    
    @Column(name = "gas_used")
    private Long gasUsed;
    
    @Column(name = "gas_price")
    private Long gasPrice;
    
    @Column(name = "contract_address", length = 256)
    private String contractAddress;
    
    @Column(name = "smart_contract_function", length = 100)
    private String smartContractFunction;
    
    @Column(name = "smart_contract_input_data", columnDefinition = "TEXT")
    private String smartContractInputData;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus;
    
    @Column(name = "verification_timestamp")
    private LocalDateTime verificationTimestamp;
    
    @Column(name = "verification_result", columnDefinition = "TEXT")
    private String verificationResult;
    
    @Column(name = "verification_confidence")
    private Double verificationConfidence;
    
    @Column(name = "zero_knowledge_proof_id", length = 256)
    private String zeroKnowledgeProofId;
    
    @Column(name = "zero_knowledge_proof_data", columnDefinition = "TEXT")
    private String zeroKnowledgeProofData;
    
    @Column(name = "attestation_certificate", columnDefinition = "TEXT")
    private String attestationCertificate;
    
    @Column(name = "attestation_authority", length = 200)
    private String attestationAuthority;
    
    @Column(name = "proof_generation_time_ms")
    private Long proofGenerationTimeMs;
    
    @Column(name = "proof_verification_time_ms")
    private Long proofVerificationTimeMs;
    
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
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (verificationStatus == null) {
            verificationStatus = VerificationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public BlockchainMessageProofEntity() {}
    
    public BlockchainMessageProofEntity(String messageId, String messageHash, String blockchainNetwork, BlockchainType blockchainType) {
        this.messageId = messageId;
        this.messageHash = messageHash;
        this.blockchainNetwork = blockchainNetwork;
        this.blockchainType = blockchainType;
        this.verificationStatus = VerificationStatus.PENDING;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getMessageHash() { return messageHash; }
    public void setMessageHash(String messageHash) { this.messageHash = messageHash; }
    
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
    
    public Long getGasUsed() { return gasUsed; }
    public void setGasUsed(Long gasUsed) { this.gasUsed = gasUsed; }
    
    public Long getGasPrice() { return gasPrice; }
    public void setGasPrice(Long gasPrice) { this.gasPrice = gasPrice; }
    
    public String getContractAddress() { return contractAddress; }
    public void setContractAddress(String contractAddress) { this.contractAddress = contractAddress; }
    
    public String getSmartContractFunction() { return smartContractFunction; }
    public void setSmartContractFunction(String smartContractFunction) { this.smartContractFunction = smartContractFunction; }
    
    public String getSmartContractInputData() { return smartContractInputData; }
    public void setSmartContractInputData(String smartContractInputData) { this.smartContractInputData = smartContractInputData; }
    
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    
    public LocalDateTime getVerificationTimestamp() { return verificationTimestamp; }
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) { this.verificationTimestamp = verificationTimestamp; }
    
    public String getVerificationResult() { return verificationResult; }
    public void setVerificationResult(String verificationResult) { this.verificationResult = verificationResult; }
    
    public Double getVerificationConfidence() { return verificationConfidence; }
    public void setVerificationConfidence(Double verificationConfidence) { this.verificationConfidence = verificationConfidence; }
    
    public String getZeroKnowledgeProofId() { return zeroKnowledgeProofId; }
    public void setZeroKnowledgeProofId(String zeroKnowledgeProofId) { this.zeroKnowledgeProofId = zeroKnowledgeProofId; }
    
    public String getZeroKnowledgeProofData() { return zeroKnowledgeProofData; }
    public void setZeroKnowledgeProofData(String zeroKnowledgeProofData) { this.zeroKnowledgeProofData = zeroKnowledgeProofData; }
    
    public String getAttestationCertificate() { return attestationCertificate; }
    public void setAttestationCertificate(String attestationCertificate) { this.attestationCertificate = attestationCertificate; }
    
    public String getAttestationAuthority() { return attestationAuthority; }
    public void setAttestationAuthority(String attestationAuthority) { this.attestationAuthority = attestationAuthority; }
    
    public Long getProofGenerationTimeMs() { return proofGenerationTimeMs; }
    public void setProofGenerationTimeMs(Long proofGenerationTimeMs) { this.proofGenerationTimeMs = proofGenerationTimeMs; }
    
    public Long getProofVerificationTimeMs() { return proofVerificationTimeMs; }
    public void setProofVerificationTimeMs(Long proofVerificationTimeMs) { this.proofVerificationTimeMs = proofVerificationTimeMs; }
    
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
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
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
    
    public boolean hasSmartContract() {
        return contractAddress != null && !contractAddress.trim().isEmpty();
    }
    
    public boolean hasZeroKnowledgeProof() {
        return zeroKnowledgeProofId != null && !zeroKnowledgeProofId.trim().isEmpty();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    // 创建验证报告
    public String generateVerificationReport() {
        StringBuilder report = new StringBuilder();
        report.append("消息区块链验证报告\n");
        report.append("========================\n");
        report.append("消息ID: ").append(messageId).append("\n");
        report.append("消息哈希: ").append(messageHash).append("\n");
        report.append("区块链网络: ").append(blockchainNetwork).append("\n");
        report.append("区块链类型: ").append(blockchainType).append("\n");
        report.append("验证状态: ").append(verificationStatus).append("\n");
        
        if (hasTransactionHash()) {
            report.append("交易哈希: ").append(blockchainTransactionHash).append("\n");
            report.append("区块高度: ").append(blockNumber).append("\n");
            report.append("交易索引: ").append(transactionIndex).append("\n");
            report.append("区块时间: ").append(blockTimestamp).append("\n");
        }
        
        if (hasSmartContract()) {
            report.append("智能合约地址: ").append(contractAddress).append("\n");
            report.append("智能合约函数: ").append(smartContractFunction).append("\n");
        }
        
        if (hasZeroKnowledgeProof()) {
            report.append("零知识证明ID: ").append(zeroKnowledgeProofId).append("\n");
        }
        
        report.append("创建时间: ").append(createdAt).append("\n");
        report.append("最后更新: ").append(updatedAt).append("\n");
        
        if (verificationConfidence != null) {
            report.append("验证置信度: ").append(String.format("%.2f%%", verificationConfidence * 100)).append("\n");
        }
        
        if (proofGenerationTimeMs != null) {
            report.append("证明生成时间: ").append(proofGenerationTimeMs).append("ms\n");
        }
        
        if (proofVerificationTimeMs != null) {
            report.append("证明验证时间: ").append(proofVerificationTimeMs).append("ms\n");
        }
        
        if (isExpired()) {
            report.append("⚠️ 注意: 此证明已过期\n");
        }
        
        return report.toString();
    }
}