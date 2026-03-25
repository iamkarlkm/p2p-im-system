package com.im.auth.dto;

import java.util.UUID;

public class BiometricRegistrationRequest {
    private UUID userId;
    private String deviceId;
    private String biometricType; // TOUCH_ID, FACE_ID, WINDOWS_HELLO, FIDO2
    private String publicKey;
    private String keyHandle;
    private String credentialId;
    private String deviceName;
    private String deviceOS;
    private String deviceBrowser;
    private String securityLevel; // BASIC, STANDARD, HIGH
    private String attestationStatement;
    private Boolean backupEligible;
    private Boolean backupState;
    private Integer flags;
    private String rpId; // Relying Party ID
    private String origin;
    private String transports; // "usb,nfc,ble,internal"
    private Boolean userVerificationRequired;
    private Boolean residentKeyRequired;
    private Boolean cloneWarning;
    
    // Getters and setters
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
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getDeviceOS() { return deviceOS; }
    public void setDeviceOS(String deviceOS) { this.deviceOS = deviceOS; }
    
    public String getDeviceBrowser() { return deviceBrowser; }
    public void setDeviceBrowser(String deviceBrowser) { this.deviceBrowser = deviceBrowser; }
    
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
    
    public Boolean getCloneWarning() { return cloneWarning; }
    public void setCloneWarning(Boolean cloneWarning) { this.cloneWarning = cloneWarning; }
}