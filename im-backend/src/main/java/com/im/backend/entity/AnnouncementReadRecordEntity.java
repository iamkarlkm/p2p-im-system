package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 群公告已读记录实体
 * 跟踪每个用户对公告的阅读状态
 */
@Entity
@Table(name = "im_announcement_read",
    uniqueConstraints = @UniqueConstraint(columnNames = {"announcementId", "userId"}),
    indexes = {
        @Index(name = "idx_ann_user", columnList = "userId"),
        @Index(name = "idx_ann_ann", columnList = "announcementId")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementReadRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 公告ID */
    @Column(nullable = false)
    private Long announcementId;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 阅读时间 */
    @Column(nullable = false)
    private LocalDateTime readTime;

    /** 阅读设备 */
    @Column(length = 50)
    private String deviceType;

    /** 是否确认过（紧急公告需要确认） */
    @Column(nullable = false)
    @Builder.Default
    private Boolean confirmed = false;

    @PrePersist
    protected void onCreate() {
        if (readTime == null) {
            readTime = LocalDateTime.now();
        }
    }
}
