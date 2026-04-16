package com.im.service.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 群组成员实体类
 * 存储群成员的信息、角色、权限、禁言状态等
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("im_group_member")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础信息 ====================

    /**
     * 成员记录ID，主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 成员昵称（在群内的显示名称）
     */
    private String nickname;

    // ==================== 角色与权限 ====================

    /**
     * 成员角色：0-成员，1-管理员，2-群主
     */
    private Integer role;

    /**
     * 是否有邀请权限：0-否，1-是
     */
    @TableField("can_invite")
    private Boolean canInvite;

    /**
     * 是否有禁言权限：0-否，1-是
     */
    @TableField("can_mute")
    private Boolean canMute;

    /**
     * 是否有修改群信息权限：0-否，1-是
     */
    @TableField("can_modify_info")
    private Boolean canModifyInfo;

    /**
     * 是否有移除成员权限：0-否，1-是
     */
    @TableField("can_remove_member")
    private Boolean canRemoveMember;

    // ==================== 禁言状态 ====================

    /**
     * 是否被禁言：0-否，1-是
     */
    @TableField("is_muted")
    private Boolean muted;

    /**
     * 禁言结束时间
     */
    @TableField("mute_end_time")
    private LocalDateTime muteEndTime;

    /**
     * 禁言原因
     */
    @TableField("mute_reason")
    private String muteReason;

    /**
     * 执行禁言的管理员ID
     */
    @TableField("muted_by")
    private Long mutedBy;

    // ==================== 进群信息 ====================

    /**
     * 加入方式：0-创建，1-邀请，2-扫码，3-链接，4-搜索加入
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * 邀请人ID（如果是被邀请加入）
     */
    @TableField("invited_by")
    private Long invitedBy;

    /**
     * 进群时间
     */
    @TableField("join_time")
    private LocalDateTime joinTime;

    // ==================== 消息设置 ====================

    /**
     * 消息免打扰：0-否，1-是
     */
    @TableField("mute_notifications")
    private Boolean muteNotifications;

    /**
     * 置顶该群：0-否，1-是
     */
    @TableField("is_pinned")
    private Boolean pinned;

    /**
     * 显示群成员昵称：0-否，1-是
     */
    @TableField("show_nickname")
    private Boolean showNickname;

    // ==================== 成员状态 ====================

    /**
     * 成员状态：0-正常，1-已退出，2-被移除
     */
    private Integer status;

    /**
     * 退出/被移除时间
     */
    @TableField("leave_time")
    private LocalDateTime leaveTime;

    /**
     * 最后发言时间
     */
    @TableField("last_speak_time")
    private LocalDateTime lastSpeakTime;

    // ==================== 扩展数据 ====================

    /**
     * 扩展数据（JSON格式）
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> extra;

    // ==================== 时间戳 ====================

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除（逻辑删除）
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted;

    // ==================== 便捷方法 ====================

    /**
     * 检查是否是群主
     */
    public boolean isOwner() {
        return role != null && role == Role.OWNER;
    }

    /**
     * 检查是否是管理员
     */
    public boolean isAdmin() {
        return role != null && role == Role.ADMIN;
    }

    /**
     * 检查是否是普通成员
     */
    public boolean isMember() {
        return role != null && role == Role.MEMBER;
    }

    /**
     * 检查是否是管理员或以上角色
     */
    public boolean isAdminOrAbove() {
        return isAdmin() || isOwner();
    }

    /**
     * 检查当前是否处于禁言状态
     */
    public boolean isCurrentlyMuted() {
        if (muted == null || !muted) {
            return false;
        }
        if (muteEndTime == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(muteEndTime);
    }

    /**
     * 禁言成员
     *
     * @param durationMinutes 禁言时长（分钟），null表示永久
     * @param reason          禁言原因
     * @param operatorId      操作者ID
     */
    public void mute(Integer durationMinutes, String reason, Long operatorId) {
        this.muted = true;
        this.muteReason = reason;
        this.mutedBy = operatorId;
        if (durationMinutes != null && durationMinutes > 0) {
            this.muteEndTime = LocalDateTime.now().plusMinutes(durationMinutes);
        } else {
            this.muteEndTime = null;
        }
    }

    /**
     * 解除禁言
     */
    public void unmute() {
        this.muted = false;
        this.muteEndTime = null;
        this.muteReason = null;
        this.mutedBy = null;
    }

    /**
     * 设置成员角色
     *
     * @param role 角色：0-成员，1-管理员，2-群主
     */
    public void setRole(Integer role) {
        this.role = role;
        // 根据角色更新默认权限
        updateDefaultPermissions();
    }

    /**
     * 更新默认权限（根据角色）
     */
    private void updateDefaultPermissions() {
        if (isOwner()) {
            // 群主拥有所有权限
            this.canInvite = true;
            this.canMute = true;
            this.canModifyInfo = true;
            this.canRemoveMember = true;
        } else if (isAdmin()) {
            // 管理员默认权限
            this.canInvite = true;
            this.canMute = true;
            this.canModifyInfo = true;
            this.canRemoveMember = true;
        } else {
            // 普通成员默认权限（依赖群组设置）
            this.canInvite = false;
            this.canMute = false;
            this.canModifyInfo = false;
            this.canRemoveMember = false;
        }
    }

    /**
     * 标记成员已退出
     */
    public void markAsLeft() {
        this.status = Status.LEFT;
        this.leaveTime = LocalDateTime.now();
    }

    /**
     * 标记成员被移除
     */
    public void markAsRemoved() {
        this.status = Status.REMOVED;
        this.leaveTime = LocalDateTime.now();
    }

    /**
     * 更新最后发言时间
     */
    public void updateLastSpeakTime() {
        this.lastSpeakTime = LocalDateTime.now();
    }

    /**
     * 设置群消息免打扰
     *
     * @param mute true-开启免打扰，false-关闭免打扰
     */
    public void setMuteNotifications(boolean mute) {
        this.muteNotifications = mute;
    }

    /**
     * 设置置顶该群
     *
     * @param pin true-置顶，false-取消置顶
     */
    public void setPinned(boolean pin) {
        this.pinned = pin;
    }

    /**
     * 更新群内昵称
     *
     * @param nickname 昵称
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 检查成员是否在群中（正常状态）
     */
    public boolean isInGroup() {
        return status != null && status == Status.NORMAL;
    }

    /**
     * 检查成员是否已离开群
     */
    public boolean hasLeft() {
        return status != null && (status == Status.LEFT || status == Status.REMOVED);
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建群主成员
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @return GroupMember对象
     */
    public static GroupMember createOwner(Long groupId, Long userId) {
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(Role.OWNER);
        member.setJoinType(JoinType.CREATE);
        member.setJoinTime(LocalDateTime.now());
        member.setStatus(Status.NORMAL);
        member.setMuteNotifications(false);
        member.setPinned(false);
        member.setShowNickname(true);
        member.updateDefaultPermissions();
        return member;
    }

    /**
     * 创建普通成员
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param joinType   加入方式
     * @param invitedBy  邀请人ID
     * @return GroupMember对象
     */
    public static GroupMember createMember(Long groupId, Long userId, Integer joinType, Long invitedBy) {
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(Role.MEMBER);
        member.setJoinType(joinType);
        member.setInvitedBy(invitedBy);
        member.setJoinTime(LocalDateTime.now());
        member.setStatus(Status.NORMAL);
        member.setMuteNotifications(false);
        member.setPinned(false);
        member.setShowNickname(true);
        member.updateDefaultPermissions();
        return member;
    }

    // ==================== 常量定义 ====================

    /**
     * 成员角色
     */
    public static final class Role {
        /** 普通成员 */
        public static final int MEMBER = 0;
        /** 管理员 */
        public static final int ADMIN = 1;
        /** 群主 */
        public static final int OWNER = 2;
    }

    /**
     * 加入方式
     */
    public static final class JoinType {
        /** 创建 */
        public static final int CREATE = 0;
        /** 邀请 */
        public static final int INVITE = 1;
        /** 扫码 */
        public static final int SCAN = 2;
        /** 链接 */
        public static final int LINK = 3;
        /** 搜索加入 */
        public static final int SEARCH = 4;
    }

    /**
     * 成员状态
     */
    public static final class Status {
        /** 正常 */
        public static final int NORMAL = 0;
        /** 已退出 */
        public static final int LEFT = 1;
        /** 被移除 */
        public static final int REMOVED = 2;
    }
}
