package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorVerifyResponse {

    private Boolean success;

    private String token;

    private Integer remainingBackupCodes;

    private String message;

    private Long expiresIn;
}
