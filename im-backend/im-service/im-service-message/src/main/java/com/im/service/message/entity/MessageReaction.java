package com.im.service.message.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 消息反应实体 - 存储用户对消息的表情反应
 * 对应数据库表: im_message_reaction
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Entity
@Table(name = "im_message_reaction", indexes = {
    @Index(name = "idx_reaction_msg", columnList = "messageId"),
    @Index(name = "idx_reaction_user", columnList = "userId"),
    @Index(name = "idx_reaction_msg_user", columnList = "messageId, userId", unique = true),
    @Index(name = "idx_reaction_type", columnList = "reactionType")
})
public class MessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    /** 
     * 消息ID - 关联消息表
     */
    @Column(nullable = false, length = 36)
    private String messageId;

    /** 
     * 用户ID - 添加反应的用户
     */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 
     * 反应类型 - emoji 字符或自定义代码
     * 如: 👍, ❤️, 😂, 😮, 😢, 🎉
     */
    @Column(nullable = false, length = 50)
    private String reactionType;

    /** 
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
