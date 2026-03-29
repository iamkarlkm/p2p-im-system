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
public class TwoFactorSetupRequest {

    @NotBlank(message = "Password is required")
    private String password;

    private String issuerName;

    private String accountName;
}
