package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorVerifyRequest {

    @NotBlank(message = "Code is required")
    private String code;

    private Boolean isBackupCode;

    private String deviceName;
}
