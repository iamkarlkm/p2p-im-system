package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分交易记录实体
 * 记录积分的获取和消费明细
 */
@Entity
@Table(name = "point_transaction_records", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_transaction_type", columnList = "transactionType"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class PointTransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 32)
    private String transactionType;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private Integer balanceBefore;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column(length = 64)
    private String relatedId;

    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String sourceType;

    @Column(length = 500)
    private String extraData;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(Integer balanceBefore) { this.balanceBefore = balanceBefore; }

    public Integer getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
