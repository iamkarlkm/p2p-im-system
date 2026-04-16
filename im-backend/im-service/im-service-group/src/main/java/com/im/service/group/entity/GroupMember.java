package com.im.service.group.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组成员实体
 */
@Data
@Entity
@Table(name = "im_group_member", indexes = {
    @Index(name = "idx_member_group", columnList = "groupId"),
    @Index(name = "idx_member_user", columnList = "userId"),
    @Index(name = "idx_member_role", columnList = "groupId,role")
})
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 群组ID */
    @Column(nullable = false, length = 36)
    private String groupId;

    /** 用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 群昵称 */
    @Column(length = 64)
    private String nickname;

    /** 角色: OWNER, ADMIN, MEMBER */
    @Column(nullable = false, length = 20)
    private String role;

    /** 是否被禁言 */
    private Boolean muted = false;

    private LocalDateTime mutedUntil;

    /** 进群时间 */
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    /** 最后活跃时间 */
    private LocalDateTime lastActiveAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        if (role == null) role = "MEMBER";
    }
}
