package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 消息@提及实体
 * 用于存储群聊中@成员的记录
 */
@Data
@Entity
@Table(name = "im_at_mention",
       indexes = {
           @Index(name = "idx_message_id", columnList = "messageId"),
           @Index(name = "idx_mentioned_user_id", columnList = "mentionedUserId"),
           @Index(name = "idx_room_id", columnList = "roomId"),
           @Index(name = "idx_mentioned_at", columnList = "mentionedAt")
       })
public class AtMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 消息ID */
    @Column(nullable = false)
    private Long messageId;

    /** 发送@的用户ID */
    @Column(nullable = false)
    private Long senderUserId;

    /** 被@提及的用户ID */
    @Column(nullable = false)
    private Long mentionedUserId;

    /** 群聊房间ID（单聊则为0或null） */
    private Long roomId;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead = false;

    /** 是否是@所有人 */
    @Column(nullable = false)
    private Boolean isAtAll = false;

    /** 是否发送了强提醒通知 */
    @Column(nullable = false)
    private Boolean notified = false;

    /** @提及时间 */
    @Column(nullable = false)
    private LocalDateTime mentionedAt;

    /** 消息内容摘要（便于展示） */
    private String messagePreview;

    /** 会话ID（用于快速关联） */
    private String conversationId;

    @PrePersist
    protected void onCreate() {
        if (mentionedAt == null) {
            mentionedAt = LocalDateTime.now();
        }
    }
}
