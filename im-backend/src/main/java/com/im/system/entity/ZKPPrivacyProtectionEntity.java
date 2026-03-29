package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "zkp_privacy_protection")
public class ZKPPrivacyProtectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "protection_id", nullable = false, unique = true)
    private String protectionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "protection_type", nullable = false)
    private String protectionType; // IDENTITY_AUTH, SELECTIVE_DISCLOSURE, ANONYMOUS_CREDENTIAL, RANGE_PROOF, MEMBERSHIP_PROOF, NON_MEMBERSHIP_PROOF, BULK_VERIFICATION, CROSS_CHAIN_PROOF

    @Column(name = "credential_schema_id")
    private String credentialSchemaId;

    @Column(name = "credential_definition_id")
    private String credentialDefinitionId;

    @Column(name = "credential_issuer_id")
    private String credentialIssuerId;

    @Column(name = "credential_revocation_registry_id")
    private String credentialRevocationRegistryId;

    @Column(name = "credential_revocation_index")
    private Long credentialRevocationIndex;

    @Column(name = "credential_valid_from")
    private LocalDateTime credentialValidFrom;

    @Column(name = "credential_valid_to")
    private LocalDateTime credentialValidTo;

    @Column(name = "credential_attributes", columnDefinition = "TEXT")
    private String credentialAttributes; // JSON string of all attributes

    @Column(name = "disclosed_attributes", columnDefinition = "TEXT")
    private String disclosedAttributes; // JSON array of disclosed attribute names

    @Column(name = "predicates", columnDefinition = "TEXT")
    private String predicates; // JSON array of predicates (e.g., age >= 18)

    @Column(name = "predicate_satisfied")
    private Boolean predicateSatisfied;

    @Column(name = "range_proof_type")
    private String rangeProofType; // BULLETPROOFS, BORROMEAN_RINGS, PEDERSEN_COMMITMENTS

    @Column(name = "range_min")
    private Long rangeMin;

    @Column(name = "range_max")
    private Long rangeMax;

    @Column(name = "range_value_committed")
    private String rangeValueCommitted; // Pedersen commitment

    @Column(name = "membership_proof_type")
    private String membershipProofType; // SET_MEMBERSHIP, MERKLE_TREE, ACCUMULATOR

    @Column(name = "membership_set_id")
    private String membershipSetId;

    @Column(name = "membership_root_hash", length = 64)
    private String membershipRootHash;

    @Column(name = "membership_merkle_path", columnDefinition = "TEXT")
    private String membershipMerklePath; // JSON array

    @Column(name = "accumulator_value", length = 128)
    private String accumulatorValue;

    @Column(name = "accumulator_witness", length = 128)
    private String accumulatorWitness;

    @Column(name = "non_membership_proof_type")
    private String nonMembershipProofType;

    @Column(name = "non_membership_set_id")
    private String nonMembershipSetId;

    @Column(name = "bulk_verification_session_id")
    private String bulkVerificationSessionId;

    @Column(name = "bulk_proof_count")
    private Integer bulkProofCount;

    @Column(name = "bulk_verification_time_ms")
    private Long bulkVerificationTimeMs;

    @Column(name = "cross_chain_source")
    private String crossChainSource; // ETHEREUM, POLYGON, SOLANA, COSMOS, POLKADOT

    @Column(name = "cross_chain_target")
    private String crossChainTarget;

    @Column(name = "cross_chain_bridge_id")
    private String crossChainBridgeId;

    @Column(name = "cross_chain_transaction_hash", length = 66)
    private String crossChainTransactionHash;

    @Column(name = "verification_policy_id")
    private String verificationPolicyId;

    @Column(name = "verification_policy_version")
    private Integer verificationPolicyVersion;

    @Column(name = "verification_context", columnDefinition = "TEXT")
    private String verificationContext; // JSON string

    @Column(name = "verification_result", nullable = false)
    private Boolean verificationResult = false;

    @Column(name = "verification_score")
    private Double verificationScore; // 0.0 to 1.0

    @Column(name = "verification_confidence")
    private Double verificationConfidence; // 0.0 to 1.0

    @Column(name = "verification_timestamp", nullable = false)
    private LocalDateTime verificationTimestamp;

    @Column(name = "verification_duration_ms")
    private Long verificationDurationMs;

    @Column(name = "proof_generation_duration_ms")
    private Long proofGenerationDurationMs;

    @Column(name = "proof_size_bytes")
    private Long proofSizeBytes;

    @Column(name = "public_inputs_size_bytes")
    private Long publicInputsSizeBytes;

    @Column(name = "circuit_complexity")
    private String circuitComplexity; // SIMPLE, MEDIUM, COMPLEX, VERY_COMPLEX

    @Column(name = "zk_snark_type", nullable = false)
    private String zkSnarkType; // GROTH16, PLONK, SONIC, MARLIN, AURORA, FRACTAL

    @Column(name = "trusted_setup_id")
    private String trustedSetupId;

    @Column(name = "trusted_setup_version")
    private Integer trustedSetupVersion;

    @Column(name = "proving_key_hash", length = 64)
    private String provingKeyHash;

    @Column(name = "verification_key_hash", length = 64)
    private String verificationKeyHash;

    @Column(name = "proof_data", columnDefinition = "TEXT")
    private String proofData; // JSON serialized proof

    @Column(name = "proof_compressed")
    private Boolean proofCompressed = false;

    @Column(name = "compression_algorithm")
    private String compressionAlgorithm; // ZLIB, GZIP, BROTLI, ZSTD

    @Column(name = "compression_ratio")
    private Double compressionRatio;

    @Column(name = "hardware_acceleration")
    private Boolean hardwareAcceleration = false;

    @Column(name = "gpu_accelerated")
    private Boolean gpuAccelerated = false;

    @Column(name = "fpga_accelerated")
    private Boolean fpgaAccelerated = false;

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

    @Column(name = "privacy_preservation_score")
    private Double privacyPreservationScore; // 0.0 to 1.0

    @Column(name = "identity_leakage_score")
    private Double identityLeakageScore; // 0.0 to 1.0 (lower is better)

    @Column(name = "attribute_correlation_score")
    private Double attributeCorrelationScore; // 0.0 to 1.0 (lower is better)

    @Column(name = "reidentification_risk")
    private Double reidentificationRisk; // 0.0 to 1.0 (lower is better)

    @Column(name = "compliance_status", nullable = false)
    private String complianceStatus; // COMPLIANT, NON_COMPLIANT, PARTIALLY_COMPLIANT, UNKNOWN

    @Column(name = "gdpr_compliant")
    private Boolean gdprCompliant = false;

    @Column(name = "ccpa_compliant")
    private Boolean ccpaCompliant = false;

    @Column(name = "hipaa_compliant")
    private Boolean hipaaCompliant = false;

    @Column(name = "compliance_evidence", columnDefinition = "TEXT")
    private String complianceEvidence; // JSON string

    @Column(name = "audit_trail_id")
    private String auditTrailId;

    @Column(name = "audit_trail_hash", length = 64)
    private String auditTrailHash;

    @Column(name = "protection_status", nullable = false)
    private String protectionStatus; // ACTIVE, REVOKED, EXPIRED, SUSPENDED, PENDING_VERIFICATION

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

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_reason")
    private String revokedReason;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional metadata

    public ZKPPrivacyProtectionEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.validFrom = LocalDateTime.now();
        this.verificationTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProtectionId() { return protectionId; }
    public void setProtectionId(String protectionId) { this.protectionId = protectionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProtectionType() { return protectionType; }
    public void setProtectionType(String protectionType) { this.protectionType = protectionType; }

    public String getCredentialSchemaId() { return credentialSchemaId; }
    public void setCredentialSchemaId(String credentialSchemaId) { this.credentialSchemaId = credentialSchemaId; }

    public String getCredentialDefinitionId() { return credentialDefinitionId; }
    public void setCredentialDefinitionId(String credentialDefinitionId) { this.credentialDefinitionId = credentialDefinitionId; }

    public String getCredentialIssuerId() { return credentialIssuerId; }
    public void setCredentialIssuerId(String credentialIssuerId) { this.credentialIssuerId = credentialIssuerId; }

    public String getCredentialRevocationRegistryId() { return credentialRevocationRegistryId; }
    public void setCredentialRevocationRegistryId(String credentialRevocationRegistryId) { this.credentialRevocationRegistryId = credentialRevocationRegistryId; }

    public Long getCredentialRevocationIndex() { return credentialRevocationIndex; }
    public void setCredentialRevocationIndex(Long credentialRevocationIndex) { this.credentialRevocationIndex = credentialRevocationIndex; }

    public LocalDateTime getCredentialValidFrom() { return credentialValidFrom; }
    public void setCredentialValidFrom(LocalDateTime credentialValidFrom) { this.credentialValidFrom = credentialValidFrom; }

    public LocalDateTime getCredentialValidTo() { return credentialValidTo; }
    public void setCredentialValidTo(LocalDateTime credentialValidTo) { this.credentialValidTo = credentialValidTo; }

    public String getCredentialAttributes() { return credentialAttributes; }
    public void setCredentialAttributes(String credentialAttributes) { this.credentialAttributes = credentialAttributes; }

    public String getDisclosedAttributes() { return disclosedAttributes; }
    public void setDisclosedAttributes(String disclosedAttributes) { this.disclosedAttributes = disclosedAttributes; }

    public String getPredicates() { return predicates; }
    public void setPredicates(String predicates) { this.predicates = predicates; }

    public Boolean getPredicateSatisfied() { return predicateSatisfied; }
    public void setPredicateSatisfied(Boolean predicateSatisfied) { this.predicateSatisfied = predicateSatisfied; }

    public String getRangeProofType() { return rangeProofType; }
    public void setRangeProofType(String rangeProofType) { this.rangeProofType = rangeProofType; }

    public Long getRangeMin() { return rangeMin; }
    public void setRangeMin(Long rangeMin) { this.rangeMin = rangeMin; }

    public Long getRangeMax() { return rangeMax; }
    public void setRangeMax(Long rangeMax) { this.rangeMax = rangeMax; }

    public String getRangeValueCommitted() { return rangeValueCommitted; }
    public void setRangeValueCommitted(String rangeValueCommitted) { this.rangeValueCommitted = rangeValueCommitted; }

    public String getMembershipProofType() { return membershipProofType; }
    public void setMembershipProofType(String membershipProofType) { this.membershipProofType = membershipProofType; }

    public String getMembershipSetId() { return membershipSetId; }
    public void setMembershipSetId(String membershipSetId) { this.membershipSetId = membershipSetId; }

    public String getMembershipRootHash() { return membershipRootHash; }
    public void setMembershipRootHash(String membershipRootHash) { this.membershipRootHash = membershipRootHash; }

    public String getMembershipMerklePath() { return membershipMerklePath; }
    public void setMembershipMerklePath(String membershipMerklePath) { this.membershipMerklePath = membershipMerklePath; }

    public String getAccumulatorValue() { return accumulatorValue; }
    public void setAccumulatorValue(String accumulatorValue) { this.accumulatorValue = accumulatorValue; }

    public String getAccumulatorWitness() { return accumulatorWitness; }
    public void setAccumulatorWitness(String accumulatorWitness) { this.accumulatorWitness = accumulatorWitness; }

    public String getNonMembershipProofType() { return nonMembershipProofType; }
    public void setNonMembershipProofType(String nonMembershipProofType) { this.nonMembershipProofType = nonMembershipProofType; }

    public String getNonMembershipSetId() { return nonMembershipSetId; }
    public void setNonMembershipSetId(String nonMembershipSetId) { this.nonMembershipSetId = nonMembershipSetId; }

    public String getBulkVerificationSessionId() { return bulkVerificationSessionId; }
    public void setBulkVerificationSessionId(String bulkVerificationSessionId) { this.bulkVerificationSessionId = bulkVerificationSessionId; }

    public Integer getBulkProofCount() { return bulkProofCount; }
    public void setBulkProofCount(Integer bulkProofCount) { this.bulkProofCount = bulkProofCount; }

    public Long getBulkVerificationTimeMs() { return bulkVerificationTimeMs; }
    public void setBulkVerificationTimeMs(Long bulkVerificationTimeMs) { this.bulkVerificationTimeMs = bulkVerificationTimeMs; }

    public String getCrossChainSource() { return crossChainSource; }
    public void setCrossChainSource(String crossChainSource) { this.crossChainSource = crossChainSource; }

    public String getCrossChainTarget() { return crossChainTarget; }
    public void setCrossChainTarget(String crossChainTarget) { this.crossChainTarget = crossChainTarget; }

    public String getCrossChainBridgeId() { return crossChainBridgeId; }
    public void setCrossChainBridgeId(String crossChainBridgeId) { this.crossChainBridgeId = crossChainBridgeId; }

    public String getCrossChainTransactionHash() { return crossChainTransactionHash; }
    public void setCrossChainTransactionHash(String crossChainTransactionHash) { this.crossChainTransactionHash = crossChainTransactionHash; }

    public String getVerificationPolicyId() { return verificationPolicyId; }
    public void setVerificationPolicyId(String verificationPolicyId) { this.verificationPolicyId = verificationPolicyId; }

    public Integer getVerificationPolicyVersion() { return verificationPolicyVersion; }
    public void setVerificationPolicyVersion(Integer verificationPolicyVersion) { this.verificationPolicyVersion = verificationPolicyVersion; }

    public String getVerificationContext() { return verificationContext; }
    public void setVerificationContext(String verificationContext) { this.verificationContext = verificationContext; }

    public Boolean getVerificationResult() { return verificationResult; }
    public void setVerificationResult(Boolean verificationResult) { this.verificationResult = verificationResult; }

    public Double getVerificationScore() { return verificationScore; }
    public void setVerificationScore(Double verificationScore) { this.verificationScore = verificationScore; }

    public Double getVerificationConfidence() { return verificationConfidence; }
    public void setVerificationConfidence(Double verificationConfidence) { this.verificationConfidence = verificationConfidence; }

    public LocalDateTime getVerificationTimestamp() { return verificationTimestamp; }
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) { this.verificationTimestamp = verificationTimestamp; }

    public Long getVerificationDurationMs() { return verificationDurationMs; }
    public void setVerificationDurationMs(Long verificationDurationMs) { this.verificationDurationMs = verificationDurationMs; }

    public Long getProofGenerationDurationMs() { return proofGenerationDurationMs; }
    public void setProofGenerationDurationMs(Long proofGenerationDurationMs) { this.proofGenerationDurationMs = proofGenerationDurationMs; }

    public Long getProofSizeBytes() { return proofSizeBytes; }
    public void setProofSizeBytes(Long proofSizeBytes) { this.proofSizeBytes = proofSizeBytes; }

    public Long getPublicInputsSizeBytes() { return publicInputsSizeBytes; }
    public void setPublicInputsSizeBytes(Long publicInputsSizeBytes) { this.publicInputsSizeBytes = publicInputsSizeBytes; }

    public String getCircuitComplexity() { return circuitComplexity; }
    public void setCircuitComplexity(String circuitComplexity) { this.circuitComplexity = circuitComplexity; }

    public String getZkSnarkType() { return zkSnarkType; }
    public void setZkSnarkType(String zkSnarkType) { this.zkSnarkType = zkSnarkType; }

    public String getTrustedSetupId() { return trustedSetupId; }
    public void setTrustedSetupId(String trustedSetupId) { this.trustedSetupId = trustedSetupId; }

    public Integer getTrustedSetupVersion() { return trustedSetupVersion; }
    public void setTrustedSetupVersion(Integer trustedSetupVersion) { this.trustedSetupVersion = trustedSetupVersion; }

    public String getProvingKeyHash() { return provingKeyHash; }
    public void setProvingKeyHash(String provingKeyHash) { this.provingKeyHash = provingKeyHash; }

    public String getVerificationKeyHash() { return verificationKeyHash; }
    public void setVerificationKeyHash(String verificationKeyHash) { this.verificationKeyHash = verificationKeyHash; }

    public String getProofData() { return proofData; }
    public void setProofData(String proofData) { this.proofData = proofData; }

    public Boolean getProofCompressed() { return proofCompressed; }
    public void setProofCompressed(Boolean proofCompressed) { this.proofCompressed = proofCompressed; }

    public String getCompressionAlgorithm() { return compressionAlgorithm; }
    public void setCompressionAlgorithm(String compressionAlgorithm) { this.compressionAlgorithm = compressionAlgorithm; }

    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }

    public Boolean getHardwareAcceleration() { return hardwareAcceleration; }
    public void setHardwareAcceleration(Boolean hardwareAcceleration) { this.hardwareAcceleration = hardwareAcceleration; }

    public Boolean getGpuAccelerated() { return gpuAccelerated; }
    public void setGpuAccelerated(Boolean gpuAccelerated) { this.gpuAccelerated = gpuAccelerated; }

    public Boolean getFpgaAccelerated() { return fpgaAccelerated; }
    public void setFpgaAccelerated(Boolean fpgaAccelerated) { this.fpgaAccelerated = fpgaAccelerated; }

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

    public Double getPrivacyPreservationScore() { return privacyPreservationScore; }
    public void setPrivacyPreservationScore(Double privacyPreservationScore) { this.privacyPreservationScore = privacyPreservationScore; }

    public Double getIdentityLeakageScore() { return identityLeakageScore; }
    public void setIdentityLeakageScore(Double identityLeakageScore) { this.identityLeakageScore = identityLeakageScore; }

    public Double getAttributeCorrelationScore() { return attributeCorrelationScore; }
    public void setAttributeCorrelationScore(Double attributeCorrelationScore) { this.attributeCorrelationScore = attributeCorrelationScore; }

    public Double getReidentificationRisk() { return reidentificationRisk; }
    public void setReidentificationRisk(Double reidentificationRisk) { this.reidentificationRisk = reidentificationRisk; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }

    public Boolean getGdprCompliant() { return gdprCompliant; }
    public void setGdprCompliant(Boolean gdprCompliant) { this.gdprCompliant = gdprCompliant; }

    public Boolean getCcpaCompliant() { return ccpaCompliant; }
    public void setCcpaCompliant(Boolean ccpaCompliant) { this.ccpaCompliant = ccpaCompliant; }

    public Boolean getHipaaCompliant() { return hipaaCompliant; }
    public void setHipaaCompliant(Boolean hipaaCompliant) { this.hipaaCompliant = hipaaCompliant; }

    public String getComplianceEvidence() { return complianceEvidence; }
    public void setComplianceEvidence(String complianceEvidence) { this.complianceEvidence = complianceEvidence; }

    public String getAuditTrailId() { return auditTrailId; }
    public void setAuditTrailId(String auditTrailId) { this.auditTrailId = auditTrailId; }

    public String getAuditTrailHash() { return auditTrailHash; }
    public void setAuditTrailHash(String auditTrailHash) { this.auditTrailHash = auditTrailHash; }

    public String getProtectionStatus() { return protectionStatus; }
    public void setProtectionStatus(String protectionStatus) { this.protectionStatus = protectionStatus; }

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

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Helper methods
    public boolean isValid() {
        if ("REVOKED".equals(protectionStatus) || "EXPIRED".equals(protectionStatus)) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }
        
        return Boolean.TRUE.equals(verificationResult);
    }

    public boolean isExpired() {
        return validTo != null && LocalDateTime.now().isAfter(validTo);
    }

    public boolean canRetry() {
        return retryCount < maxRetries && !"REVOKED".equals(protectionStatus);
    }

    public double getOverallPrivacyScore() {
        double score = 0.0;
        int factors = 0;
        
        if (privacyPreservationScore != null) {
            score += privacyPreservationScore;
            factors++;
        }
        
        if (identityLeakageScore != null) {
            score += (1.0 - identityLeakageScore); // invert: lower leakage is better
            factors++;
        }
        
        if (attributeCorrelationScore != null) {
            score += (1.0 - attributeCorrelationScore); // invert: lower correlation is better
            factors++;
        }
        
        if (reidentificationRisk != null) {
            score += (1.0 - reidentificationRisk); // invert: lower risk is better
            factors++;
        }
        
        return factors > 0 ? score / factors : 0.0;
    }
}