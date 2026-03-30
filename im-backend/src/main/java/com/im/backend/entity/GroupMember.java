package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 群组成员实体类
 * 功能 #5: 群组管理基础模块 - 成员管理(邀请/踢出)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_group_member")
public class GroupMember {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群组ID
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 成员用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 成员角色: 0-成员 1-管理员 2-群主
     */
    @TableField("role")
    private Integer role;

    /**
     * 群昵称
     */
    @TableField("group_nickname")
    private String groupNickname;

    /**
     * 是否禁言: 0-否 1-是
     */
    @TableField("muted")
    private Integer muted;

    /**
     * 禁言截止时间
     */
    @TableField("mute_until")
    private LocalDateTime muteUntil;

    /**
     * 进群方式: 0-邀请 1-扫码 2-搜索 3-名片分享
     */
    @TableField("join_method")
    private Integer joinMethod;

    /**
     * 邀请人ID
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 成员状态: 0-正常 1-退群 2-踢出
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后活跃时间
     */
    @TableField("last_active_at")
    private LocalDateTime lastActiveAt;

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
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 成员用户信息(非数据库字段)
     */
    @TableField(exist = false)
    private User user;

    /**
     * 邀请人信息(非数据库字段)
     */
    @TableField(exist = false)
    private User inviter;

    /**
     * 获取角色描述
     */
    public String getRoleDesc() {
        switch (role) {
            case 0: return "成员";
            case 1: return "管理员";
            case 2: return "群主";
            default: return "未知";
        }
    }

    /**
     * 获取进群方式描述
     */
    public String getJoinMethodDesc() {
        switch (joinMethod) {
            case 0: return "邀请进群";
            case 1: return "扫码进群";
            case 2: return "搜索进群";
            case 3: return "名片分享";
            default: return "未知";
        }
    }

    /**
     * 获取成员状态描述
     */
    public String getStatusDesc() {
        switch (status) {
            case 0: return "正常";
            case 1: return "已退群";
            case 2: return "已被踢出";
            default: return "未知";
        }
    }

    /**
     * 检查是否为群主
     */
    public boolean isOwner() {
        return role != null && role == 2;
    }

    /**
     * 检查是否为管理员
     */
    public boolean isAdmin() {
        return role != null && (role == 1 || role == 2);
    }

    /**
     * 检查是否被禁言
     */
    public boolean isMuted() {
        if (muted == null || muted == 0) {
            return false;
        }
        if (muteUntil != null && muteUntil.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否还在群中
     */
    public boolean isInGroup() {
        return status != null && status == 0;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        if (groupNickname != null && !groupNickname.isEmpty()) {
            return groupNickname;
        }
        if (user != null && user.getNickname() != null) {
            return user.getNickname();
        }
        return "未知用户";
    }
}
