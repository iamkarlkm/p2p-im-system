package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * 积分交易记录DTO
 */
public class PointTransactionDTO {
    
    private Long id;
    private String transactionType;
    private String transactionTypeName;
    private Integer points;
    private Integer balanceAfter;
    private String description;
    private String sourceType;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getTransactionTypeName() { return transactionTypeName; }
    public void setTransactionTypeName(String transactionTypeName) { this.transactionTypeName = transactionTypeName; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
