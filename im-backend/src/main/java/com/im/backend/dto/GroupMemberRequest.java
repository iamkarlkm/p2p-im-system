package com.im.backend.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 群组成员操作请求DTO
 * 功能 #5: 群组管理基础模块 - 成员管理(邀请/踢出)
 */
@Data
public class GroupMemberRequest {

    /**
     * 操作类型: invite-邀请 join-加入 kick-踢出 mute-禁言 unmute-解除禁言 admin-设为管理员 unadmin-取消管理员
     */
    public enum OperationType {
        INVITE,      // 邀请成员
        JOIN,        // 加入群组
        KICK,        // 踢出成员
        MUTE,        // 禁言
        UNMUTE,      // 解除禁言
        ADMIN,       // 设为管理员
        UNADMIN,     // 取消管理员
        TRANSFER     // 转让群主
    }

    /**
     * 群组ID
     */
    @NotBlank(message = "群组ID不能为空")
    private String groupId;

    /**
     * 目标用户ID
     */
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;

    /**
     * 操作类型
     */
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;

    /**
     * 操作原因(踢人/禁言时使用)
     */
    @Size(max = 200, message = "操作原因不能超过200个字符")
    private String reason;

    /**
     * 禁言时长(分钟), 0表示永久禁言
     */
    @Min(value = 0, message = "禁言时长不能为负数")
    private Integer muteDuration = 0;

    /**
     * 群昵称(邀请/加入时设置)
     */
    @Size(max = 50, message = "群昵称不能超过50个字符")
    private String groupNickname;

    /**
     * 邀请人ID(非必填,系统自动填充)
     */
    private Long inviterId;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群组ID不能为空");
        }
        if (targetUserId == null) {
            throw new IllegalArgumentException("目标用户ID不能为空");
        }
        if (operationType == null) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
        // 禁言操作需要验证时长
        if (operationType == OperationType.MUTE && muteDuration < 0) {
            throw new IllegalArgumentException("禁言时长不能为负数");
        }
    }

    /**
     * 是否是管理员操作
     */
    public boolean isAdminOperation() {
        return operationType == OperationType.KICK ||
               operationType == OperationType.MUTE ||
               operationType == OperationType.UNMUTE ||
               operationType == OperationType.ADMIN ||
               operationType == OperationType.UNADMIN;
    }

    /**
     * 是否是群主操作
     */
    public boolean isOwnerOperation() {
        return operationType == OperationType.TRANSFER;
    }
}
