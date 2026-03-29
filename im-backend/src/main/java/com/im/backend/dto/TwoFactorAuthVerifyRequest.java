package com.im.backend.dto;

public class TwoFactorAuthVerifyRequest {
    private Long userId;
    private String totpCode;
    private String backupCode;
    private String deviceId;
    private Boolean trustDevice;
    private String method;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTotpCode() { return totpCode; }
    public void setTotpCode(String totpCode) { this.totpCode = totpCode; }
    public String getBackupCode() { return backupCode; }
    public void setBackupCode(String backupCode) { this.backupCode = backupCode; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Boolean getTrustDevice() { return trustDevice; }
    public void setTrustDevice(Boolean trustDevice) { this.trustDevice = trustDevice; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
