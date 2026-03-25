package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorSetupResponse {

    private String secret;

    private String qrCodeUrl;

    private String manualEntryKey;

    private List<String> backupCodes;

    private String provisioningUri;

    private Map<String, Object> appInfo;
}
