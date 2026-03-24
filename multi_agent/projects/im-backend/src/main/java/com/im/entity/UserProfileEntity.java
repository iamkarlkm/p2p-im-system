package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户资料实体
 * 存储用户的个人资料信息：头像、昵称、签名、在线状态、隐私设置
 */
@Entity
@Table(name = "im_user_profile", indexes = {
    @Index(name = "idx_user_id", columnList = "userId", unique = true),
    @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户唯一标识 */
    @Column(nullable = false, unique = true, length = 64)
    private String userId;

    /** 昵称 */
    @Column(length = 50)
    private String nickname;

    /** 真实姓名 */
    @Column(length = 100)
    private String realName;

    /** 头像 URL */
    @Column(length = 500)
    private String avatarUrl;

    /** 头像缩略图 URL */
    @Column(length = 500)
    private String avatarThumbnailUrl;

    /** 个性签名 */
    @Column(length = 200)
    private String bio;

    /** 性别: 0-未知, 1-男, 2-女 */
    @Column(nullable = false)
    @Builder.Default
    private Integer gender = 0;

    /** 生日 */
    @Column(length = 10)
    private String birthday;

    /** 国家/地区 */
    @Column(length = 100)
    private String country;

    /** 省份/州 */
    @Column(length = 100)
    private String province;

    /** 城市 */
    @Column(length = 100)
    private String city;

    /** 语言 */
    @Column(length = 20)
    @Builder.Default
    private String language = "zh-CN";

    /** 时区 */
    @Column(length = 50)
    @Builder.Default
    private String timezone = "Asia/Shanghai";

    /** 个人网站 */
    @Column(length = 255)
    private String website;

    /** 邮箱 */
    @Column(length = 255)
    private String email;

    /** 手机号 */
    @Column(length = 20)
    private String phone;

    // ============ 隐私设置 ============

    /** 在线状态可见性: public, friends, private */
    @Column(length = 20)
    @Builder.Default
    private String onlineStatusVisibility = "public";

    /** 最后上线时间可见性 */
    @Column(length = 20)
    @Builder.Default
    private String lastSeenVisibility = "public";

    /** 头像可见性 */
    @Column(length = 20)
    @Builder.Default
    private String avatarVisibility = "public";

    /** 个人资料可见性 */
    @Column(length = 20)
    @Builder.Default
    private String profileVisibility = "public";

    /** 允许被搜索方式: all, phone, id */
    @Column(length = 20)
    @Builder.Default
    private String searchableBy = "all";

    /** 允许添加好友: everyone, requires_approval, nobody */
    @Column(length = 30)
    @Builder.Default
    private String friendRequestPolicy = "everyone";

    /** 是否显示已读回执 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean readReceiptEnabled = true;

    /** 是否显示在线状态 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean showOnlineStatus = true;

    /** 是否显示打字状态 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean showTypingStatus = true;

    // ============ 时间戳 ============

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
