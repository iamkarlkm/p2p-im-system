package com.im.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 好友关系实体
 * 功能ID: #5
 */
@Data
@Entity
@Table(name = "friend_relations")
public class FriendRelation {
    @Id
    private String id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "friend_id")
    private String friendId;
    
    @Column(name = "remark")
    private String remark; // 好友备注
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
