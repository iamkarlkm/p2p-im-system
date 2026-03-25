package com.im.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "biometric_auth")
public class BiometricAuthEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "biometric_type", nullable = false)
    private String biometricType; // TOUCH_ID, FACE_ID, WINDOWS_HELLO, FIDO2
    
    @Column(name = "public_key", nullable = false, length = 2000)
    private String publicKey;
    
    @Column(name = "key_handle", nullable = false, length = 1000)
    private String keyHandle;
    
    @Column(name = "credential_id", length = 1000)
    private String credentialId;
    
    @Column(name = "counter", nullable = false)
    private Integer counter;
    
    @Column(name = "device_name")
    private String deviceName;
    
    @Column(name = "device_os")
    private String deviceOS;
    
    @Column(name = "device_browser")
    private String deviceBrowser;
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "security_level", nullable = false)
    private String securityLevel; // BASIC, STANDARD, HIGH
    
    @Column(name = "attestation_statement", length = 4000)
    private String attestationStatement;
    
    @Column(name = "backup_eligible", nullable = false)
    private Boolean backupEligible;
    
    @Column(name = "backup_state", nullable = false)
    private Boolean backupState;
    
    @Column(name = "flags", nullable = false)
    private Integer flags;
    
    @Column(name = "rp_id", nullable = false)
    private String rpId; // Relying Party ID
    
    @Column(name = "origin", nullable = false)
    private String origin;
    
    @Column(name = "transports")
    private String transports; // "usb,nfc,ble,internal"
    
    @Column(name = "user_verification_required", nullable = false)
    private Boolean userVerificationRequired;
    
    @Column(name = "resident_key_required", nullable = false)
    private Boolean residentKeyRequired;
    
    @Column(name = "sign_count", nullable = false)
    private Integer signCount;
    
    @Column(name = "clone_warning", nullable = false)
    private Boolean cloneWarning;
    
    public BiometricAuthEntity() {
        this.counter = 0;
        this.isEnabled = true;
        this.securityLevel = "STANDARD";
        this.backupEligible = false;
        this.backupState = false;
        this.flags = 0;
        this.userVerificationRequired = true;
        this.residentKeyRequired = false;
        this.signCount = 0;
        this.cloneWarning = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getBiometricType() { return biometricType; }
    public void setBiometricType(String biometricType) { this.biometricType = biometricType; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getKeyHandle() { return keyHandle; }
    public void setKeyHandle(String keyHandle) { this.keyHandle = keyHandle; }
    
    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }
    
    public Integer getCounter() { return counter; }
    public void setCounter(Integer counter) { this.counter = counter; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getDeviceOS() { return deviceOS; }
    public void setDeviceOS(String deviceOS) { this.deviceOS = deviceOS; }
    
    public String getDeviceBrowser() { return deviceBrowser; }
    public void setDeviceBrowser(String deviceBrowser) { this.deviceBrowser = deviceBrowser; }
    
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public String getAttestationStatement() { return attestationStatement; }
    public void setAttestationStatement(String attestationStatement) { this.attestationStatement = attestationStatement; }
    
    public Boolean getBackupEligible() { return backupEligible; }
    public void setBackupEligible(Boolean backupEligible) { this.backupEligible = backupEligible; }
    
    public Boolean getBackupState() { return backupState; }
    public void setBackupState(Boolean backupState) { this.backupState = backupState; }
    
    public Integer getFlags() { return flags; }
    public void setFlags(Integer flags) { this.flags = flags; }
    
    public String getRpId() { return rpId; }
    public void setRpId(String rpId) { this.rpId = rpId; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getTransports() { return transports; }
    public void setTransports(String transports) { this.transports = transports; }
    
    public Boolean getUserVerificationRequired() { return userVerificationRequired; }
    public void setUserVerificationRequired(Boolean userVerificationRequired) { this.userVerificationRequired = userVerificationRequired; }
    
    public Boolean getResidentKeyRequired() { return residentKeyRequired; }
    public void setResidentKeyRequired(Boolean residentKeyRequired) { this.residentKeyRequired = residentKeyRequired; }
    
    public Integer getSignCount() { return signCount; }
    public void setSignCount(Integer signCount) { this.signCount = signCount; }
    
    public Boolean getCloneWarning() { return cloneWarning; }
    public void setCloneWarning(Boolean cloneWarning) { this.cloneWarning = cloneWarning; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}