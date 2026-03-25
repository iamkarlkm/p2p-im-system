package com.im.backend.dto;

public class TwoFactorAuthSetupRequest {
    private Long userId;
    private String currentPassword;
    private String recoveryEmail;
    private Integer verificationWindow;
    private String deviceId;
    private Boolean trustDevice;
    private Boolean enableSms;
    private String phoneNumber;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getRecoveryEmail() { return recoveryEmail; }
    public void setRecoveryEmail(String recoveryEmail) { this.recoveryEmail = recoveryEmail; }
    public Integer getVerificationWindow() { return verificationWindow; }
    public void setVerificationWindow(Integer verificationWindow) { this.verificationWindow = verificationWindow; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Boolean getTrustDevice() { return trustDevice; }
    public void setTrustDevice(Boolean trustDevice) { this.trustDevice = trustDevice; }
    public Boolean getEnableSms() { return enableSms; }
    public void setEnableSms(Boolean enableSms) { this.enableSms = enableSms; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
