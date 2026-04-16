package com.im.service.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 群组响应DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponse {

    /**
     * 群组ID
     */
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
     * 群主信息（可选）
     */
    private Map<String, Object> ownerInfo;

    /**
     * 群组类型：0-普通群，1-企业群，2-班级群，3-兴趣群，4-临时群
     */
    private Integer type;

    /**
     * 群组类型名称
     */
    private String typeName;

    // ==================== 群设置 ====================

    /**
     * 加入方式：0-自由加入，1-需验证，2-邀请加入，3-禁止加入
     */
    private Integer joinType;

    /**
     * 加入方式名称
     */
    private String joinTypeName;

    /**
     * 发言权限：0-所有人可发言，1-仅管理员可发言
     */
    private Integer speakPermission;

    /**
     * 是否允许成员邀请
     */
    private Boolean allowMemberInvite;

    /**
     * 是否允许成员修改群名
     */
    private Boolean allowMemberModifyName;

    /**
     * 是否开启群验证
     */
    private Boolean enableVerify;

    /**
     * 群最大成员数
     */
    private Integer maxMembers;

    /**
     * 当前成员数
     */
    private Integer memberCount;

    // ==================== 公告信息 ====================

    /**
     * 群公告内容
     */
    private String announcement;

    /**
     * 公告发布者ID
     */
    private Long announcementPublisherId;

    /**
     * 公告发布者信息（可选）
     */
    private Map<String, Object> announcementPublisherInfo;

    /**
     * 公告发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime announcementTime;

    /**
     * 公告置顶
     */
    private Boolean announcementPinned;

    // ==================== 禁言状态 ====================

    /**
     * 全员禁言
     */
    private Boolean allMuted;

    /**
     * 禁言结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime muteEndTime;

    /**
     * 当前是否全员禁言中
     */
    private Boolean currentlyMuted;

    // ==================== 状态信息 ====================

    /**
     * 群组状态：0-正常，1-解散
     */
    private Integer status;

    /**
     * 群组状态名称
     */
    private String statusName;

    // ==================== 当前用户相关 ====================

    /**
     * 当前用户在群中的角色：0-成员，1-管理员，2-群主
     */
    private Integer myRole;

    /**
     * 当前用户角色名称
     */
    private String myRoleName;

    /**
     * 当前用户是否被禁言
     */
    private Boolean myMuted;

    /**
     * 当前用户禁言结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime myMuteEndTime;

    /**
     * 当前用户消息免打扰设置
     */
    private Boolean myMuteNotifications;

    /**
     * 当前用户是否置顶该群
     */
    private Boolean myPinned;

    /**
     * 当前用户在群内的昵称
     */
    private String myNickname;

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
