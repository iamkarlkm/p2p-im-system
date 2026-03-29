package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "zkp_verifiable_computation")
public class ZKPVerifiableComputationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "computation_id", nullable = false, unique = true)
    private String computationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "computation_type", nullable = false)
    private String computationType; // FEDERATED_LEARNING, PRIVACY_AUTH, MESSAGE_INTEGRITY, SELECTIVE_CREDENTIAL, HOMOMORPHIC_VERIFY, MPC_PROOF, PSI_PROOF, VERIFIABLE_RANDOM

    @Column(name = "circuit_type", nullable = false)
    private String circuitType; // GROTH16, PLONK, MARLIN, SONIC, AURORA, FRACTAL

    @Column(name = "circuit_size", nullable = false)
    private Integer circuitSize; // Number of constraints

    @Column(name = "public_inputs", columnDefinition = "TEXT")
    private String publicInputs; // JSON string

    @Column(name = "private_inputs_hash", length = 64)
    private String privateInputsHash; // SHA-256 hash of private inputs

    @Column(name = "proof_generated", nullable = false)
    private Boolean proofGenerated = false;

    @Column(name = "proof_verified", nullable = false)
    private Boolean proofVerified = false;

    @Column(name = "proof_data", columnDefinition = "TEXT")
    private String proofData; // JSON serialized proof

    @Column(name = "verification_key_hash", length = 64)
    private String verificationKeyHash;

    @Column(name = "proof_size_bytes")
    private Long proofSizeBytes;

    @Column(name = "generation_time_ms")
    private Long generationTimeMs;

    @Column(name = "verification_time_ms")
    private Long verificationTimeMs;

    @Column(name = "security_level", nullable = false)
    private Integer securityLevel; // 128, 192, 256 bits

    @Column(name = "trusted_setup_id")
    private String trustedSetupId;

    @Column(name = "trusted_setup_version")
    private Integer trustedSetupVersion;

    @Column(name = "compressed_proof", columnDefinition = "TEXT")
    private String compressedProof;

    @Column(name = "compression_ratio")
    private Double compressionRatio;

    @Column(name = "batch_verification_id")
    private String batchVerificationId;

    @Column(name = "hardware_accelerated")
    private Boolean hardwareAccelerated = false;

    @Column(name = "gpu_used")
    private Boolean gpuUsed = false;

    @Column(name = "fpga_used")
    private Boolean fpgaUsed = false;

    @Column(name = "performance_metrics", columnDefinition = "TEXT")
    private String performanceMetrics; // JSON string

    @Column(name = "anonymization_level", nullable = false)
    private String anonymizationLevel; // RAW, PSEUDONYMIZED, ANONYMIZED, AGGREGATED, DIFFERENTIALLY_PRIVATE

    @Column(name = "privacy_budget_consumed")
    private Double privacyBudgetConsumed;

    @Column(name = "differential_privacy_epsilon")
    private Double differentialPrivacyEpsilon;

    @Column(name = "differential_privacy_delta")
    private Double differentialPrivacyDelta;

    @Column(name = "federated_learning_round")
    private Integer federatedLearningRound;

    @Column(name = "federated_model_id")
    private String federatedModelId;

    @Column(name = "client_contribution_weight")
    private Double clientContributionWeight;

    @Column(name = "message_hash", length = 64)
    private String messageHash;

    @Column(name = "message_integrity_verified")
    private Boolean messageIntegrityVerified = false;

    @Column(name = "credential_type")
    private String credentialType; // AGE, LOCATION, MEMBERSHIP, CERTIFICATION, REPUTATION

    @Column(name = "credential_attributes_disclosed", columnDefinition = "TEXT")
    private String credentialAttributesDisclosed; // JSON array

    @Column(name = "mpc_participants")
    private Integer mpcParticipants;

    @Column(name = "mpc_threshold")
    private Integer mpcThreshold;

    @Column(name = "psi_set_size")
    private Integer psiSetSize;

    @Column(name = "psi_intersection_size")
    private Integer psiIntersectionSize;

    @Column(name = "verifiable_random_output", length = 128)
    private String verifiableRandomOutput;

    @Column(name = "onchain_transaction_hash", length = 66)
    private String onchainTransactionHash;

    @Column(name = "onchain_block_number")
    private Long onchainBlockNumber;

    @Column(name = "onchain_confirmed")
    private Boolean onchainConfirmed = false;

    @Column(name = "computation_status", nullable = false)
    private String computationStatus; // PENDING, GENERATING_PROOF, PROOF_GENERATED, VERIFYING_PROOF, VERIFIED, FAILED, CANCELLED

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    private Integer maxRetries = 3;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "proof_generated_at")
    private LocalDateTime proofGeneratedAt;

    @Column(name = "proof_verified_at")
    private LocalDateTime proofVerifiedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional metadata

    public ZKPVerifiableComputationEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComputationId() { return computationId; }
    public void setComputationId(String computationId) { this.computationId = computationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getComputationType() { return computationType; }
    public void setComputationType(String computationType) { this.computationType = computationType; }

    public String getCircuitType() { return circuitType; }
    public void setCircuitType(String circuitType) { this.circuitType = circuitType; }

    public Integer getCircuitSize() { return circuitSize; }
    public void setCircuitSize(Integer circuitSize) { this.circuitSize = circuitSize; }

    public String getPublicInputs() { return publicInputs; }
    public void setPublicInputs(String publicInputs) { this.publicInputs = publicInputs; }

    public String getPrivateInputsHash() { return privateInputsHash; }
    public void setPrivateInputsHash(String privateInputsHash) { this.privateInputsHash = privateInputsHash; }

    public Boolean getProofGenerated() { return proofGenerated; }
    public void setProofGenerated(Boolean proofGenerated) { this.proofGenerated = proofGenerated; }

    public Boolean getProofVerified() { return proofVerified; }
    public void setProofVerified(Boolean proofVerified) { this.proofVerified = proofVerified; }

    public String getProofData() { return proofData; }
    public void setProofData(String proofData) { this.proofData = proofData; }

    public String getVerificationKeyHash() { return verificationKeyHash; }
    public void setVerificationKeyHash(String verificationKeyHash) { this.verificationKeyHash = verificationKeyHash; }

    public Long getProofSizeBytes() { return proofSizeBytes; }
    public void setProofSizeBytes(Long proofSizeBytes) { this.proofSizeBytes = proofSizeBytes; }

    public Long getGenerationTimeMs() { return generationTimeMs; }
    public void setGenerationTimeMs(Long generationTimeMs) { this.generationTimeMs = generationTimeMs; }

    public Long getVerificationTimeMs() { return verificationTimeMs; }
    public void setVerificationTimeMs(Long verificationTimeMs) { this.verificationTimeMs = verificationTimeMs; }

    public Integer getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(Integer securityLevel) { this.securityLevel = securityLevel; }

    public String getTrustedSetupId() { return trustedSetupId; }
    public void setTrustedSetupId(String trustedSetupId) { this.trustedSetupId = trustedSetupId; }

    public Integer getTrustedSetupVersion() { return trustedSetupVersion; }
    public void setTrustedSetupVersion(Integer trustedSetupVersion) { this.trustedSetupVersion = trustedSetupVersion; }

    public String getCompressedProof() { return compressedProof; }
    public void setCompressedProof(String compressedProof) { this.compressedProof = compressedProof; }

    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }

    public String getBatchVerificationId() { return batchVerificationId; }
    public void setBatchVerificationId(String batchVerificationId) { this.batchVerificationId = batchVerificationId; }

    public Boolean getHardwareAccelerated() { return hardwareAccelerated; }
    public void setHardwareAccelerated(Boolean hardwareAccelerated) { this.hardwareAccelerated = hardwareAccelerated; }

    public Boolean getGpuUsed() { return gpuUsed; }
    public void setGpuUsed(Boolean gpuUsed) { this.gpuUsed = gpuUsed; }

    public Boolean getFpgaUsed() { return fpgaUsed; }
    public void setFpgaUsed(Boolean fpgaUsed) { this.fpgaUsed = fpgaUsed; }

    public String getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(String performanceMetrics) { this.performanceMetrics = performanceMetrics; }

    public String getAnonymizationLevel() { return anonymizationLevel; }
    public void setAnonymizationLevel(String anonymizationLevel) { this.anonymizationLevel = anonymizationLevel; }

    public Double getPrivacyBudgetConsumed() { return privacyBudgetConsumed; }
    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) { this.privacyBudgetConsumed = privacyBudgetConsumed; }

    public Double getDifferentialPrivacyEpsilon() { return differentialPrivacyEpsilon; }
    public void setDifferentialPrivacyEpsilon(Double differentialPrivacyEpsilon) { this.differentialPrivacyEpsilon = differentialPrivacyEpsilon; }

    public Double getDifferentialPrivacyDelta() { return differentialPrivacyDelta; }
    public void setDifferentialPrivacyDelta(Double differentialPrivacyDelta) { this.differentialPrivacyDelta = differentialPrivacyDelta; }

    public Integer getFederatedLearningRound() { return federatedLearningRound; }
    public void setFederatedLearningRound(Integer federatedLearningRound) { this.federatedLearningRound = federatedLearningRound; }

    public String getFederatedModelId() { return federatedModelId; }
    public void setFederatedModelId(String federatedModelId) { this.federatedModelId = federatedModelId; }

    public Double getClientContributionWeight() { return clientContributionWeight; }
    public void setClientContributionWeight(Double clientContributionWeight) { this.clientContributionWeight = clientContributionWeight; }

    public String getMessageHash() { return messageHash; }
    public void setMessageHash(String messageHash) { this.messageHash = messageHash; }

    public Boolean getMessageIntegrityVerified() { return messageIntegrityVerified; }
    public void setMessageIntegrityVerified(Boolean messageIntegrityVerified) { this.messageIntegrityVerified = messageIntegrityVerified; }

    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }

    public String getCredentialAttributesDisclosed() { return credentialAttributesDisclosed; }
    public void setCredentialAttributesDisclosed(String credentialAttributesDisclosed) { this.credentialAttributesDisclosed = credentialAttributesDisclosed; }

    public Integer getMpcParticipants() { return mpcParticipants; }
    public void setMpcParticipants(Integer mpcParticipants) { this.mpcParticipants = mpcParticipants; }

    public Integer getMpcThreshold() { return mpcThreshold; }
    public void setMpcThreshold(Integer mpcThreshold) { this.mpcThreshold = mpcThreshold; }

    public Integer getPsiSetSize() { return psiSetSize; }
    public void setPsiSetSize(Integer psiSetSize) { this.psiSetSize = psiSetSize; }

    public Integer getPsiIntersectionSize() { return psiIntersectionSize; }
    public void setPsiIntersectionSize(Integer psiIntersectionSize) { this.psiIntersectionSize = psiIntersectionSize; }

    public String getVerifiableRandomOutput() { return verifiableRandomOutput; }
    public void setVerifiableRandomOutput(String verifiableRandomOutput) { this.verifiableRandomOutput = verifiableRandomOutput; }

    public String getOnchainTransactionHash() { return onchainTransactionHash; }
    public void setOnchainTransactionHash(String onchainTransactionHash) { this.onchainTransactionHash = onchainTransactionHash; }

    public Long getOnchainBlockNumber() { return onchainBlockNumber; }
    public void setOnchainBlockNumber(Long onchainBlockNumber) { this.onchainBlockNumber = onchainBlockNumber; }

    public Boolean getOnchainConfirmed() { return onchainConfirmed; }
    public void setOnchainConfirmed(Boolean onchainConfirmed) { this.onchainConfirmed = onchainConfirmed; }

    public String getComputationStatus() { return computationStatus; }
    public void setComputationStatus(String computationStatus) { this.computationStatus = computationStatus; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getProofGeneratedAt() { return proofGeneratedAt; }
    public void setProofGeneratedAt(LocalDateTime proofGeneratedAt) { this.proofGeneratedAt = proofGeneratedAt; }

    public LocalDateTime getProofVerifiedAt() { return proofVerifiedAt; }
    public void setProofVerifiedAt(LocalDateTime proofVerifiedAt) { this.proofVerifiedAt = proofVerifiedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Helper methods
    public boolean isProofValid() {
        return Boolean.TRUE.equals(proofGenerated) && Boolean.TRUE.equals(proofVerified);
    }

    public boolean canRetry() {
        return retryCount < maxRetries && !"CANCELLED".equals(computationStatus);
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}