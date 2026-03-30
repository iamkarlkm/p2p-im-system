package com.im.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 搜索历史记录实体
 */
@Entity
@Table(name = "search_history", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_search_time", columnList = "createdAt")
})
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "keyword", length = 200, nullable = false)
    private String keyword;

    @Column(name = "search_type", length = 20)
    private String searchType; // MESSAGE, USER, GROUP, ALL

    @Column(name = "result_count")
    private Integer resultCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public SearchHistory() {}

    public SearchHistory(Long userId, String keyword, String searchType) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchType = searchType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public Integer getResultCount() { return resultCount; }
    public void setResultCount(Integer resultCount) { this.resultCount = resultCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
