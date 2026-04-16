package com.im.service.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 群组实体类
 * 存储群组的基本信息、设置、公告、扩展数据等
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("im_group")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础信息 ====================

    /**
     * 群组ID，主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 群组名称
     */
    private String name;

    /**
     * 群组头像URL
     */
    private String avatar;

    /**
     * 群组描述/简介
     */
    private String description;

    /**
     * 群主用户ID
     */
    private Long ownerId;

    /**
     * 群组类型：0-普通群，1-企业群，2-班级群，3-兴趣群，4-临时群
     */
    private Integer type;

    // ==================== 群设置 ====================

    /**
     * 加入方式：0-自由加入，1-需验证，2-邀请加入，3-禁止加入
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * 发言权限：0-所有人可发言，1-仅管理员可发言
     */
    @TableField("speak_permission")
    private Integer speakPermission;

    /**
     * 是否允许成员邀请：0-否，1-是
     */
    @TableField("allow_member_invite")
    private Boolean allowMemberInvite;

    /**
     * 是否允许成员修改群名：0-否，1-是
     */
    @TableField("allow_member_modify_name")
    private Boolean allowMemberModifyName;

    /**
     * 是否开启群验证：0-否，1-是
     */
    @TableField("enable_verify")
    private Boolean enableVerify;

    /**
     * 群最大成员数，默认500
     */
    @TableField("max_members")
    private Integer maxMembers;

    /**
     * 当前成员数
     */
    @TableField("member_count")
    private Integer memberCount;

    // ==================== 公告信息 ====================

    /**
     * 群公告内容
     */
    @TableField("announcement")
    private String announcement;

    /**
     * 公告发布者ID
     */
    @TableField("announcement_publisher_id")
    private Long announcementPublisherId;

    /**
     * 公告发布时间
     */
    @TableField("announcement_time")
    private LocalDateTime announcementTime;

    /**
     * 公告置顶：0-否，1-是
     */
    @TableField("announcement_pinned")
    private Boolean announcementPinned;

    // ==================== 禁言设置 ====================

    /**
     * 全员禁言：0-否，1-是
     */
    @TableField("all_muted")
    private Boolean allMuted;

    /**
     * 禁言结束时间（全员禁言到期时间）
     */
    @TableField("mute_end_time")
    private LocalDateTime muteEndTime;

    // ==================== 状态管理 ====================

    /**
     * 群组状态：0-正常，1-解散
     */
    private Integer status;

    /**
     * 群组解散时间
     */
    @TableField("dissolve_time")
    private LocalDateTime dissolveTime;

    /**
     * 解散操作者ID
     */
    @TableField("dissolve_by")
    private Long dissolveBy;

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
     * 检查群组是否已解散
     */
    public boolean isDissolved() {
        return status != null && status == 1;
    }

    /**
     * 检查群组是否已满员
     */
    public boolean isFull() {
        return memberCount != null && maxMembers != null && memberCount >= maxMembers;
    }

    /**
     * 检查是否需要验证才能加入
     */
    public boolean needVerification() {
        return joinType != null && joinType == 1;
    }

    /**
     * 检查是否仅管理员可发言
     */
    public boolean isAdminOnlySpeak() {
        return speakPermission != null && speakPermission == 1;
    }

    /**
     * 检查当前是否全员禁言中
     */
    public boolean isCurrentlyMuted() {
        if (allMuted == null || !allMuted) {
            return false;
        }
        if (muteEndTime == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(muteEndTime);
    }

    /**
     * 检查成员是否有邀请权限
     */
    public boolean canMemberInvite() {
        return allowMemberInvite != null && allowMemberInvite;
    }

    /**
     * 解散群组
     *
     * @param operatorId 操作者ID
     */
    public void dissolve(Long operatorId) {
        this.status = 1;
        this.dissolveTime = LocalDateTime.now();
        this.dissolveBy = operatorId;
    }

    /**
     * 更新群公告
     *
     * @param content     公告内容
     * @param publisherId 发布者ID
     */
    public void updateAnnouncement(String content, Long publisherId) {
        this.announcement = content;
        this.announcementPublisherId = publisherId;
        this.announcementTime = LocalDateTime.now();
        this.announcementPinned = true;
    }

    /**
     * 清除群公告
     */
    public void clearAnnouncement() {
        this.announcement = null;
        this.announcementPublisherId = null;
        this.announcementTime = null;
        this.announcementPinned = false;
    }

    /**
     * 全员禁言
     *
     * @param durationMinutes 禁言时长（分钟），null表示永久
     */
    public void muteAll(Integer durationMinutes) {
        this.allMuted = true;
        if (durationMinutes != null && durationMinutes > 0) {
            this.muteEndTime = LocalDateTime.now().plusMinutes(durationMinutes);
        } else {
            this.muteEndTime = null;
        }
    }

    /**
     * 取消全员禁言
     */
    public void unmuteAll() {
        this.allMuted = false;
        this.muteEndTime = null;
    }

    /**
     * 增加成员数
     */
    public void incrementMemberCount() {
        if (this.memberCount == null) {
            this.memberCount = 1;
        } else {
            this.memberCount++;
        }
    }

    /**
     * 减少成员数
     */
    public void decrementMemberCount() {
        if (this.memberCount != null && this.memberCount > 0) {
            this.memberCount--;
        }
    }

    /**
     * 更新群组信息
     *
     * @param name        群组名称
     * @param avatar      群组头像
     * @param description 群组描述
     */
    public void updateInfo(String name, String avatar, String description) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (avatar != null && !avatar.isEmpty()) {
            this.avatar = avatar;
        }
        if (description != null) {
            this.description = description;
        }
    }

    /**
     * 更新群设置
     *
     * @param joinType               加入方式
     * @param speakPermission        发言权限
     * @param allowMemberInvite      允许成员邀请
     * @param allowMemberModifyName  允许成员修改群名
     * @param enableVerify           开启群验证
     */
    public void updateSettings(Integer joinType, Integer speakPermission,
                               Boolean allowMemberInvite, Boolean allowMemberModifyName,
                               Boolean enableVerify) {
        if (joinType != null) {
            this.joinType = joinType;
        }
        if (speakPermission != null) {
            this.speakPermission = speakPermission;
        }
        if (allowMemberInvite != null) {
            this.allowMemberInvite = allowMemberInvite;
        }
        if (allowMemberModifyName != null) {
            this.allowMemberModifyName = allowMemberModifyName;
        }
        if (enableVerify != null) {
            this.enableVerify = enableVerify;
        }
    }

    // ==================== 常量定义 ====================

    /**
     * 群组类型
     */
    public static final class GroupType {
        /** 普通群 */
        public static final int NORMAL = 0;
        /** 企业群 */
        public static final int ENTERPRISE = 1;
        /** 班级群 */
        public static final int CLASS = 2;
        /** 兴趣群 */
        public static final int INTEREST = 3;
        /** 临时群 */
        public static final int TEMPORARY = 4;
    }

    /**
     * 加入方式
     */
    public static final class JoinType {
        /** 自由加入 */
        public static final int FREE = 0;
        /** 需验证 */
        public static final int VERIFY = 1;
        /** 邀请加入 */
        public static final int INVITE = 2;
        /** 禁止加入 */
        public static final int FORBIDDEN = 3;
    }

    /**
     * 发言权限
     */
    public static final class SpeakPermission {
        /** 所有人可发言 */
        public static final int ALL = 0;
        /** 仅管理员可发言 */
        public static final int ADMIN_ONLY = 1;
    }

    /**
     * 群组状态
     */
    public static final class Status {
        /** 正常 */
        public static final int NORMAL = 0;
        /** 解散 */
        public static final int DISSOLVED = 1;
    }
}
