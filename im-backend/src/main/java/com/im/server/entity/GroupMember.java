package com.im.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群成员实体
 */
@Data
@Entity
@Table(name = "t_group_member")
public class GroupMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer role = 1; // 1:成员 2:管理员 3:群主
    
    @Column(length = 50)
    private String nickname;
    
    @Column(name = "join_time")
    private LocalDateTime joinTime;
    
    @PrePersist
    protected void onCreate() {
        joinTime = LocalDateTime.now();
    }
}
