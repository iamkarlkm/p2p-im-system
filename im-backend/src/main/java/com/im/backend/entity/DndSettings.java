package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "dnd_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DndSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "repeat_days")
    private String repeatDays;

    @Column(name = "allow_mentions", nullable = false)
    private Boolean allowMentions;

    @Column(name = "allow_starred", nullable = false)
    private Boolean allowStarred;

    @Column(name = "custom_message")
    private String customMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (timezone == null) {
            timezone = "Asia/Shanghai";
        }
        if (repeatDays == null) {
            repeatDays = "1,2,3,4,5,6,7";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
