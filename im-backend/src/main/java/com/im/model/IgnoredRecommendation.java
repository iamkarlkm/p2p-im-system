package com.im.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 已忽略推荐实体
 * 记录用户忽略的好友推荐
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ignored_recommendations", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_user_target", columnList = "userId, targetUserId", unique = true)
})
public class IgnoredRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long targetUserId;

    @Column
    private LocalDateTime ignoredAt;

    @Column(length = 200)
    private String ignoreReason;

    @PrePersist
    protected void onCreate() {
        ignoredAt = LocalDateTime.now();
    }
}
