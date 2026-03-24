package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户资料实体
 * 管理用户头像、昵称、个性签名等个人信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    private Long userId;

    /** 用户昵称 */
    private String nickname;

    /** 头像URL */
    private String avatarUrl;

    /** 个性签名 */
    private String bio;

    /** 性别: 0-未知, 1-男, 2-女 */
    private Integer gender;

    /** 生日 */
    private LocalDateTime birthday;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 在线状态: ONLINE, AWAY, BUSY, DND, INVISIBLE, OFFLINE */
    private String onlineStatus;

    /** 自定义状态文本 */
    private String statusText;

    /** 国家/地区 */
    private String country;

    /** 城市 */
    private String city;

    /** 语言 */
    private String language;

    /** 时区 */
    private String timezone;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}
