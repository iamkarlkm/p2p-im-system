package com.im.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 单聊会话实体
 * 功能ID: #6
 */
@Data
@Entity
@Table(name = "private_chat_sessions")
public class PrivateChatSession {
    @Id
    private String id;
    
    @Column(name = "user1_id")
    private String user1Id;
    
    @Column(name = "user2_id")
    private String user2Id;
    
    @Column(name = "last_message_id")
    private String lastMessageId;
    
    @Column(name = "last_message_content")
    private String lastMessageContent;
    
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;
    
    @Column(name = "unread_count_user1")
    private Integer unreadCountUser1;
    
    @Column(name = "unread_count_user2")
    private Integer unreadCountUser2;
    
    @Column(name = "pinned")
    private Boolean pinned;
    
    @Column(name = "muted")
    private Boolean muted;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
