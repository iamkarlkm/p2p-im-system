package com.im.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组实体
 */
@Data
@Entity
@Table(name = "t_group")
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_id", unique = true, nullable = false, length = 64)
    private String groupId;
    
    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;
    
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "member_count")
    private Integer memberCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String notice;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
