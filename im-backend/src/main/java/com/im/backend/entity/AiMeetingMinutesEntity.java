package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 协作增强 AI 助手 - 会议纪要实体
 */
@Entity
@Table(name = "ai_meeting_minutes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiMeetingMinutesEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "meeting_id", nullable = false, unique = true, length = 64)
    private String meetingId;
    
    @Column(name = "session_id", length = 64)
    private String sessionId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "organizer_id")
    private Long organizerId;
    
    @Column(name = "participant_ids")
    private String participantIds;
    
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "key_decisions", columnDefinition = "TEXT")
    private String keyDecisions;
    
    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;
    
    @Column(name = "tasks_assigned", columnDefinition = "JSON")
    private String tasksAssigned;
    
    @Column(name = "collaboration_score")
    private Double collaborationScore;
    
    @Column(name = "efficiency_rating")
    private String efficiencyRating;
    
    @Column(name = "bottlenecks_identified", columnDefinition = "TEXT")
    private String bottlenecksIdentified;
    
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;
    
    @Column(name = "full_transcript", columnDefinition = "TEXT")
    private String fullTranscript;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
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