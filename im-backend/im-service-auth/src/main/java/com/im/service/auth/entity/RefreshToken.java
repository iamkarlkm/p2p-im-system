package com.im.service.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 刷新 Token 实体类
 * 
 * 功能特性：
 * 1. 存储 Refresh Token 信息
 * 2. 支持多设备登录管理
 * 3. Token 使用状态跟踪（防止重放攻击）
 * 4. 过期时间自动管理
 * 
 * 数据表: im_refresh_token
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_refresh_token")
public class RefreshToken {

    /**
     * Token ID（JTI - JWT ID）
     * 从 JWT Token 的 jti 声明中提取
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * Refresh Token 字符串
     * 注意：实际存储的是完整 Token，生产环境建议加密存储
     */
    private String token;

    /**
     * 设备ID
     * 用于区分不同设备的登录
     */
    private String deviceId;

    /**
     * 设备类型（可选）
     * 例如：WEB, MOBILE_IOS, MOBILE_ANDROID, DESKTOP
     */
    private String deviceType;

    /**
     * 设备名称（可选）
     * 例如：iPhone 13, Chrome on Windows
     */
    private String deviceName;

    /**
     * IP 地址（可选）
     */
    private String ipAddress;

    /**
     * Token 过期时间
     */
    private Instant expiryDate;

    /**
     * 是否已使用
     * Refresh Token 只能使用一次，使用后标记为 true
     */
    private Boolean used;

    /**
     * 是否已撤销
     * 用户主动登出或安全原因撤销
     */
    private Boolean revoked;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== 业务方法 ====================

    /**
     * 检查 Token 是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    /**
     * 检查 Token 是否有效
     * 有效条件：未过期、未使用、未撤销
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return !isExpired() && !used && !revoked;
    }

    /**
     * 标记 Token 为已使用
     */
    public void markAsUsed() {
        this.used = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 撤销 Token
     */
    public void revoke() {
        this.revoked = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 获取剩余有效时间（秒）
     *
     * @return 剩余秒数，如果已过期则返回 0
     */
    public long getRemainingTimeSeconds() {
        long remaining = expiryDate.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(remaining, 0);
    }

    /**
     * 获取 Token 简要信息（用于日志）
     *
     * @return 简要信息字符串
     */
    public String getSummary() {
        return String.format("RefreshToken{id='%s', userId=%d, deviceId='%s', expired=%s, used=%s}",
                id.substring(0, Math.min(8, id.length())),
                userId,
                deviceId,
                isExpired(),
                used);
    }
}
