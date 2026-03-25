package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TwoFactorAuthResponse {
    private Long userId;
    private Boolean enabled;
    private String qrCodeUrl;
    private String secret;
    private List<String> backupCodes;
    private Integer verificationWindow;
    private LocalDateTime lastVerifiedAt;
    private String recoveryEmail;
    private List<String> trustedDevices;
    private Boolean smsEnabled;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public List<String> getBackupCodes() { return backupCodes; }
    public void setBackupCodes(List<String> backupCodes) { this.backupCodes = backupCodes; }
    public Integer getVerificationWindow() { return verificationWindow; }
    public void setVerificationWindow(Integer verificationWindow) { this.verificationWindow = verificationWindow; }
    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }
    public String getRecoveryEmail() { return recoveryEmail; }
    public void setRecoveryEmail(String recoveryEmail) { this.recoveryEmail = recoveryEmail; }
    public List<String> getTrustedDevices() { return trustedDevices; }
    public void setTrustedDevices(List<String> trustedDevices) { this.trustedDevices = trustedDevices; }
    public Boolean getSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(Boolean smsEnabled) { this.smsEnabled = smsEnabled; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
