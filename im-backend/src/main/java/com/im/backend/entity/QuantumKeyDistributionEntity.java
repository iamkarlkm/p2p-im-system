package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 量子密钥分发实体
 * 记录 QKD 会话、密钥生成和分发过程
 */
@Entity
@Table(name = "quantum_key_distribution", indexes = {
    @Index(name = "idx_session_id", columnList = "sessionId"),
    @Index(name = "idx_sender_id", columnList = "senderId"),
    @Index(name = "idx_receiver_id", columnList = "receiverId"),
    @Index(name = "idx_protocol", columnList = "protocol"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class QuantumKeyDistributionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "session_id", nullable = false, unique = true, length = 128)
    private String sessionId;
    
    @Column(name = "sender_id", nullable = false, length = 128)
    private String senderId;
    
    @Column(name = "sender_device_id", length = 128)
    private String senderDeviceId;
    
    @Column(name = "receiver_id", nullable = false, length = 128)
    private String receiverId;
    
    @Column(name = "receiver_device_id", length = 128)
    private String receiverDeviceId;
    
    @Column(name = "channel_id", length = 128)
    private String channelId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "protocol", nullable = false, length = 30)
    private QkdProtocol protocol;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QkdStatus status;
    
    @Column(name = "wavelength_nm")
    private Double wavelengthNm;
    
    @Column(name = "pulse_rate_mhz")
    private Double pulseRateMhz;
    
    @Column(name = "mean_photon_number")
    private Double meanPhotonNumber;
    
    @Column(name = "quantum_bit_error_rate")
    private Double quantumBitErrorRate;
    
    @Column(name = "sifted_key_bits")
    private Long siftedKeyBits;
    
    @Column(name = "sifted_key_rate_bps")
    private Double siftedKeyRateBps;
    
    @Column(name = "reconciled_key_bits")
    private Long reconciledKeyBits;
    
    @Column(name = "reconciliation_efficiency")
    private Double reconciliationEfficiency;
    
    @Column(name = "privacy_amplification_factor")
    private Double privacyAmplificationFactor;
    
    @Column(name = "final_key_bits")
    private Long finalKeyBits;
    
    @Column(name = "final_key_rate_bps")
    private Double finalKeyRateBps;
    
    @Column(name = "final_key_hash", length = 256)
    private String finalKeyHash;
    
    @Column(name = "key_encryption_algorithm", length = 50)
    private String keyEncryptionAlgorithm;
    
    @Column(name = "key_storage_location", length = 256)
    private String keyStorageLocation;
    
    @Column(name = "key_lifetime_hours")
    private Integer keyLifetimeHours;
    
    @Column(name = "key_usage_count")
    private Integer keyUsageCount;
    
    @Column(name = "max_key_usage_count")
    private Integer maxKeyUsageCount;
    
    @Column(name = "channel_loss_db")
    private Double channelLossDb;
    
    @Column(name = "channel_distance_km")
    private Double channelDistanceKm;
    
    @Column(name = "channel_type", length = 30)
    private String channelType;
    
    @Column(name = "environmental_temperature")
    private Double environmentalTemperature;
    
    @Column(name = "environmental_humidity")
    private Double environmentalHumidity;
    
    @Column(name = "eavesdropping_detection_enabled")
    private Boolean eavesdroppingDetectionEnabled;
    
    @Column(name = "eavesdropping_detected")
    private Boolean eavesdroppingDetected;
    
    @Column(name = "eavesdropping_detection_time")
    private LocalDateTime eavesdroppingDetectionTime;
    
    @Column(name = "eavesdropping_detection_method", length = 100)
    private String eavesdroppingDetectionMethod;
    
    @Column(name = "eavesdropping_probability")
    private Double eavesdroppingProbability;
    
    @Column(name = "security_parameter")
    private Double securityParameter;
    
    @Column(name = "fidelity")
    private Double fidelity;
    
    @Column(name = "visibility")
    private Double visibility;
    
    @Column(name = "quantum_state_preparation_error")
    private Double quantumStatePreparationError;
    
    @Column(name = "quantum_state_measurement_error")
    private Double quantumStateMeasurementError;
    
    @Column(name = "dark_count_rate")
    private Double darkCountRate;
    
    @Column(name = "afterpulse_probability")
    private Double afterpulseProbability;
    
    @Column(name = "detector_efficiency")
    private Double detectorEfficiency;
    
    @Column(name = "timing_synchronization_accuracy_ns")
    private Double timingSynchronizationAccuracyNs;
    
    @Column(name = "phase_stability_rad")
    private Double phaseStabilityRad;
    
    @Column(name = "polarization_extinction_ratio_db")
    private Double polarizationExtinctionRatioDb;
    
    @Column(name = "raw_key_data", columnDefinition = "TEXT")
    private String rawKeyData;
    
    @Column(name = "sifted_key_data", columnDefinition = "TEXT")
    private String siftedKeyData;
    
    @Column(name = "reconciled_key_data", columnDefinition = "TEXT")
    private String reconciledKeyData;
    
    @Column(name = "privacy_amplification_seed", length = 256)
    private String privacyAmplificationSeed;
    
    @Column(name = "authentication_tag", length = 256)
    private String authenticationTag;
    
    @Column(name = "authentication_method", length = 50)
    private String authenticationMethod;
    
    @Column(name = "error_correction_syndromes", columnDefinition = "TEXT")
    private String errorCorrectionSyndromes;
    
    @Column(name = "leakage_estimation_bits")
    private Long leakageEstimationBits;
    
    @Column(name = "composable_security_parameter")
    private Double composableSecurityParameter;
    
    @Column(name = "min_entropy")
    private Double minEntropy;
    
    @Column(name = "smooth_min_entropy")
    private Double smoothMinEntropy;
    
    @Column(name = "trace_distance")
    private Double traceDistance;
    
    @Column(name = "failure_probability")
    private Double failureProbability;
    
    @Column(name = "secrecy_parameter")
    private Double secrecyParameter;
    
    @Column(name = "correctness_parameter")
    private Double correctnessParameter;
    
    @Column(name = "protocol_rounds")
    private Integer protocolRounds;
    
    @Column(name = "successful_rounds")
    private Integer successfulRounds;
    
    @Column(name = "aborted_rounds")
    private Integer abortedRounds;
    
    @Column(name = "retry_count")
    private Integer retryCount;
    
    @Column(name = "max_retry_count")
    private Integer maxRetryCount;
    
    @Column(name = "session_duration_ms")
    private Long sessionDurationMs;
    
    @Column(name = "key_generation_time_ms")
    private Long keyGenerationTimeMs;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;
    
    @Column(name = "performance_metrics", columnDefinition = "JSON")
    private String performanceMetrics;
    
    @Column(name = "diagnostic_data", columnDefinition = "JSON")
    private String diagnosticData;
    
    @Column(name = "calibration_data", columnDefinition = "JSON")
    private String calibrationData;
    
    @Column(name = "additional_metadata", columnDefinition = "JSON")
    private String additionalMetadata;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
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
        if (status == null) {
            status = QkdStatus.INITIATED;
        }
        if (eavesdroppingDetectionEnabled == null) {
            eavesdroppingDetectionEnabled = true;
        }
        if (eavesdroppingDetected == null) {
            eavesdroppingDetected = false;
        }
        if (maxKeyUsageCount == null) {
            maxKeyUsageCount = 1000;
        }
        if (maxRetryCount == null) {
            maxRetryCount = 3;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public QuantumKeyDistributionEntity() {}
    
    public QuantumKeyDistributionEntity(String sessionId, String senderId, String receiverId, QkdProtocol protocol) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.protocol = protocol;
        this.status = QkdStatus.INITIATED;
        this.startedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getSenderDeviceId() { return senderDeviceId; }
    public void setSenderDeviceId(String senderDeviceId) { this.senderDeviceId = senderDeviceId; }
    
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    
    public String getReceiverDeviceId() { return receiverDeviceId; }
    public void setReceiverDeviceId(String receiverDeviceId) { this.receiverDeviceId = receiverDeviceId; }
    
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    
    public QkdProtocol getProtocol() { return protocol; }
    public void setProtocol(QkdProtocol protocol) { this.protocol = protocol; }
    
    public QkdStatus getStatus() { return status; }
    public void setStatus(QkdStatus status) { this.status = status; }
    
    public Double getWavelengthNm() { return wavelengthNm; }
    public void setWavelengthNm(Double wavelengthNm) { this.wavelengthNm = wavelengthNm; }
    
    public Double getPulseRateMhz() { return pulseRateMhz; }
    public void setPulseRateMhz(Double pulseRateMhz) { this.pulseRateMhz = pulseRateMhz; }
    
    public Double getMeanPhotonNumber() { return meanPhotonNumber; }
    public void setMeanPhotonNumber(Double meanPhotonNumber) { this.meanPhotonNumber = meanPhotonNumber; }
    
    public Double getQuantumBitErrorRate() { return quantumBitErrorRate; }
    public void setQuantumBitErrorRate(Double quantumBitErrorRate) { this.quantumBitErrorRate = quantumBitErrorRate; }
    
    public Long getSiftedKeyBits() { return siftedKeyBits; }
    public void setSiftedKeyBits(Long siftedKeyBits) { this.siftedKeyBits = siftedKeyBits; }
    
    public Double getSiftedKeyRateBps() { return siftedKeyRateBps; }
    public void setSiftedKeyRateBps(Double siftedKeyRateBps) { this.siftedKeyRateBps = siftedKeyRateBps; }
    
    public Long getReconciledKeyBits() { return reconciledKeyBits; }
    public void setReconciledKeyBits(Long reconciledKeyBits) { this.reconciledKeyBits = reconciledKeyBits; }
    
    public Double getReconciliationEfficiency() { return reconciliationEfficiency; }
    public void setReconciliationEfficiency(Double reconciliationEfficiency) { this.reconciliationEfficiency = reconciliationEfficiency; }
    
    public Double getPrivacyAmplificationFactor() { return privacyAmplificationFactor; }
    public void setPrivacyAmplificationFactor(Double privacyAmplificationFactor) { this.privacyAmplificationFactor = privacyAmplificationFactor; }
    
    public Long getFinalKeyBits() { return finalKeyBits; }
    public void setFinalKeyBits(Long finalKeyBits) { this.finalKeyBits = finalKeyBits; }
    
    public Double getFinalKeyRateBps() { return finalKeyRateBps; }
    public void setFinalKeyRateBps(Double finalKeyRateBps) { this.finalKeyRateBps = finalKeyRateBps; }
    
    public String getFinalKeyHash() { return finalKeyHash; }
    public void setFinalKeyHash(String finalKeyHash) { this.finalKeyHash = finalKeyHash; }
    
    public String getKeyEncryptionAlgorithm() { return keyEncryptionAlgorithm; }
    public void setKeyEncryptionAlgorithm(String keyEncryptionAlgorithm) { this.keyEncryptionAlgorithm = keyEncryptionAlgorithm; }
    
    public String getKeyStorageLocation() { return keyStorageLocation; }
    public void setKeyStorageLocation(String keyStorageLocation) { this.keyStorageLocation = keyStorageLocation; }
    
    public Integer getKeyLifetimeHours() { return keyLifetimeHours; }
    public void setKeyLifetimeHours(Integer keyLifetimeHours) { this.keyLifetimeHours = keyLifetimeHours; }
    
    public Integer getKeyUsageCount() { return keyUsageCount; }
    public void setKeyUsageCount(Integer keyUsageCount) { this.keyUsageCount = keyUsageCount; }
    
    public Integer getMaxKeyUsageCount() { return maxKeyUsageCount; }
    public void setMaxKeyUsageCount(Integer maxKeyUsageCount) { this.maxKeyUsageCount = maxKeyUsageCount; }
    
    public Double getChannelLossDb() { return channelLossDb; }
    public void setChannelLossDb(Double channelLossDb) { this.channelLossDb = channelLossDb; }
    
    public Double getChannelDistanceKm() { return channelDistanceKm; }
    public void setChannelDistanceKm(Double channelDistanceKm) { this.channelDistanceKm = channelDistanceKm; }
    
    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }
    
    public Double getEnvironmentalTemperature() { return environmentalTemperature; }
    public void setEnvironmentalTemperature(Double environmentalTemperature) { this.environmentalTemperature = environmentalTemperature; }
    
    public Double getEnvironmentalHumidity() { return environmentalHumidity; }
    public void setEnvironmentalHumidity(Double environmentalHumidity) { this.environmentalHumidity = environmentalHumidity; }
    
    public Boolean getEavesdroppingDetectionEnabled() { return eavesdroppingDetectionEnabled; }
    public void setEavesdroppingDetectionEnabled(Boolean eavesdroppingDetectionEnabled) { this.eavesdroppingDetectionEnabled = eavesdroppingDetectionEnabled; }
    
    public Boolean getEavesdroppingDetected() { return eavesdroppingDetected; }
    public void setEavesdroppingDetected(Boolean eavesdroppingDetected) { this.eavesdroppingDetected = eavesdroppingDetected; }
    
    public LocalDateTime getEavesdroppingDetectionTime() { return eavesdroppingDetectionTime; }
    public void setEavesdroppingDetectionTime(LocalDateTime eavesdroppingDetectionTime) { this.eavesdroppingDetectionTime = eavesdroppingDetectionTime; }
    
    public String getEavesdroppingDetectionMethod() { return eavesdroppingDetectionMethod; }
    public void setEavesdroppingDetectionMethod(String eavesdroppingDetectionMethod) { this.eavesdroppingDetectionMethod = eavesdroppingDetectionMethod; }
    
    public Double getEavesdroppingProbability() { return eavesdroppingProbability; }
    public void setEavesdroppingProbability(Double eavesdroppingProbability) { this.eavesdroppingProbability = eavesdroppingProbability; }
    
    public Double getSecurityParameter() { return securityParameter; }
    public void setSecurityParameter(Double securityParameter) { this.securityParameter = securityParameter; }
    
    public Double getFidelity() { return fidelity; }
    public void setFidelity(Double fidelity) { this.fidelity = fidelity; }
    
    public Double getVisibility() { return visibility; }
    public void setVisibility(Double visibility) { this.visibility = visibility; }
    
    public Double getQuantumStatePreparationError() { return quantumStatePreparationError; }
    public void setQuantumStatePreparationError(Double quantumStatePreparationError) { this.quantumStatePreparationError = quantumStatePreparationError; }
    
    public Double getQuantumStateMeasurementError() { return quantumStateMeasurementError; }
    public void setQuantumStateMeasurementError(Double quantumStateMeasurementError) { this.quantumStateMeasurementError = quantumStateMeasurementError; }
    
    public Double getDarkCountRate() { return darkCountRate; }
    public void setDarkCountRate(Double darkCountRate) { this.darkCountRate = darkCountRate; }
    
    public Double getAfterpulseProbability() { return afterpulseProbability; }
    public void setAfterpulseProbability(Double afterpulseProbability) { this.afterpulseProbability = afterpulseProbability; }
    
    public Double getDetectorEfficiency() { return detectorEfficiency; }
    public void setDetectorEfficiency(Double detectorEfficiency) { this.detectorEfficiency = detectorEfficiency; }
    
    public Double getTimingSynchronizationAccuracyNs() { return timingSynchronizationAccuracyNs; }
    public void setTimingSynchronizationAccuracyNs(Double timingSynchronizationAccuracyNs) { this.timingSynchronizationAccuracyNs = timingSynchronizationAccuracyNs; }
    
    public Double getPhaseStabilityRad() { return phaseStabilityRad; }
    public void setPhaseStabilityRad(Double phaseStabilityRad) { this.phaseStabilityRad = phaseStabilityRad; }
    
    public Double getPolarizationExtinctionRatioDb() { return polarizationExtinctionRatioDb; }
    public void setPolarizationExtinctionRatioDb(Double polarizationExtinctionRatioDb) { this.polarizationExtinctionRatioDb = polarizationExtinctionRatioDb; }
    
    public String getRawKeyData() { return rawKeyData; }
    public void setRawKeyData(String rawKeyData) { this.rawKeyData = rawKeyData; }
    
    public String getSiftedKeyData() { return siftedKeyData; }
    public void setSiftedKeyData(String siftedKeyData) { this.siftedKeyData = siftedKeyData; }
    
    public String getReconciledKeyData() { return reconciledKeyData; }
    public void setReconciledKeyData(String reconciledKeyData) { this.reconciledKeyData = reconciledKeyData; }
    
    public String getPrivacyAmplificationSeed() { return privacyAmplificationSeed; }
    public void setPrivacyAmplificationSeed(String privacyAmplificationSeed) { this.privacyAmplificationSeed = privacyAmplificationSeed; }
    
    public String getAuthenticationTag() { return authenticationTag; }
    public void setAuthenticationTag(String authenticationTag) { this.authenticationTag = authenticationTag; }
    
    public String getAuthenticationMethod() { return authenticationMethod; }
    public void setAuthenticationMethod(String authenticationMethod) { this.authenticationMethod = authenticationMethod; }
    
    public String getErrorCorrectionSyndromes() { return errorCorrectionSyndromes; }
    public void setErrorCorrectionSyndromes(String errorCorrectionSyndromes) { this.errorCorrectionSyndromes = errorCorrectionSyndromes; }
    
    public Long getLeakageEstimationBits() { return leakageEstimationBits; }
    public void setLeakageEstimationBits(Long leakageEstimationBits) { this.leakageEstimationBits = leakageEstimationBits; }
    
    public Double getComposableSecurityParameter() { return composableSecurityParameter; }
    public void setComposableSecurityParameter(Double composableSecurityParameter) { this.composableSecurityParameter = composableSecurityParameter; }
    
    public Double getMinEntropy() { return minEntropy; }
    public void setMinEntropy(Double minEntropy) { this.minEntropy = minEntropy; }
    
    public Double getSmoothMinEntropy() { return smoothMinEntropy; }
    public void setSmoothMinEntropy(Double smoothMinEntropy) { this.smoothMinEntropy = smoothMinEntropy; }
    
    public Double getTraceDistance() { return traceDistance; }
    public void setTraceDistance(Double traceDistance) { this.traceDistance = traceDistance; }
    
    public Double getFailureProbability() { return failureProbability; }
    public void setFailureProbability(Double failureProbability) { this.failureProbability = failureProbability; }
    
    public Double getSecrecyParameter() { return secrecyParameter; }
    public void setSecrecyParameter(Double secrecyParameter) { this.secrecyParameter = secrecyParameter; }
    
    public Double getCorrectnessParameter() { return correctnessParameter; }
    public void setCorrectnessParameter(Double correctnessParameter) { this.correctnessParameter = correctnessParameter; }
    
    public Integer getProtocolRounds() { return protocolRounds; }
    public void setProtocolRounds(Integer protocolRounds) { this.protocolRounds = protocolRounds; }
    
    public Integer getSuccessfulRounds() { return successfulRounds; }
    public void setSuccessfulRounds(Integer successfulRounds) { this.successfulRounds = successfulRounds; }
    
    public Integer getAbortedRounds() { return abortedRounds; }
    public void setAbortedRounds(Integer abortedRounds) { this.abortedRounds = abortedRounds; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(Integer maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    
    public Long getSessionDurationMs() { return sessionDurationMs; }
    public void setSessionDurationMs(Long sessionDurationMs) { this.sessionDurationMs = sessionDurationMs; }
    
    public Long getKeyGenerationTimeMs() { return keyGenerationTimeMs; }
    public void setKeyGenerationTimeMs(Long keyGenerationTimeMs) { this.keyGenerationTimeMs = keyGenerationTimeMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }
    
    public String getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(String performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public String getDiagnosticData() { return diagnosticData; }
    public void setDiagnosticData(String diagnosticData) { this.diagnosticData = diagnosticData; }
    
    public String getCalibrationData() { return calibrationData; }
    public void setCalibrationData(String calibrationData) { this.calibrationData = calibrationData; }
    
    public String getAdditionalMetadata() { return additionalMetadata; }
    public void setAdditionalMetadata(String additionalMetadata) { this.additionalMetadata = additionalMetadata; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    // 枚举类型
    public enum QkdProtocol {
        BB84,
        E91,
        B92,
        SIX_STATE,
        CV_QKD,
        MDI_QKD,
        TF_QKD,
        CUSTOM
    }
    
    public enum QkdStatus {
        INITIATED,
        QUANTUM_TRANSMISSION,
        SIFTING,
        ERROR_ESTIMATION,
        RECONCILIATION,
        PRIVACY_AMPLIFICATION,
        VERIFICATION,
        COMPLETED,
        ABORTED,
        FAILED,
        EAVESDROPPING_DETECTED
    }
    
    // 辅助方法
    public boolean isCompleted() {
        return status == QkdStatus.COMPLETED;
    }
    
    public boolean isAborted() {
        return status == QkdStatus.ABORTED || status == QkdStatus.FAILED;
    }
    
    public boolean hasEavesdropping() {
        return eavesdroppingDetected != null && eavesdroppingDetected;
    }
    
    public boolean isKeyExpired() {
        if (completedAt == null || keyLifetimeHours == null) {
            return false;
        }
        LocalDateTime expiryTime = completedAt.plusHours(keyLifetimeHours);
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    public boolean canUseKey() {
        return isCompleted() && !hasEavesdropping() && !isKeyExpired() && 
               (keyUsageCount == null || keyUsageCount < maxKeyUsageCount);
    }
    
    public boolean needsRetry() {
        return isAborted() && (retryCount == null || retryCount < maxRetryCount);
    }
    
    // 计算密钥质量评分
    public double calculateKeyQualityScore() {
        double score = 100.0;
        
        // 基于 QBER 扣分
        if (quantumBitErrorRate != null) {
            if (quantumBitErrorRate > 0.11) {
                score -= 50.0; // 超过 11% 严重扣分
            } else if (quantumBitErrorRate > 0.05) {
                score -= 20.0; // 超过 5% 中等扣分
            } else {
                score -= quantumBitErrorRate * 100; // 正常扣分
            }
        }
        
        // 基于窃听检测扣分
        if (hasEavesdropping()) {
            score = 0.0; // 检测到窃听，密钥不可用
        }
        
        // 基于距离扣分
        if (channelDistanceKm != null && channelDistanceKm > 100) {
            score -= Math.min((channelDistanceKm - 100) * 0.1, 20.0);
        }
        
        // 基于协议轮次成功率加分
        if (protocolRounds != null && protocolRounds > 0 && successfulRounds != null) {
            double successRate = (double) successfulRounds / protocolRounds;
            score += successRate * 10;
        }
        
        return Math.max(0.0, Math.min(100.0, score));
    }
    
    // 生成 QKD 会话报告
    public String generateQkdReport() {
        StringBuilder report = new StringBuilder();
        report.append("量子密钥分发会话报告\n");
        report.append("========================\n");
        report.append("会话 ID: ").append(sessionId).append("\n");
        report.append("发送方: ").append(senderId).append("\n");
        report.append("接收方: ").append(receiverId).append("\n");
        report.append("协议: ").append(protocol).append("\n");
        report.append("状态: ").append(status).append("\n");
        
        if (quantumBitErrorRate != null) {
            report.append("量子误码率 (QBER): ").append(String.format("%.4f%%", quantumBitErrorRate * 100)).append("\n");
        }
        
        if (siftedKeyBits != null) {
            report.append("筛选后密钥: ").append(siftedKeyBits).append(" bits\n");
        }
        
        if (reconciledKeyBits != null) {
            report.append("纠错后密钥: ").append(reconciledKeyBits).append(" bits\n");
        }
        
        if (finalKeyBits != null) {
            report.append("最终密钥: ").append(finalKeyBits).append(" bits\n");
        }
        
        if (finalKeyRateBps != null) {
            report.append("最终密钥率: ").append(String.format("%.2f bps", finalKeyRateBps)).append("\n");
        }
        
        if (channelDistanceKm != null) {
            report.append("信道距离: ").append(channelDistanceKm).append(" km\n");
        }
        
        if (hasEavesdropping()) {
            report.append("⚠️ 警告：检测到窃听！\n");
            report.append("窃听检测时间: ").append(eavesdroppingDetectionTime).append("\n");
            report.append("窃听检测方法: ").append(eavesdroppingDetectionMethod).append("\n");
            report.append("窃听概率: ").append(String.format("%.2f%%", eavesdroppingProbability * 100)).append("\n");
        }
        
        report.append("密钥质量评分: ").append(String.format("%.1f/100", calculateKeyQualityScore())).append("\n");
        
        if (isCompleted()) {
            report.append("✅ 密钥分发成功\n");
            report.append("完成时间: ").append(completedAt).append("\n");
        } else if (isAborted()) {
            report.append("❌ 密钥分发失败\n");
            if (errorMessage != null) {
                report.append("错误信息: ").append(errorMessage).append("\n");
            }
        }
        
        return report.toString();
    }
}