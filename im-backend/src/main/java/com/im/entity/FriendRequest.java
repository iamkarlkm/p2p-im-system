package com.im.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 好友申请实体
 * 功能ID: #5
 */
@Data
@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    private String id;
    
    @Column(name = "from_user_id")
    private String fromUserId;
    
    @Column(name = "to_user_id")
    private String toUserId;
    
    @Column(name = "message")
    private String message; // 申请留言
    
    @Column(name = "status")
    private Integer status; // 0:待处理 1:已同意 2:已拒绝
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "handled_at")
    private LocalDateTime handledAt;
}
