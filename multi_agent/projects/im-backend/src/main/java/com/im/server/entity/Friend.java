package com.im.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友关系实体
 */
@Data
@Entity
@Table(name = "t_friend")
public class Friend {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "friend_id", nullable = false)
    private Long friendId;
    
    @Column(name = "friend_remark", length = 50)
    private String friendRemark;
    
    @Column(nullable = false)
    private Integer status = 1; // 1:正常 2:拉黑
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
