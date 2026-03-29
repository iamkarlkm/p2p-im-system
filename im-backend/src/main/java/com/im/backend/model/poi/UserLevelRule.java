package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户等级规则配置实体
 * 定义各等级的积分要求和权益
 */
@Entity
@Table(name = "user_level_rules")
public class UserLevelRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String levelCode;

    @Column(nullable = false, length = 50)
    private String levelName;

    @Column(nullable = false)
    private Integer minPoints;

    @Column(nullable = false)
    private Integer maxPoints;

    @Column(nullable = false)
    private Integer levelOrder;

    @Column(length = 200)
    private String iconUrl;

    @Column(length = 500)
    private String privileges;

    @Column(nullable = false)
    private Double checkinBonus = 1.0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private LocalDateTime createdAt;

    @Column
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLevelCode() { return levelCode; }
    public void setLevelCode(String levelCode) { this.levelCode = levelCode; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public Integer getMinPoints() { return minPoints; }
    public void setMinPoints(Integer minPoints) { this.minPoints = minPoints; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getPrivileges() { return privileges; }
    public void setPrivileges(String privileges) { this.privileges = privileges; }

    public Double getCheckinBonus() { return checkinBonus; }
    public void setCheckinBonus(Double checkinBonus) { this.checkinBonus = checkinBonus; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
