package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 协作增强 AI 助手实体
 * 存储团队协作相关的 AI 助手配置、状态和分析结果
 */
@Entity
@Table(name = "collaboration_ai_config", indexes = {
    @Index(name = "idx_session_id", columnList = "sessionId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_enabled", columnList = "enabled")
})
@Data
public class CollaborationAIEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String sessionId;

    @Column(nullable = false, length = 64)
    private String userId;

    @Column(length = 64)
    private String groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CollaborationType collaborationType;

    @Column(columnDefinition = "TEXT")
    private String meetingMinutes;

    @Column(columnDefinition = "TEXT")
    private String projectProgress;

    @Column(columnDefinition = "TEXT")
    private String taskAssignments;

    @Column(columnDefinition = "TEXT")
    private String collaborationPatterns;

    @Column(columnDefinition = "TEXT")
    private String realtimeSuggestions;

    @Column(columnDefinition = "TEXT")
    private String efficiencyReport;

    @Column(columnDefinition = "TEXT")
    private String teamKnowledge;

    @Column(columnDefinition = "TEXT")
    private String bottleneckAnalysis;

    @Column(columnDefinition = "TEXT")
    private String roleAllocation;

    @Column(columnDefinition = "TEXT")
    private String meetingQuality;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Integer aiConfidence = 0;

    @Column(nullable = false)
    private Integer analysisFrequency = 60;

    @Column(nullable = false)
    private Boolean autoGenerateMinutes = true;

    @Column(nullable = false)
    private Boolean trackProgress = true;

    @Column(nullable = false)
    private Boolean identifyTasks = true;

    @Column(nullable = false)
    private Boolean analyzePatterns = true;

    @Column(nullable = false)
    private Boolean provideSuggestions = true;

    @Column(nullable = false)
    private Boolean generateReport = true;

    @Column(nullable = false)
    private Boolean buildKnowledge = true;

    @Column(nullable = false)
    private Boolean identifyBottlenecks = true;

    @Column(nullable = false)
    private Boolean optimizeRoles = true;

    @Column(nullable = false)
    private Boolean assessMeetings = true;

    @ElementCollection
    @CollectionTable(name = "collaboration_ai_insights", joinColumns = @JoinColumn(name = "collaboration_id"))
    @Column(name = "insight")
    private List<String> insights = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "collaboration_ai_recommendations", joinColumns = @JoinColumn(name = "collaboration_id"))
    @Column(name = "recommendation")
    private List<String> recommendations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "collaboration_ai_metrics", joinColumns = @JoinColumn(name = "collaboration_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> performanceMetrics = new HashMap<>();

    @Column(columnDefinition = "JSON")
    private String customSettings;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column
    private LocalDateTime lastAnalysisAt;

    @Column
    private LocalDateTime nextAnalysisAt;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CollaborationType {
        TEAM_MEETING,
        PROJECT_DISCUSSION,
        BRAINSTORM_SESSION,
        DAILY_STANDUP,
        RETROSPECTIVE,
        PLANNING_SESSION,
        CODE_REVIEW,
        DESIGN_REVIEW,
        TRAINING_SESSION,
        CLIENT_MEETING,
        ONE_ON_ONE,
        WORKSHOP,
        CONFERENCE,
        OTHER
    }
}