package com.im.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息实体类
 * 功能ID: #4
 */
@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    private String id;
    
    @Column(name = "from_user_id")
    private String fromUserId;
    
    @Column(name = "to_user_id")
    private String toUserId;
    
    @Column(name = "group_id")
    private String groupId;
    
    @Column(name = "content", length = 2000)
    private String content;
    
    @Column(name = "message_type")
    private String messageType;
    
    @Column(name = "status")
    private Integer status; // 0:未读 1:已读
    
    @Column(name = "recalled")
    private boolean recalled;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
