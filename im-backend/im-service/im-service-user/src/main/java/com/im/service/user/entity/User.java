package com.im.service.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体 - 即时通讯系统用户核心实体
 * 对应数据库表: im_user
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // ========== 基础账号信息 ==========

    /**
     * 用户名 - 唯一标识，用于登录
     */
    @TableField("username")
    private String username;

    /**
     * 密码哈希 - BCrypt加密
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 用户昵称 - 显示名称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 手机号 - 可用于登录
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱 - 可用于登录
     */
    @TableField("email")
    private String email;

    /**
     * 账号类型: NORMAL(普通用户), SERVICE(服务号), OFFICIAL(官方账号), BOT(机器人)
     */
    @TableField("user_type")
    private String userType = "NORMAL";

    /**
     * 账号状态: 1-正常, 2-禁用, 3-锁定, 4-未激活
     */
    @TableField("status")
    private Integer status = 1;

    // ========== 个人详细信息 ==========

    /**
     * 性别: MALE(男), FEMALE(女), UNKNOWN(保密)
     */
    @TableField("gender")
    private String gender = "UNKNOWN";

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDateTime birthday;

    /**
     * 个性签名
     */
    @TableField("signature")
    private String signature;

    /**
     * 所在地区
     */
    @TableField("location")
    private String location;

    /**
     * 用户标签 - JSON数组格式
     */
    @TableField("tags")
    private String tags;

    // ========== 在线状态 ==========

    /**
     * 在线状态: ONLINE(在线), AWAY(离开), BUSY(忙碌), OFFLINE(离线), INVISIBLE(隐身)
     */
    @TableField("online_status")
    private String onlineStatus = "OFFLINE";

    /**
     * 最后在线时间
     */
    @TableField("last_online_at")
    private LocalDateTime lastOnlineAt;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 最后登录设备
     */
    @TableField("last_login_device")
    private String lastLoginDevice;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    // ========== 隐私设置 ==========

    /**
     * 是否允许被搜索: 0-不允许, 1-允许
     */
    @TableField("allow_search")
    private Boolean allowSearch = true;

    /**
     * 添加好友验证方式: ANYONE(任何人), VERIFY(需要验证), NOBODY(不允许添加)
     */
    @TableField("add_friend_permission")
    private String addFriendPermission = "VERIFY";

    /**
     * 是否允许通过手机号搜索: 0-不允许, 1-允许
     */
    @TableField("allow_phone_search")
    private Boolean allowPhoneSearch = true;

    /**
     * 是否允许通过邮箱搜索: 0-不允许, 1-允许
     */
    @TableField("allow_email_search")
    private Boolean allowEmailSearch = true;

    /**
     * 在线状态可见性: ALL(所有人), FRIENDS(仅好友), NONE(不可见)
     */
    @TableField("online_status_visibility")
    private String onlineStatusVisibility = "FRIENDS";

    /**
     * 最后在线时间可见性: ALL(所有人), FRIENDS(仅好友), NONE(不可见)
     */
    @TableField("last_seen_visibility")
    private String lastSeenVisibility = "FRIENDS";

    /**
     * 个人信息可见性: ALL(所有人), FRIENDS(仅好友), NONE(不可见)
     */
    @TableField("profile_visibility")
    private String profileVisibility = "ALL";

    // ========== 安全设置 ==========

    /**
     * 登录失败次数
     */
    @TableField("login_fail_count")
    private Integer loginFailCount = 0;

    /**
     * 登录失败锁定时间
     */
    @TableField("login_lock_until")
    private LocalDateTime loginLockUntil;

    /**
     * 是否开启两步验证
     */
    @TableField("two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    /**
     * 两步验证密钥
     */
    @TableField("two_factor_secret")
    private String twoFactorSecret;

    // ========== 扩展数据 ==========

    /**
     * 扩展数据 - JSON格式存储额外信息
     */
    @TableField("extra_data")
    private String extraData;

    // ========== 元数据 ==========

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 是否删除: 0-正常, 1-已删除
     */
    @TableField("deleted")
    private Boolean deleted = false;

    // ========== 便捷方法 ==========

    /**
     * 判断用户是否被锁定
     */
    public boolean isLocked() {
        if (loginLockUntil == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(loginLockUntil);
    }

    /**
     * 增加登录失败次数
     */
    public void incrementLoginFail() {
        if (this.loginFailCount == null) {
            this.loginFailCount = 0;
        }
        this.loginFailCount++;
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginFail() {
        this.loginFailCount = 0;
        this.loginLockUntil = null;
    }

    /**
     * 锁定账号
     * @param minutes 锁定分钟数
     */
    public void lockAccount(int minutes) {
        this.loginLockUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * 更新在线状态
     */
    public void updateOnlineStatus(String status) {
        this.onlineStatus = status;
        if ("ONLINE".equals(status)) {
            this.lastOnlineAt = LocalDateTime.now();
        }
    }

    /**
     * 检查用户是否允许添加好友
     */
    public boolean canAddFriend() {
        return !"NOBODY".equals(this.addFriendPermission);
    }

    /**
     * 检查用户是否需要验证才能添加好友
     */
    public boolean needVerifyToAddFriend() {
        return "VERIFY".equals(this.addFriendPermission);
    }

    /**
     * 更新最后登录信息
     */
    public void updateLastLogin(String ip, String device) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ip;
        this.lastLoginDevice = device;
        this.onlineStatus = "ONLINE";
        this.lastOnlineAt = LocalDateTime.now();
    }
}
