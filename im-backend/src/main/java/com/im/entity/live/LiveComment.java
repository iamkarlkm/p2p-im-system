package com.im.entity.live;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 直播评论/弹幕实体类
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_live_comment")
public class LiveComment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 评论ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long commentId;

    /** 直播间ID */
    private Long roomId;

    /** 发送者用户ID */
    private Long senderId;

    /** 发送者昵称 */
    private String senderNickname;

    /** 发送者头像 */
    private String senderAvatar;

    /** 评论内容 */
    private String content;

    /** 评论类型：1-普通弹幕 2-礼物消息 3-系统消息 4-点赞 5-进入直播间 */
    private Integer commentType;

    /** 引用评论ID（回复） */
    private Long replyToId;

    /** 被回复用户ID */
    private Long replyToUserId;

    /** 被回复用户昵称 */
    private String replyToNickname;

    /** 是否置顶：0-否 1-是 */
    private Integer isPinned;

    /** 是否主播消息：0-否 1-是 */
    private Integer isAnchor;

    /** 是否管理员：0-否 1-是 */
    private Integer isAdmin;

    /** 是否已审核：0-否 1-是 */
    private Integer isReviewed;

    /** 审核状态：0-待审核 1-通过 2-拒绝 */
    private Integer reviewStatus;

    /** 礼物ID（如果是礼物消息） */
    private Long giftId;

    /** 礼物数量 */
    private Integer giftCount;

    /** 礼物价值（分） */
    private Long giftValue;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 是否删除：0-否 1-是 */
    @TableLogic
    private Integer deleted;

    // ==================== 业务方法 ====================

    /**
     * 创建普通弹幕
     */
    public static LiveComment createNormalComment(Long roomId, Long senderId, String content) {
        LiveComment comment = new LiveComment();
        comment.setRoomId(roomId);
        comment.setSenderId(senderId);
        comment.setContent(content);
        comment.setCommentType(1);
        comment.setIsPinned(0);
        comment.setIsAnchor(0);
        comment.setIsAdmin(0);
        comment.setIsReviewed(0);
        comment.setReviewStatus(0);
        return comment;
    }

    /**
     * 创建礼物消息
     */
    public static LiveComment createGiftComment(Long roomId, Long senderId, Long giftId, Integer count, Long value) {
        LiveComment comment = new LiveComment();
        comment.setRoomId(roomId);
        comment.setSenderId(senderId);
        comment.setCommentType(2);
        comment.setGiftId(giftId);
        comment.setGiftCount(count);
        comment.setGiftValue(value);
        comment.setIsReviewed(1);
        comment.setReviewStatus(1);
        return comment;
    }

    /**
     * 创建系统消息
     */
    public static LiveComment createSystemMessage(Long roomId, String content) {
        LiveComment comment = new LiveComment();
        comment.setRoomId(roomId);
        comment.setContent(content);
        comment.setCommentType(3);
        comment.setIsReviewed(1);
        comment.setReviewStatus(1);
        return comment;
    }

    /**
     * 是否为礼物消息
     */
    public boolean isGift() {
        return commentType != null && commentType == 2;
    }

    /**
     * 是否为系统消息
     */
    public boolean isSystem() {
        return commentType != null && commentType == 3;
    }
}
