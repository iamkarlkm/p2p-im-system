package com.im.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 群管理日志实体类
 * 记录群成员变动、管理员操作等日志信息
 */
@Entity
@Table(name = "group_management_logs", indexes = {
    @Index(name = "idx_group_management_logs_group_id", columnList = "groupId"),
    @Index(name = "idx_group_management_logs_operator_id", columnList = "operatorId"),
    @Index(name = "idx_group_management_logs_target_user_id", columnList = "targetUserId"),
    @Index(name = "idx_group_management_logs_action_type", columnList = "actionType"),
    @Index(name = "idx_group_management_logs_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupManagementLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * 群组ID
     */
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID groupId;

    /**
     * 操作者ID (管理员或系统)
     */
    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID operatorId;

    /**
     * 操作者类型: SYSTEM=系统, USER=用户, ADMIN=管理员
     */
    @Column(nullable = false, length = 20)
    private String operatorType;

    /**
     * 目标用户ID (被操作的用户)
     */
    @Column(columnDefinition = "BINARY(16)")
    private UUID targetUserId;

    /**
     * 操作类型
     */
    @Column(nullable = false, length = 50)
    private String actionType;

    /**
     * 操作子类型
     */
    @Column(length = 50)
    private String actionSubType;

    /**
     * 操作描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 操作详情 (JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * 操作前状态 (JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String beforeState;

    /**
     * 操作后状态 (JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String afterState;

    /**
     * IP地址
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * 设备信息
     */
    @Column(length = 200)
    private String deviceInfo;

    /**
     * 操作结果: SUCCESS=成功, FAILED=失败, PARTIAL=部分成功
     */
    @Column(nullable = false, length = 20)
    private String result;

    /**
     * 错误信息 (如果操作失败)
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 是否重要操作 (需要特殊关注)
     */
    @Column(nullable = false)
    private Boolean important = false;

    /**
     * 是否需要通知相关人员
     */
    @Column(nullable = false)
    private Boolean needNotification = false;

    /**
     * 是否已通知
     */
    @Column(nullable = false)
    private Boolean notified = false;

    /**
     * 是否已归档
     */
    @Column(nullable = false)
    private Boolean archived = false;

    /**
     * 归档时间
     */
    private LocalDateTime archivedAt;

    /**
     * 租户ID (多租户支持)
     */
    @Column(columnDefinition = "BINARY(16)")
    private UUID tenantId;

    /**
     * 版本号 (乐观锁)
     */
    @Version
    private Long version;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 操作类型枚举
     */
    public enum ActionType {
        MEMBER_ADD,        // 添加成员
        MEMBER_REMOVE,     // 移除成员
        MEMBER_KICK,       // 踢出成员
        MEMBER_LEAVE,      // 成员离开
        ROLE_CHANGE,       // 角色变更
        ADMIN_ADD,         // 添加管理员
        ADMIN_REMOVE,      // 移除管理员
        OWNER_TRANSFER,    // 群主转让
        GROUP_CREATE,      // 创建群组
        GROUP_DELETE,      // 删除群组
        GROUP_UPDATE,      // 更新群组信息
        GROUP_RENAME,      // 重命名群组
        GROUP_AVATAR,      // 更改群头像
        GROUP_DESC,        // 更改群描述
        GROUP_SETTINGS,    // 更改群设置
        ANNOUNCEMENT,      // 发布公告
        ANNOUNCEMENT_EDIT, // 编辑公告
        ANNOUNCEMENT_DELETE, // 删除公告
        MUTE_MEMBER,       // 禁言成员
        UNMUTE_MEMBER,     // 解除禁言
        BAN_MEMBER,        // 封禁成员
        UNBAN_MEMBER,      // 解封成员
        INVITE_SEND,       // 发送邀请
        INVITE_ACCEPT,     // 接受邀请
        INVITE_REJECT,     // 拒绝邀请
        INVITE_REVOKE,     // 撤销邀请
        JOIN_REQUEST,      // 加入请求
        JOIN_APPROVE,      // 批准加入
        JOIN_REJECT,       // 拒绝加入
        MESSAGE_DELETE,    // 删除消息
        MESSAGE_PIN,       // 置顶消息
        MESSAGE_UNPIN,     // 取消置顶
        FILE_UPLOAD,       // 上传文件
        FILE_DELETE,       // 删除文件
        POLL_CREATE,       // 创建投票
        POLL_CLOSE,        // 关闭投票
        EVENT_CREATE,      // 创建事件
        EVENT_UPDATE,      // 更新事件
        EVENT_DELETE,      // 删除事件
        CUSTOM             // 自定义操作
    }

    /**
     * 操作结果枚举
     */
    public enum Result {
        SUCCESS,    // 成功
        FAILED,     // 失败
        PARTIAL     // 部分成功
    }

    /**
     * 操作者类型枚举
     */
    public enum OperatorType {
        SYSTEM,     // 系统
        USER,       // 普通用户
        ADMIN,      // 管理员
        BOT         // 机器人
    }
}