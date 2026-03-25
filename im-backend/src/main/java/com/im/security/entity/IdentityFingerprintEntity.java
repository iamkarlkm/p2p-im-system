package com.im.security.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 身份指纹验证实体
 * 用于安全码验证、二维码扫描、密钥变更通知等身份验证功能
 */
@Entity
@Table(name = "identity_fingerprints", indexes = {
    @Index(name = "idx_user_fingerprint", columnList = "user_id, fingerprint_type"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
public class IdentityFingerprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fingerprint_type", nullable = false, length = 50)
    private String fingerprintType; // SAFETY_CODE, QR_SCAN, KEY_CHANGE, DEVICE_FINGERPRINT

    @Column(name = "fingerprint_value", nullable = false, length = 500)
    private String fingerprintValue; // 安全码哈希或二维码数据

    @Column(name = "verification_code", length = 10)
    private String verificationCode; // 6位验证码

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // 验证码过期时间

    @Column(name = "verification_attempts", nullable = false)
    private Integer verificationAttempts = 0;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 5;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FingerprintStatus status = FingerprintStatus.PENDING;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "device_id", length = 100)
    private String deviceId; // 发起验证的设备ID

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, WEB

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    @Column(name = "qr_image_url", length = 500)
    private String qrImageUrl; // 二维码图片URL

    @Column(name = "related_fingerprint_id")
    private Long relatedFingerprintId; // 关联的指纹ID（如密钥变更时）

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson; // 额外的元数据JSON

    @Column(name = "audit_log", columnDefinition = "TEXT")
    private String auditLog; // 审计日志

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 指纹状态枚举
     */
    public enum FingerprintStatus {
        PENDING,       // 待验证
        VERIFIED,      // 已验证
        EXPIRED,       // 已过期
        FAILED,        // 验证失败
        REVOKED,       // 已撤销
        LOCKED         // 已锁定（尝试次数过多）
    }

    /**
     * 指纹类型枚举
     */
    public enum FingerprintType {
        SAFETY_CODE,      // 安全码验证
        QR_SCAN,          // 二维码扫描
        KEY_CHANGE,       // 密钥变更
        DEVICE_FINGERPRINT, // 设备指纹
        BIOMETRIC,        // 生物识别
        BACKUP_CODE       // 备份代码
    }

    /**
     * 验证方法枚举
     */
    public enum VerificationMethod {
        CODE_VERIFICATION, // 验证码验证
        QR_SCAN,          // 二维码扫描
        PUSH_NOTIFICATION, // 推送通知
        EMAIL_LINK,       // 邮件链接
        SMS_CODE,         // 短信验证码
        MANUAL_APPROVAL   // 手动批准
    }
}