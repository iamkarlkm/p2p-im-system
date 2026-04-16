package com.im.service.local.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家评价实体
 */
@Data
@Entity
@Table(name = "im_local_review", indexes = {
    @Index(name = "idx_review_merchant", columnList = "merchantId"),
    @Index(name = "idx_review_user", columnList = "userId"),
    @Index(name = "idx_review_rating", columnList = "merchantId,rating")
})
public class MerchantReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商家ID */
    @Column(nullable = false, length = 36)
    private String merchantId;

    /** 用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 评分 1-5 */
    private Integer rating;

    /** 评价内容 */
    @Column(length = 1000)
    private String content;

    /** 评价图片 (JSON数组) */
    @Column(columnDefinition = "TEXT")
    private String images;

    /** 点赞数 */
    private Integer likeCount = 0;

    /** 是否已删除 */
    private Boolean deleted = false;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
