package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorStatusResponse {

    private Boolean isEnabled;

    private Boolean isVerified;

    private Integer backupCodesRemaining;

    private Long lastVerifiedAt;

    private String issuerName;

    private String accountName;

    private Boolean isRequired;
}
