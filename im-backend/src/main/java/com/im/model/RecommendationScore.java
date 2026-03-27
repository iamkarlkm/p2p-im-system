package com.im.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 推荐分数实体
 * 存储用户推荐计算的分数和元数据
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recommendation_scores", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_target_user_id", columnList = "targetUserId"),
    @Index(name = "idx_algorithm", columnList = "algorithmType"),
    @Index(name = "idx_score", columnList = "score")
})
public class RecommendationScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long targetUserId;

    @Column(nullable = false)
    private Double score;

    @Column(length = 50)
    private String algorithmType;

    @Column
    private Integer mutualFriendCount;

    @Column
    private Integer matchedTagCount;

    @Column
    private Integer commonGroupCount;

    @Column
    private Boolean isHelpful;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
