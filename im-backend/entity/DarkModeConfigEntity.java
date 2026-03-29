package com.im.backend.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 应用内暗黑模式配置实体
 * 存储用户或系统级别的暗黑模式偏好设置
 */
@Entity
@Table(name = "dark_mode_configs", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_theme_mode", columnList = "themeMode"),
    @Index(name = "idx_is_active", columnList = "isActive"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DarkModeConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID (null 表示系统级默认配置)
     */
    @Column(name = "user_id", nullable = true)
    private String userId;

    /**
     * 主题模式枚举
     * LIGHT - 明亮模式
     * DARK - 暗黑模式
     * SYSTEM - 跟随系统设置
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "theme_mode", nullable = false, length = 20)
    private ThemeMode themeMode;

    /**
     * 是否激活此配置
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 配置名称 (自定义名称，可选)
     */
    @Column(name = "config_name", length = 100)
    private String configName;

    /**
     * 主题色 (HEX格式，如 #121212)
     */
    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    /**
     * 背景色 (HEX格式)
     */
    @Column(name = "background_color", length = 20)
    private String backgroundColor;

    /**
     * 文字颜色 (HEX格式)
     */
    @Column(name = "text_color", length = 20)
    private String textColor;

    /**
     * 次要文字颜色 (HEX格式)
     */
    @Column(name = "secondary_text_color", length = 20)
    private String secondaryTextColor;

    /**
     * 强调色 (HEX格式)
     */
    @Column(name = "accent_color", length = 20)
    private String accentColor;

    /**
     * 控件颜色 (HEX格式)
     */
    @Column(name = "control_color", length = 20)
    private String controlColor;

    /**
     * 边框颜色 (HEX格式)
     */
    @Column(name = "border_color", length = 20)
    private String borderColor;

    /**
     * 悬停颜色 (HEX格式)
     */
    @Column(name = "hover_color", length = 20)
    private String hoverColor;

    /**
     * 是否使用系统配色方案
     */
    @Column(name = "use_system_colors")
    private Boolean useSystemColors;

    /**
     * 透明度级别 (0.0-1.0)
     */
    @Column(name = "opacity_level")
    private Double opacityLevel;

    /**
     * 字体大小缩放因子 (0.8-1.5)
     */
    @Column(name = "font_scale_factor")
    private Double fontScaleFactor;

    /**
     * 是否启用高对比度模式
     */
    @Column(name = "high_contrast")
    private Boolean highContrast;

    /**
     * 是否启用减少动画效果
     */
    @Column(name = "reduce_motion")
    private Boolean reduceMotion;

    /**
     * 是否启用深色模式夜间保护 (减少蓝光)
     */
    @Column(name = "night_protection")
    private Boolean nightProtection;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 最后同步时间 (与客户端同步)
     */
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    /**
     * 设备标识 (用于多设备同步)
     */
    @Column(name = "device_id", length = 100)
    private String deviceId;

    /**
     * 平台标识 (DESKTOP/MOBILE/WEB)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 20)
    private Platform platform;

    /**
     * 配置版本 (用于版本控制)
     */
    @Column(name = "config_version", nullable = false)
    private Integer configVersion;

    /**
     * 是否启用自动切换 (根据时间/地理位置)
     */
    @Column(name = "auto_switch_enabled")
    private Boolean autoSwitchEnabled;

    /**
     * 自动切换开始时间 (HH:mm)
     */
    @Column(name = "auto_switch_start", length = 10)
    private String autoSwitchStart;

    /**
     * 自动切换结束时间 (HH:mm)
     */
    @Column(name = "auto_switch_end", length = 10)
    private String autoSwitchEnd;

    /**
     * 元数据 (JSON格式存储额外配置)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.configVersion == null) {
            this.configVersion = 1;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.themeMode == null) {
            this.themeMode = ThemeMode.SYSTEM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 主题模式枚举
     */
    public enum ThemeMode {
        LIGHT,
        DARK,
        SYSTEM,
        CUSTOM
    }

    /**
     * 平台枚举
     */
    public enum Platform {
        DESKTOP,
        MOBILE,
        WEB,
        TABLET
    }
}