package com.im.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户标签实体
 * 存储用户的兴趣标签
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tags", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_tag_name", columnList = "tagName"),
    @Index(name = "idx_user_tag", columnList = "userId, tagName", unique = true)
})
public class UserTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String tagName;

    @Column(length = 20)
    private String category;

    @Column
    private Integer weight;

    @Column
    private Boolean isPublic;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (weight == null) {
            weight = 1;
        }
        if (isPublic == null) {
            isPublic = true;
        }
    }
}
