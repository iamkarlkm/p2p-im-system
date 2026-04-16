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
 * 好友关系实体 - 用户好友关系核心实体
 * 对应数据库表: im_friend
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_friend")
public class Friend {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // ========== 关系主体信息 ==========

    /**
     * 用户ID - 关系发起方
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 好友ID - 关系接受方
     */
    @TableField("friend_id")
    private Long friendId;

    // ========== 好友申请信息 ==========

    /**
     * 关系状态: PENDING(待确认), ACCEPTED(已接受), REJECTED(已拒绝), BLOCKED(已拉黑), DELETED(已删除)
     */
    @TableField("status")
    private String status = "PENDING";

    /**
     * 申请来源: SEARCH(搜索), QR_CODE(二维码), CONTACT(通讯录), GROUP(群聊), RECOMMEND(推荐), OTHER(其他)
     */
    @TableField("source")
    private String source;

    /**
     * 申请附言/验证消息
     */
    @TableField("apply_message")
    private String applyMessage;

    /**
     * 拒绝理由
     */
    @TableField("reject_reason")
    private String rejectReason;

    // ========== 好友设置 ==========

    /**
     * 好友备注名
     */
    @TableField("remark")
    private String remark;

    /**
     * 好友标签 - JSON数组格式
     */
    @TableField("tags")
    private String tags;

    /**
     * 是否星标好友: 0-否, 1-是
     */
    @TableField("starred")
    private Boolean starred = false;

    /**
     * 星标时间
     */
    @TableField("starred_at")
    private LocalDateTime starredAt;

    /**
     * 是否置顶聊天: 0-否, 1-是
     */
    @TableField("pinned")
    private Boolean pinned = false;

    /**
     * 置顶时间
     */
    @TableField("pinned_at")
    private LocalDateTime pinnedAt;

    /**
     * 消息免打扰: 0-否, 1-是
     */
    @TableField("mute_notifications")
    private Boolean muteNotifications = false;

    /**
     * 是否屏蔽好友: 0-否, 1-是
     */
    @TableField("blocked")
    private Boolean blocked = false;

    /**
     * 屏蔽时间
     */
    @TableField("blocked_at")
    private LocalDateTime blockedAt;

    // ========== 互动统计 ==========

    /**
     * 成为好友时间
     */
    @TableField("became_friends_at")
    private LocalDateTime becameFriendsAt;

    /**
     * 最后聊天时间
     */
    @TableField("last_chat_at")
    private LocalDateTime lastChatAt;

    /**
     * 聊天记录置顶消息ID
     */
    @TableField("pinned_message_id")
    private String pinnedMessageId;

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
     * 接受好友申请
     */
    public void accept() {
        this.status = "ACCEPTED";
        this.becameFriendsAt = LocalDateTime.now();
    }

    /**
     * 拒绝好友申请
     */
    public void reject(String reason) {
        this.status = "REJECTED";
        this.rejectReason = reason;
    }

    /**
     * 标记为星标好友
     */
    public void star() {
        this.starred = true;
        this.starredAt = LocalDateTime.now();
    }

    /**
     * 取消星标
     */
    public void unstar() {
        this.starred = false;
        this.starredAt = null;
    }

    /**
     * 置顶聊天
     */
    public void pin() {
        this.pinned = true;
        this.pinnedAt = LocalDateTime.now();
    }

    /**
     * 取消置顶
     */
    public void unpin() {
        this.pinned = false;
        this.pinnedAt = null;
    }

    /**
     * 屏蔽好友
     */
    public void block() {
        this.blocked = true;
        this.blockedAt = LocalDateTime.now();
        this.status = "BLOCKED";
    }

    /**
     * 取消屏蔽
     */
    public void unblock() {
        this.blocked = false;
        this.blockedAt = null;
        if ("BLOCKED".equals(this.status)) {
            this.status = "ACCEPTED";
        }
    }

    /**
     * 删除好友关系
     */
    public void delete() {
        this.status = "DELETED";
        this.deleted = true;
    }

    /**
     * 更新最后聊天时间
     */
    public void updateLastChatAt() {
        this.lastChatAt = LocalDateTime.now();
    }

    /**
     * 判断是否为双向好友关系
     */
    public boolean isMutualFriend() {
        return "ACCEPTED".equals(this.status);
    }

    /**
     * 判断是否被拉黑
     */
    public boolean isBlocked() {
        return this.blocked || "BLOCKED".equals(this.status);
    }

    /**
     * 判断是否在等待确认中
     */
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
}
