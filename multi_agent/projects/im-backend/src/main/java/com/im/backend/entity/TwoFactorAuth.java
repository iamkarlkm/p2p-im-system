package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "im_two_factor_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "secret", nullable = false, length = 512)
    private String secret;

    @Column(name = "qr_code_url", length = 512)
    private String qrCodeUrl;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @ElementCollection
    @CollectionTable(name = "im_two_factor_backup_codes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "backup_code")
    private List<String> backupCodes;

    @Column(name = "backup_codes_used")
    private Integer backupCodesUsed;

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "issuer_name")
    private String issuerName;

    @Column(name = "account_name")
    private String accountName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isEnabled == null) isEnabled = false;
        if (isVerified == null) isVerified = false;
        if (backupCodesUsed == null) backupCodesUsed = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
