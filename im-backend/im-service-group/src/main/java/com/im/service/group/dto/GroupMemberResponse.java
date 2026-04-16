package com.im.service.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 群成员响应DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupMemberResponse {

    /**
     * 成员记录ID
     */
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
     * 用户信息
     */
    private Map<String, Object> userInfo;

    /**
     * 成员昵称（在群内的显示名称）
     */
    private String nickname;

    /**
     * 显示名称（优先使用群昵称，否则使用用户昵称）
     */
    private String displayName;

    // ==================== 角色与权限 ====================

    /**
     * 成员角色：0-成员，1-管理员，2-群主
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否有邀请权限
     */
    private Boolean canInvite;

    /**
     * 是否有禁言权限
     */
    private Boolean canMute;

    /**
     * 是否有修改群信息权限
     */
    private Boolean canModifyInfo;

    /**
     * 是否有移除成员权限
     */
    private Boolean canRemoveMember;

    // ==================== 禁言状态 ====================

    /**
     * 是否被禁言
     */
    private Boolean muted;

    /**
     * 禁言结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime muteEndTime;

    /**
     * 当前是否处于禁言状态
     */
    private Boolean currentlyMuted;

    /**
     * 禁言原因
     */
    private String muteReason;

    /**
     * 执行禁言的管理员ID
     */
    private Long mutedBy;

    /**
     * 执行禁言的管理员信息
     */
    private Map<String, Object> mutedByInfo;

    // ==================== 进群信息 ====================

    /**
     * 加入方式：0-创建，1-邀请，2-扫码，3-链接，4-搜索加入
     */
    private Integer joinType;

    /**
     * 加入方式名称
     */
    private String joinTypeName;

    /**
     * 邀请人ID
     */
    private Long invitedBy;

    /**
     * 邀请人信息
     */
    private Map<String, Object> invitedByInfo;

    /**
     * 进群时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    // ==================== 消息设置 ====================

    /**
     * 消息免打扰
     */
    private Boolean muteNotifications;

    /**
     * 置顶该群
     */
    private Boolean pinned;

    /**
     * 显示群成员昵称
     */
    private Boolean showNickname;

    // ==================== 状态信息 ====================

    /**
     * 成员状态：0-正常，1-已退出，2-被移除
     */
    private Integer status;

    /**
     * 成员状态名称
     */
    private String statusName;

    /**
     * 退出/被移除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveTime;

    /**
     * 最后发言时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSpeakTime;

    // ==================== 时间戳 ====================

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
